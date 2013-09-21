package org.aigps.wq.service;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqJoinMqService;
import org.aigps.wq.util.DistrictUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.gps.util.common.LonLatUtil;
import org.springframework.stereotype.Component;

@Component
public class WqGpsService {
	private static final Log log = LogFactory.getLog(WqGpsService.class);
	public  boolean correctGpsTime(String sysTime,String maxFutureTime,GisPosition gisPos)throws Exception{
		boolean retFlag = true;
		String gpsTime = gisPos.getRptTime();
		//��λʱ�䳬��ϵͳʱ��
		if(maxFutureTime.compareTo(gpsTime)<=0){
			retFlag = false;
			if(log.isErrorEnabled()){
				log.error("�ն�:"+gisPos.getTmnKey()+"�ϱ���δ��ʱ��-->"+gpsTime+"��λ, ����!");
			}
		}
		return retFlag;
	}
	
	/**
	 * ������λ����
	 * @param tmnCode
	 * @param ymGpsModel
	 * @throws Exception
	 */
	public void receiveGpsInfo(String tmnCode,GisPosition gisPos)
			throws Exception {
		//��������ն���ҵ��ID�Ķ���,���ö���
		if(DcGpsCache.getTmnSysIdMap().containsKey(tmnCode)){
			gisPos.setTmnAlias(DcGpsCache.getTmnSysIdMap().get(tmnCode));
		}
		String sysTime = DateUtil.sysNumDateTime;
		String nextHour = DateUtil.sysNumNextHour;
		//�����λ
		GisPosition preGps = DcGpsCache.getLastGps(tmnCode);
		if(preGps!=null){
			if(preGps.getRptTime()!=null && preGps.getRptTime().equalsIgnoreCase(gisPos.getRptTime())){
				log.warn(tmnCode+" �ϱ��ظ���λ��Ϣ:"+gisPos.getRptTime());
				return;
			}
		}
		//���ý���ʱ��
		gisPos.setServerTime(sysTime);
		//�������ȷʱ������ܾ�����ʱ�䣬���ն�λ
		if(correctGpsTime(sysTime, nextHour, gisPos)){
			//�������Ч��λ
			if(LonLatUtil.isValidLocation(gisPos.getLon(), gisPos.getLat())){
				//��������
				String zCode = DistrictUtil.getCityZcode(gisPos.getLon(), gisPos.getLat());
				gisPos.setzCode(zCode);
			}else{//��Ч��λ��ȡ�����Ч��λ
				if(preGps!=null){//�Ѿ����������Ч��λ
					/**
					 * ��ʱ��������Ч��λ
					 */
					gisPos.setLon(preGps.getLon());
					gisPos.setLat(preGps.getLat());
					gisPos.setzCode(preGps.getzCode());
				}
			}
			
			//������������
			DcGpsCache.updateDcRgAreaHis(preGps,gisPos);
			//�������λ
			DcGpsCache.updateLastGps(tmnCode, gisPos);
			
			//������λ��Ϣ
			DcGpsCache.gpsReportMinuCache.add(gisPos);
			
			MqMsg mqMsg = new MqMsg(tmnCode, "WQ", 0, "GPS","RPT");
			mqMsg.setData(gisPos);
			WqJoinMqService.addMsg(mqMsg);
		}else{//���ܾ����Ķ�λ����������������
			
		}
	}

}
