package org.aigps.wq.ibatis;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ibatis.sqlmap.client.SqlMapClient;

@Component
public class GpsDataIbatis {
	private static final Log log = LogFactory.getLog(GpsDataIbatis.class);
	
	@Autowired
	private static SqlMapClient sqlMapClient;
	public static SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}
	
	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		GpsDataIbatis.sqlMapClient = sqlMapClient;
	}
	

	
	public static void main(String[] args){
    	try {
//    		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"dbContext.xml"});
////    		ConfigConstants configConstants = (ConfigConstants)ctx.getBean("configConstants");
//    		GpsDataIbatis cacheContext = (GpsDataIbatis)ctx.getBean("IbatisContext");
//    		while(true){
//    			Thread.currentThread().sleep(10*1000);
//    		}
    	} catch (Exception e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}	
	}
}
