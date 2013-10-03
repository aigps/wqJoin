package org.aigps.wq;

import org.aigps.wq.join.WqJoinHttpService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.netty4.server.HttpServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date：  2012-3-2上午10:58:38
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州元码科技有限公司
 */
public class WqJoinContext {
	private static final Log log = LogFactory.getLog(WqJoinContext.class);
	
	

	public static ApplicationContext context ;
	
	
	public static <T> T getBean(String beanName,Class<T> clazz)throws Exception{
		return context.getBean(beanName, clazz);
	}
	
	public static void main(String[] args) {
		try {
			context = new ClassPathXmlApplicationContext(new String[]{"wqJoinContext.xml"});
			WqConfig wqConfig = getBean("wqConfig", WqConfig.class);
			HttpServer httpServer = new HttpServer(wqConfig.getHttpIp(), wqConfig.getHttpPort(), getBean("wqJoinHttpService", WqJoinHttpService.class));
			log.error("  start wq join  success!");
			while(true){
				Thread.sleep(30*1000);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
	}

}
