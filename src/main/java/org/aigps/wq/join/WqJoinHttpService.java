package org.aigps.wq.join;

import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aigps.wq.model.LiaModel;
import org.aigps.wq.model.LtaModel;
import org.aigps.wq.model.MessageModel;
import org.aigps.wq.model.Picture;
import org.aigps.wq.model.StatusModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.netty.ChannelUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import org.jboss.netty.handler.codec.http.multipart.DiskAttribute;
import org.jboss.netty.handler.codec.http.multipart.DiskFileUpload;
import org.jboss.netty.handler.codec.http.multipart.HttpDataFactory;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

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
public class WqJoinHttpService implements IHttpService {
	protected static final Log log = LogFactory.getLog(WqJoinHttpService.class);
	
	HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
	static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }
	
	@Override
	public String execute(Channel channel, SocketAddress address, HttpRequest request) throws Exception {
		
		//�ж��ǲ���FORM�ύ�ϴ�ͼƬ
		try{
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
			if(decoder.isMultipart()){
				Picture pic = FormAnalyse.analysePicture(decoder);
				sendPicture(pic);
				return "true";
			}
		} catch(Exception e){
		}
		
		ChannelBuffer buffer = request.getContent();
		byte[] msgByte = new byte[buffer.readableBytes()];
		buffer.readBytes(msgByte);
		String xmlString = convertXmlToLowerCase(new String(msgByte,"UTF-8"));
		
		XStream xstream = new XStream();
		try{
			if(xmlString.indexOf("<lia>") != -1){//��λ��Ϣ�ظ�
				xstream.processAnnotations(LiaModel.class);
				LiaModel model = (LiaModel) xstream.fromXML(xmlString);
				List<String> ymDatas = model.convertToYmData();
				if(ymDatas == null || ymDatas.isEmpty()){
					log.error("�޶�λ��ϢXML:\n" + xmlString);
					return xmlString;
				}
				String phone = model.getPhone(), msId = model.getMsid();
				log.error("�ж�λ��ϢXML:\n" + xmlString);
				Picture pic = model.getLia().getPicture();
				
				//�ϱ���λ��Ϣ
				for(String data : ymDatas){
					YmAccessMsg ymMsg = StringUtils.isBlank(phone) ? 
							new YmAccessMsg("GPS", "IMSI", msId, data) :
							new YmAccessMsg("GPS", "BJDX|"+msId, phone, data);
					log.error("phone:" + phone + "  msId:" + msId + " data:"+data);
					ChannelUtil.sendMsg(WqJoinContext.getYmNettyClient().getChannel(), ymMsg.toYmString().getBytes());
				}
				
				//����ǩ��ǩ�ˣ�����״̬
				if(model.isSignIn() || model.isSignOut()){
					String data =  "0|UploadStatus|"+model.getTime()+(model.isSignIn()?"|98":"|99");
					YmAccessMsg ymMsg = new YmAccessMsg("CMD", "IMSI", msId, data);
					log.error("status msId:" + model.getMsid()+" data:"+data);
		       	 	ChannelUtil.sendMsg(WqJoinContext.getYmNettyClient().getChannel(), ymMsg.toYmString().getBytes());
				}else if(model.isPicture() && pic!=null){//����
					
					pic.setTime(model.getTime());
					pic.setMsid(msId);
					sendPicture(pic);
//					
//					String picData = pic.getData();
//					List<String> list = new ArrayList<String>();
//					int index = 0, length = picData.length();
//					while(index+7000 < length){
//						list.add(picData.substring(index,index+7000));
//						index += 7000;
//					}
//					list.add(picData.substring(index));
//					int packIndex = 1, totalPack = list.size();
//					Channel cnel = WqJoinContext.getYmNettyClient().getChannel();
//					for(String data:list){
//						String picd =  "0|"+model.getTime()+"|0|"+packIndex+"|"+totalPack+"|"+data+"|"+pic.getDesc()+"|"+pic.getName();
//						YmAccessMsg ymMsg = new YmAccessMsg("PIC", "IMSI", msId, picd);
//			       	 	ChannelUtil.sendMsg(cnel, ymMsg.toYmString().getBytes());
//						log.error("pic msId:" + model.getMsid()+" data:"+picd);
//						packIndex++;
//					}
				}
				
				if(StringUtils.isNotBlank(model.getLia().getUserdata())){//��������
					String data =  "0|WqAlarm|"+model.getTime()+"|"+model.getLia().getUserdata();
					YmAccessMsg ymMsg = StringUtils.isBlank(phone) ? 
							new YmAccessMsg("CMD", "IMSI", msId, data) :
							new YmAccessMsg("CMD", "BJDX|"+msId, phone, data);
					log.error("userdata msId:" + model.getMsid()+" data:"+data);
		       	 	ChannelUtil.sendMsg(WqJoinContext.getYmNettyClient().getChannel(), ymMsg.toYmString().getBytes());
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
	        	
				YmAccessMsg ymMsg = StringUtils.isBlank(phone) ? 
						new YmAccessMsg("CMD_RESP", "IMSI", msId, ymData) :
						new YmAccessMsg("CMD_RESP", "BJDX|"+msId, phone, ymData); 
	        	ChannelUtil.sendMsg(WqJoinContext.getYmNettyClient().getChannel(), ymMsg.toYmString().getBytes());
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
				YmAccessMsg ymMsg = new YmAccessMsg("CMD", "IMSI", model.getMsid(), ymData);
				log.error("status msId:" + model.getMsid()+" data:"+ymData);
	       	 	ChannelUtil.sendMsg(WqJoinContext.getYmNettyClient().getChannel(), ymMsg.toYmString().getBytes());
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
				YmAccessMsg ymMsg = new YmAccessMsg("CMD", "IMSI", model.getMsid(), ymData);
				log.error("messages  msId:" + model.getMsid()+" data:"+ymData);
	       	 	ChannelUtil.sendMsg(WqJoinContext.getYmNettyClient().getChannel(), ymMsg.toYmString().getBytes());
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
		Channel cnel = WqJoinContext.getYmNettyClient().getChannel();
		for(String data:list){
			String picd =  "0|"+pic.getTime()+"|0|"+packIndex+"|"+totalPack+"|"+data+"|"+pic.getDesc()+"|"+pic.getName();
			YmAccessMsg ymMsg = new YmAccessMsg("PIC", "IMSI", pic.getMsid(), picd);
       	 	ChannelUtil.sendMsg(cnel, ymMsg.toYmString().getBytes());
       	 	log.error("pic index:"+packIndex);
			packIndex++;
			Thread.sleep(10);
		}
	}

	public static String getEndTime(String startTime,int interval,int times) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("HHmmss");
		SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
		long start = format.parse(startTime).getTime();
		long end = start + interval*times*1000;
		return format1.format(new Date(end));
	}
	

}
