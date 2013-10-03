package org.aigps.wq.join.common;

import org.springframework.beans.factory.annotation.Value;

public class Cfg {

	// ¼àÌý¶Ë¿Ú
	public static Integer tcpClientPort;
	public static Integer tcpServerPort;
	public static Integer socketTimeout;

	@Value("${tcp.client.port}")
	public void setTcpClientPort(Integer tcpClientPort) {
		Cfg.tcpClientPort = tcpClientPort;
	}

	@Value("${tcp.server.port}")
	public void setTcpServerPort(Integer tcpServerPort) {
		Cfg.tcpServerPort = tcpServerPort;
	}

	@Value("${socket.timeout}")
	public void setSocketTimeout(Integer socketTimeout) {
		Cfg.socketTimeout = socketTimeout;
	}

}
