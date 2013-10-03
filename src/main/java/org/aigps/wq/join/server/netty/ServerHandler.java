package org.aigps.wq.join.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.aigps.wq.join.client.handler.HandleActive;
import org.aigps.wq.join.client.handler.HandleKeepAlive;
import org.aigps.wq.join.client.handler.HandlePosition;
import org.aigps.wq.join.client.handler.HandleResponse;
import org.aigps.wq.join.client.handler.HandleSetting;
import org.aigps.wq.join.client.handler.HandleSign;
import org.aigps.wq.join.client.handler.HandleStateGps;
import org.aigps.wq.join.client.handler.HandleStateNet;
import org.aigps.wq.join.client.handler.HandleStatePhone;
import org.aigps.wq.join.client.handler.IHandler;
import org.aigps.wq.join.common.ChannelUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerHandler extends ChannelInboundHandlerAdapter {
	
	private static final Log log = LogFactory.getLog(ServerHandler.class);
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	log.error("收到手机服务端链接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	ChannelUtil.removeDevice(ctx.channel());
    	ctx.close();
    	log.error("手机服务端关闭链接");
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if(!(msg instanceof String)){
    		return;
    	}
    	// [cmd,imsi,...]
    	String[] items = ((String)msg).split("\\|");
		ChannelUtil.setChannel(items[1], ctx.channel());
		
		IHandler handler = getHandler(items[0]);
		if (handler != null) {
			handler.receive(ctx.channel(), items);
		}
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


	private static IHandler getHandler(String cmd) {
		if (HandleResponse.CMD.equals(cmd)) {//通用应答
			return HandleResponse.getInstance();
		}
		if (HandleActive.CMD.equals(cmd)) {//激活
			return HandleActive.getInstance();
		}
		if (HandleSetting.CMD.equals(cmd)) {//设置
			return HandleSetting.getInstance();
		}
		if (HandleKeepAlive.CMD.equals(cmd)) {//心跳
			return HandleKeepAlive.getInstance();
		}
		if (HandlePosition.CMD.equals(cmd)) {//定位
			return HandlePosition.getInstance();
		}
		if (HandleSign.CMD.equals(cmd)) {//签到
			return HandleSign.getInstance();
		}
		if (HandleStatePhone.CMD.equals(cmd)) {//手机开关机
			return HandleStatePhone.getInstance();
		}
		if (HandleStateNet.CMD.equals(cmd)) {//手机开关网络
			return HandleStateNet.getInstance();
		}
		if (HandleStateGps.CMD.equals(cmd)) {//手机开关GPS
			return HandleStateGps.getInstance();
		}
		
		return null;
	}
}
