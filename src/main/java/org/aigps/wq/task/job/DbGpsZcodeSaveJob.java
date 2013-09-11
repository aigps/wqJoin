/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.Date;
import java.util.List;

import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.DcRgAreaHis;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.cache.Cache;

/**
 * @author Administrator
 *
 */
public class DbGpsZcodeSaveJob implements Job {
	private static final Log log = LogFactory.getLog(DbGpsZcodeSaveJob.class);
	
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="DbGpsZcodeSaveJob";
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			String minTime = SystemCache.getSysMinTime();
			try {
				Cache<String, List<DcRgAreaHis>> cache = GpsDataCache.getDcRgAreaHisCache();
				Date date = ParseDate.getDateByFormatStr(minTime, ParseDate.MINUTE_FORMAT_STR);
				long dateTime = date.getTime();
				int count =0;
				while(count<20){
					count++;
					dateTime -=60*1000;
					long startTime = System.currentTimeMillis();
					List<DcRgAreaHis> list = cache.get(minTime);
					long endTime = System.currentTimeMillis();
					if(list!=null && list.size()>0){
						log.error(">>>>>>>>>>>>>时间:"+minTime+ " 历史数据量:"+list.size()+" takes Time:"+(endTime-startTime));
						startTime = System.currentTimeMillis();
						GpsDataDao.saveDcRgAreaHis(list);
						endTime = System.currentTimeMillis();
						log.error(">>>>>>>>>>>>>gps his to db takes time:"+(endTime-startTime));
					}
					cache.remove(minTime);
					minTime = ParseDate.getDateFormatTime(new Date(dateTime), ParseDate.MINUTE_FORMAT_STR);
				}
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
