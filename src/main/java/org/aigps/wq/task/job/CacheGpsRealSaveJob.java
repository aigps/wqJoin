/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aigps.wq.DcGpsCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 保存实时定位
 * @author Administrator
 *
 */
public class CacheGpsRealSaveJob implements Job {
	private static final Log log = LogFactory.getLog(CacheGpsRealSaveJob.class);
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="CacheGpsRealSaveJob";
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			String sysStartTime = SystemCache.getSysSecondTime();
			long startTime = System.currentTimeMillis();
			ConcurrentHashMap<String,YmGpsModel> oldCreLastGpsReport= null;
			HashMap<String, YmGpsModel> allGps = null;
			long gpsSize =0;
			try {
				oldCreLastGpsReport = DcGpsCache.inCreLastGpsReport;
				ConcurrentHashMap<String,YmGpsModel> newInCreLastGpsReport = new ConcurrentHashMap<String, YmGpsModel>(50000);
				DcGpsCache.inCreLastGpsReport = newInCreLastGpsReport;
				gpsSize = oldCreLastGpsReport.size();
				/**
				 * 更新地理描述
				 */
				List<YmGpsModel> increGpsMap = getIncreLastGps(oldCreLastGpsReport);
				for (Iterator iterator = increGpsMap.iterator(); iterator
						.hasNext();) {
					YmGpsModel ymGpsModel = (YmGpsModel) iterator.next();
					String tmnCode = ymGpsModel.getTmnCode();
					VhcLocDesc locDesc = DcGpsCache.getVhcLocDesc(tmnCode);
					//实时表中必须有地理描述
					ymGpsModel.setLocDesc(locDesc.getLocDesc());
					if(locDesc!=null && locDesc.isChanged()){
						locDesc.setChanged(false);
					}
				}
				List<YmGpsModel> increLastGps = getIncreLastGps(oldCreLastGpsReport);
				GpsDataCommonCache.saveIncreGps(increLastGps);
//				//入库
//				GpsDataDao.saveGpsReal(getIncreLastGps(oldCreLastGpsReport));
				allGps = DcGpsCache.getLastGpsReport();
				//入缓存
				GpsDataCommonCache.saveLastGps(allGps);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning = false;
				if(oldCreLastGpsReport!=null){
					oldCreLastGpsReport.clear();
				}
				String sysEndTime = SystemCache.getSysSecondTime();
				long endTime = System.currentTimeMillis();
				log.error("begin time:"+sysStartTime+"  endTime:"+sysEndTime+" 实时记录数:"+gpsSize +"   take times:"+(endTime-startTime));
			}
		}
	}
	
	public List<YmGpsModel> getIncreLastGps(Map<String,YmGpsModel> map)throws Exception{
		List<YmGpsModel> list = new ArrayList<YmGpsModel>();
		Iterator<String> tmnIt = map.keySet().iterator();
		while(tmnIt.hasNext()){
			String tmnCode = tmnIt.next();
			YmGpsModel ymGpsModel = map.get(tmnCode);
			list.add(ymGpsModel);
		}
		return list;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
