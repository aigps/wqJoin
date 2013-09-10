/**
 * 
 */
package org.aigps.wq.entity;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 终端地理行政区域历史
 * @author Administrator
 *
 */
public class DcRgAreaHis implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2830080859317134486L;

	private static final Log log = LogFactory.getLog(DcRgAreaHis.class);
	
	private String tmnCode;
	
	private String tmnAlias;
	
	private String startTime;
	
	private String endTime;
	
	private String rgAreaCode;
	
	
	
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



	public String getRgAreaCode() {
		return rgAreaCode;
	}



	public void setRgAreaCode(String rgAreaCode) {
		this.rgAreaCode = rgAreaCode;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
