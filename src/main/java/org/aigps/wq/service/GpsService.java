package org.aigps.wq.service;

import java.util.ArrayList;
import java.util.List;
import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.entity.WqTmnSttsHis;
import org.aigps.wq.ibatis.IbatisUpdateJob;
import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqJoinMqService;
import org.aigps.wq.util.DistrictUtil;
import org.aigps.wq.xmlmodel.LiaModel;
import org.aigps.wq.xmlmodel.Picture;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.gps.util.common.LonLatUtil;
import com.thoughtworks.xstream.XStream;


public class GpsService {
	private static final Log log = LogFactory.getLog(GpsService.class);
	public static boolean correctGpsTime(String sysTime,String maxFutureTime,GisPosition gisPos)throws Exception{
		boolean retFlag = true;
		String gpsTime = gisPos.getRptTime();
		//定位时间超过系统时间
		if(maxFutureTime.compareTo(gpsTime)<=0){
			retFlag = false;
			if(log.isErrorEnabled()){
				log.error("终端:"+gisPos.getTmnKey()+"上报了未来时间-->"+gpsTime+"定位, 丢弃!");
			}
		}
		return retFlag;
	}
	
	public static String execute(String xmlString, XStream xstream) throws Exception {
		xstream.processAnnotations(LiaModel.class);
		LiaModel model = (LiaModel) xstream.fromXML(xmlString);
		List<GisPosition> gpsList = model.toGps();
		if(gpsList == null || gpsList.isEmpty()){
			log.error("无定位信息XML:\n" + xmlString);
			return xmlString;
		}
		String phone = model.getPhone(), msId = model.getMsid();
		log.error("有定位信息XML:\n" + xmlString);
		Picture pic = model.getLia().getPicture();
		
		//上报定位信息
		for(GisPosition gps : gpsList){
			String tmnKey = StringUtils.isBlank(phone) ? msId : phone;
			gps.setTmnKey(tmnKey);
			//定位处理
			receiveGpsInfo(tmnKey, gps);
		}
		
		String staffId = DcGpsCache.getTmnSysIdMap().get(phone);
		//主动签到签退，发送状态
		if(model.isSignIn() || model.isSignOut()){
			String status = model.isSignIn()?"98":"99";
			
			WqTmnSttsHis wqTmnSttsHis = new WqTmnSttsHis();
			wqTmnSttsHis.setStaffId(msId);
			wqTmnSttsHis.setRptTime(DateUtil.converDateFormat(model.getTime(), DateUtil.YMDHMS, DateUtil.YMD_HMS));
			wqTmnSttsHis.setStts(status);
			
			MqMsg mqMsg = new MqMsg(msId, "IMSI", 0, "CMD","UploadStatus");
			mqMsg.setData(wqTmnSttsHis);
			WqJoinMqService.addMsg(mqMsg);
			
			IbatisUpdateJob ibatisUpdateJob = WqJoinContext.getBean("ibatisUpdateJob", IbatisUpdateJob.class);
			ibatisUpdateJob.addExeSql("WQ_TMN_STTS_HIS.insert", wqTmnSttsHis);
		}else if(model.isPicture() && pic!=null){//拍照
			pic.setTime(model.getTime());
			pic.setMsid(msId);
			sendPicture(pic);
		}
		return null;
	}
	
	public static void sendPicture(Picture pic) throws Exception{
		String picData = pic.getData();
		List<String> list = new ArrayList<String>();
		int index = 0, length = picData.length();
		while(index+2000 < length){
			list.add(picData.substring(index,index+2000));
			index += 2000;
		}
		list.add(picData.substring(index));
		int packIndex = 1, totalPack = list.size();
//		Channel cnel = WqJoinContext.getYmNettyClient().getChannel();
//		for(String data:list){
//			String picd =  "0|"+pic.getTime()+"|0|"+packIndex+"|"+totalPack+"|"+data+"|"+pic.getDesc()+"|"+pic.getName();
//			YmAccessMsg ymMsg = new YmAccessMsg("PIC", "IMSI", pic.getMsid(), picd);
//       	 	ChannelUtil.sendMsg(cnel, ymMsg.toYmString().getBytes());
//       	 	log.error("pic index:"+packIndex);
//			packIndex++;
//			Thread.sleep(10);
//		}
	}
	
	/**
	 * 解析定位数据
	 * @param tmnCode
	 * @param ymGpsModel
	 * @throws Exception
	 */
	public static void receiveGpsInfo(String tmnCode,GisPosition gisPos)
			throws Exception {
		//如果存在终端与业务ID的对照,设置对照
		if(DcGpsCache.getTmnSysIdMap().containsKey(tmnCode)){
			gisPos.setTmnAlias(DcGpsCache.getTmnSysIdMap().get(tmnCode));
		}
		String sysTime = DateUtil.sysNumDateTime;
		String nextHour = DateUtil.sysNumNextHour;
		//最近定位
		GisPosition preGps = DcGpsCache.getLastGps(tmnCode);
		if(preGps!=null){
			if(preGps.getRptTime()!=null && preGps.getRptTime().equalsIgnoreCase(gisPos.getRptTime())){
				log.warn(tmnCode+" 上报重复定位信息:"+gisPos.getRptTime());
				return;
			}
		}
		//设置接收时间
		gisPos.setServerTime(sysTime);
		//如果是正确时间或者能纠正的时间，接收定位
		if(correctGpsTime(sysTime, nextHour, gisPos)){
			//如果是有效定位
			if(LonLatUtil.isValidLocation(gisPos.getLon(), gisPos.getLat())){
				//行政区域
				String zCode = DistrictUtil.getCityZcode(gisPos.getLon(), gisPos.getLat());
				gisPos.setzCode(zCode);
			}else{//无效定位，取最近有效定位
				if(preGps!=null){//已经有最近的有效定位
					/**
					 * 暂时不采用有效定位
					 */
					gisPos.setLon(preGps.getLon());
					gisPos.setLat(preGps.getLat());
					gisPos.setzCode(preGps.getzCode());
				}
			}
			
			//更新行政区域
			DcGpsCache.updateDcRgAreaHis(preGps,gisPos);
			//更新最后定位
			DcGpsCache.updateLastGps(tmnCode, gisPos);
			
			//增量定位信息
			DcGpsCache.gpsAddCache.add(gisPos);
			
			MqMsg mqMsg = new MqMsg(tmnCode, "WQ", 0, "GPS","RPT");
			mqMsg.setData(gisPos);
			WqJoinMqService.addMsg(mqMsg);
		}else{//不能纠正的定位，丢弃掉，不处理
		}
	}

}
