
package org.aigps.wq.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("lta")
public class Lta {

	private String app_id;//应用标识，现为手机号后9位
	private String req_id;//请求标识，现为手机号前3位+2位定位类型
	//0:延迟定位设置成功
	//1:取消定位成功
	//3:启动定位设置成功
	//4:短信格式错误
	//8:拒绝此次
	//9:加入黑名
	//10:信任此次
	//11:加入白名单
	//12:鉴权无响应
	//14:未找到要取消的定位请求
	private String result;
	private Msids msids;	//手机IMSI
	
	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public String getReq_id() {
		return req_id;
	}
	public void setReq_id(String req_id) {
		this.req_id = req_id;
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

