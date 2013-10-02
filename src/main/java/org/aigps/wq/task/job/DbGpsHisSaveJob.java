/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.GisPosition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Administrator
 *
 */
public class DbGpsHisSaveJob implements Job {
	private static final Log log = LogFactory.getLog(DbGpsHisSaveJob.class);
	private static AtomicBoolean isRunning = new AtomicBoolean();
	public static final String ID="DbGpsHisSaveJob";
	public static Queue<GisPosition> swapGpsQueue = new ConcurrentLinkedQueue<GisPosition>();
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){
			Queue<GisPosition> gpsHis = DcGpsCache.gpsAddCache;
			DcGpsCache.gpsAddCache = swapGpsQueue;
			try {
				GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
				gpsDataDao.saveGpsHis(gpsHis);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}finally{
				gpsHis.clear();
				swapGpsQueue = gpsHis;
				isRunning.set(false);
			}
		}
	}
}
