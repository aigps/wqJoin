package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandlePosition extends IHandler{
	private static final Log log = LogFactory.getLog("HandlePosition");
	
	public static String CMD = "50";
	private static IHandler handler = new HandlePosition();
	
	public static IHandler getInstance(){
		return handler;
	}
	
	@Override
	//[cmd,water,imsi,state,time,lng,lat,height,accuracy,speed,direction,provider]
	public void receive(Channel channel, String[] msg) {
		log.info("接收定位信息:" + Arrays.toString(msg));

		//通用应答
		this.response(channel, msg[1], msg[0]);
	}

	@Override
	public void send(String imsi, String[] msg) {
	}

}
