
package org.aigps.wq.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.YmConstants;
import org.gps.util.MathUtil;
import org.gps.util.ParseDate;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ans")
public class LiaModel {
	protected static final Log log = LogFactory.getLog(LiaModel.class);
	
	private String result;//���ؽ����0-�ɹ���1-ʧ��
	private Lia lia;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Lia getLia() {
		return lia;
	}
	public void setLia(Lia lia) {
		this.lia = lia;
	}
	
	//����λ��Ϣƴװ��Ԫ��Э���������
	public List<String> convertToYmData(){
		List<Posinfo> ps = lia.getPosinfos();
		if(ps == null || ps.isEmpty()){
			return null;
		}
		String reqType = getReqType();//��λ����  01���ζ�λ;02���λ;03���ڶ�λ
		List<String> datas = new ArrayList<String>();
		
		for (Posinfo p : ps) {
			String fixTime = getFixTime(p);//��λʱ��
			if(fixTime == null){
				continue;
			}
			
			String lat = StringUtils.isBlank(p.getLatitude()) ? "0" : p.getLatitude();
			String lng = StringUtils.isBlank(p.getLongitude()) ? "0" : p.getLongitude();
			String altitude = StringUtils.isBlank(p.getAltitude()) ? "0" : p.getAltitude();
			String dire = StringUtils.isBlank(p.getDirection()) ? "0" : p.getDirection();
			String speed = StringUtils.isBlank(p.getVelocity()) ? "0" : p.getVelocity();
			String precision = StringUtils.isBlank(p.getPrecision()) ? "0" : p.getPrecision();
			String fixMode = p.getFixmode();
			
			//�淶���ݳ���
			try{
				lng = String.valueOf(MathUtil.setScale(Double.parseDouble(lng), 6));
				lat = String.valueOf(MathUtil.setScale(Double.parseDouble(lat), 6));
				speed = String.valueOf((int)Double.parseDouble(speed));
				dire = String.valueOf((int)Double.parseDouble(dire));
				altitude = String.valueOf((int)Double.parseDouble(altitude));
				precision = String.valueOf(MathUtil.setScale(Double.parseDouble(precision), 1));
			}catch(Exception e){
				log.error(e.getMessage(),e);
			}
			
			StringBuilder ymData = new StringBuilder();
			ymData.append("0|").append(YmConstants.DATA_SPLIT_CHAR).append(fixTime);
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append(lng);
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append(lat);
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append(speed);
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append(dire);
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("00000000");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("00000000");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");// ����״̬����
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append(altitude);// �߶�
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append(reqType);
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append(precision);
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append(fixMode);// ��λ���� 0 MSA��1 Google��2 GPS��3 GPSOne��4 Hybrid
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");
			ymData.append(YmConstants.DATA_SPLIT_CHAR).append("");// ��վID

			datas.add(ymData.toString());
		}
		return datas;
	}
	
	//��ȡ�ϱ�ʱ�䣬�ڼ���8��Сʱ�����ǵ�ǰ����ʱ��
	private String getFixTime(Posinfo p){
		String fixTime = p.getFixtime();
		if (StringUtils.isBlank(fixTime)) {
			return null;
		}
		try{
			if (!NumberUtils.isNumber(fixTime)) {
				fixTime = ParseDate.getNumberDate(fixTime);
			}
			return ParseDate.addTime(fixTime, "yyyyMMddHHmmss", 8*60*60);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return null;
		}
	}
	
	//��ȡ�ֻ�IMSI��
	public String getMsid(){
		try{
			return lia.getMsids().getMsid();
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return "";
		}
	}
	
	//��ȡ�ֻ���
	public String getPhone(){
		String appId = lia.getApp_id();//Ӧ�ñ�ʶ
		String reqId = lia.getReq_id();//�����ʶ
		try{
			//��װ�ֻ��� reqId���ֻ���ǰ3λ+2λ��λ����   appId���ֻ��ź�9λ
			if(StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(reqId)){
				String phone = reqId.substring(0,3)+appId;
				return NumberUtils.isNumber(phone) ? phone : null;
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	private String getReqType(){
		if(isPicture()){
			return "97";
		}
		if(isSignIn()){
			return "98";
		}
		if(isSignOut()){
			return "99";
		}
		if(lia.getReq_id()!=null && lia.getReq_id().length()>3){
			return lia.getReq_id().substring(3);
		}
		return "02";
	}
	
	//�Ƿ�������ǩ��
	public boolean isSignIn(){
		return "CHKACT".equalsIgnoreCase(lia.getReq_id());
	}
	//�Ƿ�������ǩ��
	public boolean isSignOut(){
		return "BCKACT".equalsIgnoreCase(lia.getReq_id());
	}
	//�Ƿ�������
	public boolean isPicture(){
		return "IMAGELOC".equalsIgnoreCase(lia.getReq_id());
	}
	//��ȡʱ��
	public String getTime(){
		for (Posinfo p : lia.getPosinfos()) {
			String fixTime = getFixTime(p);
			if(fixTime != null){
				return fixTime;
			}
		}
		return "";
	}
	
}

