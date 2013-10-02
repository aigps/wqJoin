/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.DcCmdTrace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Administrator
 *
 */
public class DbCmdTraceSaveJob implements Job {
	private static final Log log = LogFactory.getLog(DbCmdTraceSaveJob.class);
	
	private static AtomicBoolean isRunning = new AtomicBoolean();//同一个时间点，只允许一个job跑数
	public static final String ID="DbCmdTraceSaveJob";
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){
			try {
				GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
				Queue<DcCmdTrace> newCmdList = DcGpsCache.inCreCmdTrace;
				gpsDataDao.saveDcCmdTrace(newCmdList);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning.set(false);
			}
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		try {
//			Logger.getRootLogger().setLevel(Level.INFO); 
//			DailyRollingFileAppender fileAppender = 
//				  new DailyRollingFileAppender(
//				    new PatternLayout("%d{yyyy-M-d HH:mm:ss}%x[%5p](%F:%L) %m%n"),
//				   "./log/gpsData.log","'.'yyyy-MM-dd");
//			fileAppender.setThreshold(Priority.ERROR);
//			Logger.getRootLogger().addAppender(fileAppender);
//			ConsoleAppender consoleAppender = 
//				  new ConsoleAppender(
//				    new PatternLayout("%d{yyyy-M-d HH:mm:ss}%x[%5p](%F:%L) %m%n")); 
//			Logger.getRootLogger().addAppender(consoleAppender);
//			consoleAppender.setThreshold(Priority.INFO);
//			ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"gpsDataDbContext.xml"});
//			YmAccessMsg ymMsg = new YmAccessMsg("*00045,0,1,CMD,<0910154354|SL>,<0|GetPos|>,F#".getBytes());
//			DcCmdTrace dcCmdTrace = new DcCmdTrace(ymMsg.getDeviceCode(),"",
//					DcCmdTrace.ACTION_SEND,ParseDate.getSysSecTime()," ",ymMsg);
//			List<DcCmdTrace> list = new ArrayList<DcCmdTrace>();
//			list.add(dcCmdTrace);
//			GpsDataDao.saveDcCmdTrace(list);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
		

	}

}
