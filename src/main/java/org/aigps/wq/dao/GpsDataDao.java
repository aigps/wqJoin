/**
 * 
 */
package org.aigps.wq.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.aigps.wq.entity.DcCmdTrace;
import org.aigps.wq.entity.DcRgAreaHis;
import org.aigps.wq.entity.GisPosition;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据中心数据入库
 * @author Administrator
 *
 */
@Component 
public class GpsDataDao {
	private static final Log log = LogFactory.getLog(GpsDataDao.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate ;
	
	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}



	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public List<GisPosition> loadDbGps()throws Exception{
		return (List<GisPosition>)GpsDataIbatis.getSqlMapClient().queryForList("DC_GPS_REAL.selectAll");
	}



	/**
	 * 保存过往指令
	 * @param queue
	 * @throws Exception
	 */
	public void saveDcCmdTrace(Queue<DcCmdTrace> queue)throws Exception{
		if(queue!=null && !queue.isEmpty()){
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				DcCmdTrace dcCmdTrace = null;
				while((dcCmdTrace = queue.poll()) != null){
					GpsDataIbatis.getSqlMapClient().update("DC_CMD_TRACE.insert", dcCmdTrace);
				}
				GpsDataIbatis.getSqlMapClient().executeBatch();
			} catch (Exception e) {
				throw e;
			}finally{
				GpsDataIbatis.getSqlMapClient().commitTransaction();
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
	public HashMap<String,String> getTmnSysIdMap(String sql)throws Exception{
		HashMap<String, String> retMap = new HashMap<String, String>();
		int firstComma = sql.indexOf(",");
		int firstBlank = sql.indexOf(" ");
		String tmnColumn = sql.substring(firstBlank, firstComma).trim();
		int firstFrom = sql.toLowerCase().indexOf("from");
		String sysIdColumn = sql.substring(firstComma+1, firstFrom).trim();
		List list = jdbcTemplate.queryForList(sql);
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
	public  void saveGpsReal(Collection<GisPosition> list)throws Exception{
		if(list!=null && list.size()>0){
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					try {
						GisPosition gisPosition = (GisPosition) iterator.next();
						GpsDataIbatis.getSqlMapClient().delete("DC_GPS_REAL.deleteByPrimaryKey", gisPosition);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				GpsDataIbatis.getSqlMapClient().executeBatch();
			}catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				GpsDataIbatis.getSqlMapClient().commitTransaction();
				GpsDataIbatis.getSqlMapClient().endTransaction();
			}
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					try {
						GisPosition gisPosition = (GisPosition) iterator.next();
						GpsDataIbatis.getSqlMapClient().insert("DC_GPS_REAL.insert", gisPosition);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				GpsDataIbatis.getSqlMapClient().executeBatch();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				GpsDataIbatis.getSqlMapClient().commitTransaction();
				GpsDataIbatis.getSqlMapClient().endTransaction();
			}
		}
	}
	
	/**
	 * 批量保存历史定位
	 * @param list
	 * @throws Exception
	 */
	public  void saveGpsHis(Queue<GisPosition> queue)throws Exception{
		if(queue!=null && !queue.isEmpty()){
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				GisPosition p = null;
				while((p = queue.poll())!= null){
					GpsDataIbatis.getSqlMapClient().insert("DC_GPS_HIS.insert", p);
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
	 * @param queue
	 * @throws Exception
	 */
	public  void saveDcRgAreaHis(Queue<DcRgAreaHis> queue)throws Exception{
		if(queue!=null && !queue.isEmpty()){
			try {
				GpsDataIbatis.getSqlMapClient().startTransaction();
				GpsDataIbatis.getSqlMapClient().startBatch();
				DcRgAreaHis dcRgAreaHis = null;
				while((dcRgAreaHis = queue.poll())!=null){
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
	
	
	public List<DcRgAreaHis> loadDcRgAreaReal()throws Exception{
			return GpsDataIbatis.getSqlMapClient().queryForList("DC_RG_AREA_REAL.selectAll");
	}
	
	
	


}
