package org.aigps.wq.service;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.task.job.ParseGpsLocDescJob;
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
					gisPos.setMile(preGps.getMile());
				}
			}
			/**
			 * ������̵ļ���
			 * 
			 *   (��ȷ���Ƿ���Ҫ)
			 */
			

			//������������
			DcGpsCache.updateDcRgAreaHis(preGps,gisPos);
			//�������λ
			DcGpsCache.updateLastGps(tmnCode, gisPos);
			/**
			 * ��������(һ�����ȸ������ڴ�Ķ�λ���ٵ��ø��µ������������ڲ��ǵ������ڴ�Ķ�λ��Ϣ)
			 */
			
			DcGpsCache.inCreLastGpsReport.put(tmnCode, gisPos);
			DcGpsCache.gpsReportMinuCache.add(gisPos);
		}else{//���ܾ����Ķ�λ����������������
			
		}
	}

}
