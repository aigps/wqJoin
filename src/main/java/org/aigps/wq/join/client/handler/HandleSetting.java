package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HandleSetting extends IHandler{
	private static final Log log = LogFactory.getLog("HandleSetting");

	public static String CMD = "20";
	private static IHandler handler = new HandleSetting();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[cmd,water,imsi,state,start_time,end_time,inteval,weeks]
	public void receive(Channel channel, String[] msg) {
		log.info("���ն�λ���ûظ�ָ��:" + Arrays.toString(msg));
	}
	
	//[water,start_time,end_time,inteval,week]
	public void send(String imsi, String[] msg) {
		log.info("�·���λ����ָ��:" + imsi + ":" + Arrays.toString(msg));
		super.send(imsi, CMD, msg);
	}
	
}