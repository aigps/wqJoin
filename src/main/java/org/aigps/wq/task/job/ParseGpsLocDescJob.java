package org.aigps.wq.task.job;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.entity.GisPosition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.map.util.MapTileUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sunleads.dc.GeoCache;
/**
 * 分析实时地理位置的定位信息
 * @author Administrator
 *
 */
public class ParseGpsLocDescJob implements Job {
	private static final Log log = LogFactory.getLog(ParseGpsLocDescJob.class);
	public static final String ID="ParseGpsLocDescJob";
	private static AtomicBoolean isRunning = new AtomicBoolean();//同一个时间点，只允许一个job跑数
	
	private static BasicThreadFactory tfactory = new BasicThreadFactory.Builder().namingPattern("parseGpsLoc-pool-%d").daemon(true).build();
	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(15, 15, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(500000),tfactory);
	private static Set<String> runSet = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());

	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){//如果当前job没有运行，则触发
			try {
				execute();
			} catch (Throwable e) {
				log.error("",e);
			}
			finally{
				isRunning.set(false);;
			}
		}
	}

	private void execute() throws Throwable{
		for(final GisPosition gps : DcGpsCache.getLastGpsReport().values() ){
			if(runSet.contains(gps.getTmnKey()) || !needToGeo(gps)) {
				continue;
			}
			runSet.add(gps.getTmnKey());
			pool.execute(new Runnable(){
				public void run() {
					try {
						String geo = GeoCache.getGeo(gps.getLon(), gps.getLat());
						if(StringUtils.isBlank(geo)) {
							return;
						}
						GisPosition newLoc = setNewLoc(gps, geo);
						DcGpsCache.setVhcLocDesc(gps.getTmnKey(), newLoc);
					} catch (Throwable e) {
						log.error(e.getMessage());
					} finally {
						runSet.remove(gps.getTmnKey());
					}
				}
			});
		}
	}
	
	private static boolean needToGeo(GisPosition gps){
		try {
			double lng = gps.getLon();
			double lat = gps.getLat();
			if(lng<=0 || lat<=0 || lng>180 || lat>60){
				return false;
			}
			GisPosition preLoc = DcGpsCache.getVhcLocDesc(gps.getTmnKey());
			if(preLoc == null) {
				return true;
			}
			if (lng==preLoc.getLon() && lat==preLoc.getLat()) {
				return false;
			}
			if (preLoc.getRptTime().compareToIgnoreCase( gps.getRptTime()) >=0) {// 补报定位，不解析
				return false;
			}
			
			int[] prevXY = MapTileUtil.getXY(preLoc.getLon(),preLoc.getLat());
			int[] nowXY = MapTileUtil.getXY(lng,lat);
			
			//在同一个地图瓦片上，不进行解析
			if(prevXY[0] == nowXY[0] && prevXY[1] == nowXY[1]) {
				return false;
			}
			return true;
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
			return false;
		}
	}

	private static GisPosition setNewLoc(GisPosition gps, String geo) throws Exception {
		gps.setLocDesc(geo);
		return gps;
	}

}
