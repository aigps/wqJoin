package org.aigps.wq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.DcCmdTrace;
import org.aigps.wq.entity.DcRgAreaHis;
import org.aigps.wq.entity.GisPosition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 数据中心的内存缓存
 * @author Administrator
 *
 */
public class DcGpsCache extends Observable{
	private static final Log log = LogFactory.getLog(DcGpsCache.class);
	
	private static DcGpsCache gpsCache = new DcGpsCache();
	
	static{
		//当定位增加时主动通知
		gpsCache.addObserver(new DataClientContainer());
	}
	
	/**
	 * 车辆最近上报的定位信息集合key--tmnCode, value--YmGpsModel
	 */
	private static Map<String,GisPosition> lastGpsReport = new HashMap<String,GisPosition>(50000);

	/**
	 * 保存一分钟的上报GPS定位信息<分钟,上报gps定位信息集合>
	 */
	public static List<GisPosition> gpsReportMinuCache = new ArrayList<GisPosition>(50000);
	
	/**
	 * 增量的最近上报的定位信息集合key--tmnCode, value--HSGpsModel
	 */
	public static ConcurrentHashMap<String,GisPosition> inCreLastGpsReport = new ConcurrentHashMap<String,GisPosition>(10000);

	/**
	 * 终端当前行政区域
	 */
	public static HashMap<String, DcRgAreaHis> dcRgAreaHisMap = new HashMap<String, DcRgAreaHis>();
	/**
	 * 增量的已经驶出的行政区域
	 */
	public static List<DcRgAreaHis> inCreDcRgAreaHis = new ArrayList<DcRgAreaHis>();
	/**
	 * 保存车辆最新的地理位置
	 */
	private static HashMap<String,VhcLocDesc> gpsLocDescMap = null;
	/**
	 * 终端与业务系统的ID对照
	 */
	public static HashMap<String,String> tmnSysIdMap = new HashMap<String, String>();
	/**
	 * 过往的指令集合
	 */
	public static List<DcCmdTrace> inCreCmdTrace = new ArrayList<DcCmdTrace>(10000);
	
	public static HashMap<String, String> getTmnSysIdMap() {
		return tmnSysIdMap;
	}

	public static void setTmnSysIdMap(HashMap<String, String> tmnSysIdMap) {
		DcGpsCache.tmnSysIdMap = tmnSysIdMap;
	}

	public static void setGpsLocDescMap(HashMap<String, VhcLocDesc> gpsLocDescMap)throws Exception {
		DcGpsCache.gpsLocDescMap = gpsLocDescMap;
		GpsDataCache.getContextCache().put("gpsCurrLoc", gpsLocDescMap);
	}

	public static HashMap<String, VhcLocDesc> getGpsLocDescMap()throws Exception {
		if(gpsLocDescMap==null && GpsDataCache.getContextCache().containsKey("gpsCurrLoc")){
			gpsLocDescMap = (HashMap<String, VhcLocDesc>)GpsDataCache.getContextCache().get("gpsCurrLoc");
		}else if(gpsLocDescMap==null){
			gpsLocDescMap = new HashMap<String, VhcLocDesc>(10000);
		}
		return gpsLocDescMap;
	}

	public static void setVhcLocDesc(String vehicleCode,VhcLocDesc vhcLocDesc)throws Exception{
//		gpsLocDescMap.put(vehicleCode, vhcLocDesc);
		getGpsLocDescMap().put(vehicleCode, vhcLocDesc);
//		//主动通知定位更新
//		gpsCache.setChanged();
//		gpsCache.notifyObservers(vhcLocDesc);
	}
	
	public static VhcLocDesc getVhcLocDesc(String vehicleCode)throws Exception{
		VhcLocDesc model = null;
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
	 * @param ymGpsModel
	 */
	public static void updateLastGps(String tmnCode,GisPosition ymGpsModel){
		lastGpsReport.put(tmnCode, ymGpsModel);
	}
	
	/**
	 * 取到终端的最新定位
	 * @param tmnCode
	 * @return
	 */
	public static GisPosition getLastGps(String tmnCode){
		return lastGpsReport.get(tmnCode);
	}
	
	public static void updateDcRgAreaHis(GisPosition lastGps,GisPosition ymGpsModel)throws Exception{
		String tmnCode = ymGpsModel.getTmnCode();
		String zCode = ymGpsModel.getzCode();
		DcRgAreaHis oldDcRgAreaHis = dcRgAreaHisMap.get(tmnCode);
		if(oldDcRgAreaHis!=null){//说明已经在某个行政区域
			
			//跨天，先结束前一天的行政区域,再生成当天的行政区域 
			if(lastGps.getGpsTime().substring(0, 8).compareTo(ymGpsModel.getGpsTime().substring(0, 8))<0){
				/**
				 * 更新老的行政区域驶出时间
				 */
				oldDcRgAreaHis.setEndTime(lastGps.getGpsTime());
				inCreDcRgAreaHis.add(oldDcRgAreaHis);
				/**
				 * 刷新新的行政区域
				 */
				DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
				dcRgAreaHis.setTmnCode(tmnCode);
				dcRgAreaHis.setRgAreaCode(zCode);
				dcRgAreaHis.setTmnAlias(ymGpsModel.getTmnAlias());
				dcRgAreaHis.setStartTime(ymGpsModel.getGpsTime());
				dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
				
			}
			oldDcRgAreaHis = dcRgAreaHisMap.get(tmnCode);
			String oldZcode = oldDcRgAreaHis.getRgAreaCode();
			if(oldZcode!=null && zCode!=null){
				if(!zCode.equalsIgnoreCase(oldZcode)){//如果已经在新的行政区域
					//当前时间大于内存时间，正常情况
					if(ymGpsModel.getGpsTime().compareToIgnoreCase(oldDcRgAreaHis.getStartTime())>0){
						/**
						 * 更新老的行政区域驶出时间
						 */
						oldDcRgAreaHis.setEndTime(lastGps.getGpsTime());
						inCreDcRgAreaHis.add(oldDcRgAreaHis);
						/**
						 * 刷新新的行政区域
						 */
						DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
						dcRgAreaHis.setTmnCode(tmnCode);
						dcRgAreaHis.setRgAreaCode(zCode);
						dcRgAreaHis.setTmnAlias(ymGpsModel.getTmnAlias());
						dcRgAreaHis.setStartTime(ymGpsModel.getGpsTime());
						dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
					}else{//当前时间小于内存时间，忽略内存的行政区域
						/**
						 * 刷新新的行政区域
						 */
						DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
						dcRgAreaHis.setTmnCode(tmnCode);
						dcRgAreaHis.setRgAreaCode(zCode);
						dcRgAreaHis.setTmnAlias(ymGpsModel.getTmnAlias());
						dcRgAreaHis.setStartTime(ymGpsModel.getGpsTime());
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
			dcRgAreaHis.setTmnAlias(ymGpsModel.getTmnAlias());
			dcRgAreaHis.setStartTime(ymGpsModel.getGpsTime());
			dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
		}
	}
	
	public static void receiveRespCmd(YmAccessMsg ymMsg)throws Exception{
		//主动通知定位更新
		gpsCache.setChanged();
		gpsCache.notifyObservers(ymMsg);
	}
	
	public void init()throws Exception{
		tmnSysIdMap = GpsDataDao.getTmnSysIdMap(DcConstants.getTmnSysIdSql());
		
		HashMap<String,YmGpsModel> lastGps = GpsDataCommonCache.getLastGps();
		if(lastGps!=null){
			lastGpsReport = lastGps;
		}
		HashMap<String, DcRgAreaHis> gpsDistrictMap = GpsDataCommonCache.getGpsDistrictMap();
		if(gpsDistrictMap!=null){
			dcRgAreaHisMap = gpsDistrictMap;
		}
	}
	
}
