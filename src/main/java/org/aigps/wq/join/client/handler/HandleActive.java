package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandleActive extends IHandler{
	private static final Log log = LogFactory.getLog("HandleActive");
	
	public static String CMD = "10";
	private static IHandler handler = new HandleActive();
	
	public static IHandler getInstance(){
		return handler;
	}
	
	@Override
	public void receive(Channel channel, String[] msg) {
		//[cmd,water,imsi,phone]
		log.info("接收激活信息:" + Arrays.toString(msg));
	}
	
	@Override
	//msg: [water,ip,port]
	public void send(String imsi, String[] msg) {
		log.info("下发激活指令:" + imsi + ":" + Arrays.toString(msg));
		super.send(imsi, CMD, msg);
	}

}
