package org.aigps.wq.entity;

import java.io.Serializable;

public class VhcLocDesc implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 6743043025410171341L;

	private String vehicleCode;

    private Long reportTime;

    private double longit;

    private double lat;

    private String locDesc;
    
    private boolean isChanged;
    
    private long serverTime;
    

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public String getVehicleCode() {
        return vehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        this.vehicleCode = vehicleCode;
    }

    public Long getReportTime() {
        return reportTime;
    }

    public void setReportTime(Long reportTime) {
        this.reportTime = reportTime;
    }

    public double getLongit() {
        return longit;
    }

    public void setLongit(double longit) {
        this.longit = longit;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getLocDesc() {
        return locDesc;
    }

    public void setLocDesc(String locDesc) {
        this.locDesc = locDesc;
    }
    

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}
	
	

}
