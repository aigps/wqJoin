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
 * 分析实时地理位置的定位信息
 * @author Administrator
 *
 */
public class ParseGpsLocDescJob implements Job {
	private static final Log log = LogFactory.getLog(ParseGpsLocDescJob.class);
	public static final String ID="ParseGpsLocDescJob";
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	
	private static String getLocDescType="compose";//默认是免费组合
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
		if(isRunning==false){//如果当前job没有运行，则触发
			isRunning = true;
			try {
				final List<VhcLocDesc> newList = new ArrayList<VhcLocDesc>(10000);
				//本地车辆
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
						log.info("还有"+pool.getActiveCount()+"个任务在进行地理解析");
					}
					Thread.currentThread().sleep(2000);
				}
				//更新内存
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
		//数据库中已经存在该车的地理描述
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
						//与当前定位　如果距离超过100米
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
								//如果当前的时间大于内存的时间，更新内存
//								if(vhcLocDesc.getReportTime()<Long.parseLong(reportTime)){
									DcGpsCache.setVhcLocDesc(tmnCode, newVhcLocDesc);
//								}
								gpsModel.setLocDesc(desc);
							}
						}
					}
				}
			}	
		}else{//计算生成新的地理描述
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
	 * 获取地理位置描述
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
