
package org.aigps.wq.join;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2012-3-30����01:58:54
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
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

