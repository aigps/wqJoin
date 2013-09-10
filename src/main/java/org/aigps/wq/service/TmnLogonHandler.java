package org.aigps.wq.service;

import java.net.SocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.model.ProtocolTypeEnum;
import org.gps.protocol.model.TmnClient;
import org.gps.protocol.model.TmnClientContainer;
import org.jboss.netty.channel.Channel;

/**
 * 终端登录
 * @author Administrator
 *
 */
public class TmnLogonHandler {
	private static final Log log = LogFactory.getLog(TmnLogonHandler.class);
	
	/**
	 * 新上线终端
	 * @param channel
	 * @param address
	 * @param vehicleInfo
	 * @param vehicleCode
	 * @throws Exception
	 */
	private static TmnClient online(String tmnCode,Channel channel, SocketAddress address,boolean isTcp,ProtocolTypeEnum protocol) throws Exception {
		TmnClient tmnClient = new TmnClient(tmnCode, channel, address, isTcp, protocol);
		return tmnClient;
	}
	
	/**
	 * 终端上线登录
	 * @param channel
	 * @param address
	 * @param tmnCode
	 * @param isTcp
	 * @throws Exception
	 */
	public static TmnClient logon(Channel channel, SocketAddress address,
			String tmnCode,boolean isTcp,ProtocolTypeEnum protocol) throws Exception {
		TmnClient tmnClient = TmnClientContainer.getClientByTmnCode(tmnCode);
		if(tmnClient!=null){//终端已经登录过
			if(tmnClient.getChannel()!=null){
				//如果是同一个会话，不做任何处理
				if(tmnClient.getChannel().equals(channel)){
					
				}else{//不是同一个会话
					if(!tmnClient.isTcp() == isTcp){//一个TCP，一个是UDP
						if(isTcp){//如果当前是TCP
							tmnClient.refresh(channel, address, isTcp, protocol);
						}else{//当前是UDp
						}
					}else{//同样的链接方式，
						//更新一下
						tmnClient.refresh(channel, address, isTcp, protocol);
					}
				}
			}
		}else{//终端未登录过
			tmnClient = online(tmnCode, channel, address, isTcp, protocol);
		}
		return tmnClient;
	}
	
	public static void offLine(TmnClient client)throws Exception{
		if(client!=null){
			String tmnCode = client.getTmnCode();
			TmnClientContainer.removeClient(client);
		}
	}
}
