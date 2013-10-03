package org.aigps.wq.join.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import org.aigps.wq.join.common.ChannelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class IHandler {
	private static final Log log = LogFactory.getLog("IHandler");

	//接收到手机发来的消息
	public abstract void receive(Channel channel, String[] msg);

	//向手机发送消息
	//msg:[water...]
	public abstract void send(String imsi, String[] msg);
	
	//向手机发送消息
	//msg:[water...]
	protected void send(String imsi, String cmd, String[] msg){
		String msgs = "&"+cmd+"|"+StringUtils.join(msg,"|")+"&";
		try {
			ChannelUtil.sendMsg(imsi, msgs);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	//以通用应答回复
	protected void response(Channel channel, String water, String cmd) {
		String msg = "&00|"+water+"|"+cmd+"&";
		log.error("回复通用应答：" + msg);
		try {
			ByteBuf buf = channel.alloc().buffer();
			buf.writeBytes(msg.getBytes());
			channel.writeAndFlush(buf);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
