package org.aigps.wq.mq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MqMsg implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4842953318898603188L;
	private static final Log log = LogFactory.getLog(MqMsg.class);
	//终端ID
	private String tmnId;
	//终端类型
	private String tmnType;
	//流水号
	private long msgSeq;
	//指令集合
	private List<String> cmdList = new ArrayList<String>();
	//数据内容
	private Object data;
	
	public MqMsg(String tmnId,String tmnType,long msgSeq,List<String> cmdList) {
		this.tmnId = tmnId;
		this.tmnType = tmnType;
		this.msgSeq = msgSeq;
		this.cmdList = cmdList;
	}
	
	public MqMsg(String tmnId,String tmnType,long msgSeq,String... cmdList) {
		this.tmnId = tmnId;
		this.tmnType = tmnType;
		this.msgSeq = msgSeq;
		for (int index = 0; index < cmdList.length; index++) {
			this.cmdList.add(cmdList[index]);
		}
	}
	
	public void addDataProperty(String proKey,Object proValue)throws Exception{
		if(data == null){
			data = new HashMap<String,Object>();
		}
		if(data instanceof HashMap){
			HashMap<String,Object> map = (HashMap<String,Object>)data;
			map.put(proKey, proValue);
		}else{
			throw new Exception("data is not map,can't put key value!!!");
		}
	}
	

	public String getTmnId() {
		return tmnId;
	}

	public void setTmnId(String tmnId) {
		this.tmnId = tmnId;
	}

	public String getTmnType() {
		return tmnType;
	}

	public void setTmnType(String tmnType) {
		this.tmnType = tmnType;
	}

	public long getMsgSeq() {
		return msgSeq;
	}

	public void setMsgSeq(long msgSeq) {
		this.msgSeq = msgSeq;
	}

	public List<String> getCmdList() {
		return cmdList;
	}

	public void setCmdList(List<String> cmdList) {
		this.cmdList = cmdList;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	

}
