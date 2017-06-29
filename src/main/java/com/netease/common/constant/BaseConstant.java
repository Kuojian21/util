package com.netease.common.constant;

import java.util.Enumeration;
import java.util.HashMap;

import com.netease.common.util.PropertyUtil;

/**
 * 基础框架用到的全局变量。包括：<br />
 * 1.公司账户系统的域名后缀。<br />
 * 2.basic-framework.properties文件中的配置信息。<br />
 * 
 * @author 开发支持中心 
 */
public class BaseConstant {
	//帐号域。
	public static final String DOMAIN_163 = "@163.com";
	public static final String DOMAIN_126 = "@126.com";
	public static final String DOMAIN_YEAH = "@yeah.net";
	public static final String DOMAIN_188 = "@188.com";
	public static final String DOMAIN_188_VIP = "@vip.188.com";
	public static final String DOMAIN_VIP = "@vip.163.com";
	public static final String DOMAIN_POPO = "@popo.163.com";
	public static final String DOMAIN_NETEASE = "@netease.com";

	/**
	 * 取basic-framework.properties配置文件中key的值。
	 * 
	 * @param key
	 *        配置文件中等号左边的值。
	 * @return
	 *        key对应的value。
	 */
	public static String getValue(String key) {
		return PropertyUtil.getProperty(key);
	}


}
