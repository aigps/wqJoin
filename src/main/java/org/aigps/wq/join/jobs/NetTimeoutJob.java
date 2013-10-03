package org.aigps.wq.join.jobs;

import org.aigps.wq.join.common.Cfg;
import org.aigps.wq.join.common.ChannelUtil;
import org.aigps.wq.join.common.Device;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetTimeoutJob implements Runnable{
	private final static Log log = LogFactory.getLog(NetTimeoutJob.class);
	
	public void run() {
		long now = System.currentTimeMillis();
		try {
			for(Device device : ChannelUtil.getDeviceMap().values()) {
				if(now - device.getLastTime() > Cfg.socketTimeout) {
					log.error("³¬Ê±ÏÂÏß£º"+device.getDeviceId());
					device.getChannel().close();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
