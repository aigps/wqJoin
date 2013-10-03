package org.aigps.wq.entity;

import java.io.Serializable;
public class GisPosition implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3877679755390311347L;
	//终端号
	private String tmnKey;
	//
	private String tmnAlias;
	//定位时间yyyyMMddHHmmss
	private String rptTime;
	//经度
	private double lon;
	//纬度
	private double lat;
	//速度
	private double speed;
	//方向
	private double dire;
	//海拔
	private double altitude;
	//gsm信号
	private int gsmSign;
	//卫星信号
	private int satlSign;
	//定位类型0 MSA，1 Google，2 GPS，3 GPSOne，4 Hybrid，5北斗，6GPS北斗兼容模式
	private String gpsType;
	//触发类型 00 普通定位,01点名,02激活,03周期, 97照片,98登签,99退签
	private String trigType;
	//地理描述
	private String locDesc;
		//精度
	private int precision ;
	//接收时间yyyyMMddHHmmss
	private String serverTime;
	//行政区域编号
	private String zCode;
	//查询开始时间yyyyMMddHHmmss
	private String queryStartTime;
	//查询结束时间yyyyMMddHHmmss
	private String queryEndTime;
	
	
	
	//基站ID  暂时不支持
//	private String cellId;
	public String getzCode() {
		return zCode;
	}
	public void setzCode(String zCode) {
		this.zCode = zCode;
	}
	public String getServerTime() {
		return serverTime;
	}
	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}
	public String getTmnAlias() {
		return tmnAlias;
	}
	public void setTmnAlias(String tmnAlias) {
		this.tmnAlias = tmnAlias;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	public String getTmnKey() {
		return tmnKey;
	}
	public void setTmnKey(String tmnKey) {
		this.tmnKey = tmnKey;
	}
	
	public String getRptTime() {
		return rptTime;
	}
	public void setRptTime(String rptTime) {
		this.rptTime = rptTime;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getDire() {
		return dire;
	}
	public void setDire(double dire) {
		this.dire = dire;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public int getGsmSign() {
		return gsmSign;
	}
	public void setGsmSign(int gsmSign) {
		this.gsmSign = gsmSign;
	}
	public int getSatlSign() {
		return satlSign;
	}
	public void setSatlSign(int satlSign) {
		this.satlSign = satlSign;
	}
	
	public String getGpsType() {
		return gpsType;
	}
	public void setGpsType(String gpsType) {
		this.gpsType = gpsType;
	}
	public String getTrigType() {
		return trigType;
	}
	public void setTrigType(String trigType) {
		this.trigType = trigType;
	}
	public String getLocDesc() {
		return locDesc;
	}
	public void setLocDesc(String locDesc) {
		this.locDesc = locDesc;
	}
	public String getQueryStartTime() {
		return queryStartTime;
	}
	public void setQueryStartTime(String queryStartTime) {
		this.queryStartTime = queryStartTime;
	}
	public String getQueryEndTime() {
		return queryEndTime;
	}
	public void setQueryEndTime(String queryEndTime) {
		this.queryEndTime = queryEndTime;
	}

//	public String getCellId() {
//		return cellId;
//	}
//	public void setCellId(String cellId) {
//		this.cellId = cellId;
//	}
	
	
}
