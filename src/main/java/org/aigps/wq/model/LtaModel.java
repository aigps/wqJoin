
package org.aigps.wq.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ans")
public class LtaModel {
	protected static final Log log = LogFactory.getLog(LtaModel.class);

	private Lta lta;

	public Lta getLta() {
		return lta;
	}

	public void setLta(Lta lta) {
		this.lta = lta;
	}

	//将信息拼装成元码协议的数据链
	public String convertToYmData(){
		if(lta == null){
			return null;
		}
		return "0|"+getCmdType()+"|"+getYmResult();
	}
	
	//获取手机IMSI号
	public String getMsid(){
		try{
			return lta.getMsids().getMsid();
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return "";
		}
	}
	
	//获取手机号
	public String getPhone(){
		String appId = lta.getApp_id();//应用标识
		String reqId = lta.getReq_id();//请求标识
		try{
			//组装手机号 reqId：手机号前3位+2位定位类型   appId：手机号后9位
			if(StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(reqId)){
				String phone = reqId.substring(0,3)+appId;
				return NumberUtils.isNumber(phone) ? phone : null;
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	//获取响应的类型
	private String getCmdType(){
		String reqType = lta.getReq_id();//定位类型  01单次定位;02激活定位;03周期定位
		if(reqType!=null && reqType.length()>3){
			reqType = reqType.substring(3);
		}
		if ("01".equals(reqType)) {//单次定位
			return "LCSNow";
		} else if ("02".equals(reqType)) {//激活定位
			return "ActiveLCS";
		} else if ("03".equals(reqType)) {//周期定位
			return "TimingInterval";
		} else if ("04".equals(reqType)) {//失活定位
			return "CancelActiveLCS";
		}
		log.error("============新reqType :"+reqType);
		return "LCSNow";
	}

	//将手机的执行结果，转换成元码的执行结果
	private String getYmResult(){
		String result = lta.getResult();
		//0:延迟定位设置成功 
		//1:取消定位成功 
		//3:启动定位设置成功 
		//10:信任此次 
		//11:加入白名单 
		//14:未找到要取消的定位请求
		if("0,1,3,10,11,14,".indexOf(result+",") != -1){
			return "0";//成功
		}
		//4:短信格式错误
		if("4".equals(result)){
			return "10";//指令格式不对
		}
		//8:拒绝此次 9:加入黑名单
		if("8".equals(result)||"9".equals(result)){
			return "12";//指令被拒绝
		}
		//12:鉴权无响应
		if("12".equals(result)){
			return "13";//指令未被处理
		}
		log.error("============新LTA RESULT :"+result);
		return "0";//成功
	}
}

