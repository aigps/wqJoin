package org.aigps.wq.join;

import java.util.Observable;
import java.util.Observer;

import org.aigps.wq.SmsClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date：  2012-3-2上午11:17:12
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州元码科技有限公司
 */
public class WqReceiveServerMsgHandler implements Observer {
	private static final Log log = LogFactory.getLog(WqReceiveServerMsgHandler.class);

	public void update(Observable obs, Object args) {
			try {
				String cmdType = "";
				String phone = "";
				String[] params = null;
				String mobileType = params[0];
				String classId = "";
				
				if("LCSNow".equalsIgnoreCase(cmdType)){//单次定位
					SmsClient.sendSms(classId, phone, "01", "1", params[1], "0", "#0",params[2]);
				}
				else if("ActiveLCS".equalsIgnoreCase(cmdType)){//定位激活
					int secondInterval = Integer.parseInt(params[1]);//定位间隔
					String startTime = params[2];//开始时间
					String endTime = params[3];//结束时间
					String fixModel = params[4];//定位类型
					String workWeekDays = params[5];//上班星期
					String smsSender = params[6];//网关
					long diffTime = DateUtil.getBetweenTime(startTime, endTime, "HHmmss");
					if(diffTime <= 0){
						diffTime = 24*60*60*1000 + diffTime;
					}
					long gpsCount = diffTime/1000/secondInterval;
					
					String otherParams = "#" + startTime + "#" + secondInterval + "#" + gpsCount + "#1";
					if("hxylSmsSender".equals(smsSender)){
						otherParams += "#" + endTime + "#" + workWeekDays + "#";
					}
					SmsClient.sendSms(classId, phone, "02", "3", fixModel, "0", otherParams,smsSender);
				}
				else if("CancelActiveLCS".equalsIgnoreCase(cmdType)){//定位取消激活
					SmsClient.sendSms(classId, phone, "04", "4", null, null, null,params[1]);
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
	}
}
