package org.aigps.wq.join.jobs;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.aigps.wq.join.common.ChannelUtil;
import org.aigps.wq.join.common.Device;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SendMsgJob implements Runnable{
	
	private static final Log log = LogFactory.getLog(SendMsgJob.class);
	private ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 5, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(500000));
	
	public void run() {
		for(final Device device : ChannelUtil.getDeviceMap().values()){
			try {
				if(device != null && !device.isSendingMsg() && !device.msgIsEmpty()){
					pool.execute(new Runnable() {
						public void run() {
							device.sendMsg();
						}
					});
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		
		long notRunTask = pool.getTaskCount()-pool.getCompletedTaskCount();
		if(notRunTask > 3000){
			log.error("有太多消息需要发送，请留意!!!  count=" +notRunTask);
		}
	}
}

