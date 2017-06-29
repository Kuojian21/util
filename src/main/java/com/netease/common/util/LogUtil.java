package com.netease.common.util;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;


/**
 * debug、info、error级别的日志记录工具类。IP地址通过NDC的方式实现。<br/>
 *  
 * @author 开发支持中心
 */
public class LogUtil {

	public static final String LOG_WARN = "ErrorLog";

	public static final String LOG_ERROR = "ErrorLog";

	//基础框架里输出的日志。
	public static final String LOG_BASIC_FRAMEWORK = "basicFrameworkFile";

	protected static final String VALUE_EMPTY_IP = "--.--.--.--";

	//protected static final String VALUE_EMPTY_ROOT = "";


	protected static final String VALUE_EMPTY_MSG = "";

	// protected String loginUser;

	// protected String commisionUser;

	//protected String ipAddress;

	//protected String root;

	//protected static ThreadLocal<String> loginUser = new ThreadLocal<String>();
	//protected static ThreadLocal<String> commisionUser = new ThreadLocal<String>();
	protected static ThreadLocal<String> ipAddress = new ThreadLocal<String>();

	public LogUtil() {

	}

	/*public LogUtil(String root) {
		this.setRoot(root);
	}

	public LogUtil(Class clazz) {
		this.setRoot(clazz);
	}*/

	public static LogUtil getInstance() {
		return new LogUtil();
	}

/*	public static LogUtil getInstance(String root) {
		return new LogUtil(root);
	}

	public static LogUtil getInstance(Class clazz) {
		String root = null;
		if (clazz != null) {
			root = clazz.getName();
		}
		return getInstance(root);
	}*/

	/*public String getRoot() {
		if (root == null || "".equals(root)) {
			return VALUE_EMPTY_ROOT;
		} else {
			return root;
		}
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public void setRoot(Class clazz) {
		if (clazz != null) {
			this.root = clazz.getName();
		}
	}
*/
	public void setIpAddress(String ipAddress) {
		LogUtil.ipAddress.set(ipAddress);
	}
	
	private String getIpAddress() {
		if (ipAddress.get() == null || "".equals(ipAddress.get())) {
			return VALUE_EMPTY_IP;
		} else {
			return ipAddress.get();
		}
	}

	

	/*public void debug(String msg) {
		Logger logger = Logger.getLogger(LOG_DEBUG + ":" + this.getRoot());
		logger.debug(msg);
	}*/
	
	/**
	 * 写debug日志。
	 * 
	 * @param loggerName
	 *        日志配置文件中logger的name。
	 * @param msg
	 *        日志信息。
	 * 
	 */
	public static void debug(String loggerName,String msg) {
		
		Logger logger = Logger.getLogger(loggerName);
		logger.debug(msg);
		
	}

	/**
	 * 写WARN日志。
	 * 
	 * @param msg
	 *        日志信息。
	 *
	 */
	public void warn(String msg) {
		Logger logger = Logger.getLogger(LOG_WARN);
		logger.warn(msg);
	}

	/**
	 * 写error日志。error级别的日志统一输出到固定的配置文件中。
	 * 
	 * @param msg
	 *        日志信息。
	 * 
	 */
	public static void error(String msg) {
		Logger logger = Logger.getLogger(LOG_ERROR);
		logger.error(msg);
	}
	
    /**
	 * 写error日志。error级别的日志统一输出到固定的配置文件中。
	 * 
	 * @param e
	 *        抛出的异常类。
	 * 
	 */
	public static void error(Throwable e) {
		Logger logger = Logger.getLogger(LOG_ERROR);
		logger.error(e.getMessage(), e);
	}

    /**
	 * 写error日志。error级别的日志统一输出到固定的配置文件中。
	 * 
	 * @param msg
	 *        日志信息。
	 * @param e
	 *        抛出的异常类。
	 *
	 */
	public static void error(String msg, Throwable e) {
		Logger logger = Logger.getLogger(LOG_ERROR );
		logger.error(msg, e);
	}

	/**
	 * 写INFO级别的日志。
	 * @param msg
	 *        日志内容。
	 * @param loggerName
	 *        配置文件中的logger name。
	 * 
	 */
	public void info(String loggerName, String msg) {
		try {
			if (loggerName == null || "".equals(loggerName)) {
				return;
			}

			NDC.push(this.getIpAddress());
			Logger logger = Logger.getLogger(loggerName);
			logger.info(msg == null ? VALUE_EMPTY_MSG : msg);
			NDC.pop();
			NDC.remove();
		} catch (Exception e) {
			error(e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
