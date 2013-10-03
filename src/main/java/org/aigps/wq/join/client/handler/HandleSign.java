package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandleSign extends IHandler{
	private static final Log log = LogFactory.getLog("HandleSign");
	
	public static String CMD = "60";
	private static IHandler handler = new HandleSign();
	
	public static IHandler getInstance(){
		return handler;
	}
	
	//[cmd,water,imsi,state,time]
	public void receive(Channel channel, String[] msg) {
		log.info("接收签到信息:" + Arrays.toString(msg));
		//通用应答回复
		this.response(channel, msg[1], msg[0]);
	}

	public void send(String imsi, String[] msg) {
	}

}
