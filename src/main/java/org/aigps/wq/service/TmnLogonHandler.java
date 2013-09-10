package org.aigps.wq.service;

import java.net.SocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.model.ProtocolTypeEnum;
import org.gps.protocol.model.TmnClient;
import org.gps.protocol.model.TmnClientContainer;
import org.jboss.netty.channel.Channel;

/**
 * �ն˵�¼
 * @author Administrator
 *
 */
public class TmnLogonHandler {
	private static final Log log = LogFactory.getLog(TmnLogonHandler.class);
	
	/**
	 * �������ն�
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
	 * �ն����ߵ�¼
	 * @param channel
	 * @param address
	 * @param tmnCode
	 * @param isTcp
	 * @throws Exception
	 */
	public static TmnClient logon(Channel channel, SocketAddress address,
			String tmnCode,boolean isTcp,ProtocolTypeEnum protocol) throws Exception {
		TmnClient tmnClient = TmnClientContainer.getClientByTmnCode(tmnCode);
		if(tmnClient!=null){//�ն��Ѿ���¼��
			if(tmnClient.getChannel()!=null){
				//�����ͬһ���Ự�������κδ���
				if(tmnClient.getChannel().equals(channel)){
					
				}else{//����ͬһ���Ự
					if(!tmnClient.isTcp() == isTcp){//һ��TCP��һ����UDP
						if(isTcp){//�����ǰ��TCP
							tmnClient.refresh(channel, address, isTcp, protocol);
						}else{//��ǰ��UDp
						}
					}else{//ͬ�������ӷ�ʽ��
						//����һ��
						tmnClient.refresh(channel, address, isTcp, protocol);
					}
				}
			}
		}else{//�ն�δ��¼��
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
