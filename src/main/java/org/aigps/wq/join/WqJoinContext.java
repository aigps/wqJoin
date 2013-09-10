package org.aigps.wq.join;

import org.aigps.wq.join.WqReceiveServerMsgHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.net.client.ym.YmNettyClient;
import org.gps.net.client.ym.YmRecMsgPool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-3-2����10:58:38
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company������Ԫ��Ƽ����޹�˾
 */
public class WqJoinContext {
	private static final Log log = LogFactory.getLog(WqJoinContext.class);
	
	private static YmNettyClient ymNettyClient;
	
	
	public static YmNettyClient getYmNettyClient() {
		return ymNettyClient;
	}


	public static void setYmNettyClient(YmNettyClient ymNettyClient) {
		WqJoinContext.ymNettyClient = ymNettyClient;
	}


	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		ClassIdMap.startup();
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"wqJoinContext.xml"});
			YmNettyClient ymNettyClient = context.getBean("ymClient",YmNettyClient.class);
			setYmNettyClient(ymNettyClient);
			ymNettyClient.connect();
			WqReceiveServerMsgHandler observer = new WqReceiveServerMsgHandler();
			YmRecMsgPool.getInstrance(ymNettyClient).addObserver(observer);
			while(true){
				Thread.currentThread().sleep(30*1000);
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

}
