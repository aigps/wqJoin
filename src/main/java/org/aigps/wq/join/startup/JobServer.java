package org.aigps.wq.join.startup;

import org.aigps.wq.join.common.JobUtil;
import org.aigps.wq.join.jobs.NetTimeoutJob;
import org.aigps.wq.join.jobs.SendMsgJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;

public class JobServer {
	private final static Log log = LogFactory.getLog(JobServer.class);

	private static Scheduler sched;

	public static void startup() {
		try {
//			Properties jobCfg = getCfg();
//			sched = new StdSchedulerFactory().getScheduler();
//
//			sched.start();

			// 发送终端消息的任务
			JobUtil.interval(0.1, "SendMsgJob", new SendMsgJob());

			// 启动超时链接清理
			JobUtil.interval(10, "NetTimeoutJob", new NetTimeoutJob());

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

//	private static Properties getCfg() throws Exception {
//		ClassPathResource resource = new ClassPathResource("job.properties");
//		Properties jobCfg = new Properties();
//		jobCfg.load(resource.getInputStream());
//		return jobCfg;
//	}

	public static void destroy() {
		try {
//			sched.clear();
//			sched.shutdown(true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
