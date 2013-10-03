
package org.aigps.wq.xmlmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("posinfo")
public class Posinfo {

	//定位结果
	//0：成功
	//1：最近结果
	//2：GPS搜星超时
	//3：GPS定位超时
	//4：禁用GPS定位 
	//5：GPS定位未知错误 
	//6：不支持MSA定位 
	//7：网络无应答
	//8：网络连接失败
	//9：MSA定位超时
	private String positionresult;
	private String fixmode;			//定位类型 0 MSA，1 Google，2 GPS，3 GPSOne，4 Hybrid
	private String fixtime;			//得到定位结果或定位失败的时间，比当前时间晚8小时
	private String latitudetype;	//纬度类型：1-北纬；0-南纬
	private String latitude;		//纬度，精度为小数点后7位
	private String longitudetype;	//经度类型：1-西经；0-东经
	private String longitude;		//经度，精度为小数点后7位
	private String altitude;		//高度，精度为小数点后1位，没有数据的话为空
	private String direction;		//方向，精度为小数点后1位，没有数据的话为空
	private String velocity;		//速度，精度为小数点后1位，没有数据的话为空
	private String precision;		//精度，精度为小数点后1位，没有数据的话为空
	
	//以下属性没用，只有brew手机才有
	private String rssi;
	private String wsysid;
	private String dwbaselatitudetype;
	private String dwbaselatitude;
	private String dwbaselongitudetype;
	private String dwbaselongitude;
	
	public String getPositionresult() {
		return positionresult;
	}
	public void setPositionresult(String positionresult) {
		this.positionresult = positionresult;
	}
	public String getFixmode() {
		return fixmode;
	}
	public void setFixmode(String fixmode) {
		this.fixmode = fixmode;
	}
	public String getFixtime() {
		return fixtime;
	}
	public void setFixtime(String fixtime) {
		this.fixtime = fixtime;
	}
	public String getLatitudetype() {
		return latitudetype;
	}
	public void setLatitudetype(String latitudetype) {
		this.latitudetype = latitudetype;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitudetype() {
		return longitudetype;
	}
	public void setLongitudetype(String longitudetype) {
		this.longitudetype = longitudetype;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAltitude() {
		return altitude;
	}
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getVelocity() {
		return velocity;
	}
	public void setVelocity(String velocity) {
		this.velocity = velocity;
	}
	public String getPrecision() {
		return precision;
	}
	public void setPrecision(String precision) {
		this.precision = precision;
	}
	public String getRssi() {
		return rssi;
	}
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	public String getWsysid() {
		return wsysid;
	}
	public void setWsysid(String wsysid) {
		this.wsysid = wsysid;
	}
	public String getDwbaselatitudetype() {
		return dwbaselatitudetype;
	}
	public void setDwbaselatitudetype(String dwbaselatitudetype) {
		this.dwbaselatitudetype = dwbaselatitudetype;
	}
	public String getDwbaselatitude() {
		return dwbaselatitude;
	}
	public void setDwbaselatitude(String dwbaselatitude) {
		this.dwbaselatitude = dwbaselatitude;
	}
	public String getDwbaselongitudetype() {
		return dwbaselongitudetype;
	}
	public void setDwbaselongitudetype(String dwbaselongitudetype) {
		this.dwbaselongitudetype = dwbaselongitudetype;
	}
	public String getDwbaselongitude() {
		return dwbaselongitude;
	}
	public void setDwbaselongitude(String dwbaselongitude) {
		this.dwbaselongitude = dwbaselongitude;
	}
	
	
}

