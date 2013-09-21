package org.aigps.wq.entity;

import java.io.Serializable;

/**
 * 
 * <pre>
 * Title:
 * Description: 
 * </pre>
 * @author chencongquan  ccq282738@163.com
 * @version 1.00.00
 * <pre>
 * modify record
 * modify version :      modify by :       modify date:        modify content:  
 * </pre>
 */
public class MapLocation implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 5423890220360606564L;
    private double longtitude;
    private double latitude;
    public double getLongtitude() {
        return longtitude;
    }
    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public MapLocation(double longtitude,double latitude) {
        this.longtitude = longtitude;
        this.latitude = latitude;
    }
    
    public MapLocation(){
        
    }
    
    public void setMapLocation(MapLocation newMapLocation){
    	setLongtitude(newMapLocation.getLongtitude());
    	setLatitude(newMapLocation.getLatitude());
    }
   
    public String toString(){
        StringBuffer str = new StringBuffer();
        str.append("  longtitude=").append(longtitude);
        str.append("  latitude=").append(latitude);
        return str.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean retFlag = false;
        if(obj instanceof MapLocation){
            MapLocation temp = (MapLocation)obj;
            if(this.latitude== temp.latitude){
                if(this.longtitude == temp.longtitude){
                    retFlag = true;
                }
            }
        }
        return retFlag;
    }
    
    @Override
    public int hashCode() {
        String code = this.latitude+this.longtitude+"";
        return code.hashCode();
    }

}
