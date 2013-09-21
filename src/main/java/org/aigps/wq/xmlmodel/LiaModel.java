
package org.aigps.wq.xmlmodel;

import java.util.ArrayList;
import java.util.List;

import org.aigps.wq.entity.GisPosition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.gps.util.common.MathUtil;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ans")
public class LiaModel {
	protected static final Log log = LogFactory.getLog(LiaModel.class);
	
	private String result;//返回结果：0-成功；1-失败
	private Lia lia;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Lia getLia() {
		return lia;
	}
	public void setLia(Lia lia) {
		this.lia = lia;
	}
	
	public List<GisPosition> toGps(){
		List<GisPosition> list = new ArrayList<GisPosition>();
		List<Posinfo> ps = lia.getPosinfos();
		if(ps == null || ps.isEmpty()){
			return null;
		}
		String reqType = getReqType();//定位类型  01单次定位;02激活定位;03周期定位
		for (Posinfo p : ps) {
			String fixTime = getFixTime(p);//定位时间
			if(fixTime == null){
				continue;
			}
			String lat = StringUtils.isBlank(p.getLatitude()) ? "0" : p.getLatitude();
			String lng = StringUtils.isBlank(p.getLongitude()) ? "0" : p.getLongitude();
			String altitude = StringUtils.isBlank(p.getAltitude()) ? "0" : p.getAltitude();
			String dire = StringUtils.isBlank(p.getDirection()) ? "0" : p.getDirection();
			String speed = StringUtils.isBlank(p.getVelocity()) ? "0" : p.getVelocity();
			String precision = StringUtils.isBlank(p.getPrecision()) ? "0" : p.getPrecision();
			String fixMode = p.getFixmode();
			
			//规范数据长度
			try{
				GisPosition gisPos = new GisPosition();
				gisPos.setRptTime(fixTime);
				gisPos.setLon(MathUtil.setScale(Double.parseDouble(lng), 6));
				gisPos.setLat(MathUtil.setScale(Double.parseDouble(lat), 6));
				gisPos.setAltitude((int)Double.parseDouble(altitude));
				gisPos.setDire((int)Double.parseDouble(dire));
				gisPos.setSpeed((int)Double.parseDouble(speed));
				gisPos.setPrecision((int)Double.parseDouble(precision));
				gisPos.setTrigType(reqType);
				gisPos.setGpsType(fixMode);
				list.add(gisPos);
			}catch(Exception e){
				log.error(e.getMessage(),e);
			}
		}
		return list;
	}
	
	//将定位信息拼装成元码协议的数据链
	public List<String> convertToYmData(){
		List<Posinfo> ps = lia.getPosinfos();
		if(ps == null || ps.isEmpty()){
			return null;
		}
		String reqType = getReqType();//定位类型  01单次定位;02激活定位;03周期定位
		List<String> datas = new ArrayList<String>();
		
		for (Posinfo p : ps) {
			String fixTime = getFixTime(p);//定位时间
			if(fixTime == null){
				continue;
			}
			
			String lat = StringUtils.isBlank(p.getLatitude()) ? "0" : p.getLatitude();
			String lng = StringUtils.isBlank(p.getLongitude()) ? "0" : p.getLongitude();
			String altitude = StringUtils.isBlank(p.getAltitude()) ? "0" : p.getAltitude();
			String dire = StringUtils.isBlank(p.getDirection()) ? "0" : p.getDirection();
			String speed = StringUtils.isBlank(p.getVelocity()) ? "0" : p.getVelocity();
			String precision = StringUtils.isBlank(p.getPrecision()) ? "0" : p.getPrecision();
			String fixMode = p.getFixmode();
			
			//规范数据长度
			try{
				lng = String.valueOf(MathUtil.setScale(Double.parseDouble(lng), 6));
				lat = String.valueOf(MathUtil.setScale(Double.parseDouble(lat), 6));
				speed = String.valueOf((int)Double.parseDouble(speed));
				dire = String.valueOf((int)Double.parseDouble(dire));
				altitude = String.valueOf((int)Double.parseDouble(altitude));
				precision = String.valueOf(MathUtil.setScale(Double.parseDouble(precision), 1));
			}catch(Exception e){
				log.error(e.getMessage(),e);
			}
			
			StringBuilder ymData = new StringBuilder();
			ymData.append("0|").append("|").append(fixTime);
			ymData.append("|").append(lng);
			ymData.append("|").append(lat);
			ymData.append("|").append(speed);
			ymData.append("|").append(dire);
			ymData.append("|").append("");
			ymData.append("|").append("00000000");
			ymData.append("|").append("00000000");
			ymData.append("|").append("");
			ymData.append("|").append("");
			ymData.append("|").append("");
			ymData.append("|").append("");// 报警状态掩码
			ymData.append("|").append("");
			ymData.append("|").append("");
			ymData.append("|").append("");
			ymData.append("|").append("");
			ymData.append("|").append(altitude);// 高度
			ymData.append("|").append(reqType);
			ymData.append("|").append(precision);
			ymData.append("|").append("");
			ymData.append("|").append("");
			ymData.append("|").append("");
			ymData.append("|").append("");
			ymData.append("|").append(fixMode);// 定位类型 0 MSA，1 Google，2 GPS，3 GPSOne，4 Hybrid
			ymData.append("|").append("");
			ymData.append("|").append("");// 基站ID

			datas.add(ymData.toString());
		}
		return datas;
	}
	
	//获取上报时间，在加上8个小时，就是当前真正时间
	private String getFixTime(Posinfo p){
		String fixTime = p.getFixtime();
		if (StringUtils.isBlank(fixTime)) {
			return null;
		}
		try{
			if (!NumberUtils.isNumber(fixTime)) {
				fixTime = DateUtil.parseToNum(fixTime);
			}
			return DateUtil.addTime(fixTime, "yyyyMMddHHmmss", 8*60*60);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return null;
		}
	}
	
	//获取手机IMSI号
	public String getMsid(){
		try{
			return lia.getMsids().getMsid();
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return "";
		}
	}
	
	//获取手机号
	public String getPhone(){
		String appId = lia.getApp_id();//应用标识
		String reqId = lia.getReq_id();//请求标识
		try{
			//组装手机号 reqId：手机号前3位+2位定位类型   appId：手机号后9位
			if(StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(reqId)){
				String phone = reqId.substring(0,3)+appId;
				return NumberUtils.isNumber(phone) ? phone : null;
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	private String getReqType(){
		if(isPicture()){
			return "97";
		}
		if(isSignIn()){
			return "98";
		}
		if(isSignOut()){
			return "99";
		}
		if(lia.getReq_id()!=null && lia.getReq_id().length()>3){
			return lia.getReq_id().substring(3);
		}
		return "02";
	}
	
	//是否是主动签到
	public boolean isSignIn(){
		return "CHKACT".equalsIgnoreCase(lia.getReq_id());
	}
	//是否是主动签退
	public boolean isSignOut(){
		return "BCKACT".equalsIgnoreCase(lia.getReq_id());
	}
	//是否是拍照
	public boolean isPicture(){
		return "IMAGELOC".equalsIgnoreCase(lia.getReq_id());
	}
	//获取时间
	public String getTime(){
		for (Posinfo p : lia.getPosinfos()) {
			String fixTime = getFixTime(p);
			if(fixTime != null){
				return fixTime;
			}
		}
		return "";
	}
	
}

