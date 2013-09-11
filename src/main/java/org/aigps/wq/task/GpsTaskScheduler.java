/**
 * 
 */
package org.aigps.wq.task;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.cache.task.job.SysSecondTimeJob;
import org.gps.protocol.task.job.CacheGpsZcodeSaveJob;
import org.gps.protocol.task.job.DbCmdTraceSaveJob;
import org.gps.protocol.task.job.DbGpsDayMileJob;
import org.gps.protocol.task.job.DbGpsHisSaveJob;
import org.gps.protocol.task.job.DbGpsRealSaveJob;
import org.gps.protocol.task.job.DbGpsZcodeSaveJob;
import org.gps.protocol.task.job.ParseGpsLocDescJob;
import org.gps.protocol.task.job.CacheGpsHisSaveJob;
import org.gps.protocol.task.job.CacheGpsRealSaveJob;
import org.gps.protocol.task.job.TmnSysIdRefreshJob;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

/**
 * �����������������
 * @author Administrator
 *
 */
public class GpsTaskScheduler {
	private static final Log log = LogFactory.getLog(GpsTaskScheduler.class);
	private static Scheduler sched;
	private static Properties config;
	
	
	 /**
     * ��������ƻ�
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
			 * ��ʷ��λ�뻺��
			 */
			if("true".equalsIgnoreCase(config.getProperty("cacheGpsHisSaveJob"))){
				JobDetail saveGpsHisJob = new JobDetail(CacheGpsHisSaveJob.ID,sched.getSchedulerName(),CacheGpsHisSaveJob.class);
				CronTrigger saveGpsHisJobTrigger = new CronTrigger("cacheGpsHisSaveJobTrigger", sched.getSchedulerName(), CacheGpsHisSaveJob.ID, sched.getSchedulerName(),config.getProperty("cacheGpsHisSaveJobTrigger"));
				sched.addJob(saveGpsHisJob, true);
				ft = sched.scheduleJob(saveGpsHisJobTrigger);
				log.info(saveGpsHisJob.getFullName() + " has been scheduled to run at: " + ft
			        + " and repeat based on expression: "
			        + saveGpsHisJobTrigger.getCronExpression());
			}
			/**
			 * ��ʷ��λ�����ݿ�
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
			 * ʵʱ��λ�뻺��
			 */
			if("true".equalsIgnoreCase(config.getProperty("cacheGpsRealSaveJob"))){
				JobDetail saveGpsRealJob = new JobDetail(CacheGpsRealSaveJob.ID,sched.getSchedulerName(),CacheGpsRealSaveJob.class);
				CronTrigger saveGpsRealJobTrigger = new CronTrigger("cacheGpsRealSaveJobTrigger", sched.getSchedulerName(), CacheGpsRealSaveJob.ID, sched.getSchedulerName(),config.getProperty("cacheGpsRealSaveJobTrigger"));
				sched.addJob(saveGpsRealJob, true);
				ft = sched.scheduleJob(saveGpsRealJobTrigger);
				log.info(saveGpsRealJob.getFullName() + " has been scheduled to run at: " + ft
			        + " and repeat based on expression: "
			        + saveGpsRealJobTrigger.getCronExpression());
			}
			/**
			 * ʵʱ��λ�����ݿ�
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
			 * �������³����ĵ���λ��
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
			 * ����GPS�������򵽻��棨ȫ��ʵʱ��������
			 */
			if("true".equalsIgnoreCase(config.getProperty("cacheGpsZcodeSaveJob"))){
				JobDetail cacheGpsZcodeSaveJob = new JobDetail(CacheGpsZcodeSaveJob.ID,sched.getSchedulerName(),CacheGpsZcodeSaveJob.class);
				CronTrigger cacheGpsZcodeSaveJobTrigger = new CronTrigger("cacheGpsZcodeSaveJobTrigger", sched.getSchedulerName(), CacheGpsZcodeSaveJob.ID, sched.getSchedulerName(),config.getProperty("cacheGpsZcodeSaveJobTrigger"));
				sched.addJob(cacheGpsZcodeSaveJob, true);
				ft = sched.scheduleJob(cacheGpsZcodeSaveJobTrigger);
				log.info(cacheGpsZcodeSaveJob.getFullName() + " has been scheduled to run at: " + ft
		        + " and repeat based on expression: "
		        + cacheGpsZcodeSaveJobTrigger.getCronExpression());
			}
			/**
			 * �ӻ���ȡ����GPS�������򣬲����浽���ݿ�
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
			 * �������³����ĵ���λ��
			 */
			if("true".equalsIgnoreCase(config.getProperty("tmnSysIdRefreshJob"))){
				JobDetail tmnSysIdRefreshJob = new JobDetail(TmnSysIdRefreshJob.ID,sched.getSchedulerName(),TmnSysIdRefreshJob.class);
				CronTrigger tmnSysIdRefreshJobTrigger = new CronTrigger("tmnSysIdRefreshJobTrigger", sched.getSchedulerName(), TmnSysIdRefreshJob.ID, sched.getSchedulerName(),config.getProperty("tmnSysIdRefreshJobTrigger"));
				sched.addJob(tmnSysIdRefreshJob, true);
				ft = sched.scheduleJob(tmnSysIdRefreshJobTrigger);
				log.info(tmnSysIdRefreshJob.getFullName() + " has been scheduled to run at: " + ft
		        + " and repeat based on expression: "
		        + tmnSysIdRefreshJobTrigger.getCronExpression());
			}
			/**
			 * ����ϵͳʱ��
			 */
			if("true".equalsIgnoreCase(config.getProperty("sysSecondTimeJob"))){
				JobDetail sysSecondTimeJob = new JobDetail(SysSecondTimeJob.ID,sched.getSchedulerName(),SysSecondTimeJob.class);
				CronTrigger sysSecondTimeJobTrigger = new CronTrigger("sysSecondTimeJobTrigger", sched.getSchedulerName(), SysSecondTimeJob.ID, sched.getSchedulerName(),config.getProperty("sysSecondTimeJobTrigger"));
				sched.addJob(sysSecondTimeJob, true);
				ft = sched.scheduleJob(sysSecondTimeJobTrigger);
				log.info(sysSecondTimeJob.getFullName() + " has been scheduled to run at: " + ft
		        + " and repeat based on expression: "
		        + sysSecondTimeJobTrigger.getCronExpression());
			}
			/**
			 * ͳ��ÿ�쵱ǰ��̼����������
			 */
			if("true".equalsIgnoreCase(config.getProperty("dbGpsDayMileJob"))){
				JobDetail dbGpsDayMileJob = new JobDetail(DbGpsDayMileJob.ID,sched.getSchedulerName(),DbGpsDayMileJob.class);
				CronTrigger dbGpsDayMileJobTrigger = new CronTrigger("dbGpsDayMileJobTrigger", sched.getSchedulerName(), DbGpsDayMileJob.ID, sched.getSchedulerName(),config.getProperty("dbGpsDayMileJobTrigger"));
				sched.addJob(dbGpsDayMileJob, true);
				ft = sched.scheduleJob(dbGpsDayMileJobTrigger);
				log.info(dbGpsDayMileJob.getFullName() + " has been scheduled to run at: " + ft
		        + " and repeat based on expression: "
		        + dbGpsDayMileJobTrigger.getCronExpression());
			}
			/**
			 * ����ָ�����
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
