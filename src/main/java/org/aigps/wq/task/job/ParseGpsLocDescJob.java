package org.aigps.wq.task.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.aigps.wq.DcGpsCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
/**
 * ����ʵʱ����λ�õĶ�λ��Ϣ
 * @author Administrator
 *
 */
public class ParseGpsLocDescJob implements Job {
	private static final Log log = LogFactory.getLog(ParseGpsLocDescJob.class);
	public static final String ID="ParseGpsLocDescJob";
	private static boolean isRunning = false;//ͬһ��ʱ��㣬ֻ����һ��job����
	
	private static String getLocDescType="compose";//Ĭ����������
	private static BlockingQueue taskQueue = new ArrayBlockingQueue(500);
	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(8,10,30,TimeUnit.SECONDS,taskQueue,new ThreadPoolExecutor.CallerRunsPolicy());

	public static String getGetLocDescType() {
		return getLocDescType;
	}

	public void setGetLocDescType(String getLocDescType) {
		ParseGpsLocDescJob.getLocDescType = getLocDescType;
	}

	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning==false){//�����ǰjobû�����У��򴥷�
			isRunning = true;
			try {
				final List<VhcLocDesc> newList = new ArrayList<VhcLocDesc>(10000);
				//���س���
				for (Iterator iterator = DcGpsCache.getLastGpsReport().keySet().iterator(); iterator.hasNext();) {
					try {
						final String tmnCode = (String)iterator.next();
						pool.execute(new Runnable(){
							public void run() {
								try {
									VhcLocDesc newVhcLocDesc = getNewVhcLocDesc(tmnCode);
									if(newVhcLocDesc!=null){
										newList.add(newVhcLocDesc);
									}
								} catch (Exception e) {
									log.error("",e);
								}
							}
						});
					} catch (Exception e) {
//						log.error(e);
					}
				}
				
				while(pool.getActiveCount()!=0){
					if(log.isInfoEnabled()){
						log.info("����"+pool.getActiveCount()+"�������ڽ��е������");
					}
					Thread.currentThread().sleep(2000);
				}
				//�����ڴ�
				DcGpsCache.setGpsLocDescMap(DcGpsCache.getGpsLocDescMap());
			} catch (Exception e) {
				log.error("",e);
			}
			finally{
				isRunning = false;
			}
		}
	}

	public static VhcLocDesc getNewVhcLocDesc(String tmnCode)
			throws Exception {
		VhcLocDesc newVhcLocDesc = null;
		//���ݿ����Ѿ����ڸó��ĵ�������
		if(DcGpsCache.getVhcLocDesc(tmnCode)!=null){
			VhcLocDesc vhcLocDesc = DcGpsCache.getVhcLocDesc(tmnCode);
			if(DcGpsCache.getLastGps(tmnCode)!=null){
				YmGpsModel gpsModel = DcGpsCache.getLastGps(tmnCode);
				MapLocation oldLoc = new MapLocation(vhcLocDesc.getLongit(),vhcLocDesc.getLat());
				if(gpsModel.isValidLocation()){
					if(gpsModel.getLatOffset()!=null && gpsModel.getLonOffset()!=null){
						MapLocation newOffset = new MapLocation(Double.parseDouble(gpsModel.getLonOffset()),Double.parseDouble(gpsModel.getLatOffset()));
						MapLocation newLoc = new MapLocation(Double.parseDouble(gpsModel.getLon()),Double.parseDouble(gpsModel.getLat()));
						String reportTime = gpsModel.getGpsTime();
						if(!NumberUtils.isDigits(reportTime)){
							reportTime = ParseDate.converDateFormat(reportTime, ParseDate.SECOND_FORMAT_STR, "yyyyMMddHHmmss");
						}
						//�뵱ǰ��λ��������볬��100��
						if(MapUtil.distance(oldLoc, newLoc, 'K')>=0.1){
							String desc = getGpsDesc(newOffset, getLocDescType);//MapAbcUtil.getGpsDescByCompose(newOffset);
							if(desc!=null && desc.trim().length()>0){
								newVhcLocDesc = new VhcLocDesc();
								newVhcLocDesc.setVehicleCode(tmnCode);
								newVhcLocDesc.setReportTime(Long.parseLong(reportTime));
								newVhcLocDesc.setLongit(Double.parseDouble(gpsModel.getLon()));
								newVhcLocDesc.setLat(Double.parseDouble(gpsModel.getLat()));
								newVhcLocDesc.setLocDesc(desc);
								newVhcLocDesc.setChanged(true);
								//�����ǰ��ʱ������ڴ��ʱ�䣬�����ڴ�
//								if(vhcLocDesc.getReportTime()<Long.parseLong(reportTime)){
									DcGpsCache.setVhcLocDesc(tmnCode, newVhcLocDesc);
//								}
								gpsModel.setLocDesc(desc);
							}
						}
					}
				}
			}	
		}else{//���������µĵ�������
			if(DcGpsCache.getLastGps(tmnCode)!=null){
				YmGpsModel gpsModel = DcGpsCache.getLastGps(tmnCode);
				if(gpsModel.isValidLocation()){
					MapLocation newOffset = new MapLocation(Double.parseDouble(gpsModel.getLonOffset()),Double.parseDouble(gpsModel.getLatOffset()));
					String desc = getGpsDesc(newOffset, getLocDescType);//MapAbcUtil.getSimpleGpsDesc(newOffset);
					if(desc!=null && desc.trim().length()>0){
						newVhcLocDesc = new VhcLocDesc();
						newVhcLocDesc.setVehicleCode(tmnCode);
						String reportTime = gpsModel.getGpsTime();
						if(!NumberUtils.isDigits(reportTime)){
							reportTime = ParseDate.converDateFormat(reportTime, ParseDate.SECOND_FORMAT_STR, "yyyyMMddHHmmss");
						}
						newVhcLocDesc.setReportTime(Long.parseLong(reportTime));
						newVhcLocDesc.setLongit(Double.parseDouble(gpsModel.getLon()));
						newVhcLocDesc.setLat(Double.parseDouble(gpsModel.getLat()));
						newVhcLocDesc.setLocDesc(desc);
						newVhcLocDesc.setChanged(true);
						DcGpsCache.setVhcLocDesc(tmnCode, newVhcLocDesc);
						gpsModel.setLocDesc(desc);
					}
				}
			}
		}
		return newVhcLocDesc;
	}
	/**
	 * ��ȡ����λ������
	 * @param loc
	 * @param getType
	 * @return
	 * @throws Exception
	 */
	public static String getGpsDesc(MapLocation loc,String getType)throws Exception{
		String desc = null;
		try {
			if("compose".equalsIgnoreCase(getType)){
				desc = MapAbcUtil.getGpsDescByCompose(loc);
			}else if("simple".equalsIgnoreCase(getType)){
				desc = MapAbcUtil.getSimpleGpsDesc(loc);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return desc;
	}
	
	public static void main(String[] args){
		try {
			MapLocation oldLoc = new MapLocation(121.5918,29.91479);
			log.error(getGpsDesc(oldLoc, "simple"));
			MapLocation newLoc = new MapLocation(121.447567,29.353762);
			log.error(getGpsDesc(newLoc, "simple"));
			
			log.error(MapUtil.distance(oldLoc, newLoc, 'K'));
			
			Long oldTime = new Long("20110211154511");
			String reportTime = ParseDate.converDateFormat("2011-02-12 13:58:01", ParseDate.SECOND_FORMAT_STR, "yyyyMMddHHmmss");
			log.error(oldTime<Long.parseLong(reportTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
