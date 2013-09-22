package org.aigps.wq.service;

import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;

import org.aigps.wq.xmlmodel.Picture;

import com.thoughtworks.xstream.core.util.Base64Encoder;

public class FormAnalyse {
	
	public static Picture analysePicture(HttpPostRequestDecoder decoder) throws Exception{
		Base64Encoder base64 = new Base64Encoder();
		
		Picture pic = new Picture();
		pic.setName((String)getValue(decoder, "fileName"));
		String desc = (String)getValue(decoder, "fileDesc");
		if (desc != null) {
			desc = base64.encode(URLDecoder.decode(desc).getBytes());
		}
		pic.setDesc(desc);
		String time = (String)getValue(decoder, "uploadTime");
		if(time != null){
			time = time.replaceAll("[-| |:]", "");
			while(time.length()<14){
				time += "0";
			}
		}
		pic.setTime(time);
		pic.setMsid((String)getValue(decoder, "msid"));
		
		File file = (File)getValue(decoder, "picFile");
		InputStream is = new FileInputStream(file);
		byte[] bytes = new byte[is.available()];
		is.read(bytes);
		pic.setData(base64.encode(bytes));
		
		is.close();
		decoder.cleanFiles();
		
		return pic;
	}
	
	private static Object getValue(HttpPostRequestDecoder decoder, String key) throws Exception{
		InterfaceHttpData ihd = decoder.getBodyHttpData(key);
		
		if(ihd == null){
			return null;
		}
		
		if (ihd.getHttpDataType() == HttpDataType.Attribute) {
			return ((Attribute) ihd).getValue();
		} else if (ihd.getHttpDataType() == HttpDataType.FileUpload) {
			FileUpload fileUpload = (FileUpload) ihd;
			if (fileUpload.isCompleted()) {
				return fileUpload.getFile();
			} else {//文件未上传完成
				return null;
			}
		}
		return null;
	}
}
