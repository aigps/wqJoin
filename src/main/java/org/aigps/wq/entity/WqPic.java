package org.aigps.wq.entity;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

public class WqPic {
    private String staffId;

    private String phone;

    private BigDecimal picTime;

    private String picDesc;

    private String picName;

    private byte[] picData;
    
    private String packSum;
    
    private String packIndex;
    
    private HashMap<String, String> picDataMap = new HashMap<String, String>();
    
    public WqPic() {
    	
	}
    
    public void setPicData(String packIndex,String picData){
    	picDataMap.put(packIndex, picData);
    }
    
    public boolean isDoneOver(){
    	boolean retFlag = false;
    	if(Integer.parseInt(packSum)==picDataMap.size()){
    		retFlag = true;
    		StringBuilder picData = new StringBuilder();
    		for (int index = 1; index <= picDataMap.size(); index++) {
				picData.append(picDataMap.get(index+""));
			}
    		setPicData(Base64.decodeBase64(picData.toString().getBytes()));
    	}
    	return retFlag;
    }

    public String getPackSum() {
		return packSum;
	}

	public void setPackSum(String packSum) {
		this.packSum = packSum;
	}

	public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getPicTime() {
        return picTime;
    }

    public void setPicTime(BigDecimal picTime) {
        this.picTime = picTime;
    }

    public String getPicDesc() {
        return picDesc;
    }

    public void setPicDesc(String picDesc) {
        this.picDesc = picDesc;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public byte[] getPicData() {
        return picData;
    }

    public void setPicData(byte[] picData) {
        this.picData = picData;
    }
}