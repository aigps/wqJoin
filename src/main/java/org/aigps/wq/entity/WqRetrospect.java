package org.aigps.wq.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

public class WqRetrospect {
	private static final Log log = LogFactory.getLog(WqRetrospect.class);
	public static final String STATE_NO_RUNNING = "0";
	public static final String STATE_RUNNING = "1";
	public static final String STATE_DONE = "2";
	public static final String STATE_GET = "3";
	public static final String STATE_MDF = "4";
	public static final String STATE_ERROR = "5";
	
    private String id;

    private String staffId;

    private String regionIds;

    private String startTime;

    private String endTime;

    private String state;

    private String createTime;

    private String companyId;

    private String remark;
    
    private String oldState;
    
    

    public String getOldState() {
		return oldState;
	}

	public void setOldState(String oldState) {
		this.oldState = oldState;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getRegionIds() {
        return regionIds;
    }

    public void setRegionIds(String regionIds) {
        this.regionIds = regionIds;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public List<String> getRegionList(){
    	List<String> list= new ArrayList<String>();
    	if(regionIds!=null && !regionIds.trim().equalsIgnoreCase("")){
    		String[] regionArray = StringUtils.delimitedListToStringArray(regionIds, ",");
    		for (int index = 0; index < regionArray.length; index++) {
    			if(regionArray[index]!=null && !regionArray[index].trim().equalsIgnoreCase("")){
    				list.add(regionArray[index]);
    			}
			}
    	}
    	return list;
    }
}