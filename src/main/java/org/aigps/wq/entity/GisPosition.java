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
	//里程
	private long mile;
	//gsm信号
	private int gsmSign;
	//卫星信号
	private int satlSign;
	//状态
	private String stts;
	//报警状态
	private String alarmStts;
	//Io状态
	private String ioStts;
	//油量
	private double oil;
	//模拟量1
	private long moniData1;
	//模拟量2
	private long moniData2;
	//定位类型0 MSA，1 Google，2 GPS，3 GPSOne，4 Hybrid，5北斗，6GPS北斗兼容模式
	private String gpsType;
	//触发类型 00 普通定位,01点名,02激活,03周期, 97照片,98登签,99退签
	private String trigType;
	//地理描述
	private String locDesc;
	//部标扩展信息
	private String bbExtInfo;
	//基站ID
	private String cellId;
	//ACC开累计总时长
	private long accTotalTime;
	//ACC开时长
	private long accCurTime;
	//继电器状态
	private String eleStts;
	//精度
	private int precision ;
	
	
	
	
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
	public long getMile() {
		return mile;
	}
	public void setMile(long mile) {
		this.mile = mile;
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
	public String getStts() {
		return stts;
	}
	public void setStts(String stts) {
		this.stts = stts;
	}
	public String getAlarmStts() {
		return alarmStts;
	}
	public void setAlarmStts(String alarmStts) {
		this.alarmStts = alarmStts;
	}
	public String getIoStts() {
		return ioStts;
	}
	public void setIoStts(String ioStts) {
		this.ioStts = ioStts;
	}
	public double getOil() {
		return oil;
	}
	public void setOil(double oil) {
		this.oil = oil;
	}
	public long getMoniData1() {
		return moniData1;
	}
	public void setMoniData1(long moniData1) {
		this.moniData1 = moniData1;
	}
	public long getMoniData2() {
		return moniData2;
	}
	public void setMoniData2(long moniData2) {
		this.moniData2 = moniData2;
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
	public String getBbExtInfo() {
		return bbExtInfo;
	}
	public void setBbExtInfo(String bbExtInfo) {
		this.bbExtInfo = bbExtInfo;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public long getAccTotalTime() {
		return accTotalTime;
	}
	public void setAccTotalTime(long accTotalTime) {
		this.accTotalTime = accTotalTime;
	}
	public long getAccCurTime() {
		return accCurTime;
	}
	public void setAccCurTime(long accCurTime) {
		this.accCurTime = accCurTime;
	}
	public String getEleStts() {
		return eleStts;
	}
	public void setEleStts(String eleStts) {
		this.eleStts = eleStts;
	}
}
