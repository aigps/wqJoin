/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.ArrayList;
import java.util.List;

import org.aigps.wq.DcGpsCache;
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
public class CacheGpsZcodeSaveJob implements Job {
	private static final Log log = LogFactory.getLog(CacheGpsZcodeSaveJob.class);
	
	private static boolean isRunning = false;//ͬһ��ʱ��㣬ֻ����һ��job����
	public static final String ID="CacheGpsZcodeSaveJob";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			String sysStartTime = SystemCache.getSysSecondTime();
			String minTime = sysStartTime.substring(0, (sysStartTime.length()-3));
			try {
				/**
				 * ����ȫ����δʻ����������ļ���
				 */
				GpsDataCommonCache.saveGpsDistrict(DcGpsCache.dcRgAreaHisMap);
				
				/**
				 * ������������ʻ����������ļ���
				 */
				List<DcRgAreaHis> dcRgAreaHisList = DcGpsCache.inCreDcRgAreaHis;
				if(dcRgAreaHisList!=null && dcRgAreaHisList.size()>0){
					GpsDataCache.getDcRgAreaHisCache().put(minTime, dcRgAreaHisList);
				}
				DcGpsCache.inCreDcRgAreaHis = new ArrayList<DcRgAreaHis>();
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
