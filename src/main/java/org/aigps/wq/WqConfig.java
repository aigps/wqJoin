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
	
	
	
	
	

}
