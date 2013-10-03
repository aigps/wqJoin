package org.aigps.wq.join;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.util.CharsetUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aigps.wq.service.GpsService;
import org.aigps.wq.service.RspService;
import org.aigps.wq.service.SmsService;
import org.aigps.wq.service.SttsRptService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.netty4.server.http.IHttpService;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;

/**
 * @Title：http 接收  手机上报的数据。
 * @Description：<类描述>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date：  2012-3-2上午11:27:44
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州元码科技有限公司
 */
@Component
public class WqJoinHttpService  implements IHttpService{
	protected static final Log log = LogFactory.getLog(WqJoinHttpService.class);
	
	HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
	static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }
	
	public String execute(ChannelHandlerContext ctx, Object request) throws Exception {
		if(request instanceof HttpContent){
			HttpContent httpContent = (HttpContent) request;
			ByteBuf content  = httpContent.content();
			String xmlString = convertXmlToLowerCase(content.toString(CharsetUtil.UTF_8));
			XStream xstream = new XStream();
			try{
				if(xmlString.indexOf("<lia>") != -1){//定位信息回复
					xmlString = GpsService.execute(xmlString, xstream);
				}else if(xmlString.indexOf("<lta>") != -1){//立即应答消息
					xmlString = RspService.execute(xmlString, xstream);
				}else if(xmlString.indexOf("<status>")!=-1){//手机状态消息
					xmlString = SttsRptService.execute(xmlString, xstream);
				}else if(xmlString.indexOf("<messages>")!=-1){//上报短消息
					xmlString = SmsService.execute(xmlString, xstream);
				}else if(xmlString.indexOf("<net_test>")!=-1){//测试网络是否连接
					log.error("测试网络连接:\n" + xmlString);
				}else{
					log.error("未知结构XML:\n" + xmlString);
				}
			}catch(Exception e){
				log.error("异常XML:\n" + xmlString);
				log.error(e.getMessage(),e);
			}
			return xmlString;
		}
		return null;
	}

	
	//将XML里的<XXXX>标签转成小写
	private static String convertXmlToLowerCase(String xml){
		Pattern pattern = Pattern.compile("<.+?>");
		StringBuilder sb = new StringBuilder();
		int lastIdx = 0;
		Matcher matcher = pattern.matcher(xml);
		while (matcher.find()) {
			String str = matcher.group();
			sb.append(xml.substring(lastIdx, matcher.start()));
			sb.append(str.toLowerCase());
			lastIdx = matcher.end();
		}
		return sb.append(xml.substring(lastIdx)).toString();
	}

	public static String getEndTime(String startTime,int interval,int times) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("HHmmss");
		SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
		long start = format.parse(startTime).getTime();
		long end = start + interval*times*1000;
		return format1.format(new Date(end));
	}
}
