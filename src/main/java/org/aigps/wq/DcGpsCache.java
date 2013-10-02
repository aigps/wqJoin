package org.aigps.wq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.DcCmdTrace;
import org.aigps.wq.entity.DcRgAreaHis;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.entity.WqStaffInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
/**
 * 数据中心的内存缓存
 * @author Administrator
 *
 */
public class DcGpsCache{
	private static final Log log = LogFactory.getLog(DcGpsCache.class);
	
	
	/**
	 * 终端与业务系统的ID对照
	 */
	public static Map<String,String> tmnStaffIdMap = new HashMap<String, String>();
	public static Map<String, String> getTmnSysIdMap() {
		return tmnStaffIdMap;
	}

	public static void setTmnSysIdMap(HashMap<String, String> tmnSysIdMap) {
		DcGpsCache.tmnStaffIdMap = tmnSysIdMap;
	}
	
	
	
	
	/**
	 * 员工ID与员工模型实例的对照
	 */
	private static Map<String, WqStaffInfo> staffMap = new ConcurrentLinkedHashMap.Builder<String,WqStaffInfo>().build();
	public static Map<String, WqStaffInfo> getStaffMap()throws Exception {
		if(staffMap.size() == 0){
			staffMap = loadStaffMap();
		}
		return staffMap;
	}
	public static Map<String,WqStaffInfo> loadStaffMap()throws Exception{
		Map<String, WqStaffInfo> tempMap = new ConcurrentLinkedHashMap.Builder<String,WqStaffInfo>().build();
		tempMap.putAll(WqJoinContext.getBean("gpsDataDao", GpsDataDao.class).loadWqStaffInfo());
		return tempMap;
	}
	
	
	/**
	 * 手机序列号  与  公司职员  对照
	 */
	private static Map<String, String> msidStaffMap = new ConcurrentLinkedHashMap.Builder<String, String>().build();
	public static Map<String, String> getMsidStaffMap()throws Exception {
		if(msidStaffMap.size() == 0){
			msidStaffMap = loadMsidStaffMap();
		}
		return msidStaffMap;
	}
	public static Map<String, String> loadMsidStaffMap()throws Exception {
		Map<String, WqStaffInfo> staffMap = getStaffMap();
		Map<String,String> tempMap = new ConcurrentLinkedHashMap.Builder<String, String>().build();
		Iterator<String> staffIdIt = staffMap.keySet().iterator();
		while(staffIdIt.hasNext()){
			String staffId = staffIdIt.next();
			WqStaffInfo wqStaffInfo = staffMap.get(staffId);
			String msid = wqStaffInfo.getMsid();
			if(StringUtils.isNotBlank(msid)){
				tempMap.put(msid, staffId);
			}
		}
		return tempMap;
	}

	
	
	
	/**
	 * 车辆最近上报的定位信息集合key--tmnCode, value--YmGpsModel
	 */
	private static Map<String,GisPosition> gpsLastCache = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
	/**
	 * 取到所有终端的最新定位集合
	 * @return
	 */
	public static Map<String, GisPosition> getLastGpsReport() {
		return gpsLastCache;
	}
	/**
	 * 更新终端的最新定位
	 * @param tmnCode
	 * @param gisPos
	 */
	public static void updateLastGps(String tmnCode,GisPosition gisPos){
		gpsLastCache.put(tmnCode, gisPos);
		gpsChangeCache.put(tmnCode, gisPos);
	}
	/**
	 * 取到终端的最新定位
	 * @param tmnCode
	 * @return
	 */
	public static GisPosition getLastGps(String tmnCode){
		return gpsLastCache.get(tmnCode);
	}
	
	
	
	
	/**
	 * 变动过的定位信息
	 */
	public static Map<String,GisPosition> gpsChangeCache = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
	/**
	 * 保存一分钟的上报GPS定位信息<分钟,上报gps定位信息集合>
	 */
	public static Queue<GisPosition> gpsAddCache = new ConcurrentLinkedQueue<GisPosition>();
	
	
	
	/**
	 * 保存车辆最新的地理位置
	 */
	private static Map<String,GisPosition> gpsLocDescMap =  new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
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
	 * 过往的指令集合
	 */
	public static Queue<DcCmdTrace> inCreCmdTrace = new ConcurrentLinkedQueue<DcCmdTrace>();

	
	
	/**
	 * 终端当前行政区域
	 */
	public static Map<String, DcRgAreaHis> dcRgAreaRealMap = new ConcurrentLinkedHashMap.Builder<String, DcRgAreaHis>().maximumWeightedCapacity(20000).build();
	/**
	 * 增量的已经驶出的行政区域
	 */
	public static Queue<DcRgAreaHis> dcRgAreaHisQueue = new ConcurrentLinkedQueue<DcRgAreaHis>();
	public static void updateDcRgAreaHis(GisPosition preGps,GisPosition gisPos)throws Exception{
		String tmnCode = gisPos.getTmnKey();
		String zCode = gisPos.getzCode();
		DcRgAreaHis oldDcRgAreaHis = dcRgAreaRealMap.get(tmnCode);
		if(oldDcRgAreaHis == null){
			//第一次进入行政区域
			/**
			 * 刷新新的行政区域
			 */
			DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
			dcRgAreaHis.setTmnCode(tmnCode);
			dcRgAreaHis.setRgAreaCode(zCode);
			dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
			dcRgAreaHis.setStartTime(gisPos.getRptTime());
			dcRgAreaRealMap.put(tmnCode, dcRgAreaHis);
			return;
		}
		//说明已经在某个行政区域
		
		//跨天，先结束前一天的行政区域,再生成当天的行政区域 
		if(preGps.getRptTime().substring(0, 8).compareTo(gisPos.getRptTime().substring(0, 8))<0){
			/**
			 * 更新老的行政区域驶出时间
			 */
			oldDcRgAreaHis.setEndTime(preGps.getRptTime());
			dcRgAreaHisQueue.add(oldDcRgAreaHis);
			/**
			 * 刷新新的行政区域
			 */
			DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
			dcRgAreaHis.setTmnCode(tmnCode);
			dcRgAreaHis.setRgAreaCode(zCode);
			dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
			dcRgAreaHis.setStartTime(gisPos.getRptTime());
			dcRgAreaRealMap.put(tmnCode, dcRgAreaHis);
			return;
		}
		oldDcRgAreaHis = dcRgAreaRealMap.get(tmnCode);
		String oldZcode = oldDcRgAreaHis.getRgAreaCode();
		if(StringUtils.isNotBlank(oldZcode) && StringUtils.isNotBlank(zCode) && !zCode.equalsIgnoreCase(oldZcode)){
			//在新的行政区域内
			//当前时间大于内存时间，正常情况
			if(gisPos.getRptTime().compareToIgnoreCase(oldDcRgAreaHis.getStartTime())>0){
				/**
				 * 更新老的行政区域驶出时间
				 */
				oldDcRgAreaHis.setEndTime(preGps.getRptTime());
				dcRgAreaHisQueue.add(oldDcRgAreaHis);
				/**
				 * 刷新新的行政区域
				 */
				DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
				dcRgAreaHis.setTmnCode(tmnCode);
				dcRgAreaHis.setRgAreaCode(zCode);
				dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
				dcRgAreaHis.setStartTime(gisPos.getRptTime());
				dcRgAreaRealMap.put(tmnCode, dcRgAreaHis);
			}else{//当前时间小于内存时间，忽略内存的行政区域
				/**
				 * 刷新新的行政区域
				 */
				DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
				dcRgAreaHis.setTmnCode(tmnCode);
				dcRgAreaHis.setRgAreaCode(zCode);
				dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
				dcRgAreaHis.setStartTime(gisPos.getRptTime());
				dcRgAreaRealMap.put(tmnCode, dcRgAreaHis);
			}
		}
	}
	
	/**
	 * 刷新内存
	 * @throws Exception
	 */
	public static void refresh()throws Exception{
		GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
		tmnStaffIdMap = gpsDataDao.getTmnSysIdMap(WqJoinContext.getBean("wqConfig", WqConfig.class).getTmnSysIdSql());
		staffMap  = loadStaffMap();
		msidStaffMap = loadMsidStaffMap();
	}

	/**
	 * 初始化加载数据
	 * @throws Exception
	 */
	public void init()throws Exception{
		refresh();
		
		
		GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
		List<GisPosition> lastGps = gpsDataDao.loadDbGps();
		if(lastGps!=null){
			for (GisPosition gisPosition : lastGps) {
				gpsLastCache.put(gisPosition.getTmnKey(), gisPosition);
				gpsLocDescMap.put(gisPosition.getTmnKey(), gisPosition);
			}
		}
		List<DcRgAreaHis> gpsDistrictMap = gpsDataDao.loadDcRgAreaReal();
		for (DcRgAreaHis dcRgAreaHis : gpsDistrictMap) {
			dcRgAreaRealMap.put(dcRgAreaHis.getTmnCode(), dcRgAreaHis);
		}
	}
	
}
