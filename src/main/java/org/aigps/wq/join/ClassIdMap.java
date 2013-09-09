
package org.aigps.wq.join;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2012-3-30下午01:58:54
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
public class ClassIdMap {
	private static Properties props = new Properties();

	@SuppressWarnings("rawtypes")
	public static void startup(){
		Class cls = ClassIdMap.class.getClass();
		InputStream is = cls.getResourceAsStream("/classid.properties");
		try{
	        props.load(is);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getClassId(String mobileType){
		String value = props.getProperty("classid."+mobileType);
		if(value == null){
			return props.getProperty("classid.0");
		}
		return value;
	}
}

