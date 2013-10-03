
package org.aigps.wq.xmlmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("status")
public class Status {

	//30:开启飞行模式
	//31:关闭飞行模式
	//32:启用数据网络
	//33:禁用数据网络
	//34:启用GPS定位
	//35:禁用GPS定位
	//36:开机
	//37:关机
	//38:重新启动
	//39:电池低
	//40:更换SIM卡
	//98:主动签到
	//99:主动签退
	private String result;	
	private String time;	//时间
	private Msids msids;	//手机IMSI

	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Msids getMsids() {
		return msids;
	}
	public void setMsids(Msids msids) {
		this.msids = msids;
	}
}

