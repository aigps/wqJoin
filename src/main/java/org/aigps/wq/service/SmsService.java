package org.aigps.wq.service;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.entity.WqEmergency;
import org.aigps.wq.entity.WqMood;
import org.aigps.wq.ibatis.IbatisUpdateJob;
import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqJoinMqService;
import org.aigps.wq.xmlmodel.MessageModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.gps.util.common.StringUtil;

import com.thoughtworks.xstream.XStream;

public class SmsService {
	private static final Log log = LogFactory.getLog(SmsService.class);
	
	public static String execute(String xmlString, XStream xstream) throws Exception {
		xstream.processAnnotations(MessageModel.class);
		MessageModel model = (MessageModel) xstream.fromXML(xmlString);
		String ymData = model.convertToYmData();
		if(StringUtils.isBlank(ymData)){
			log.error("无MESSAGES信息XML:\n" + xmlString);
			return xmlString;
		}
		log.error("有MESSAGES信息XML:\n" + xmlString);
		MqMsg mqMsg = new MqMsg(model.getMsid(), "IMSI", 0, "CMD","UploadSMS");
		mqMsg.addDataProperty("type", model.getMsgType());
		mqMsg.addDataProperty("content", model.getMsgCnt());
		WqJoinMqService.addMsg(mqMsg);
		String staffId = DcGpsCache.getTmnSysIdMap().get(model.getMsid());
		String companyId = "";
		if(DcGpsCache.getStaffMap().containsKey(staffId)){
			companyId = DcGpsCache.getStaffMap().get(staffId).getCompanyId();
		}
		IbatisUpdateJob ibatisUpdateJob = WqJoinContext.getBean("ibatisUpdateJob", IbatisUpdateJob.class);
		if("1".equalsIgnoreCase(model.getMsgType())){//心情短语
			WqMood wqMood = new WqMood();
			wqMood.setId(StringUtil.getUUID());
			wqMood.setStaffId(staffId);
			wqMood.setCompanyId(companyId);
			wqMood.setMessage(model.getMsgCnt());
			wqMood.setMsgTime(DateUtil.sysDateTime);
			ibatisUpdateJob.addExeSql("WQ_MOOD.insert", wqMood);
		}else if("2".equalsIgnoreCase(model.getMsgType())){//紧急事件
			WqEmergency wqEmergency = new WqEmergency();
			wqEmergency.setId(StringUtil.getUUID());
			wqEmergency.setCompanyId(companyId);
			wqEmergency.setStaffId(staffId);
			wqEmergency.setMessage(model.getMsgCnt());
			wqEmergency.setMsgTime(DateUtil.sysDateTime);
//						WqDataIbatis.getSqlMapClient().insert("WQ_EMERGENCY.insert",wqEmergency);
		}
		return null;
	}

}
