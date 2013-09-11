/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.HashMap;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.dao.GpsDataDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 终端与业务ID的对照更新
 * @author Administrator
 *
 */
public class TmnSysIdRefreshJob implements Job {
	private static final Log log = LogFactory.getLog(TmnSysIdRefreshJob.class);
	public static final String ID="TmnSysIdRefreshJob";
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			try {
				HashMap<String, String> tmnSysIdMap = GpsDataDao.getTmnSysIdMap(DcConstants.getTmnSysIdSql());
				DcGpsCache.setTmnSysIdMap(tmnSysIdMap);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning = false;
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
