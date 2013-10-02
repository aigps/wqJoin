/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.DcRgAreaHis;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Administrator
 *
 */
public class DbGpsZcodeSaveJob implements Job {
	private static final Log log = LogFactory.getLog(DbGpsZcodeSaveJob.class);
	
	private static AtomicBoolean isRunning = new AtomicBoolean();//同一个时间点，只允许一个job跑数
	public static final String ID="DbGpsZcodeSaveJob";
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){
			Queue<DcRgAreaHis> dcRgAreaHis  =DcGpsCache.dcRgAreaHisQueue;
			try {
				GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
				if(dcRgAreaHis!=null && !dcRgAreaHis.isEmpty()){
					gpsDataDao.saveDcRgAreaHis(dcRgAreaHis);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning.set(false);;
			}
		}
	}


}
