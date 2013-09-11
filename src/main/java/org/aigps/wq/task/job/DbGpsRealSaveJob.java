/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.List;

import org.aigps.wq.dao.GpsDataDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Administrator
 *
 */
public class DbGpsRealSaveJob implements Job {
	private static final Log log = LogFactory.getLog(DbGpsRealSaveJob.class);
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="DbGpsRealSaveJob";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			try {
				List<YmGpsModel> list = GpsDataCommonCache.getIncreGps();
				if(list!=null && list.size()>0){
					GpsDataDao.saveGpsReal(list);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning = false;
			}
		}
	}

}
