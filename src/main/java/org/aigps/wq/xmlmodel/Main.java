package org.aigps.wq.xmlmodel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {
		String html = "<?xml version=\"1.0\"?><ANS VER=\"1.0\"><MESSAGES><TYPE>DDDDD</TYPE><MESSAGE>ÐØºÃDdKJDdddkdfKKKKK</MESSAGE><MSIDS><MSID>460030900107481</MSID><MSID_TYPE>2</MSID_TYPE></MSIDS></MESSAGES></ANS>"
				;//.toLowerCase();

		Pattern pattern = Pattern.compile("<.+?>");
		StringBuilder res = new StringBuilder();
		int lastIdx = 0;
		Matcher matchr = pattern.matcher(html);
		while (matchr.find()) {
			String str = matchr.group();
			res.append(html.substring(lastIdx, matchr.start()));
			res.append(str.toLowerCase());
			lastIdx = matchr.end();
		}
		res.append(html.substring(lastIdx));

		System.out.println(res.toString());

		//
		// try{
		// XStream xstream = new XStream();
		// xstream.processAnnotations(MessageModel.class);
		// MessageModel a = (MessageModel) xstream.fromXML(xml);
		// String aa = a.convertToYmData();
		// System.out.println(aa);
		//
		// }catch(Exception e){
		// e.printStackTrace();
		// }
	}
}
