package org.aigps.wq.service;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.entity.GisPosition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class WqGpsService {
	private static final Log log = LogFactory.getLog(WqGpsService.class);
	
	
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
			if(gisPos.isValidLocation()){
				//��������
				String zCode = DistrictUtil.getCityZcode(gisPos.getLon(), gisPos.getLat());
				gisPos.setzCode(zCode);
				//ƫ�ƾ�γ��ת��
				MapLocation real = new MapLocation(Double.parseDouble(gisPos.getLon()),Double.parseDouble(gisPos.getLat()));
				MapLocation fake = MapAbcServerUtil.getFakeLocation2(real);
				gisPos.setLonOffset(fake.getLongtitude()+"");
				gisPos.setLatOffset(fake.getLatitude()+"");
			}else{//��Ч��λ��ȡ�����Ч��λ
				if(preGps!=null){//�Ѿ����������Ч��λ
//					ymGpsModel.setLon(lastYmGpsModel.getLon());
//					ymGpsModel.setLat(lastYmGpsModel.getLat());
//					ymGpsModel.setLonOffset("0");
//					ymGpsModel.setLatOffset("0");
//					ymGpsModel.setzCode(lastYmGpsModel.getzCode());
//					ymGpsModel.setMile(lastYmGpsModel.getMile());
					/**
					 * ��ʱ��������Ч��λ
					 */
					gisPos.setLon(preGps.getLon());
					gisPos.setLat(preGps.getLat());
					gisPos.setLonOffset(preGps.getLonOffset());
					gisPos.setLatOffset(preGps.getLatOffset());
					gisPos.setzCode(preGps.getzCode());
					gisPos.setMile(preGps.getMile());
				}else{//û���������Ч��λ
					
				}
			}
			/**
			 * ������̵ļ���
			 */
			if(preGps!=null){
				gisPos.refreshMile(preGps);
			}

			//������������
			DcGpsCache.updateDcRgAreaHis(preGps,gisPos);
			//�������λ
			DcGpsCache.updateLastGps(tmnCode, gisPos);
			/**
			 * ��������(һ�����ȸ������ڴ�Ķ�λ���ٵ��ø��µ������������ڲ��ǵ������ڴ�Ķ�λ��Ϣ)
			 */
			VhcLocDesc newVhcLocDesc = ParseGpsLocDescJob.getNewVhcLocDesc(tmnCode);
			if(newVhcLocDesc!=null){
				gisPos.setLocDesc(newVhcLocDesc.getLocDesc());
//				newVhcLocDesc.setChanged(false);
			}else{
				VhcLocDesc vhcLocDesc = DcGpsCache.getVhcLocDesc(tmnCode);
				if(vhcLocDesc!=null){//&& vhcLocDesc.isChanged()ֻ�е��������䶯�ˣ�������ʷ��
					gisPos.setLocDesc(vhcLocDesc.getLocDesc());
//					vhcLocDesc.setChanged(false);
				}
			}
			DcGpsCache.inCreLastGpsReport.put(tmnCode, gisPos);
			DcGpsCache.gpsReportMinuCache.add(gisPos);
			gpsHandler.setChanged();
			gpsHandler.notifyObservers(gisPos);
		}else{//���ܾ����Ķ�λ����������������
			
		}
	}

}
