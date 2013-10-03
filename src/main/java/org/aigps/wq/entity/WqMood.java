package org.aigps.wq.entity;
/**
 * 心情语表
 * @author Administrator
 *
 */
public class WqMood {
	//ID
    private String id;

    //员工ID
    private String staffId;

    //信息
    private String message;

    //信息时间
    private String msgTime;

    //公司ID
    private String companyId;

    //备注
    private String remark;


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


    public String getMessage() {
        return message;
    }

 
    public void setMessage(String message) {
        this.message = message;
    }

 
    public String getMsgTime() {
        return msgTime;
    }


    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
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
}