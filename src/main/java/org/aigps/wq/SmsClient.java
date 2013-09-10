package org.aigps.wq;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.http.HttpGpsRequest;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date：  2012-3-2下午03:36:52
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州元码科技有限公司
 */
public class SmsClient {
	protected static final Log log = LogFactory.getLog(SmsClient.class);
	
	private static String httpIp;
	
	private static String httpPort;
	
	private static String dxSmsUrl;
	private static String ltSmsUrl;
	
	//  "//BREW:classId:V01#ip:port#SLtest#appId#reqId#fixType";
	private static String smsPrefix = "//BREW:%s:V01#%s:%s#SLtest#%s#%s#%s";
	
	public static void sendSms(String classId,String phone,String taskId,String fixType,String fixMode,String isPosition,String otherParams,String smsSender)throws Exception{
		//例子 http://122.224.88.34:8004/wqgps/SendSmsServlet?sendPhone=15157102348&smsformat=0&sendContent=hello
		
		String appId = phone.substring(3);
		String reqId = phone.substring(0,3) + taskId;

		StringBuilder sms = new StringBuilder(String.format(smsPrefix, classId, httpIp, httpPort, appId, reqId, fixType));
		
		if(fixMode!=null){
			sms.append("#").append(fixMode);
			if(isPosition!=null){
				sms.append("#").append(isPosition);
				if(otherParams!=null){
					sms.append(otherParams);
				}
			}
		}
		StringBuilder params = new StringBuilder("sendPhone=").append(phone).append("&smsformat=0").append("&sendContent=");
		params.append(new String(Base64.encodeBase64(sms.toString().getBytes())));
		
		String smsUrl = "hxylSmsSender".equals(smsSender)?ltSmsUrl:dxSmsUrl;

		log.error("网关:"+smsSender+" 发送手机"+phone  +"\n短信指令:"+sms+"\n参数:"+params+"\nURL:"+smsUrl);
		HttpGpsRequest.httpRequest(smsUrl, "GET",params.toString() , 10000, "UTF-8");
	}
	
	public static void main(String[] args){

		try{
			HttpGpsRequest.httpRequest("http://www.163.com", "GET","" , 10000, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static String getHttpIp() {
		return httpIp;
	}

	public  void setHttpIp(String httpIp) {
		SmsClient.httpIp = httpIp;
	}

	public static String getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(String httpPort) {
		SmsClient.httpPort = httpPort;
	}

	public void setDxSmsUrl(String dxSmsUrl) {
		SmsClient.dxSmsUrl = dxSmsUrl;
	}
	public void setLtSmsUrl(String ltSmsUrl) {
		SmsClient.ltSmsUrl = ltSmsUrl;
	}
	
	
	
	
	
	
	

}
