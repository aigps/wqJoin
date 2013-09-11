package org.aigps.wq.util;

import java.math.BigDecimal;

import org.aigps.wq.entity.GisPosition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;


public class MileUtil {
	private static final Log log = LogFactory.getLog(MileUtil.class);
	
	
	/**
	 * �������ͳ������
	 * @param preGisPos
	 * @throws Exception
	 */
	public void refreshMile(GisPosition preGisPos,GisPosition gisPos)throws Exception{
		/**
		 * ���ͳ��
		 */
		//����ն˺Ų�һ��
		if(preGisPos.getTmnKey()!=null && gisPos.getTmnKey()!=null && !preGisPos.getTmnKey().equals(gisPos.getTmnKey())){
			gisPos.setMile(preGisPos.getMile());
		}else if(gisPos.getRptTime().compareTo(preGisPos.getRptTime())>0){
			//�����ǰ��λʱ������ϴζ�λʱ��
			long hisTotalMile = new BigDecimal(preGisPos.getMile()).longValue();
			long currMile = new BigDecimal(getCurrMile()).longValue();
			long hisMile = new BigDecimal(preGisPos.getCurrMile()).longValue();
			long diffMile = Math.abs(currMile-hisMile);
			//�����ǰ��̴��ڵ����ϴ���̣���������£�
			if(currMile>=hisMile){
				//i.������ƫ��ֵС��10����
				if(diffMile<10000){
					setMile((hisTotalMile+diffMile)+"");
				}else{
					long diffSecTime = Math.abs(DateUtil.getBetweenTime(getGpsTime(), preGisPos.getGpsTime(), DateUtil.YMDHMS)/1000); 
					long avgSpeed = diffMile/diffSecTime;
					//ii.	ƽ���ٶ�С��120����/ʱ
					if(avgSpeed<33){
						setMile((hisTotalMile+diffMile)+"");
					}else{
						setMile(hisTotalMile+"");
					}
				}
			}else{//��1������ ��2��Ҳ�������ն�����
				//i.	�����ǰ���С��10����
				if(currMile<10000){//����
					setMile((hisTotalMile+currMile)+"");
				}else{
					long diffSecTime = Math.abs(DateUtil.getBetweenTime(getGpsTime(), preGisPos.getGpsTime(), DateUtil.YMDHMS)/1000); 
					if(diffMile<15000){//�������ն�����
						setMile(hisTotalMile+"");
					}else if(currMile/diffSecTime<33){//ii.	ƽ���ٶȣ���ǰ���/ʱ��С��120����/ʱ
						setMile((hisTotalMile+currMile)+"");
					}else{
						MapLocation currMap = new MapLocation(Double.parseDouble(this.lon),Double.parseDouble(this.lat));
						MapLocation hisMap = new MapLocation(Double.parseDouble(preGisPos.getLon()),Double.parseDouble(preGisPos.getLat()));
						long distance = new BigDecimal(MapUtil.distance(currMap, hisMap, 'K')*1000).longValue();
						//iii.	ƽ���ٶȣ���γ�Ⱦ���/ʱ��С��120����/ʱ
						if(distance/diffSecTime<33){
							setMile((hisTotalMile+distance)+"");
						}else{
							setMile(hisTotalMile+"");
						}
					}
				}
			}
		}else{//3��	�����ǰ��λʱ��С���ϴζ�λʱ��
			long hisTotalMile = (long)Double.parseDouble(preGisPos.getMile());
			long currMile = (long)Double.parseDouble(getCurrMile());
			long hisMile = (long)Double.parseDouble(preGisPos.getCurrMile());
			//a)	�����ǰ���С���ϴ���̣���λ������
			if(currMile<hisMile){
				long diffMile = hisMile-currMile;
				if(currMile==0){//�������ʱ��ǰ�����Ϊ�㣬����ԭ����ֵ
					setMile(hisTotalMile+"");
				}else if(diffMile<10000){
					if((hisTotalMile+(currMile-hisMile))<=0){//����Ǹ�ֵ������ԭ����ֵ
						setMile(hisTotalMile+"");
					}else{
						setMile((hisTotalMile+(currMile-hisMile))+"");
					}
				}else{
					long diffSecTime = Math.abs(DateUtil.getBetweenTime(getGpsTime(), preGisPos.getGpsTime(), DateUtil.YMDHMS)/1000); 
					if(diffSecTime!=0 && diffMile/diffSecTime<33){
						if((hisTotalMile+(currMile-hisMile))<=0){//����Ǹ�ֵ������ԭ����ֵ
							setMile(hisTotalMile+"");
						}else{
							setMile((hisTotalMile+(currMile-hisMile))+"");
						}
					}else{
						setMile(hisTotalMile+"");
					}
				}
			}else{//b)	�����ǰ��̴����ϴ���̣��ն��󱨣�
				setMile(hisTotalMile+"");
			}
		}
	}
	
}
