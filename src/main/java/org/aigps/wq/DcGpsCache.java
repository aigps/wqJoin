package org.aigps.wq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.DcCmdTrace;
import org.aigps.wq.entity.DcRgAreaHis;
import org.aigps.wq.entity.GisPosition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
/**
 * 数据中心的内存缓存
 * @author Administrator
 *
 */
public class DcGpsCache extends Observable{
	private static final Log log = LogFactory.getLog(DcGpsCache.class);
	
	private static DcGpsCache gpsCache = new DcGpsCache();
	
	
	/**
	 * 车辆最近上报的定位信息集合key--tmnCode, value--YmGpsModel
	 */
	private static Map<String,GisPosition> lastGpsReport = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();

	/**
	 * 变动过的定位信息
	 */
	public static Map<String,GisPosition> changeGpsReport = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
	/**
	 * 保存一分钟的上报GPS定位信息<分钟,上报gps定位信息集合>
	 */
	public static Queue<GisPosition> gpsReportMinuCache = new ConcurrentLinkedQueue<GisPosition>();
	
	/**
	 * 终端当前行政区域
	 */
	public static Map<String, DcRgAreaHis> dcRgAreaHisMap = new ConcurrentLinkedHashMap.Builder<String, DcRgAreaHis>().maximumWeightedCapacity(20000).build();
	/**
	 * 增量的已经驶出的行政区域
	 */
	public static Queue<DcRgAreaHis> dcRgAreaHisQueue = new ConcurrentLinkedQueue<DcRgAreaHis>();
	/**
	 * 保存车辆最新的地理位置
	 */
	private static Map<String,GisPosition> gpsLocDescMap =  new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
	/**
	 * 终端与业务系统的ID对照
	 */
	public static HashMap<String,String> tmnSysIdMap = new HashMap<String, String>();
	/**
	 * 过往的指令集合
	 */
	public static Queue<DcCmdTrace> inCreCmdTrace = new ConcurrentLinkedQueue<DcCmdTrace>();
	
	public static HashMap<String, String> getTmnSysIdMap() {
		return tmnSysIdMap;
	}

	public static void setTmnSysIdMap(HashMap<String, String> tmnSysIdMap) {
		DcGpsCache.tmnSysIdMap = tmnSysIdMap;
	}


	public static Map<String, GisPosition> getGpsLocDescMap()throws Exception {
		return gpsLocDescMap;
	}

	public static void setVhcLocDesc(String vehicleCode,GisPosition gisPos)throws Exception{
		getGpsLocDescMap().put(vehicleCode, gisPos);
	}
	
	public static GisPosition getVhcLocDesc(String vehicleCode)throws Exception{
		GisPosition model = null;
		if(getGpsLocDescMap().containsKey(vehicleCode)){
			model = getGpsLocDescMap().get(vehicleCode);
		}
		return model;
	}

	/**
	 * 取到所有终端的最新定位集合
	 * @return
	 */
	public static Map<String, GisPosition> getLastGpsReport() {
		return lastGpsReport;
	}
	
	/**
	 * 更新终端的最新定位
	 * @param tmnCode
	 * @param gisPos
	 */
	public static void updateLastGps(String tmnCode,GisPosition gisPos){
		lastGpsReport.put(tmnCode, gisPos);
		changeGpsReport.put(tmnCode, gisPos);
	}
	
	/**
	 * 取到终端的最新定位
	 * @param tmnCode
	 * @return
	 */
	public static GisPosition getLastGps(String tmnCode){
		return lastGpsReport.get(tmnCode);
	}
	
	public static void updateDcRgAreaHis(GisPosition lastGps,GisPosition gisPos)throws Exception{
		String tmnCode = gisPos.getTmnKey();
		String zCode = gisPos.getzCode();
		DcRgAreaHis oldDcRgAreaHis = dcRgAreaHisMap.get(tmnCode);
		if(oldDcRgAreaHis!=null){//说明已经在某个行政区域
			
			//跨天，先结束前一天的行政区域,再生成当天的行政区域 
			if(lastGps.getRptTime().substring(0, 8).compareTo(gisPos.getRptTime().substring(0, 8))<0){
				/**
				 * 更新老的行政区域驶出时间
				 */
				oldDcRgAreaHis.setEndTime(lastGps.getRptTime());
				dcRgAreaHisQueue.add(oldDcRgAreaHis);
				/**
				 * 刷新新的行政区域
				 */
				DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
				dcRgAreaHis.setTmnCode(tmnCode);
				dcRgAreaHis.setRgAreaCode(zCode);
				dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
				dcRgAreaHis.setStartTime(gisPos.getRptTime());
				dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
				
			}
			oldDcRgAreaHis = dcRgAreaHisMap.get(tmnCode);
			String oldZcode = oldDcRgAreaHis.getRgAreaCode();
			if(oldZcode!=null && zCode!=null){
				if(!zCode.equalsIgnoreCase(oldZcode)){//如果已经在新的行政区域
					//当前时间大于内存时间，正常情况
					if(gisPos.getRptTime().compareToIgnoreCase(oldDcRgAreaHis.getStartTime())>0){
						/**
						 * 更新老的行政区域驶出时间
						 */
						oldDcRgAreaHis.setEndTime(lastGps.getRptTime());
						dcRgAreaHisQueue.add(oldDcRgAreaHis);
						/**
						 * 刷新新的行政区域
						 */
						DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
						dcRgAreaHis.setTmnCode(tmnCode);
						dcRgAreaHis.setRgAreaCode(zCode);
						dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
						dcRgAreaHis.setStartTime(gisPos.getRptTime());
						dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
					}else{//当前时间小于内存时间，忽略内存的行政区域
						/**
						 * 刷新新的行政区域
						 */
						DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
						dcRgAreaHis.setTmnCode(tmnCode);
						dcRgAreaHis.setRgAreaCode(zCode);
						dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
						dcRgAreaHis.setStartTime(gisPos.getRptTime());
						dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
					}
				}else{//在老的行政区域，不处理
					
				}
			}
		}else{//第一次进入行政区域
			/**
			 * 刷新新的行政区域
			 */
			DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
			dcRgAreaHis.setTmnCode(tmnCode);
			dcRgAreaHis.setRgAreaCode(zCode);
			dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
			dcRgAreaHis.setStartTime(gisPos.getRptTime());
			dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
		}
	}

	
	public void init()throws Exception{
		GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
		tmnSysIdMap = gpsDataDao.getTmnSysIdMap(WqJoinContext.getBean("wqConfig", WqConfig.class).getTmnSysIdSql());
		
		
		List<GisPosition> lastGps = gpsDataDao.loadDbGps();
		if(lastGps!=null){
			for (GisPosition gisPosition : lastGps) {
				lastGpsReport.put(gisPosition.getTmnKey(), gisPosition);
				gpsLocDescMap.put(gisPosition.getTmnKey(), gisPosition);
			}
		}
		List<DcRgAreaHis> gpsDistrictMap = gpsDataDao.loadDcRgAreaReal();
		for (DcRgAreaHis dcRgAreaHis : gpsDistrictMap) {
			dcRgAreaHisMap.put(dcRgAreaHis.getTmnCode(), dcRgAreaHis);
		}
	}
	
}
