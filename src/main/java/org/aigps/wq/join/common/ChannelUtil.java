package org.aigps.wq.join.common;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
* @Title：远程会话终端缓存
* @Description：<类描述>
*
* @author ccq
* @version 1.0
*
* Create Date：  2012-10-23上午10:00:52
* Modified By：  <修改人中文名或拼音缩写>
* Modified Date：<修改日期，格式:YYYY-MM-DD>
*
* Copyright：Copyright(C),1995-2012 浙IPC备09004804号
* Company：杭州中导科技有限公司
 */
public class ChannelUtil {
	public final static Log log = LogFactory.getLog(ChannelUtil.class);

	// <终端号,Device>
	private static Map<String, Device> deviceMap = new ConcurrentHashMap<String, Device>();
	
	/**
	 * 设置更新远程终端会话，如果缓存中没有则创建终端会话
	 * @param deviceId
	 * @param tcpChannel
	 * @throws Exception
	 */
	public static void setChannel(String deviceId,Channel channel)throws Exception{
		Device device = deviceMap.get(deviceId);
		if (device != null) {
			device.setChannel(channel);
			device.setLastTime(System.currentTimeMillis());
		} else {
			deviceMap.put(deviceId, new Device(deviceId, channel));
		}
	}

	/**
	 * 发送数据
	 */
	public static void sendMsg(String deviceId, String msg) throws Exception{
		Device device = deviceMap.get(deviceId);
		if(device != null){
			device.addMsg(msg);
		}
	}
	
	/**
	 * 根据连接通道，返回终端号
	 * @param tcpChannel
	 * @return
	 * @throws Exception
	 */
	public static String getDeviceId(Channel channel)throws Exception{
		for(Device device : deviceMap.values()){
			if(channel == device.getChannel()){
				return device.getDeviceId();
			}
		}
		return null;
	}
	
	public static void removeDevice(Channel channel)throws Exception{
		for(Device device : deviceMap.values()){
			if(channel == device.getChannel()){
				deviceMap.remove(device.getDeviceId());
			}
		}
	}
	
	/**
	 * 获取所有终端会话缓存
	 * @return
	 */
	public static Map<String, Device> getDeviceMap(){
		return deviceMap;
	}
	

}
