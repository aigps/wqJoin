package org.aigps.wq.service;

import org.aigps.wq.WqJoinContext;
import org.aigps.wq.entity.WqTmnSttsHis;
import org.aigps.wq.ibatis.IbatisUpdateJob;
import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqJoinMqService;
import org.aigps.wq.xmlmodel.StatusModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import com.thoughtworks.xstream.XStream;

public class SttsRptService {
	private static final Log log = LogFactory.getLog(SttsRptService.class);
	
	public static String execute(String xmlString, XStream xstream) throws Exception {
		xstream.processAnnotations(StatusModel.class);
		StatusModel model = (StatusModel) xstream.fromXML(xmlString);
		String ymData = model.convertToYmData();
		if(StringUtils.isBlank(ymData)){
			log.error("无STATUS信息XML:\n" + xmlString);
			return xmlString;
		}
		log.error("有STATUS信息XML:\n" + xmlString);
		
		String status = model.getResult();
		
		WqTmnSttsHis wqTmnSttsHis = new WqTmnSttsHis();
		wqTmnSttsHis.setStaffId(model.getMsid());
		wqTmnSttsHis.setRptTime(DateUtil.converDateFormat(model.getTime(), DateUtil.YMDHMS, DateUtil.YMD_HMS));
		wqTmnSttsHis.setStts(status);
		
		MqMsg mqMsg = new MqMsg(model.getMsid(), "IMSI", 0, "CMD","UploadStatus");
		mqMsg.setData(wqTmnSttsHis);
		WqJoinMqService.addMsg(mqMsg);
		
		IbatisUpdateJob ibatisUpdateJob = WqJoinContext.getBean("ibatisUpdateJob", IbatisUpdateJob.class);
		ibatisUpdateJob.addExeSql("WQ_TMN_STTS_HIS.insert", wqTmnSttsHis);
		return null;
	}
}
