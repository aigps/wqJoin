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
 * 数据中心数据入库
 * @author Administrator
 *
 */
public class GpsDataDao {
	private static final Log log = LogFactory.getLog(GpsDataDao.class);
	
	/**
	 * 保存过往指令
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
	 * 获取终端业务ID的对照集合
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
	 * 批量保存实时定位
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
	 * 批量保存历史定位
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
	 * 保存驶出行政区域的历史
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
