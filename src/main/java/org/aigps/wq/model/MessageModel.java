
package org.aigps.wq.model;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ans")
public class MessageModel {
	protected static final Log log = LogFactory.getLog(MessageModel.class);

	private Messages messages;

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public Messages getMessages() {
		return messages;
	}

	//将信息拼装成元码协议的数据链
	public String convertToYmData(){
		if(messages == null){
			return null;
		}
		//手机上报的消息类型，和元码协议类型相差1
		int type = Integer.parseInt(messages.getType()) + 1;
		String msg = messages.getMessage();
		try{
			log.error("before message = "+msg);
			msg = new String(msg.getBytes(),"UTF-8");
			log.error("after message = "+msg);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return "";
		}
		return "0|UploadSMS|"+type+"|"+new String(Base64.encodeBase64(msg.getBytes()));
	}

	public String getMsid(){
		try{
			return messages.getMsids().getMsid();
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return "";
		}
	}
}

