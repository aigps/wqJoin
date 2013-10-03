package org.aigps.wq.ibatis;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.JobUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @Title：批量执行更新数据库操作
 * @Description：<类描述>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date：  2012-10-19下午03:50:21
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州元码科技有限公司
 */
@Component
public class IbatisUpdateJob {
	private static final Log log = LogFactory.getLog(IbatisUpdateJob.class);
	
	// 更新SQL的缓存 key -ibatisSQL   value -参数对象的队列
	private static Map<String, Queue<Object>> updateSqlObjectMap = new ConcurrentHashMap<String, Queue<Object>>();
	
	// SQL语句的执行状况  key - ibatisSQL  value - boolean(是否正在执行)
	private static Map<String, AtomicBoolean> updateSqlSttsMap = new ConcurrentHashMap<String, AtomicBoolean>();
	
	// 当前运行任务的总个数
	private static AtomicInteger taskCounter = new AtomicInteger();
	
	private static BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(200);
	private static BasicThreadFactory tfactory = new BasicThreadFactory.Builder().namingPattern("ibatisUpdateJob-pool-%d").daemon(true).build();
	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(2,5,30,TimeUnit.SECONDS,taskQueue,tfactory,new ThreadPoolExecutor.CallerRunsPolicy());

	@Autowired
	private static SqlMapClient sqlMapClient;
	public static SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}
	
	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		IbatisUpdateJob.sqlMapClient = sqlMapClient;
	}
	public void addExeSql(String ibatisSql,Object param){
		Queue<Object> params = updateSqlObjectMap.get(ibatisSql);
		if(params == null) {
			updateSqlObjectMap.put(ibatisSql, params = new ConcurrentLinkedQueue<Object>());
		}
		params.add(param);
	}
	
	public IbatisUpdateJob() {
		//定时执行缓存中的SQL语句
		JobUtil.interval(0.5, "Thread-DB-IbatisUpdateJob", new JobUtil.Callback() {
			public void execute() throws Exception {
				Iterator<String> sqlIt = updateSqlObjectMap.keySet().iterator();
				//创建线程池执行每一个SQL语句的批量更新
				while(sqlIt.hasNext()){
					final String sql = sqlIt.next();
					final Queue<Object> queue = updateSqlObjectMap.get(sql);
					boolean isSQLRunning = false;
					if(updateSqlSttsMap.containsKey(sql)){
						isSQLRunning = updateSqlSttsMap.get(sql).get();
					}
					//如果有数据，并且当然SQL没有在执行，放到一个单独的线程池中去执行
					if(queue.peek()!=null && !isSQLRunning){
						updateSqlSttsMap.put(sql, new AtomicBoolean(true));
						taskCounter.incrementAndGet();
						pool.execute(new Runnable() {
							public void run() {
								try {
									if(log.isTraceEnabled()){
										log.trace("正在执行ibatisSQL更新语句:"+sql);
									}
									sqlMapClient.startBatch();
									while(queue.peek()!=null){
										Object obj = queue.poll();
										sqlMapClient.update(sql, obj);
									}
									sqlMapClient.executeBatch();
								}catch (Exception e) {
									log.error("执行更新语句:"+sql+" 时出错!",e);
								}finally{
									taskCounter.decrementAndGet();
									updateSqlSttsMap.get(sql).set(false);
								}
							}
						});
					}
				}
				//如果任务数过大，扩充线程池
				if(taskCounter.get()/5>5){
					pool.setMaximumPoolSize(taskCounter.get()/5);
					if(log.isWarnEnabled()){
						log.warn("更新任务数有:"+taskCounter.get()+"  扩充线程池个数为:"+pool.getMaximumPoolSize());
					}
				}
			}
		});
	}
	
}
