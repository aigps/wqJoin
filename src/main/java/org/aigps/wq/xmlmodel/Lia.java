
package org.aigps.wq.xmlmodel;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("lia")
public class Lia {

	private String app_id;			//应用标识，现为手机号后9位
	private String req_id;			//请求标识，现为手机号前3位+2位定位类型
	private Msids msids;			//手机IMSI
	private List<Posinfo> posinfos;	//定位信息
	private String cellids;			//基站IDS
	private Picture picture;		//照片
	private String userdata;		//手镯数据
	
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
	public Msids getMsids() {
		return msids;
	}
	public void setMsids(Msids msids) {
		this.msids = msids;
	}
	public List<Posinfo> getPosinfos() {
		return posinfos;
	}
	public void setPosinfos(List<Posinfo> posinfos) {
		this.posinfos = posinfos;
	}
	public Picture getPicture() {
		return picture;
	}
	public void setPicture(Picture picture) {
		this.picture = picture;
	}
	public String getCellids() {
		return cellids;
	}
	public void setCellids(String cellids) {
		this.cellids = cellids;
	}
	public String getUserdata() {
		return userdata;
	}
	public void setUserdata(String userdata) {
		this.userdata = userdata;
	}
	
	
	
}

