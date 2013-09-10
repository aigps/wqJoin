package org.aigps.wq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
			ClassIdMap.startup();
			context = new ClassPathXmlApplicationContext(new String[]{"joinContext.xml"});
			while(true){
				Thread.sleep(30*1000);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
	}

}
