package org.aigps.wq.util;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapinfo.dp.Feature;
import com.mapinfo.dp.FeatureSet;
import com.mapinfo.dp.QueryParams;
import com.mapinfo.dp.SearchType;
import com.mapinfo.mapj.FeatureLayer;
import com.mapinfo.mapj.MapJ;
import com.mapinfo.util.DoublePoint;

/**
 * 行政区域工具类
 * @author Administrator
 *
 */
public class DistrictUtil {
	private static FeatureLayer layer;
	private static final Log log = LogFactory.getLog(DistrictUtil.class);
	
	// 只查询名称字段
	@SuppressWarnings("deprecation")
	private static QueryParams queryParams = new QueryParams(false, false, false, true, false, false, SearchType.entire);
	
	//初始化层
	public DistrictUtil(String mapFile)throws Exception{
		try {
			MapJ cityMapJ = new MapJ();
			if (mapFile.endsWith(".gst")) {
				File file = new File(mapFile);
				String mapFileDir = file.getParentFile().getAbsolutePath();
				cityMapJ.loadGeoset(mapFile, mapFileDir, null);
			} else {
				cityMapJ.loadMapDefinition(mapFile);
			}
			// 获得层
			layer = (FeatureLayer) cityMapJ.getLayers().get(0);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**根据坐标字符串获取坐标点(字符串转DB)
	 * @param longit
	 * @param lat
	 * @return
	 */
	private static DoublePoint getDoublePoint(double longit,double lat){
		return new DoublePoint(longit,lat);
	}
	
	/**
	 * 验证城市,并获取城市CODE
     * @param longit 经度
     * @param lat 纬度
     * @return String 获取城市CODE
     */
	public static String getCityZcode(double longit, double lat) throws Exception {
		String cityCode = "";
		try {
			List<String> columnList = new ArrayList<String>();
			columnList.add("Zcode");
			FeatureSet featureSet = layer.searchAtPoint(columnList, getDoublePoint(longit, lat), queryParams);
			Feature feature = featureSet.getNextFeature();
			if (feature != null) {
				cityCode = feature.getAttribute(0).toString();
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return cityCode;
	}

	/**
	 * 验证城市,并获取城市CODE
     * @param longit 经度
     * @param lat 纬度
     * @return String 获取城市CODE
     */
	public static String getCityName(double longit, double lat) throws Exception {
		StringBuilder cityName = new StringBuilder();
		try {
			DoublePoint point = getDoublePoint(longit, lat);
			List<String> columnList = new ArrayList<String>();
			columnList.add("Prov");// 省
			FeatureSet featureSet = layer.searchAtPoint(columnList, point, queryParams);
			Feature feature = featureSet.getNextFeature();
			if (feature != null) {
				cityName.append(feature.getAttribute(0).toString());
			}
			columnList.clear();
			columnList.add("City");// 市
			featureSet = layer.searchAtPoint(columnList, point, queryParams);
			feature = featureSet.getNextFeature();
			if (feature != null) {
				cityName.append(feature.getAttribute(0).toString());
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return cityName.toString();
	}

	public static void main(String[] args) {
		try {
			new DistrictUtil("D:\\programs\\mapinfo\\district\\ChinaArea.gst");
			long startTime = System.currentTimeMillis();
			String cityCode = null;
			String cityName = null;
			for (int i = 0; i < 3000; i++) {
				cityCode = getCityZcode(122.268817,40.06786);
//				cityName = getCityName("117.1475","25.28428");
			}
			cityCode = getCityZcode(117.1475,25.28428);
			cityName = getCityName(117.1475,25.28428);
			long endTime = System.currentTimeMillis();
			System.out.print((endTime-startTime)+" cityCode="+cityCode+" cityName="+cityName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
