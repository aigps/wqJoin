/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.DcGpsCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 终端与业务ID的对照更新
 * @author Administrator
 *
 */
public class SystemRefreshJob implements Job {
	private static final Log log = LogFactory.getLog(SystemRefreshJob.class);
	public static final String ID="TmnSysIdRefreshJob";
	private static AtomicBoolean isRunning = new AtomicBoolean();//同一个时间点，只允许一个job跑数
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){
			try {
				DcGpsCache.refresh();
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning.set(false);;
			}
		}
	}
}
