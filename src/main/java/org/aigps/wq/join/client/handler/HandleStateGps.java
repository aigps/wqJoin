package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandleStateGps extends IHandler{
	private static final Log log = LogFactory.getLog("HandleStateGps");
	
	public static String CMD = "72";
	private static IHandler handler = new HandleStateGps();
	
	public static IHandler getInstance(){
		return handler;
	}
	
	//[cmd,water,imsi,state,time]
	public void receive(Channel channel, String[] msg) {
		log.info("接收开关GPS信息:" + Arrays.toString(msg));
		//通用应答回复
		this.response(channel, msg[1], msg[0]);
	}

	public void send(String imsi, String[] msg) {
	}

}
