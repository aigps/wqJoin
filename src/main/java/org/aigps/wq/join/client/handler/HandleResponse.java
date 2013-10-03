package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandleResponse extends IHandler{
	private static final Log log = LogFactory.getLog(HandleResponse.class);
	
	public static String CMD = "00";
	private static IHandler handler = new HandleResponse();
	
	public static IHandler getInstance(){
		return handler;
	}
	
	@Override
	//[CMD,water,imsi,cmd]
	public void receive(Channel channel, String[] msg) {
		log.info("接收通用应答:" + Arrays.toString(msg));
	}

	@Override
	public void send(String imsi, String[] msg) {
	}
	
}
