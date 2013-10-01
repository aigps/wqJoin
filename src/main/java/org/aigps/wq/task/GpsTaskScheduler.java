/**
 * 
 */
package org.aigps.wq.task;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.aigps.wq.task.job.DbCmdTraceSaveJob;
import org.aigps.wq.task.job.DbGpsHisSaveJob;
import org.aigps.wq.task.job.DbGpsRealSaveJob;
import org.aigps.wq.task.job.DbGpsZcodeSaveJob;
import org.aigps.wq.task.job.ParseGpsLocDescJob;
import org.aigps.wq.task.job.SystemRefreshJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 数据中心任务调度器
 * @author Administrator
 *
 */
public class GpsTaskScheduler {
	private static final Log log = LogFactory.getLog(GpsTaskScheduler.class);
	private static Scheduler sched;
	private static Properties config;
	
	
	 /**
     * 启动任务计划
     */
	private static void init() {
		InputStream in = null;
        try {
            // Grab the Scheduler instance from the Factory 
        	in = GpsTaskScheduler.class.getResourceAsStream("gpsDataTask.properties");
        	config = new Properties();
        	config.load(in);
        	StdSchedulerFactory factory = new StdSchedulerFactory(config); 
            sched = factory.getScheduler();
        } catch (Exception se) {
            se.printStackTrace();
        }finally{
        	try {
				if(in!=null){
					in.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
        }
	}
	
	public GpsTaskScheduler(){
		try {
			init();
			Date ft;
			/**
			 * 历史定位入数据库
			 */
			if("true".equalsIgnoreCase(config.getProperty("dbGpsHisSaveJob"))){
				JobDetail dbGpsHisSaveJob = new JobDetail(DbGpsHisSaveJob.ID,sched.getSchedulerName(),DbGpsHisSaveJob.class);
				CronTrigger dbGpsHisSaveJobTrigger = new CronTrigger("dbGpsHisSaveJobTrigger", sched.getSchedulerName(), DbGpsHisSaveJob.ID, sched.getSchedulerName(),config.getProperty("dbGpsHisSaveJobTrigger"));
				sched.addJob(dbGpsHisSaveJob, true);
				ft = sched.scheduleJob(dbGpsHisSaveJobTrigger);
				log.info(dbGpsHisSaveJob.getFullName() + " has been scheduled to run at: " + ft
			        + " and repeat based on expression: "
			        + dbGpsHisSaveJobTrigger.getCronExpression());
			}
			/**
			 * 实时定位入数据库
			 */
			if("true".equalsIgnoreCase(config.getProperty("dbGpsRealSaveJob"))){
				JobDetail dbGpsRealSaveJob = new JobDetail(DbGpsRealSaveJob.ID,sched.getSchedulerName(),DbGpsRealSaveJob.class);
				CronTrigger dbGpsRealSaveJobTrigger = new CronTrigger("dbGpsRealSaveJobTrigger", sched.getSchedulerName(), DbGpsRealSaveJob.ID, sched.getSchedulerName(),config.getProperty("dbGpsRealSaveJobTrigger"));
				sched.addJob(dbGpsRealSaveJob, true);
				ft = sched.scheduleJob(dbGpsRealSaveJobTrigger);
				log.info(dbGpsRealSaveJob.getFullName() + " has been scheduled to run at: " + ft
			        + " and repeat based on expression: "
			        + dbGpsRealSaveJobTrigger.getCronExpression());
			}
			/**
			 * 批量更新车辆的地理位置
			 */
			if("true".equalsIgnoreCase(config.getProperty("parseGpsLocDescJob"))){
				JobDetail parseGpsLocDescJob = new JobDetail(ParseGpsLocDescJob.ID,sched.getSchedulerName(),ParseGpsLocDescJob.class);
				CronTrigger parseGpsLocDescJobTrigger = new CronTrigger("parseGpsLocDescJobTrigger", sched.getSchedulerName(), ParseGpsLocDescJob.ID, sched.getSchedulerName(),config.getProperty("parseGpsLocDescJobTrigger"));
				sched.addJob(parseGpsLocDescJob, true);
				ft = sched.scheduleJob(parseGpsLocDescJobTrigger);
				log.info(parseGpsLocDescJob.getFullName() + " has been scheduled to run at: " + ft
		        + " and repeat based on expression: "
		        + parseGpsLocDescJobTrigger.getCronExpression());
			}
			/**
			 * 从缓存取增量GPS行政区域，并保存到数据库
			 */
			if("true".equalsIgnoreCase(config.getProperty("dbGpsZcodeSaveJob"))){
				JobDetail dbGpsZcodeSaveJob = new JobDetail(DbGpsZcodeSaveJob.ID,sched.getSchedulerName(),DbGpsZcodeSaveJob.class);
				CronTrigger dbGpsZcodeSaveJobTrigger = new CronTrigger("dbGpsZcodeSaveJobTrigger", sched.getSchedulerName(), DbGpsZcodeSaveJob.ID, sched.getSchedulerName(),config.getProperty("dbGpsZcodeSaveJobTrigger"));
				sched.addJob(dbGpsZcodeSaveJob, true);
				ft = sched.scheduleJob(dbGpsZcodeSaveJobTrigger);
				log.info(dbGpsZcodeSaveJob.getFullName() + " has been scheduled to run at: " + ft
		        + " and repeat based on expression: "
		        + dbGpsZcodeSaveJobTrigger.getCronExpression());
			}
			/**
			 * 批量更新车辆的地理位置
			 */
			if("true".equalsIgnoreCase(config.getProperty("systemRefreshJob"))){
				JobDetail systemRefreshJob = new JobDetail(SystemRefreshJob.ID,sched.getSchedulerName(),SystemRefreshJob.class);
				CronTrigger systemRefreshJobTrigger = new CronTrigger("systemRefreshJobTrigger", sched.getSchedulerName(), SystemRefreshJob.ID, sched.getSchedulerName(),config.getProperty("systemRefreshJobTrigger"));
				sched.addJob(systemRefreshJob, true);
				ft = sched.scheduleJob(systemRefreshJobTrigger);
				log.info(systemRefreshJob.getFullName() + " has been scheduled to run at: " + ft
		        + " and repeat based on expression: "
		        + systemRefreshJobTrigger.getCronExpression());
			}
			/**
			 * 过往指令入库
			 */
			if("true".equalsIgnoreCase(config.getProperty("dbCmdTraceSaveJob"))){
				JobDetail dbCmdTraceSaveJob = new JobDetail(DbCmdTraceSaveJob.ID,sched.getSchedulerName(),DbCmdTraceSaveJob.class);
				CronTrigger dbCmdTraceSaveJobTrigger = new CronTrigger("dbCmdTraceSaveJobTrigger", sched.getSchedulerName(), DbCmdTraceSaveJob.ID, sched.getSchedulerName(),config.getProperty("dbCmdTraceSaveJobTrigger"));
				sched.addJob(dbCmdTraceSaveJob, true);
				ft = sched.scheduleJob(dbCmdTraceSaveJobTrigger);
				log.info(dbCmdTraceSaveJob.getFullName() + " has been scheduled to run at: " + ft
		        + " and repeat based on expression: "
		        + dbCmdTraceSaveJobTrigger.getCronExpression());
			}
			sched.start();
		} catch (Exception e) {
			log.error("",e);
		}
	}
	public void close()throws Exception{
		if(sched!=null){
			sched.shutdown();
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
