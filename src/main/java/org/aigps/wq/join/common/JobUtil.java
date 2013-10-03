package org.aigps.wq.join.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.Scheduler;

public class JobUtil {
private static final Log log = LogFactory.getLog(JobUtil.class);
	
	public static abstract class Callback {
		public abstract void execute() throws Exception ;
	}
	
	public static void start(String name, Thread thread) {
		thread.setDaemon(true);
		if(name != null) {
			thread.setName(name);
		}
		thread.start();
	}
	
	public static void interval(final double second, String name, final Callback callback) {
		start(name, new Thread(){
			public void run() {
				while(true) {
					try {
						Thread.sleep((int)(second * 1000));
						callback.execute();
					} catch (Throwable e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		});
	}
	
	public static void interval(final double second, String name, final Runnable callback) {
		start(name, new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.sleep((int)(second * 1000));
						callback.run();
					} catch (Throwable e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		});
	}
//
//	public static void addCronJob(Scheduler sched,Class<? extends Job> jobClass, String cron) throws Exception{
//		String jobName = jobClass.getSimpleName();
//		String group = jobName + "Group";
//		String triggerName = jobName + "Trigger";
//		
//		JobDetailImpl job = new JobDetailImpl();
//		job.setJobClass(jobClass);
//		job.setName(jobName);
//		job.setGroup(group);
//
//		CronTriggerImpl trigger = new CronTriggerImpl();
//		trigger.setName(triggerName);
//		trigger.setGroup(group);
//		trigger.setCronExpression(cron);
//
//		sched.scheduleJob(job, trigger);
//	}

}
