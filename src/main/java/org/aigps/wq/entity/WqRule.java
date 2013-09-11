package org.aigps.wq.entity;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 外勤员工规则
 * @author Administrator
 *
 */
public class WqRule {
	private static final Log log = LogFactory.getLog(WqRule.class);
	
    private String id;

    private String eleFenceId;

    private String name;

    private String standby1;

    private String startDate;

    private String endDate;

    private String standby2;

    private String startTime;

    private String endTime;

    private String weekDays;

    private String type;

    private String companyId;

    private Date createTime;

    private String remark;

    private String creater;

    private String monitorPhone2;

    private String monitorPhone1;

    private String isEnable;

    private String standby3;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEleFenceId() {
        return eleFenceId;
    }

    public void setEleFenceId(String eleFenceId) {
        this.eleFenceId = eleFenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandby1() {
        return standby1;
    }

    public void setStandby1(String standby1) {
        this.standby1 = standby1;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStandby2() {
        return standby2;
    }

    public void setStandby2(String standby2) {
        this.standby2 = standby2;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = weekDays;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getMonitorPhone2() {
        return monitorPhone2;
    }

    public void setMonitorPhone2(String monitorPhone2) {
        this.monitorPhone2 = monitorPhone2;
    }

    public String getMonitorPhone1() {
        return monitorPhone1;
    }

    public void setMonitorPhone1(String monitorPhone1) {
        this.monitorPhone1 = monitorPhone1;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public String getStandby3() {
        return standby3;
    }

    public void setStandby3(String standby3) {
        this.standby3 = standby3;
    }
}