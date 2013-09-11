package org.aigps.wq.util;

import java.math.BigDecimal;

import org.aigps.wq.entity.GisPosition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;


public class MileUtil {
	private static final Log log = LogFactory.getLog(MileUtil.class);
	
	
	/**
	 * 更新里程统计数据
	 * @param preGisPos
	 * @throws Exception
	 */
	public void refreshMile(GisPosition preGisPos,GisPosition gisPos)throws Exception{
		/**
		 * 里程统计
		 */
		//如果终端号不一致
		if(preGisPos.getTmnKey()!=null && gisPos.getTmnKey()!=null && !preGisPos.getTmnKey().equals(gisPos.getTmnKey())){
			gisPos.setMile(preGisPos.getMile());
		}else if(gisPos.getRptTime().compareTo(preGisPos.getRptTime())>0){
			//如果当前定位时间大于上次定位时间
			long hisTotalMile = new BigDecimal(preGisPos.getMile()).longValue();
			long currMile = new BigDecimal(getCurrMile()).longValue();
			long hisMile = new BigDecimal(preGisPos.getCurrMile()).longValue();
			long diffMile = Math.abs(currMile-hisMile);
			//如果当前里程大于等于上次里程（正常情况下）
			if(currMile>=hisMile){
				//i.如果里程偏差值小于10公里
				if(diffMile<10000){
					setMile((hisTotalMile+diffMile)+"");
				}else{
					long diffSecTime = Math.abs(DateUtil.getBetweenTime(getGpsTime(), preGisPos.getGpsTime(), DateUtil.YMDHMS)/1000); 
					long avgSpeed = diffMile/diffSecTime;
					//ii.	平均速度小于120公里/时
					if(avgSpeed<33){
						setMile((hisTotalMile+diffMile)+"");
					}else{
						setMile(hisTotalMile+"");
					}
				}
			}else{//（1、清零 ；2、也可能是终端重启
				//i.	如果当前里程小于10公里
				if(currMile<10000){//清零
					setMile((hisTotalMile+currMile)+"");
				}else{
					long diffSecTime = Math.abs(DateUtil.getBetweenTime(getGpsTime(), preGisPos.getGpsTime(), DateUtil.YMDHMS)/1000); 
					if(diffMile<15000){//可能是终端重启
						setMile(hisTotalMile+"");
					}else if(currMile/diffSecTime<33){//ii.	平均速度（当前里程/时间差）小于120公里/时
						setMile((hisTotalMile+currMile)+"");
					}else{
						MapLocation currMap = new MapLocation(Double.parseDouble(this.lon),Double.parseDouble(this.lat));
						MapLocation hisMap = new MapLocation(Double.parseDouble(preGisPos.getLon()),Double.parseDouble(preGisPos.getLat()));
						long distance = new BigDecimal(MapUtil.distance(currMap, hisMap, 'K')*1000).longValue();
						//iii.	平均速度（经纬度距离/时间差）小于120公里/时
						if(distance/diffSecTime<33){
							setMile((hisTotalMile+distance)+"");
						}else{
							setMile(hisTotalMile+"");
						}
					}
				}
			}
		}else{//3）	如果当前定位时间小于上次定位时间
			long hisTotalMile = (long)Double.parseDouble(preGisPos.getMile());
			long currMile = (long)Double.parseDouble(getCurrMile());
			long hisMile = (long)Double.parseDouble(preGisPos.getCurrMile());
			//a)	如果当前里程小于上次里程（定位补报）
			if(currMile<hisMile){
				long diffMile = hisMile-currMile;
				if(currMile==0){//如果补报时当前的里程为零，保留原来的值
					setMile(hisTotalMile+"");
				}else if(diffMile<10000){
					if((hisTotalMile+(currMile-hisMile))<=0){//如果是负值，保留原来的值
						setMile(hisTotalMile+"");
					}else{
						setMile((hisTotalMile+(currMile-hisMile))+"");
					}
				}else{
					long diffSecTime = Math.abs(DateUtil.getBetweenTime(getGpsTime(), preGisPos.getGpsTime(), DateUtil.YMDHMS)/1000); 
					if(diffSecTime!=0 && diffMile/diffSecTime<33){
						if((hisTotalMile+(currMile-hisMile))<=0){//如果是负值，保留原来的值
							setMile(hisTotalMile+"");
						}else{
							setMile((hisTotalMile+(currMile-hisMile))+"");
						}
					}else{
						setMile(hisTotalMile+"");
					}
				}
			}else{//b)	如果当前里程大于上次里程（终端误报）
				setMile(hisTotalMile+"");
			}
		}
	}
	
}
