package org.aigps.wq.entity;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 数据中心日里程统计
 * @author Administrator
 *
 */
public class DcDayMile implements Serializable{
	private static final Log log = LogFactory.getLog(DcDayMile.class);
    /**
	 * 
	 */
	private static final long serialVersionUID = -7860050183297974563L;

	private String tmnCode;

    private String tmnAlias;

    private String day;

    private String dayMile;

    private String currMile;

    public String getTmnCode() {
        return tmnCode;
    }

    public void setTmnCode(String tmnCode) {
        this.tmnCode = tmnCode;
    }

    public String getTmnAlias() {
        return tmnAlias;
    }

    public void setTmnAlias(String tmnAlias) {
        this.tmnAlias = tmnAlias;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDayMile() {
        return dayMile;
    }

    public void setDayMile(String dayMile) {
        this.dayMile = dayMile;
    }

    public String getCurrMile() {
        return currMile;
    }

    public void setCurrMile(String currMile) {
        this.currMile = currMile;
    }
}