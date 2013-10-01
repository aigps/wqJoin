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
import org.aigps.wq.entity.WqStaffInfo;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.ibatis.sqlmap.client.SqlMapClient;

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

	@Autowired
	private  SqlMapClient sqlMapClient;
	public  SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}
	
	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/**
	 * 加载实时定位信息
	 * @return
	 * @throws Exception
	 */
	public List<GisPosition> loadDbGps()throws Exception{
		return (List<GisPosition>)getSqlMapClient().queryForList("DC_GPS_REAL.selectAll");
	}



	/**
	 * 保存过往指令
	 * @param queue
	 * @throws Exception
	 */
	public void saveDcCmdTrace(Queue<DcCmdTrace> queue)throws Exception{
		if(queue!=null && !queue.isEmpty()){
			try {
				getSqlMapClient().startTransaction();
				getSqlMapClient().startBatch();
				DcCmdTrace dcCmdTrace = null;
				while((dcCmdTrace = queue.poll()) != null){
					getSqlMapClient().update("DC_CMD_TRACE.insert", dcCmdTrace);
				}
				getSqlMapClient().executeBatch();
			} catch (Exception e) {
				throw e;
			}finally{
				getSqlMapClient().commitTransaction();
				getSqlMapClient().endTransaction();
			}
		}
	}
	
	
	
	/**
	 * 获取终端业务ID的对照集合
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> getTmnSysIdMap(String sql)throws Exception{
		Map<String, String> retMap = new HashMap<String, String>();
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
				getSqlMapClient().startTransaction();
				getSqlMapClient().startBatch();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					try {
						GisPosition gisPosition = (GisPosition) iterator.next();
						getSqlMapClient().delete("DC_GPS_REAL.deleteByPrimaryKey", gisPosition);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				getSqlMapClient().executeBatch();
			}catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				getSqlMapClient().commitTransaction();
				getSqlMapClient().endTransaction();
			}
			try {
				getSqlMapClient().startTransaction();
				getSqlMapClient().startBatch();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					try {
						GisPosition gisPosition = (GisPosition) iterator.next();
						getSqlMapClient().insert("DC_GPS_REAL.insert", gisPosition);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				getSqlMapClient().executeBatch();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				getSqlMapClient().commitTransaction();
				getSqlMapClient().endTransaction();
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
				getSqlMapClient().startTransaction();
				getSqlMapClient().startBatch();
				GisPosition p = null;
				while((p = queue.poll())!= null){
					getSqlMapClient().insert("DC_GPS_HIS.insert", p);
				}
				getSqlMapClient().executeBatch();
				getSqlMapClient().commitTransaction();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				getSqlMapClient().endTransaction();
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
				getSqlMapClient().startTransaction();
				getSqlMapClient().startBatch();
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
					getSqlMapClient().insert("DC_RG_AREA_HIS.insert", dcRgAreaHis);
					dcRgAreaHis.setStartTime(startTime);
					dcRgAreaHis.setEndTime(endTime);
				}
				getSqlMapClient().executeBatch();
				getSqlMapClient().commitTransaction();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				getSqlMapClient().endTransaction();
			}
		}
	}
	
	
	/**
	 * 加载实时人员区域
	 * @return
	 * @throws Exception
	 */
	public List<DcRgAreaHis> loadDcRgAreaReal()throws Exception{
			return getSqlMapClient().queryForList("DC_RG_AREA_REAL.selectAll");
	}
	
	
	/**
	 * 加载所有的员工
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, WqStaffInfo> loadWqStaffInfo()throws Exception{
		HashMap<String, WqStaffInfo> retMap = new HashMap<String, WqStaffInfo>();
		List<WqStaffInfo> list = getSqlMapClient().queryForList("WQ_STAFF_INFO.selectAll");
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			WqStaffInfo wqStaffInfo = (WqStaffInfo) iterator.next();
			retMap.put(wqStaffInfo.getId(), wqStaffInfo);
		}
		return retMap;
	}


}
