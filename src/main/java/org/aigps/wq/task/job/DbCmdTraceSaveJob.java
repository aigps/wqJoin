/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.ArrayList;
import java.util.List;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.DcCmdTrace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Administrator
 *
 */
public class DbCmdTraceSaveJob implements Job {
	private static final Log log = LogFactory.getLog(DbCmdTraceSaveJob.class);
	
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="DbCmdTraceSaveJob";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			try {
				List<DcCmdTrace> newCmdList = DcGpsCache.inCreCmdTrace;
				DcGpsCache.inCreCmdTrace = new ArrayList<DcCmdTrace>(10000);
				GpsDataDao.saveDcCmdTrace(newCmdList);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning = false;
			}
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Logger.getRootLogger().setLevel(Level.INFO); 
			DailyRollingFileAppender fileAppender = 
				  new DailyRollingFileAppender(
				    new PatternLayout("%d{yyyy-M-d HH:mm:ss}%x[%5p](%F:%L) %m%n"),
				   "./log/gpsData.log","'.'yyyy-MM-dd");
			fileAppender.setThreshold(Priority.ERROR);
			Logger.getRootLogger().addAppender(fileAppender);
			ConsoleAppender consoleAppender = 
				  new ConsoleAppender(
				    new PatternLayout("%d{yyyy-M-d HH:mm:ss}%x[%5p](%F:%L) %m%n")); 
			Logger.getRootLogger().addAppender(consoleAppender);
			consoleAppender.setThreshold(Priority.INFO);
			ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"gpsDataDbContext.xml"});
			YmAccessMsg ymMsg = new YmAccessMsg("*00045,0,1,CMD,<0910154354|SL>,<0|GetPos|>,F#".getBytes());
			DcCmdTrace dcCmdTrace = new DcCmdTrace(ymMsg.getDeviceCode(),"",
					DcCmdTrace.ACTION_SEND,ParseDate.getSysSecTime()," ",ymMsg);
			List<DcCmdTrace> list = new ArrayList<DcCmdTrace>();
			list.add(dcCmdTrace);
			GpsDataDao.saveDcCmdTrace(list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}

}
