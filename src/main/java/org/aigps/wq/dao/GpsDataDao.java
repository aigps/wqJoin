/**
 * 
 */
package org.aigps.wq.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aigps.wq.entity.DcCmdTrace;
import org.aigps.wq.entity.DcDayMile;
import org.aigps.wq.entity.DcRgAreaHis;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.gps.util.common.EncodeUtil;

/**
 * ���������������
 * @author Administrator
 *
 */
public class GpsDataDao {
	private static final Log log = LogFactory.getLog(GpsDataDao.class);
	
	/**
	 * �������ָ��
	 * @param list
	 * @throws Exception
	 */
	public static void saveDcCmdTrace(List<DcCmdTrace> list)throws Exception{
		if(list!=null && list.size()>0){
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					DcCmdTrace dcCmdTrace = (DcCmdTrace) iterator.next();
					GpsDataIbatis.getSqlMapClient().update("DC_CMD_TRACE.insert", dcCmdTrace);
				}
				GpsDataIbatis.getSqlMapClient().executeBatch();
				GpsDataIbatis.getSqlMapClient().commitTransaction();
			} catch (Exception e) {
				throw e;
			}finally{
				GpsDataIbatis.getSqlMapClient().endTransaction();
			}
		}
	}
	
	/**
	 * ����������������
	 * @param date
	 * @throws Exception
	 */
	public static void saveDayMile(String date)throws Exception{
		String yesterday = DateUtil.getPrevDate(date, DateUtil.Y_M_D);
		List<DcDayMile> yesterdayList = (List<DcDayMile>)GpsDataIbatis.getSqlMapClient().queryForList("DC_DAY_MILE.selectByDay",yesterday);
		List<DcDayMile> increYesList = new ArrayList<DcDayMile>();
		HashMap<String, DcDayMile> yesterdayMap = new HashMap<String, DcDayMile>();
		for (Iterator iterator = yesterdayList.iterator(); iterator.hasNext();) {
			DcDayMile dcDayMile = (DcDayMile) iterator.next();
			yesterdayMap.put(dcDayMile.getTmnCode(), dcDayMile);
		}
		List<DcDayMile> todayList = (List<DcDayMile>)GpsDataIbatis.getSqlMapClient().queryForList("DC_DAY_MILE.selectByDay", date);
		for (Iterator iterator = todayList.iterator(); iterator.hasNext();) {
			DcDayMile dcDayMile = (DcDayMile) iterator.next();
			/**
			 * �������������
			 */
			if(yesterdayMap.containsKey(dcDayMile.getTmnCode())){
				DcDayMile yesterdayMile = yesterdayMap.get(dcDayMile.getTmnCode());
				String todayMile = dcDayMile.getCurrMile();
				String yesterMile = yesterdayMile.getCurrMile();
				if(NumberUtils.isDigits(yesterMile) && NumberUtils.isDigits(todayMile)){
					yesterdayMile.setDayMile((NumberUtils.toLong(todayMile)-Long.parseLong(yesterMile))+"");
				}else{
					yesterdayMile.setDayMile(todayMile);
				}
			}else{
				/**
				 * ���������ĳ���
				 */
				DcDayMile newDcDayMile = new DcDayMile();
				newDcDayMile.setTmnCode(dcDayMile.getTmnCode());
				newDcDayMile.setTmnAlias(dcDayMile.getTmnAlias());
				newDcDayMile.setCurrMile("0");
				newDcDayMile.setDay(yesterday);
				newDcDayMile.setDayMile(dcDayMile.getCurrMile());
				increYesList.add(newDcDayMile);
			}
		}
		/**
		 * ��������������
		 */
		GpsDataIbatis.getSqlMapClient().startTransaction();
		GpsDataIbatis.getSqlMapClient().startBatch();
		for (Iterator iterator = yesterdayList.iterator(); iterator.hasNext();) {
			DcDayMile dcDayMile = (DcDayMile) iterator.next();
			GpsDataIbatis.getSqlMapClient().update("DC_DAY_MILE.updateDayMile", dcDayMile);
		}
		GpsDataIbatis.getSqlMapClient().executeBatch();
		GpsDataIbatis.getSqlMapClient().commitTransaction();
		GpsDataIbatis.getSqlMapClient().endTransaction();
		/**
		 * ������������ն˵������
		 */
		GpsDataIbatis.getSqlMapClient().startTransaction();
		GpsDataIbatis.getSqlMapClient().startBatch();
		for (Iterator iterator = increYesList.iterator(); iterator.hasNext();) {
			DcDayMile dcDayMile = (DcDayMile) iterator.next();
			GpsDataIbatis.getSqlMapClient().update("DC_DAY_MILE.insert", dcDayMile);
		}
		GpsDataIbatis.getSqlMapClient().executeBatch();
		GpsDataIbatis.getSqlMapClient().commitTransaction();
		GpsDataIbatis.getSqlMapClient().endTransaction();
		
	}
	
	/**
	 * ���浱ǰ��ʵʱ�����Ϊ��������
	 * @param sysDate
	 * @throws Exception
	 */
	public static void saveCurrMile(String sysDate)throws Exception{
		List<YmGpsModel> list = (List<YmGpsModel>)GpsDataIbatis.getSqlMapClient().queryForList("DC_GPS_REAL.selectAll");
		List<DcDayMile> retList = new ArrayList<DcDayMile>(list.size());
		GpsDataIbatis.getSqlMapClient().startTransaction();
		GpsDataIbatis.getSqlMapClient().startBatch();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			YmGpsModel ymGpsModel = (YmGpsModel) iterator.next();
			DcDayMile dcDayMile = new DcDayMile();
			dcDayMile.setTmnCode(ymGpsModel.getTmnCode());
			dcDayMile.setTmnAlias(ymGpsModel.getTmnAlias());
			dcDayMile.setCurrMile(ymGpsModel.getMile());
			dcDayMile.setDay(sysDate);
			retList.add(dcDayMile);
			GpsDataIbatis.getSqlMapClient().insert("DC_DAY_MILE.insert", dcDayMile);
		}
		GpsDataIbatis.getSqlMapClient().executeBatch();
		GpsDataIbatis.getSqlMapClient().commitTransaction();
		GpsDataIbatis.getSqlMapClient().endTransaction();
	}
	
	/**
	 * ��ȡ�ն�ҵ��ID�Ķ��ռ���
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String,String> getTmnSysIdMap(String sql)throws Exception{
		HashMap<String, String> retMap = new HashMap<String, String>();
		int firstComma = sql.indexOf(",");
		int firstBlank = sql.indexOf(" ");
		String tmnColumn = sql.substring(firstBlank, firstComma).trim();
		int firstFrom = sql.toLowerCase().indexOf("from");
		String sysIdColumn = sql.substring(firstComma+1, firstFrom).trim();
//		log.error(tmnColumn+ "   "+sysIdColumn);
		List list = DataBaseUtil.getCommonJdbcTemplate().queryForList(sql);
		if(list!=null && list.size()>0){
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Map map = (Map) iterator.next();
				Object tmnObject = map.get(tmnColumn);
				Object sysIdObject = map.get(sysIdColumn);
				if(tmnObject!=null && sysIdColumn!=null){
					String tmnCode = null;
					String sysId = null;
					if(tmnObject instanceof BigDecimal){
						tmnCode = ((BigDecimal)tmnObject).longValue()+"";
					}else if(tmnObject instanceof String){
						tmnCode = (String)tmnObject;
					}else{
						tmnCode = tmnObject.toString();
					}
					if(sysIdObject instanceof BigDecimal){
						sysId = ((BigDecimal)sysIdObject).longValue()+"";
					}else if(sysIdObject instanceof String){
						sysId = (String)sysIdObject;
					}else{
						sysId = sysIdObject.toString();
					}
					retMap.put(tmnCode, sysId);
				}
			}
		}
		return retMap;
	}
	
	/**
	 * ��������ʵʱ��λ
	 * @param list
	 * @throws Exception
	 */
	public static void saveGpsReal(List<YmGpsModel> list)throws Exception{
		if(list!=null && list.size()>0){
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					try {
						YmGpsModel ymGpsModel = (YmGpsModel) iterator.next();
						GpsDataIbatis.getSqlMapClient().delete("DC_GPS_REAL.deleteByPrimaryKey", ymGpsModel);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				GpsDataIbatis.getSqlMapClient().executeBatch();
				GpsDataIbatis.getSqlMapClient().commitTransaction();
				GpsDataIbatis.getSqlMapClient().endTransaction();
			}
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					try {
						YmGpsModel ymGpsModel = (YmGpsModel) iterator.next();
						String gpsTime = ymGpsModel.getGpsTime();
						ymGpsModel.setGpsTime(DateUtil.parseToNum(gpsTime));
						String stts1 = ymGpsModel.getStts1();
						ymGpsModel.setStts1(EncodeUtil.getHexStr(EncodeUtil.binaryStrToByte(stts1)));
						String stts2 = ymGpsModel.getStts2();
						ymGpsModel.setStts2(EncodeUtil.getHexStr(EncodeUtil.binaryStrToByte(stts2)));
						GpsDataIbatis.getSqlMapClient().insert("DC_GPS_REAL.insert", ymGpsModel);
						ymGpsModel.setGpsTime(gpsTime);
						ymGpsModel.setStts1(stts1);
						ymGpsModel.setStts2(stts2);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				GpsDataIbatis.getSqlMapClient().executeBatch();
				GpsDataIbatis.getSqlMapClient().commitTransaction();
				GpsDataIbatis.getSqlMapClient().endTransaction();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
			}
		}
	}
	
	/**
	 * ����������ʷ��λ
	 * @param list
	 * @throws Exception
	 */
	public static void saveGpsHis(List<YmGpsModel> list)throws Exception{
		if(list!=null && list.size()>0){
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					YmGpsModel ymGpsModel = (YmGpsModel) iterator.next();
					String gpsTime = ymGpsModel.getGpsTime();
					ymGpsModel.setGpsTime(DateUtil.parseToNum(gpsTime));
					String stts1 = ymGpsModel.getStts1();
					ymGpsModel.setStts1(EncodeUtil.getHexStr(EncodeUtil.binaryStrToByte(stts1)));
					String stts2 = ymGpsModel.getStts2();
					ymGpsModel.setStts2(EncodeUtil.getHexStr(EncodeUtil.binaryStrToByte(stts2)));
					GpsDataIbatis.getSqlMapClient().insert("DC_GPS_HIS.insert", ymGpsModel);
					ymGpsModel.setGpsTime(gpsTime);
					ymGpsModel.setStts1(stts1);
					ymGpsModel.setStts2(stts2);
				}
				GpsDataIbatis.getSqlMapClient().executeBatch();
				GpsDataIbatis.getSqlMapClient().commitTransaction();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				GpsDataIbatis.getSqlMapClient().endTransaction();
			}
		}
	}
	
	/**
	 * ����ʻ�������������ʷ
	 * @param list
	 * @throws Exception
	 */
	public static void saveDcRgAreaHis(List<DcRgAreaHis> list)throws Exception{
		if(list!=null && list.size()>0){
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					DcRgAreaHis dcRgAreaHis = (DcRgAreaHis) iterator.next();
					String startTime = dcRgAreaHis.getStartTime();
					String endTime = dcRgAreaHis.getEndTime();
					if(NumberUtils.isDigits(dcRgAreaHis.getStartTime())){
						dcRgAreaHis.setStartTime(DateUtil.converDateFormat(startTime, DateUtil.YMDHMS, DateUtil.YMD_HMS));
					}
					if(NumberUtils.isDigits(dcRgAreaHis.getEndTime())){
						dcRgAreaHis.setEndTime(DateUtil.converDateFormat(endTime, DateUtil.YMDHMS, DateUtil.YMD_HMS));
					}
					GpsDataIbatis.getSqlMapClient().insert("DC_RG_AREA_HIS.insert", dcRgAreaHis);
					dcRgAreaHis.setStartTime(startTime);
					dcRgAreaHis.setEndTime(endTime);
				}
				GpsDataIbatis.getSqlMapClient().executeBatch();
				GpsDataIbatis.getSqlMapClient().commitTransaction();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				GpsDataIbatis.getSqlMapClient().endTransaction();
			}
		}
	}
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			getTmnSysIdMap("select mobile_number,id from wq_staff_info");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
