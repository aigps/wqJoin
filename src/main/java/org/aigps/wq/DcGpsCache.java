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
 * �������ĵ��ڴ滺��
 * @author Administrator
 *
 */
public class DcGpsCache extends Observable{
	private static final Log log = LogFactory.getLog(DcGpsCache.class);
	
	private static DcGpsCache gpsCache = new DcGpsCache();
	
	static{
		//����λ����ʱ����֪ͨ
		gpsCache.addObserver(new DataClientContainer());
	}
	
	/**
	 * ��������ϱ��Ķ�λ��Ϣ����key--tmnCode, value--YmGpsModel
	 */
	private static Map<String,GisPosition> lastGpsReport = new HashMap<String,GisPosition>(50000);

	/**
	 * ����һ���ӵ��ϱ�GPS��λ��Ϣ<����,�ϱ�gps��λ��Ϣ����>
	 */
	public static List<GisPosition> gpsReportMinuCache = new ArrayList<GisPosition>(50000);
	
	/**
	 * ����������ϱ��Ķ�λ��Ϣ����key--tmnCode, value--HSGpsModel
	 */
	public static ConcurrentHashMap<String,GisPosition> inCreLastGpsReport = new ConcurrentHashMap<String,GisPosition>(10000);

	/**
	 * �ն˵�ǰ��������
	 */
	public static HashMap<String, DcRgAreaHis> dcRgAreaHisMap = new HashMap<String, DcRgAreaHis>();
	/**
	 * �������Ѿ�ʻ������������
	 */
	public static List<DcRgAreaHis> inCreDcRgAreaHis = new ArrayList<DcRgAreaHis>();
	/**
	 * ���泵�����µĵ���λ��
	 */
	private static HashMap<String,VhcLocDesc> gpsLocDescMap = null;
	/**
	 * �ն���ҵ��ϵͳ��ID����
	 */
	public static HashMap<String,String> tmnSysIdMap = new HashMap<String, String>();
	/**
	 * ������ָ���
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
//		//����֪ͨ��λ����
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
	 * ȡ�������ն˵����¶�λ����
	 * @return
	 */
	public static Map<String, GisPosition> getLastGpsReport() {
		return lastGpsReport;
	}
	
	/**
	 * �����ն˵����¶�λ
	 * @param tmnCode
	 * @param ymGpsModel
	 */
	public static void updateLastGps(String tmnCode,GisPosition ymGpsModel){
		lastGpsReport.put(tmnCode, ymGpsModel);
	}
	
	/**
	 * ȡ���ն˵����¶�λ
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
		if(oldDcRgAreaHis!=null){//˵���Ѿ���ĳ����������
			
			//���죬�Ƚ���ǰһ�����������,�����ɵ������������ 
			if(lastGps.getGpsTime().substring(0, 8).compareTo(ymGpsModel.getGpsTime().substring(0, 8))<0){
				/**
				 * �����ϵ���������ʻ��ʱ��
				 */
				oldDcRgAreaHis.setEndTime(lastGps.getGpsTime());
				inCreDcRgAreaHis.add(oldDcRgAreaHis);
				/**
				 * ˢ���µ���������
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
				if(!zCode.equalsIgnoreCase(oldZcode)){//����Ѿ����µ���������
					//��ǰʱ������ڴ�ʱ�䣬�������
					if(ymGpsModel.getGpsTime().compareToIgnoreCase(oldDcRgAreaHis.getStartTime())>0){
						/**
						 * �����ϵ���������ʻ��ʱ��
						 */
						oldDcRgAreaHis.setEndTime(lastGps.getGpsTime());
						inCreDcRgAreaHis.add(oldDcRgAreaHis);
						/**
						 * ˢ���µ���������
						 */
						DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
						dcRgAreaHis.setTmnCode(tmnCode);
						dcRgAreaHis.setRgAreaCode(zCode);
						dcRgAreaHis.setTmnAlias(ymGpsModel.getTmnAlias());
						dcRgAreaHis.setStartTime(ymGpsModel.getGpsTime());
						dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
					}else{//��ǰʱ��С���ڴ�ʱ�䣬�����ڴ����������
						/**
						 * ˢ���µ���������
						 */
						DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
						dcRgAreaHis.setTmnCode(tmnCode);
						dcRgAreaHis.setRgAreaCode(zCode);
						dcRgAreaHis.setTmnAlias(ymGpsModel.getTmnAlias());
						dcRgAreaHis.setStartTime(ymGpsModel.getGpsTime());
						dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
					}
				}else{//���ϵ��������򣬲�����
					
				}
			}
		}else{//��һ�ν�����������
			/**
			 * ˢ���µ���������
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
		//����֪ͨ��λ����
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
