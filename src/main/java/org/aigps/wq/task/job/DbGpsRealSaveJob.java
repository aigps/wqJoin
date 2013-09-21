/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.Map;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.GisPosition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

/**
 * @author Administrator
 *
 */
public class DbGpsRealSaveJob implements Job {
	private static final Log log = LogFactory.getLog(DbGpsRealSaveJob.class);
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="DbGpsRealSaveJob";
	/**
	 * 变动过的定位信息
	 */
	private static Map<String,GisPosition> swapGps = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();

	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			Map<String,GisPosition> gpsRealMap = DcGpsCache.changeGpsReport;
			DcGpsCache.changeGpsReport = swapGps;
			try {
				GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
				if(gpsRealMap!=null && gpsRealMap.size()>0){
					gpsDataDao.saveGpsReal(gpsRealMap.values());
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				gpsRealMap.clear();
				swapGps = gpsRealMap;
				isRunning = false;
			}
		}
	}

}
