/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.ArrayList;
import java.util.List;

import org.aigps.wq.DcGpsCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 保存历史定位
 * @author Administrator
 *
 */
public class CacheGpsHisSaveJob implements Job {
	private static final Log log = LogFactory.getLog(CacheGpsHisSaveJob.class);
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="CacheGpsHisSaveJob";
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			long startTime = System.currentTimeMillis();
			String sysStartTime = SystemCache.getSysSecondTime();
			String minTime = sysStartTime.substring(0, (sysStartTime.length()-3));
			List<YmGpsModel> gpsList = null;
			long gpsHisSize = 0;
			log.error("车载终端个数:(根据终端)"+TmnClientContainer.tmnClientHash.size()+"  (根据会话):"+" 历史记录数:"+DcGpsCache.gpsReportMinuCache.size()+"  实时记录数:"+DcGpsCache.inCreLastGpsReport.size());
			try {
				gpsList = DcGpsCache.gpsReportMinuCache;
				DcGpsCache.gpsReportMinuCache = new ArrayList<YmGpsModel>(50000);
				gpsHisSize = gpsList.size();
				if(gpsHisSize>0){
					//入缓存
					GpsDataCache.getGpsMinHisCache().put(minTime,gpsList);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning = false;
				if(gpsList!=null){
					gpsList.clear();
				}
				long endTime = System.currentTimeMillis();
				log.error("begin time:"+sysStartTime+"  endTime:"+SystemCache.getSysSecondTime()+"　入缓存的总记录数:"+gpsHisSize+"  save into cache take times:"+(endTime-startTime));
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
