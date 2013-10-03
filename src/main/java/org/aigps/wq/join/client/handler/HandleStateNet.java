package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandleStateNet extends IHandler{
	private static final Log log = LogFactory.getLog("HandleStateNet");
	
	public static String CMD = "71";
	private static IHandler handler = new HandleStateNet();
	
	public static IHandler getInstance(){
		return handler;
	}
	
	//[cmd,water,imsi,state,time]
	public void receive(Channel channel, String[] msg) {
		log.info("���տ���������Ϣ:" + Arrays.toString(msg));
		//ͨ��Ӧ��ظ�
		this.response(channel, msg[1], msg[0]);
	}

	public void send(String imsi, String[] msg) {
	}

}