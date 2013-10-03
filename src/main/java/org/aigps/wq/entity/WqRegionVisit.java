package org.aigps.wq.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 外勤人员当前所在区域
 * @author Administrator
 *
 */
public class WqRegionVisit implements Serializable{
	private static final long serialVersionUID = 7953781787732168205L;
	private static final Log log = LogFactory.getLog(WqRegionVisit.class);
	

	//员工ID
    private String staffId;
    //进入时间
    private String enterTime;
    //区域ID
    private String regionId;
    //离开时间
    private String leaveTime;
    //停留时长(秒)
    private BigDecimal stayLong;
    //所在公司
    private String companyId;
    
    private String remark;

    private String standby1;

    private String standby2;
    
    private String standby3;
    
    private String queryStartTime;
    
    private String queryEndTime;
    

    public String getQueryStartTime() {
		return queryStartTime;
	}

	public void setQueryStartTime(String queryStartTime) {
		this.queryStartTime = queryStartTime;
	}

	public String getQueryEndTime() {
		return queryEndTime;
	}

	public void setQueryEndTime(String queryEndTime) {
		this.queryEndTime = queryEndTime;
	}

	public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStandby1() {
        return standby1;
    }

    public void setStandby1(String standby1) {
        this.standby1 = standby1;
    }

    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public BigDecimal getStayLong() {
        return stayLong;
    }

    public void setStayLong(BigDecimal stayLong) {
        this.stayLong = stayLong;
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
}