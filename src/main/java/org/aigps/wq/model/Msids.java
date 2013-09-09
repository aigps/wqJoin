
package org.aigps.wq.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("msids")
public class Msids {
	private String msid;			//手机IMSI号
	private String msid_type;		//固定值2
	private String new_msid;		//新手机IMSI号
	private String new_msid_type;	//新固定值2
	
	public String getMsid() {
		return msid;
	}
	public void setMsid(String msid) {
		this.msid = msid;
	}
	public String getMsid_type() {
		return msid_type;
	}
	public void setMsid_type(String msid_type) {
		this.msid_type = msid_type;
	}
	public String getNew_msid() {
		return new_msid;
	}
	public void setNew_msid(String new_msid) {
		this.new_msid = new_msid;
	}
	public String getNew_msid_type() {
		return new_msid_type;
	}
	public void setNew_msid_type(String new_msid_type) {
		this.new_msid_type = new_msid_type;
	}
	
}

