package org.aigps.wq.join.startup;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContext {

	private static ApplicationContext ctx = null;

	public static void startup(){
		ctx = new ClassPathXmlApplicationContext("org/aigps/wq/join/applicationContext.xml");
	}
	
	public static <T>T getBean(String bean,Class<T> clazz){
		return ctx.getBean(bean,clazz);
	}

}
