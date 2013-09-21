package org.aigps.wq.mq;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date：  2012-12-5上午11:01:08
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州元码科技有限公司
 */
public class VmMQFactory {
	private static BrokerService brokerService;

    private static transient ConnectionFactory factory;
    
    public static String url;
    
	public static String getUrl() {
		return url;
	}

	public  void setUrl(String url) {
		VmMQFactory.url = url;
	}
    /**
	 * 
	 */
	public VmMQFactory(String url)throws Exception {
		setUrl(url);
		brokerService = new BrokerService();
        //configure the broker to use the Memory Store
		brokerService.setPersistent(false);
		brokerService.getSystemUsage().getMemoryUsage().setLimit(10*1024*1024);
		brokerService.setBrokerName("gpsAdapter");  
        //Add a transport connector
		brokerService.addConnector(getUrl());
        //now start the broker
		brokerService.start();
    	factory = new ActiveMQConnectionFactory("vm://gpsAdapter?jms.useAsyncSend=true");
	}

	/**
	 * @return the brokerService
	 */
	public static BrokerService getBrokerService() {
		return brokerService;
	}

	/**
	 * @param brokerService the brokerService to set
	 */
	public static void setBrokerService(BrokerService brokerService) {
		VmMQFactory.brokerService = brokerService;
	}

	/**
	 * @return the factory
	 */
	public static ConnectionFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory the factory to set
	 */
	public static void setFactory(ConnectionFactory factory) {
		VmMQFactory.factory = factory;
	}
	
	

}
