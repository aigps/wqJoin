package org.aigps.wq.join.common;

import io.netty.channel.Channel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Title：远程会话终端
 * @Description：<类描述>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date：  2012-10-23上午09:02:59
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技有限公司
 */
public class Device {
	private static final Log log = LogFactory.getLog(Device.class);
	
	private String deviceId;
	private Channel channel;
	private Long lastTime;
	private AtomicBoolean isSending = new AtomicBoolean(false);
	private Queue<String> msgs = new ConcurrentLinkedQueue<String>();
	
	public Device(String deviceId,Channel channel) {
		this.deviceId = deviceId;
		this.channel = channel;
		this.lastTime = System.currentTimeMillis();
	}
	
	public void addMsg(String msg)throws Exception{
		msgs.add(msg);
	}
	
	/**
	 * 发送给远程终端
	 * @throws Exception
	 */
	public void sendMsg(){
		if(!msgs.isEmpty() && isSending.compareAndSet(false, true)){
			try {
				while(!msgs.isEmpty() && channel!=null && channel.isWritable()){
					channel.writeAndFlush(msgs.poll());
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			} finally {
				isSending.set(false);
			}
		}
	}
	
	public boolean isSendingMsg(){
		return isSending.get();
	}
	
	public boolean msgIsEmpty(){
		return msgs.isEmpty();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Long getLastTime() {
		return lastTime;
	}

	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}

}
