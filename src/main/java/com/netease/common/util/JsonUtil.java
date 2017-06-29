package com.netease.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * JsonUtil提供在Json数据类型与Map、Bean、List、Array之间相互转换的方法<br/>
 * JsonUtil工具类依赖的jar包:<br>
 * (1)json-lib-2.2.3-jdk15.jar <br>
 * (2)jakarta commons-lang 2.5,<br>
 * (3)jakarta commons-beanutils 1.8.0, <br>
 * (4)jakarta commons-collections 3.2.1, <br>
 * (5)jakarta commons-logging 1.1.1,<br>
 * (6)ezmorph 1.0.6<br>
 * 
 * @author 开发支持中心
 */
@SuppressWarnings("unchecked")
public class JsonUtil {
	
	/**
	 * Json类型转换为Map类型
	 * @param jsonStr
	 * @return jsonStr对应的map对象
	 */
	public static Map<String,Object> jsonToMap(String jsonStr) {

		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		return (Map<String,Object>) JsonUtil.parseJSONObj(jsonObj);
	}
	
	
	/**
	 * Json类型转换为Map<String, objectClass>类型
	 * @param jsonStr
	 * @param objectClass
	 * @return jsonStr对应的Map数据
	 */
	public static Map<String,Object> jsonToMap(String jsonStr, Class objectClass) {

		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		Map mapClass = new HashMap<String, Object>();
		Iterator iterator = jsonObj.keys();
		while (iterator.hasNext()) {
			mapClass.put(iterator.next().toString(), objectClass);
		}
		return (Map<String,Object>) JSONObject.toBean(jsonObj, Map.class, mapClass);
	}
	
	/**
	 * Map类型转换为Json类型
	 * @param map
	 * @return map对应的json数据
	 */
	public static String mapToJson(Map map) {

		JSONObject jsonObj = JSONObject.fromObject(map);
		return jsonObj.toString();
	}
	
	/**
	 * Json类型转换为Bean类型
	 * @param jsonStr
	 * @param objectClass
	 * @return jsonStr对应的Bean对象
	 */
	public static Object jsonToBean(String jsonStr, Class objectClass) {

		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		return JSONObject.toBean(jsonObj, objectClass);
	}
	
	/**
	 * Bean类型转换为Json类型
	 * @param object
	 * @return bean对应的json串
	 */
	public static String BeanToJson(Object object) {

		JSONObject jsonObj = JSONObject.fromObject(object);
		return jsonObj.toString();
	}
	
	/**
	 * List类型转换为Json类型
	 * @param list
	 * @return list对应的json串
	 */
	public static String ListToJson(List list) {

		JSONArray jsonArray = JSONArray.fromObject(list);
		return jsonArray.toString();
	}
	
	/**
	 * Json类型转换为List<objectClass>类型
	 * @param jsonStr
	 * @param listClass
	 * @param objectClass
	 * @return jsonStr对应的list数据
	 */
	public static List jsonToList(String jsonStr, Class listClass, Class objectClass) {

		JsonConfig config = new JsonConfig();
		config.setCollectionType(listClass);
		config.setRootClass(objectClass);
		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		return (List) JSONArray.toCollection(jsonArray, config);
	}
	
	/**
	 * Json类型转换为List<objectClass>类型
	 * @param jsonStr
	 * @param objectClass
	 * @return jsonStr对应的List数据
	 */
	public static List jsonToList(String jsonStr, Class objectClass) {

		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		return (List) JSONArray.toCollection(jsonArray, objectClass);
	}
	
	/**
	 * Json类型转换为List<Map>类型
	 * @param jsonStr
	 * @return jsonStr对应的List
	 */
	public static List jsonToList(String jsonStr) {

		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		return (List) JsonUtil.parseJSONObj(jsonArray);
	}
	
	/**
	 * Json类型转换为Array<objectClass>类型
	 * @param jsonStr
	 * @param objectClass
	 * @return jsonStr对应的array对象
	 */
	public static Object jsonToArray(String jsonStr, Class objectClass) {

		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		return (Object) JSONArray.toArray(jsonArray, objectClass);
	}
	
	/**
	 * Json类型转换为Array类型
	 * @param jsonStr
	 * @return json对应的array类型
	 */
	public static Object jsonToArray(String jsonStr) {

		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		List list = (List) JsonUtil.parseJSONObj(jsonArray);
		return list.toArray();
	}
	
	/**
	 * Array类型转换为Json类型
	 * @param array
	 * @return array对应的json串
	 */
	public static String ArrayToJson(Object array) {

		JSONArray jsonArray = JSONArray.fromObject(array);
		return jsonArray.toString();
	}
	
	/**
	 * 递归地将JSONArray转换为List对象，将JSONObject转换为Map对象
	 * 
	 * @param obj
	 * @return
	 */
	private static Object parseJSONObj(Object obj) {

		Object result = null;
		if (obj == null) {
			// error
			return null;
		} else {
			if (obj instanceof JSONArray) {
				JSONArray arrayObj = (JSONArray) obj;
				List<Object> list = new ArrayList<Object>();
				for (Object element : arrayObj.toArray()) {
					list.add(JsonUtil.parseJSONObj(element));
				}
				result = list;
			} else if (obj instanceof JSONObject) {
				JSONObject jsonObj = (JSONObject) obj;
				Map<String,Object> map = new HashMap<String,Object>();
				for (Object key : jsonObj.keySet()) {
					map.put(key.toString(), JsonUtil.parseJSONObj(jsonObj.get(key.toString())));
				}
				return map;
			} else {
				result = obj;
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String jsonStr =FileUtil.read("F:\\testJson.txt");//"{\\"value\\":{\\"cinemas\\":{\\"TotalCount\\": 6,List:[{Id: 3680,NameCn:\\"大地数字影院-苏州昆山乐购影城（暂未营业）\\",Latitude: 31.3508,Longitude: 120.9603,Address: \\"昆山市柏庐南路999号乐购超市3楼\\",Telephone: \\"0512-36625960\\", TrafficRoutes: \\"乘坐3、4、5、7、12、16、18、33、34、58、111、117、122、124、125、126、206路公交车到达\\",DistrictId: 0}]}}"; 
		//"[{'PayFlag':3,'SID':'0000059814','Stype':1,'Stime':'2012-10-29 11:50:38','Mobile':'15810437404','CinemaName':'首都电影院西单店','HallName':'1号厅','FilmTime':'2012-10-29 22:35:00','Amount':30.00,'FilmName':'飓风营救2（数字）','EffectiveTime':'2012-10-29 11:50:37','TicketID':'598140','Pwd':'','SeatInfo':'8:01','RecordCount':0,'PayType':9990,'Sign':'070cccd7edabc7d1eb6d700e2f3f1dca'}]";
        System.out.println(JsonUtil.jsonToMap(jsonStr));
	}
}
