/**
 * 
 */
package org.aigps.wq.task.job;

import org.aigps.wq.dao.GpsDataDao;
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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Administrator
 *
 */
public class DbGpsDayMileJob implements Job {
	private static final Log log = LogFactory.getLog(DbGpsDayMileJob.class);
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="DbGpsDayMileJob";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			try {
				String today = SystemCache.getSysDate();
				GpsDataDao.saveCurrMile(today);
				GpsDataDao.saveDayMile(today);
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
//		org.apache.log4j.spi.RootLogger.getRootLogger().setLevel(Level.ERROR);
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
			String today = ParseDate.getSystemFormatTime(ParseDate.DATE_FORMAT_STR);
			GpsDataDao.saveCurrMile(today);
			GpsDataDao.saveDayMile(today);
		} catch (BeansException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
