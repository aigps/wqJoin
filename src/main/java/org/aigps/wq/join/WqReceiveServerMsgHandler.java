package org.aigps.wq.join;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	public static void main(String[] args){
		try {
			long diffTime = ParseDate.getBetweenTime("090002", "090001", "HHmmss");
			System.out.print(diffTime);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	@Override
	public void update(Observable obs, Object args) {
		if(!(obs instanceof YmRecMsgPool)){
			return;
		}
		if(args instanceof byte[]){
			byte[] msg = (byte[])args;
			try {
				log.info("msg-->"+new String(msg));
				YmAccessMsg ymMsg = new YmAccessMsg(msg);
				log.info("成功接收ym："+ymMsg.getDataType()+" 字节大小："+msg.length+" content:"+ymMsg.getData());
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}else if(args instanceof YmAccessMsg){
			YmAccessMsg ymMsg = (YmAccessMsg)args;
			log.info("消息包长度:"+ymMsg.getMsgByte().length+" 内容："+new String(ymMsg.getMsgByte()));
			if(!"CMD".equalsIgnoreCase(ymMsg.getDataType())){
				return;
			}
			try {
				YmCmdModel ymCmdModel = new YmCmdModel(ymMsg);
				String phone = ymCmdModel.getYmAccessMsg().getDeviceCode();
				String[] params = ymCmdModel.getCmdParams();
				String mobileType = params[0];
				String classId = ClassIdMap.getClassId(mobileType);
				
				if("LCSNow".equalsIgnoreCase(ymCmdModel.getCmdType())){//单次定位
					SmsClient.sendSms(classId, phone, "01", "1", params[1], "0", "#0",params[2]);
				}
				else if("ActiveLCS".equalsIgnoreCase(ymCmdModel.getCmdType())){//定位激活
					int secondInterval = Integer.parseInt(params[1]);//定位间隔
					String startTime = params[2];//开始时间
					String endTime = params[3];//结束时间
					String fixModel = params[4];//定位类型
					String workWeekDays = params[5];//上班星期
					String smsSender = params[6];//网关
					long diffTime = ParseDate.getBetweenTime(startTime, endTime, "HHmmss");
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
				else if("CancelActiveLCS".equalsIgnoreCase(ymCmdModel.getCmdType())){//定位取消激活
					SmsClient.sendSms(classId, phone, "04", "4", null, null, null,params[1]);
				}
//				else if("TimingInterval".equalsIgnoreCase(ymCmdModel.getCmdType())){//定时监控
//					String action = params[0];
//					if(action.equalsIgnoreCase("2")){//取消
//						SmsClient.sendSms(phone, "03", "4", null, null, null);
//					}else if(action.equalsIgnoreCase("1")){//设置
//						StringBuilder otherParams = new StringBuilder();
//						otherParams.append("#0");//开始时间
//						otherParams.append("#").append(params[1]);//定位间隔
//						otherParams.append("#").append(params[2]);//定位次数
//						otherParams.append("#1");//每次上报定位的个数
//						SmsClient.sendSms(phone, "03", "2", params[3], "0", "");
//					}
//				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		else if(args instanceof YmBinAccMsg){
			try {
				YmBinAccMsg ymBinMsg = (YmBinAccMsg)args;
				String deviceCode = ymBinMsg.getDeviceCode();
				if(ymBinMsg.getDataType().equals("GP")){//二进制定位信息
					YmBinGpsModel ymBinGpsModel = new YmBinGpsModel(ymBinMsg);
					log.info(deviceCode+"   收到二进制信息  "+" 包长:"+ymBinMsg.getSrcMsg().length+" 内容:"+ymBinGpsModel.toString());
					ymBinGpsModel.getzCode();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	}
}
