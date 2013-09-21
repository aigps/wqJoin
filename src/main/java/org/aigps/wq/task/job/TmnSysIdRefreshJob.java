/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.HashMap;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqConfig;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.dao.GpsDataDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * �ն���ҵ��ID�Ķ��ո���
 * @author Administrator
 *
 */
public class TmnSysIdRefreshJob implements Job {
	private static final Log log = LogFactory.getLog(TmnSysIdRefreshJob.class);
	public static final String ID="TmnSysIdRefreshJob";
	private static boolean isRunning = false;//ͬһ��ʱ��㣬ֻ����һ��job����
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			try {
				GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
				WqConfig wqConfig = WqJoinContext.getBean("wqConfig", WqConfig.class);
				HashMap<String, String> tmnSysIdMap = gpsDataDao.getTmnSysIdMap(wqConfig.getTmnSysIdSql());
				DcGpsCache.setTmnSysIdMap(tmnSysIdMap);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning = false;
			}
		}
	}
}
