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
 * �������ĵ��ڴ滺��
 * @author Administrator
 *
 */
public class DcGpsCache extends Observable{
	private static final Log log = LogFactory.getLog(DcGpsCache.class);
	
	private static DcGpsCache gpsCache = new DcGpsCache();
	
	
	/**
	 * ��������ϱ��Ķ�λ��Ϣ����key--tmnCode, value--YmGpsModel
	 */
	private static Map<String,GisPosition> lastGpsReport = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();

	/**
	 * �䶯���Ķ�λ��Ϣ
	 */
	public static Map<String,GisPosition> changeGpsReport = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
	/**
	 * ����һ���ӵ��ϱ�GPS��λ��Ϣ<����,�ϱ�gps��λ��Ϣ����>
	 */
	public static Queue<GisPosition> gpsReportMinuCache = new ConcurrentLinkedQueue<GisPosition>();
	
	/**
	 * �ն˵�ǰ��������
	 */
	public static Map<String, DcRgAreaHis> dcRgAreaHisMap = new ConcurrentLinkedHashMap.Builder<String, DcRgAreaHis>().maximumWeightedCapacity(20000).build();
	/**
	 * �������Ѿ�ʻ������������
	 */
	public static Queue<DcRgAreaHis> dcRgAreaHisQueue = new ConcurrentLinkedQueue<DcRgAreaHis>();
	/**
	 * ���泵�����µĵ���λ��
	 */
	private static Map<String,GisPosition> gpsLocDescMap =  new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
	/**
	 * �ն���ҵ��ϵͳ��ID����
	 */
	public static HashMap<String,String> tmnSysIdMap = new HashMap<String, String>();
	/**
	 * ������ָ���
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
	 * ȡ�������ն˵����¶�λ����
	 * @return
	 */
	public static Map<String, GisPosition> getLastGpsReport() {
		return lastGpsReport;
	}
	
	/**
	 * �����ն˵����¶�λ
	 * @param tmnCode
	 * @param gisPos
	 */
	public static void updateLastGps(String tmnCode,GisPosition gisPos){
		lastGpsReport.put(tmnCode, gisPos);
		changeGpsReport.put(tmnCode, gisPos);
	}
	
	/**
	 * ȡ���ն˵����¶�λ
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
		if(oldDcRgAreaHis!=null){//˵���Ѿ���ĳ����������
			
			//���죬�Ƚ���ǰһ�����������,�����ɵ������������ 
			if(lastGps.getRptTime().substring(0, 8).compareTo(gisPos.getRptTime().substring(0, 8))<0){
				/**
				 * �����ϵ���������ʻ��ʱ��
				 */
				oldDcRgAreaHis.setEndTime(lastGps.getRptTime());
				dcRgAreaHisQueue.add(oldDcRgAreaHis);
				/**
				 * ˢ���µ���������
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
				if(!zCode.equalsIgnoreCase(oldZcode)){//����Ѿ����µ���������
					//��ǰʱ������ڴ�ʱ�䣬�������
					if(gisPos.getRptTime().compareToIgnoreCase(oldDcRgAreaHis.getStartTime())>0){
						/**
						 * �����ϵ���������ʻ��ʱ��
						 */
						oldDcRgAreaHis.setEndTime(lastGps.getRptTime());
						dcRgAreaHisQueue.add(oldDcRgAreaHis);
						/**
						 * ˢ���µ���������
						 */
						DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
						dcRgAreaHis.setTmnCode(tmnCode);
						dcRgAreaHis.setRgAreaCode(zCode);
						dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
						dcRgAreaHis.setStartTime(gisPos.getRptTime());
						dcRgAreaHisMap.put(tmnCode, dcRgAreaHis);
					}else{//��ǰʱ��С���ڴ�ʱ�䣬�����ڴ����������
						/**
						 * ˢ���µ���������
						 */
						DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
						dcRgAreaHis.setTmnCode(tmnCode);
						dcRgAreaHis.setRgAreaCode(zCode);
						dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
						dcRgAreaHis.setStartTime(gisPos.getRptTime());
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
