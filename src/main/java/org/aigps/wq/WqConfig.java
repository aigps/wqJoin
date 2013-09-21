package org.aigps.wq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WqConfig {
	
	@Value("${tmn.sys.id.rela.sql}")
	private String tmnSysIdSql;

	public String getTmnSysIdSql() {
		return tmnSysIdSql;
	}

	public void setTmnSysIdSql(String tmnSysIdSql) {
		this.tmnSysIdSql = tmnSysIdSql;
	}
	@Value("${http.server.port}")
	private int httpPort;

	public int getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}
	
	@Value("${http.server.ip}")
	private String httpIp;

	public String getHttpIp() {
		return httpIp;
	}

	public void setHttpIp(String httpIp) {
		this.httpIp = httpIp;
	}
	
	
	

}
