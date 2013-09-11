package org.aigps.wq.service;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.task.job.ParseGpsLocDescJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.gps.util.common.LonLatUtil;
import org.springframework.stereotype.Component;

@Component
public class WqGpsService {
	private static final Log log = LogFactory.getLog(WqGpsService.class);
	public  boolean correctGpsTime(String sysTime,String maxFutureTime,GisPosition gisPos)throws Exception{
		boolean retFlag = true;
		String gpsTime = gisPos.getRptTime();
		//定位时间超过系统时间
		if(maxFutureTime.compareTo(gpsTime)<=0){
			retFlag = false;
			if(log.isErrorEnabled()){
				log.error("终端:"+gisPos.getTmnKey()+"上报了未来时间-->"+gpsTime+"定位, 丢弃!");
			}
		}
		return retFlag;
	}
	
	/**
	 * 解析定位数据
	 * @param tmnCode
	 * @param ymGpsModel
	 * @throws Exception
	 */
	public void receiveGpsInfo(String tmnCode,GisPosition gisPos)
			throws Exception {
		//如果存在终端与业务ID的对照,设置对照
		if(DcGpsCache.getTmnSysIdMap().containsKey(tmnCode)){
			gisPos.setTmnAlias(DcGpsCache.getTmnSysIdMap().get(tmnCode));
		}
		String sysTime = DateUtil.sysNumDateTime;
		String nextHour = DateUtil.sysNumNextHour;
		//最近定位
		GisPosition preGps = DcGpsCache.getLastGps(tmnCode);
		if(preGps!=null){
			if(preGps.getRptTime()!=null && preGps.getRptTime().equalsIgnoreCase(gisPos.getRptTime())){
				log.warn(tmnCode+" 上报重复定位信息:"+gisPos.getRptTime());
				return;
			}
		}
		//设置接收时间
		gisPos.setServerTime(sysTime);
		//如果是正确时间或者能纠正的时间，接收定位
		if(correctGpsTime(sysTime, nextHour, gisPos)){
			//如果是有效定位
			if(LonLatUtil.isValidLocation(gisPos.getLon(), gisPos.getLat())){
				//行政区域
				String zCode = DistrictUtil.getCityZcode(gisPos.getLon(), gisPos.getLat());
				gisPos.setzCode(zCode);
			}else{//无效定位，取最近有效定位
				if(preGps!=null){//已经有最近的有效定位
					/**
					 * 暂时不采用有效定位
					 */
					gisPos.setLon(preGps.getLon());
					gisPos.setLat(preGps.getLat());
					gisPos.setzCode(preGps.getzCode());
					gisPos.setMile(preGps.getMile());
				}
			}
			/**
			 * 更新里程的计算
			 * 
			 *   (待确认是否需要)
			 */
			

			//更新行政区域
			DcGpsCache.updateDcRgAreaHis(preGps,gisPos);
			//更新最后定位
			DcGpsCache.updateLastGps(tmnCode, gisPos);
			/**
			 * 地理描述(一定得先更新了内存的定位后再调用更新地理描述，其内部是调用了内存的定位信息)
			 */
			
			DcGpsCache.inCreLastGpsReport.put(tmnCode, gisPos);
			DcGpsCache.gpsReportMinuCache.add(gisPos);
		}else{//不能纠正的定位，丢弃掉，不处理
			
		}
	}

}
