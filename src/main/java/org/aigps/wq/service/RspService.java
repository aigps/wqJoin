package org.aigps.wq.service;

import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqJoinMqService;
import org.aigps.wq.xmlmodel.LtaModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.XStream;

/**
 * 处理应答消息
 * @author Administrator
 *
 */
public class RspService {
	private static final Log log= LogFactory.getLog(RspService.class);
	
	/**
	 * 
	 * @param xmlString
	 * @param xstream
	 * @return
	 * @throws Exception
	 */
	public static String execute(String xmlString, XStream xstream) throws Exception {
		xstream.processAnnotations(LtaModel.class);
		LtaModel model = (LtaModel) xstream.fromXML(xmlString);
		String ymData = model.convertToYmData();
		if(StringUtils.isBlank(ymData)){
			log.error("无LTA信息XML:\n" + xmlString);
			return xmlString;
		}
		String phone = model.getPhone(), msId = model.getMsid();
		log.error("有LTA信息XML:\n" + xmlString);
		log.error("lta phone:" + phone + " msId:" + msId + " data:"+ymData);
		MqMsg mqMsg = new MqMsg(StringUtils.isBlank(phone) ? msId : phone, StringUtils.isBlank(phone) ? "IMSI" :"BJDX", 0, "CMD_RSP",model.getCmdType());
		mqMsg.addDataProperty("respResult", model.getYmResult());
		WqJoinMqService.addMsg(mqMsg);
		return null;
	}

}
