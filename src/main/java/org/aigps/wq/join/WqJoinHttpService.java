package org.aigps.wq.join;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.CharsetUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aigps.wq.WqJoinContext;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.entity.UploadStatus;
import org.aigps.wq.ibatis.IbatisUpdateJob;
import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqJoinMqService;
import org.aigps.wq.service.FormAnalyse;
import org.aigps.wq.service.WqGpsService;
import org.aigps.wq.task.job.TmnSysIdRefreshJob;
import org.aigps.wq.xmlmodel.LiaModel;
import org.aigps.wq.xmlmodel.LtaModel;
import org.aigps.wq.xmlmodel.MessageModel;
import org.aigps.wq.xmlmodel.Picture;
import org.aigps.wq.xmlmodel.StatusModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.netty.netty4.server.http.IHttpService;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-3-2����11:27:44
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company������Ԫ��Ƽ����޹�˾
 */
@Component
public class WqJoinHttpService  implements IHttpService{
	protected static final Log log = LogFactory.getLog(WqJoinHttpService.class);
	
	HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
	static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }
	
	public String execute(ChannelHandlerContext ctx, Object request) throws Exception {
		if(request  instanceof HttpRequest){
			HttpRequest httpRequest = (HttpRequest)request;
			//�ж��ǲ���FORM�ύ�ϴ�ͼƬ
			try{
				HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, httpRequest);
				if(decoder.isMultipart()){
					Picture pic = FormAnalyse.analysePicture(decoder);
					sendPicture(pic);
					return "true";
				}
			} catch(Exception e){
			}
		}
		if(request instanceof HttpContent){
			HttpContent httpContent = (HttpContent) request;
			ByteBuf content  = httpContent.content();
			String xmlString = convertXmlToLowerCase(content.toString(CharsetUtil.UTF_8));
			XStream xstream = new XStream();
			try{
				if(xmlString.indexOf("<lia>") != -1){//��λ��Ϣ�ظ�
					xstream.processAnnotations(LiaModel.class);
					LiaModel model = (LiaModel) xstream.fromXML(xmlString);
					List<String> ymDatas = model.convertToYmData();
					List<GisPosition> gpsList = model.toGps();
					if(gpsList == null || gpsList.isEmpty()){
						log.error("�޶�λ��ϢXML:\n" + xmlString);
						return xmlString;
					}
					String phone = model.getPhone(), msId = model.getMsid();
					log.error("�ж�λ��ϢXML:\n" + xmlString);
					Picture pic = model.getLia().getPicture();
					
					//�ϱ���λ��Ϣ
					for(GisPosition gps : gpsList){
						String tmnKey = StringUtils.isBlank(phone) ? msId : phone;
						gps.setTmnKey(tmnKey);
						//��λ����
						WqGpsService service = WqJoinContext.getBean("wqGpsService", WqGpsService.class);
						service.receiveGpsInfo(tmnKey, gps);
					}
					
					//����ǩ��ǩ�ˣ�����״̬
					if(model.isSignIn() || model.isSignOut()){
						UploadStatus uploadStatus = new UploadStatus();
						uploadStatus.setRptTime(model.getTime());
						uploadStatus.setStatus(model.isSignIn()?"98":"99");
						uploadStatus.setPhone(msId);
						MqMsg mqMsg = new MqMsg(msId, "WQ", 0, "CMD","UploadStatus");
						mqMsg.setData(uploadStatus);
						WqJoinMqService.addMsg(mqMsg);
						IbatisUpdateJob ibatisUpdateJob = WqJoinContext.getBean("ibatisUpdateJob", IbatisUpdateJob.class);
						ibatisUpdateJob.addExeSql("", uploadStatus);
					}else if(model.isPicture() && pic!=null){//����
						pic.setTime(model.getTime());
						pic.setMsid(msId);
						sendPicture(pic);
					}
				}
				else if(xmlString.indexOf("<lta>") != -1){//����Ӧ����Ϣ
					xstream.processAnnotations(LtaModel.class);
					LtaModel model = (LtaModel) xstream.fromXML(xmlString);
					String ymData = model.convertToYmData();
					if(StringUtils.isBlank(ymData)){
						log.error("��LTA��ϢXML:\n" + xmlString);
						return xmlString;
					}
					String phone = model.getPhone(), msId = model.getMsid();
		        	log.error("��LTA��ϢXML:\n" + xmlString);
					log.error("lta phone:" + phone + " msId:" + msId + " data:"+ymData);
		        	MqMsg mqMsg = new MqMsg(StringUtils.isBlank(phone) ? msId : phone, StringUtils.isBlank(phone) ? "IMSI" :"BJDX", 0, "CMD_RSP",model.getCmdType());
		        	mqMsg.addDataProperty("respResult", model.getYmResult());
		        	WqJoinMqService.addMsg(mqMsg);
				}
				else if(xmlString.indexOf("<status>")!=-1){//�ֻ�״̬��Ϣ
					xstream.processAnnotations(StatusModel.class);
					StatusModel model = (StatusModel) xstream.fromXML(xmlString);
					String ymData = model.convertToYmData();
					if(StringUtils.isBlank(ymData)){
						log.error("��STATUS��ϢXML:\n" + xmlString);
						return xmlString;
					}
					log.error("��STATUS��ϢXML:\n" + xmlString);
					UploadStatus uploadStatus = new UploadStatus();
					uploadStatus.setRptTime(model.getTime());
					uploadStatus.setStatus(model.getResult());
					uploadStatus.setPhone(model.getMsid());
					MqMsg mqMsg = new MqMsg(model.getMsid(), "IMSI", 0, "CMD","UploadStatus");
					mqMsg.setData(uploadStatus);
					WqJoinMqService.addMsg(mqMsg);
				}
				else if(xmlString.indexOf("<messages>")!=-1){//�ϱ�����Ϣ
					xstream.processAnnotations(MessageModel.class);
					MessageModel model = (MessageModel) xstream.fromXML(xmlString);
					String ymData = model.convertToYmData();
					if(StringUtils.isBlank(ymData)){
						log.error("��MESSAGES��ϢXML:\n" + xmlString);
						return xmlString;
					}
					log.error("��MESSAGES��ϢXML:\n" + xmlString);
					MqMsg mqMsg = new MqMsg(model.getMsid(), "IMSI", 0, "CMD","UploadSMS");
					mqMsg.addDataProperty("type", model.getMsgType());
					mqMsg.addDataProperty("content", model.getMsgCnt());
					WqJoinMqService.addMsg(mqMsg);
				}
				else if(xmlString.indexOf("<net_test>")!=-1){//���������Ƿ�����
					log.error("������������:\n" + xmlString);
				}
				else{
					log.error("δ֪�ṹXML:\n" + xmlString);
				}
			}catch(Exception e){
				log.error("�쳣XML:\n" + xmlString);
				log.error(e.getMessage(),e);
			}
			return xmlString;
		}
		return null;
	}
	
	//��XML���<XXXX>��ǩת��Сд
	private static String convertXmlToLowerCase(String xml){
		Pattern pattern = Pattern.compile("<.+?>");
		StringBuilder sb = new StringBuilder();
		int lastIdx = 0;
		Matcher matcher = pattern.matcher(xml);
		while (matcher.find()) {
			String str = matcher.group();
			sb.append(xml.substring(lastIdx, matcher.start()));
			sb.append(str.toLowerCase());
			lastIdx = matcher.end();
		}
		return sb.append(xml.substring(lastIdx)).toString();
	}
	
	private void sendPicture(Picture pic) throws Exception{
		String picData = pic.getData();
		List<String> list = new ArrayList<String>();
		int index = 0, length = picData.length();
		while(index+2000 < length){
			list.add(picData.substring(index,index+2000));
			index += 2000;
		}
		list.add(picData.substring(index));
		int packIndex = 1, totalPack = list.size();
//		Channel cnel = WqJoinContext.getYmNettyClient().getChannel();
//		for(String data:list){
//			String picd =  "0|"+pic.getTime()+"|0|"+packIndex+"|"+totalPack+"|"+data+"|"+pic.getDesc()+"|"+pic.getName();
//			YmAccessMsg ymMsg = new YmAccessMsg("PIC", "IMSI", pic.getMsid(), picd);
//       	 	ChannelUtil.sendMsg(cnel, ymMsg.toYmString().getBytes());
//       	 	log.error("pic index:"+packIndex);
//			packIndex++;
//			Thread.sleep(10);
//		}
	}

	public static String getEndTime(String startTime,int interval,int times) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("HHmmss");
		SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
		long start = format.parse(startTime).getTime();
		long end = start + interval*times*1000;
		return format1.format(new Date(end));
	}
	

}
