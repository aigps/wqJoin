package org.aigps.wq.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.map.model.MapLocation;
import org.gps.map.model.ShapeEnum;
import org.gps.util.common.StringUtil;

/**
 * 外勤地理区域模型
 * @author Administrator
 *
 */
public class WqMapRegion implements Serializable{
	private static final Log log = LogFactory.getLog(WqMapRegion.class);
	private static final long serialVersionUID = -1356188858051973276L;

	private String id;
	/**
	 * 地图区域名称
	 */
    private String name;
    /**
     * 圆半径
     */
    private BigDecimal radius;
    /**
     * 缩放级别
     */
    private BigDecimal zoom;
    /**
     * 类型0：线形1：矩形2：多边形 3：圆形
     */
    private String type;
    /**
     * 区域点
     */
    private String points;

    private String standby1;

    private String standby2;

    private String standby3;
    /**
     * 区域点集合
     */
    private List<MapLocation> locations;
    /**
     * 形状
     */
    private ShapeEnum shapeEnum;
    
    /**
     * 地图形状
     * @return
     */
    public ShapeEnum getShapeEnum() {
    	if(shapeEnum==null){
    		if(type!=null){
    			if("0".equalsIgnoreCase(type)){
    				shapeEnum = ShapeEnum.LINE;
    			}else if("1".equalsIgnoreCase(type)){
    				shapeEnum = ShapeEnum.RECTANGLE;
    			}else if("2".equalsIgnoreCase(type)){
    				shapeEnum = ShapeEnum.POLYGON;
    			}else if("3".equalsIgnoreCase(type)){
    				shapeEnum = ShapeEnum.CIRCLE;
    			}
    		}
    	}
		return shapeEnum;
	}



	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStandby2() {
        return standby2;
    }

    public void setStandby2(String standby2) {
        this.standby2 = standby2;
    }

    public BigDecimal getRadius() {
        return radius;
    }

    public void setRadius(BigDecimal radius) {
        this.radius = radius;
    }

    public BigDecimal getZoom() {
        return zoom;
    }

    public void setZoom(BigDecimal zoom) {
        this.zoom = zoom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStandby3() {
        return standby3;
    }

    public void setStandby3(String standby3) {
        this.standby3 = standby3;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
    
	/**
	 * 获取地图点集合
	 * @return
	 * @throws Exception
	 */
	public List<MapLocation> getLocations()throws Exception{
		if(locations==null){
		    List<MapLocation> list = new ArrayList<MapLocation>();
		    String[] pointArray = StringUtil.splitStr(points, ";");
		    for (int index = 0; index < pointArray.length; index++) {
	            String[] lonlat = StringUtil.splitStr(pointArray[index], ",");
	            MapLocation location = new MapLocation(Double.parseDouble(lonlat[0]),Double.parseDouble(lonlat[1]));
	            list.add(location);
		    }
		    locations = list;
		}
	    return locations;
	}
}