package org.aigps.wq.join;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-3-2����11:17:12
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company������Ԫ��Ƽ����޹�˾
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
				log.info("�ɹ�����ym��"+ymMsg.getDataType()+" �ֽڴ�С��"+msg.length+" content:"+ymMsg.getData());
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}else if(args instanceof YmAccessMsg){
			YmAccessMsg ymMsg = (YmAccessMsg)args;
			log.info("��Ϣ������:"+ymMsg.getMsgByte().length+" ���ݣ�"+new String(ymMsg.getMsgByte()));
			if(!"CMD".equalsIgnoreCase(ymMsg.getDataType())){
				return;
			}
			try {
				YmCmdModel ymCmdModel = new YmCmdModel(ymMsg);
				String phone = ymCmdModel.getYmAccessMsg().getDeviceCode();
				String[] params = ymCmdModel.getCmdParams();
				String mobileType = params[0];
				String classId = ClassIdMap.getClassId(mobileType);
				
				if("LCSNow".equalsIgnoreCase(ymCmdModel.getCmdType())){//���ζ�λ
					SmsClient.sendSms(classId, phone, "01", "1", params[1], "0", "#0",params[2]);
				}
				else if("ActiveLCS".equalsIgnoreCase(ymCmdModel.getCmdType())){//��λ����
					int secondInterval = Integer.parseInt(params[1]);//��λ���
					String startTime = params[2];//��ʼʱ��
					String endTime = params[3];//����ʱ��
					String fixModel = params[4];//��λ����
					String workWeekDays = params[5];//�ϰ�����
					String smsSender = params[6];//����
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
				else if("CancelActiveLCS".equalsIgnoreCase(ymCmdModel.getCmdType())){//��λȡ������
					SmsClient.sendSms(classId, phone, "04", "4", null, null, null,params[1]);
				}
//				else if("TimingInterval".equalsIgnoreCase(ymCmdModel.getCmdType())){//��ʱ���
//					String action = params[0];
//					if(action.equalsIgnoreCase("2")){//ȡ��
//						SmsClient.sendSms(phone, "03", "4", null, null, null);
//					}else if(action.equalsIgnoreCase("1")){//����
//						StringBuilder otherParams = new StringBuilder();
//						otherParams.append("#0");//��ʼʱ��
//						otherParams.append("#").append(params[1]);//��λ���
//						otherParams.append("#").append(params[2]);//��λ����
//						otherParams.append("#1");//ÿ���ϱ���λ�ĸ���
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
				if(ymBinMsg.getDataType().equals("GP")){//�����ƶ�λ��Ϣ
					YmBinGpsModel ymBinGpsModel = new YmBinGpsModel(ymBinMsg);
					log.info(deviceCode+"   �յ���������Ϣ  "+" ����:"+ymBinMsg.getSrcMsg().length+" ����:"+ymBinGpsModel.toString());
					ymBinGpsModel.getzCode();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	}
}
