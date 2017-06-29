package com.netease.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * properties配置加载类 加载classpath下的.properties配置文件
 * 
 * @author 开发支持中心。
 */
public class PropertyUtil {
	
	private static Logger logger = Logger.getLogger(PropertyUtil.class);
	
	public static String config = "basic-framework.properties";
	private static Map<String, String> config_map = new HashMap<String, String>();	
//	private ResourceBundle bundle = null;
	
	static {
		auto_load(config);		
	}	
		
	@SuppressWarnings("rawtypes")
	private static void auto_load(String name) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		Properties p = new Properties();
		try {
			p.load(is);
			if (config.equals(name)) {
				for (Map.Entry e : p.entrySet()) {
					config_map.put((String)e.getKey(), (String)e.getValue());
				}
			}			
		} catch (IOException e) {
			logger.fatal("load property file failed. file name: " + name, e);
		}
	}
	
	public static String getProperty(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		return config_map.get(key);
	}


	public static void main(String[] args){
		System.out.println(getProperty("image.temp.dir"));
	}	
}
