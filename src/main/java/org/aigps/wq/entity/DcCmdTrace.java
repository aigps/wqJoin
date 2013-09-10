package org.aigps.wq.entity;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;

/**
 * 数据中心指令下发
 * @author Administrator
 *
 */
public class DcCmdTrace implements Serializable{
	private static final Log log = LogFactory.getLog(DcCmdTrace.class);
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -1787418477608586985L;
	//接收
	public static final String ACTION_REC ="00";
	//发送
	public static final String ACTION_SEND="01";
	

	private String tmnCode;

    private String tmnAlias;

    private String actionType;

    private String actionTime;

    private String cmdType;

    private String cnt;

    private String netAddr;
    
    public DcCmdTrace() {

    }
    public DcCmdTrace(String tmnCode,String tmnAlias,String actionType,String actionTime,String address,YmAccessMsg ymMsg)throws Exception{
    	this.tmnAlias = tmnAlias;
    	this.tmnCode = tmnCode;
    	this.actionType = actionType;
    	this.actionTime = actionTime;
    	this.netAddr = address;
    	this.cmdType = ymMsg.getDataType();
    	this.cnt = ymMsg.toYmString();
    }

    public String getTmnCode() {
        return tmnCode;
    }

    public void setTmnCode(String tmnCode) {
        this.tmnCode = tmnCode;
    }

    public String getTmnAlias() {
        return tmnAlias;
    }

    public void setTmnAlias(String tmnAlias) {
        this.tmnAlias = tmnAlias;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getNetAddr() {
        return netAddr;
    }

    public void setNetAddr(String netAddr) {
        this.netAddr = netAddr;
    }
}