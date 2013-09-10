package org.aigps.wq.entity;

import java.math.BigDecimal;

public class DcChinaArea {
    private String zcode;

    private String prov;

    private String city;

    private String town;
    
    private String areaName;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal lonOffset;

    private BigDecimal latOffset;

    private BigDecimal sort;
    
    

    public String getAreaName() {
    	if(areaName==null){
    		areaName = prov+city+town;
    	}
		return areaName;
	}

	public String getZcode() {
        return zcode;
    }

    public void setZcode(String zcode) {
        this.zcode = zcode;
    }

    public Object getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Object getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLonOffset() {
        return lonOffset;
    }

    public void setLonOffset(BigDecimal lonOffset) {
        this.lonOffset = lonOffset;
    }

    public BigDecimal getLatOffset() {
        return latOffset;
    }

    public void setLatOffset(BigDecimal latOffset) {
        this.latOffset = latOffset;
    }

    public BigDecimal getSort() {
        return sort;
    }

    public void setSort(BigDecimal sort) {
        this.sort = sort;
    }
}