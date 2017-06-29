package com.netease.common.util;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具类，主要对外提供如下接口：<br/>
 * 0. 提供通过请求request查询用户所在省、市的功能,对应方法：getLocationByRequest(HttpServletRequest request);<br/>
 * 1. 提供通过Ip查询所在省、市的功能,对应方法：getLocationByIp(String ip);<br/>
 * 2. 提供通过省的拼音简写查询省的中文名称,对应方法：getProvinceByAbbrs(String abbrs);<br/>
 * 3. 提供通过市的行政码查询市的中文名称,对应方法：getCityByDivisionCode(String divisionCode);<br/>
 * 4. 提供通过request获取用户ip功能，对应方法：getRemoteAddr(HttpServletRequest request);<br/>
 * 5. 提供获取服务器ip的功能，对应方法：getServerIp();<br/>
 * @author 开发支持中心
 *
 */
public class IPUtil {
	
	static Long[] minsArray = {};
	static Long[] maxsArray = {};	
	static String[] provinceArray = {};
	static String[] provinceAbbrArray = {};
	static String[] provinceAreaCodeArray = {};
	static String[] cityArray = {};
	static String[] cityDivisionCodeArray = {};
	static String[] cityAreaCodeArray = {};
	static String[] operatorArray = {};
	
	//在ip库中每条记录中对应的字段
	private final static int IP_START_INDEX = 0;//ip起始段
	private final static int IP_END_INDEX = 1;//ip终止段
	private final static int PROVINCE_INDEX = 2;//省或者直辖市的中文名称
	private final static int PROVINCE_ABBRS_INDEX = 3;//省或者直辖市的拼音简写
	private final static int PROVINCE_AREACODE_INDEX = 4;//省或者直辖市的电话区号
	private final static int CITY_INDEX = 5;//地级市的中文名称
	private final static int CITY_DIVISIONCODE_INDEX = 6;//国家行政码：一般为地级市的行政码，如果地级市不存在，则为省或者地级市的行政码
	private final static int CITY_AREACODE_INDEX = 7;//电话区号：一般为地级市的电话区号，如果地级市不存在，则为省或者地级市的电话区号
	private final static int OPERATOR_INDEX = 8;//运营商
	
	private final static String IP_FILE_NAME = "ip_province.txt";
	private final static String NULL = "";
	//省和直辖市的中文和拼音映射Map
	public static ConcurrentHashMap<String, String> provinceMapping = new ConcurrentHashMap<String, String>();
	//市的中文和行政码映射Map
	public static ConcurrentHashMap<String, String> cityMapping = new ConcurrentHashMap<String, String>();
	private static Object syn = new Object();
	private static boolean isLoading = false; // 此变量用来标识：ip地址库是否正在加载
	private static boolean loaded = false; // 此变量用来标识：ip地址库已经加载完成
	private static HashMap<String,String> centerCity = new HashMap<String,String>();
	static {
		centerCity.put("110000", "北京");
		centerCity.put("500000", "重庆");
		centerCity.put("310000", "上海");
		centerCity.put("120000", "天津");
	}
	
	/**
	 * 需要加载ip地址库时，才去加载 在加载前，先判断是否ip库已经加载
	 */
	public static void loadIPArea() {
		
		if(! isLoading && ! loaded){//还没有线程加载
			
			synchronized(syn){
				if(! isLoading && ! loaded){//还没有线程加载
					isLoading = true;
					long begin = System.currentTimeMillis();
					loaded = doLoadIPArea();
					long end = System.currentTimeMillis();
					System.out.println("load ip libaray cost: " + (end - begin) + " ms. Loaded result: " + loaded);
					isLoading = false;
				}
			}
		}

	}
	
	/**
	 * 加载ip地址库
	 * 
	 * @return
	 */
	private static boolean doLoadIPArea() {	
		ArrayList<Long> mins = new ArrayList<Long>();
		ArrayList<Long> maxs = new ArrayList<Long>();
		ArrayList<String> provinces = new ArrayList<String>();
		ArrayList<String> provinceAbbrs = new ArrayList<String>();
		ArrayList<String> provinceAreaCodes = new ArrayList<String>();
		ArrayList<String> citys = new ArrayList<String>();
		ArrayList<String> cityDivisionCodes = new ArrayList<String>();
		ArrayList<String> cityAreaCodes = new ArrayList<String>();
		ArrayList<String> operators = new ArrayList<String>();
		try {
			// 文件必须是按照数字顺序排序的
//			BufferedReader br = new BufferedReader(new FileReader(IP_FILE_NAME));
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(IP_FILE_NAME);
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] range = line.split("\\,");
				if (range.length <= PROVINCE_INDEX) {
					continue;
				}
				mins.add(new Long(range[IP_START_INDEX]));
				maxs.add(new Long(range[IP_END_INDEX]));
				
				//添加省拼音简写和中文名的映射
				fillMap(provinceMapping, range[PROVINCE_ABBRS_INDEX], range[PROVINCE_INDEX]);
				
				//添加市的行政码和中文名的映射
				fillMap(cityMapping, range[CITY_DIVISIONCODE_INDEX], range[CITY_INDEX]);
				
				//加载运营商
				fillIndex(operators, OPERATOR_INDEX, range);
				//加载地级市的电话区号
				fillIndex(cityAreaCodes, CITY_AREACODE_INDEX, range);
				//加载省的电话区号
				fillIndex(provinceAreaCodes, PROVINCE_AREACODE_INDEX, range);
				//加载地级市的国家行政代码
				fillIndex(cityDivisionCodes, CITY_DIVISIONCODE_INDEX, range);
				//加载地级市名字
				fillIndex(citys, CITY_INDEX, range);
				//加载省或者直辖市名字的拼音简写
				fillIndex(provinceAbbrs, PROVINCE_ABBRS_INDEX, range);
				//加载省或者直辖市名字
				fillIndex(provinces, PROVINCE_INDEX, range);

			}
			br.close();
			int count = mins.size();
			minsArray = new Long[count];
			maxsArray = new Long[count];
			provinceArray = new String[count];
			provinceAbbrArray = new String[count];
			provinceAreaCodeArray = new String[count];
			cityArray = new String[count];
			cityDivisionCodeArray = new String[count];
			cityAreaCodeArray = new String[count];
			operatorArray = new String[count];
			
			mins.toArray(minsArray);
			maxs.toArray(maxsArray);
			provinces.toArray(provinceArray);
			provinceAbbrs.toArray(provinceAbbrArray);
			provinceAreaCodes.toArray(provinceAreaCodeArray);
			citys.toArray(cityArray);
			cityDivisionCodes.toArray(cityDivisionCodeArray);
			cityAreaCodes.toArray(cityAreaCodeArray);
			operators.toArray(operatorArray);

		} catch (IOException e) {
			e.printStackTrace();
			return false; // 加载失败
		}
		return true; // 加载成功
	}
	
	/**
	 * 把ip库中的每条记录对应的字段放到列表中
	 * @param list
	 * @param index
	 * @param range
	 */
	public static void fillIndex(ArrayList<String> list, int index, String[] range){
		if(range.length > index){
			list.add(range[index]);
		}else{
			list.add(NULL);
		}
	}
	
	/**
	 * 把ip库中字段映射到Map
	 * @param map
	 * @param key
	 * @param value
	 */
	public static void fillMap(Map<String, String> map, String key, String value){
		if(key != null && ! NULL.equals(key) && ! NULL.equals(value)){
			if(! map.containsKey(key)){
				map.put(key, value);
			}
		}
	}
	
	/**
	 * 通过IP获得省份
	 */
	public static Location getLocationByIp(String ip) {
	
		loadIPArea();
		long ipValue = ipToLong(ip);
		int fitIndex = Arrays.binarySearch(minsArray, ipValue);
		if (fitIndex < 0) {
			// 小于最小范围的最小值，不在特殊范围内
			if (fitIndex == -1) {
				return new Location();
			}
			if (fitIndex == (-1 - minsArray.length)) {
				// 大于最大范围的最小值，检查是否在最后一个范围内
				if (ipValue > maxsArray[maxsArray.length - 1]) {
					return new Location();
				}
			}
			// 是否属于某一范围
			if (ipValue > minsArray[0 - fitIndex - 2] && ipValue <= maxsArray[0 - fitIndex - 2]) {
				return getLocation(0 - fitIndex - 2);
			} else {
				return new Location();
			}
		}
		// 等于某一范围的最小值
		return getLocation(fitIndex);
	}

	/**
	 * 把ip库字段信息封装到Location类中
	 * @param index
	 * @return 获取Location
	 */
	public static Location getLocation(int index) {
	
		Location location = new Location();
		location.setProvince(provinceArray[index]);
		location.setProvinceAbbr(provinceAbbrArray[index]);
		location.setProvinceAreaCode(provinceAreaCodeArray[index]);
		location.setCity(cityArray[index]);
		location.setCityDivisionCode(cityDivisionCodeArray[index]);
		location.setCityAreaCode(cityAreaCodeArray[index]);
		location.setOperator(operatorArray[index]);
		
		return location;
	}
	/**
	 * 通过request获取用户位置信息
	 * @param request
	 * @return 用户位置
	 */
	public static Location getLocationByRequest(HttpServletRequest request){
		String ip = getRemoteAddr(request);
		return getLocationByIp(ip);
	}
	
	/**
	 * 获得用户最初ip地址
	 * 
	 * @param request
	 * @return 用户请求原始ip地址
	 */
	public static final String getRemoteAddr(HttpServletRequest request) {
	
		String rip = request.getRemoteAddr();
		String xff = request.getHeader("X-Forwarded-For");
		String ip;
		if (xff != null && xff.length() != 0) {
			int px = xff.indexOf(',');
			if (px != -1) {
				ip = xff.substring(0, px);
			} else {
				ip = xff;
			}
		} else {
			ip = rip;
		}
		return ip.trim();
	}
	
	/**
	 * 将127.0.0.1 形式的IP地址转换成10进制整数，这里没有进行任何错误处理
	 * 
	 * @param strIP
	 * @return 10进制ip地址
	 */
	public static long ipToLong(String strIP) {
	
		if (isIPV4(strIP)) {
			String[] ip = strIP.split("\\x2e");
			return (Long.valueOf(ip[0]).longValue() << 24) + (Long.valueOf(ip[1]).longValue() << 16) + (Long.valueOf(ip[2]).longValue() << 8) + Long.valueOf(ip[3]).longValue(); // ip1*256*256*256+ip2*256*256+ip3*256+ip4
		} else {
			return 0;
		}
	}
	
	/**
	 * 判断ip是否是ipv4
	 * @param ip
	 * @return 是否IPV4判断结果
	 */
	public static boolean isIPV4(String ip){
		if(ip == null || ip.length() == 0)
			return false;
		String reg = "^((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(ip);
		return m.matches();
	}
	
	/**
	 * 调用shell取服务器的ip地址。多个内外网地址之间用,分隔。
	 * 
	 * @return 用分隔符,分隔的多个ip地址。
	 */
	public static String getServerIp() {
	
		StringBuffer ips = new StringBuffer();
		try {
			String[] command = { "/bin/sh", "-c", "/sbin/ifconfig | grep 'Bcast' | awk '{print $2}' | sed -e 's/addr.//g'" };
			Process process = Runtime.getRuntime().exec(command);
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			BufferedReader input = new BufferedReader(ir);
			String line = "";
			while ((line = input.readLine()) != null) {
				ips.append(line).append(",");
			}
			input.close();
			ir.close();
			input = null;
			ir = null;
			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();
			process.destroy();
			process = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ips.toString();
	}
	
	/**
	 * 获取linux机器的内网ip地址。
	 * 
	 * @return 内网IP地址
	 */
	public static String getIP() {
	
		String serverIP = getServerIp();
		if (serverIP != null) {
			String ipArray[] = serverIP.split(",");
			// 取最后一个IP地址 , 内网IP地址。
			if (ipArray != null && ipArray.length > 0) {
				return ipArray[ipArray.length - 1];
			}
			return null;
		}
		return null;
	}
	
	
	/**
	 * 通过省的拼音简写查找省的中文名称
	 * @param abbrs
	 * @return 省份中文名称
	 */
	public static String getProvinceByAbbrs(String abbrs){
		if(abbrs == null){
			return null;
		}
		loadIPArea();
		return provinceMapping.get(abbrs);
	}
	
	/**
	 * 通过行政划分码查找对应的地级市中文名称
	 * @param divisionCode
	 * @return 地级市中文名称
	 */
	public static String getCityByDivisionCode(String divisionCode){
		if(divisionCode == null){
			return null;
		}
		loadIPArea();
		String city = cityMapping.get(divisionCode);
		if(city == null){
			//查看行政码是否是直辖市
			return centerCity.get(divisionCode);
		}
		return city;
	}
	
	public static void main(String[] args) throws Exception{
		
//		loadIPArea();
//		String ip = "61.135.255.88";
//		Location l = getCityByIp(ip);
//		System.out.println(l.toString());
		String[] abbrs = new String[]{
			"shd","guz","jix","chq","nmg","hub","lin","hun","jil",
			"fuj","shh","bej","gux","gud","sic","han","yun","anh",
			"jis","zhj","qih","nix","hlj","tij","sxi","gas","xij",
			"hen","heb","shx","xiz","kjw","rxt","cck","ttw","dxw",
			"ytw","wtw","ltw","fkx","wst",                       
		};
		String[] divisionCodes = new String[]{
			"653200",
			"654000",
			"654002",
			"654003",
			"654200",
			"654202",
			"654300",
			"659001",
			"500000",
			"640000",                                
		};
		for(int i = 0; i < divisionCodes.length; i++){
			new Thread(new Runnable(){
				public void run(){
					String ip = "61.135.255.88";//createIp();
					Location l = getLocationByIp(ip);
					System.out.println(ip + "	" + l.toString());
				}
				
			}).start();
			Thread.sleep(100);
//			String p = getProvinceByAbbrs(abbrs[i]);
//			System.out.println(p + " " + abbrs[i]);
			String c = getCityByDivisionCode(divisionCodes[i]);
			System.out.println(c + " " + divisionCodes[i]);
//			System.out.println(createIp());
		}
		
	}
	//for test
	public static String createIp(){
		StringBuffer sb = new StringBuffer();
		int ip; 
		for(int i = 0; i < 4; i++){
			ip = new Double(Math.random() * 255).intValue();
			sb.append(ip).append(".");
		}
		return sb.substring(0, sb.lastIndexOf("."));
	}
	
}

