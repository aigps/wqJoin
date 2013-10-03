package org.aigps.wq.join;

import org.aigps.wq.join.client.netty.ClientStartServer;
import org.aigps.wq.join.common.Cfg;
import org.aigps.wq.join.server.netty.ServerStartServer;
import org.aigps.wq.join.startup.JobServer;
import org.aigps.wq.join.startup.SpringContext;
import org.gps.map.offset.OffSetCache2;



public class JoinMain {

	public static void main(String[] args) throws Exception{
		try {
			// 加载spring上下文
			SpringContext.startup();

			// 加载偏移数据
			OffSetCache2.loadData();

			// 启动系统JOB
			JobServer.startup();
			
			// 程序关闭监听
			setShutdownListener();
			
			//启动手机客户端监听端口
			new ClientStartServer(Cfg.tcpClientPort);

			//启动手机服务端监听端口
			new ServerStartServer(Cfg.tcpServerPort);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	// 程序关闭时
	private static void setShutdownListener() {
		Thread thread = new Thread() {
			public void run() {
				JobServer.destroy();
			}
		};
		thread.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(thread);
	}
	
}
