package org.aigps.wq.entity;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 外勤禁出禁入报警
 * @author Administrator
 *
 */
public class WqAlarmInfo implements Serializable{
	
	private static final long serialVersionUID = 3965384537941115673L;
	
	private static final Log log = LogFactory.getLog(WqAlarmInfo.class);
	/**
	 * 规则ID
	 */
	private String ruleId;
	/**
	 * 报警时间
	 */
    private String alarmTime;
    /**
     * 报警消息
     */
    private String alarmMsg;
    /**
     * 地理描述
     */
    private String locDesc;
    /**
     * 公司ID
     */
    private String companyId;

    private String remark;

    private String staffId;

    private String standby1;
    
    private String standby2;
    
    private String standby3;
    
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getStandby1() {
        return standby1;
    }

    public void setStandby1(String standby1) {
        this.standby1 = standby1;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmMsg() {
        return alarmMsg;
    }

    public void setAlarmMsg(String alarmMsg) {
        this.alarmMsg = alarmMsg;
    }

    public String getLocDesc() {
        return locDesc;
    }

    public void setLocDesc(String locDesc) {
        this.locDesc = locDesc;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getStandby2() {
        return standby2;
    }

    public void setStandby2(String standby2) {
        this.standby2 = standby2;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStandby3() {
        return standby3;
    }

    public void setStandby3(String standby3) {
        this.standby3 = standby3;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}