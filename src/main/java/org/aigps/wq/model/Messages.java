
package org.aigps.wq.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("messages")
public class Messages {

	private String type;
	private String message;
	private Msids msids;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Msids getMsids() {
		return msids;
	}
	public void setMsids(Msids msids) {
		this.msids = msids;
	}
	
	
}

