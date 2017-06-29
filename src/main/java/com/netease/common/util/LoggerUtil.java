package com.netease.common.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
/**
 * 日志工具类
 */
public class LoggerUtil {

	/**支付（与网易宝通信）相关业务的Logger*/
	public static void epayInfo(Object msg) {
		Logger.getLogger("epayLogger").info(msg);
	}
	
	/**记录外部系统调用商城接口的Logger*/
	public static void outInInfo(Object msg) {
		Logger.getLogger("outInLogger").info(msg);
	}
	
	/**记录商城调用外部系统接口的Logger*/
	public static void inOutInfo(Object msg) {
		Logger.getLogger("inOutLogger").info(msg);
	}
	
	/**订单管理模块的Logger*/
	public static void orderInfo(Object msg) {
		Logger.getLogger("orderLogger").info(msg);
	}
	
	/**押宝管理模块的Logger*/
	public static void indiInfo(Object msg) {
		Logger.getLogger("indiLogger").info(msg);
	}
	
	/**商品管理模块的Logger*/
	public static void productInfo(Object msg) {
		Logger.getLogger("productLogger").info(msg);
	}
	
	/**登录模块的Logger*/
	public static void loginInfo(Object msg) {
		Logger.getLogger("loginLogger").info(msg);
	}
	
	/**红包发放logger**/
	public static void hongBaoInfo(Object msg) {
		Logger.getLogger("hongBaoLogger").info(msg);
	}
	/**话费模块Logger*/
	public static void mobileInfo(Object msg) {
		Logger.getLogger("mobileLogger").info(msg);
	}
	
	/**debugLogger*/
	public static void debug(Object msg) {
		Logger.getLogger("debugLogger").debug(msg);
	}
	
	/** 订单报警logger */
	public static void orderAlarmInfo(Object msg) {
		Logger.getLogger("orderAlarmLogger").info(msg);
	}

	/**物流查询logger**/
	public static void logisticsInfo(Object msg) {
		Logger.getLogger("logisticsLogger").info(msg);
	}
	
	/** 活动接口的logger */
	public static void activityInfo(Object msg) {
		Logger.getLogger("activityLogger").info(msg);
	}
	
	/** 优惠券接口的logger */
	public static void couponInfo(Object msg) {
		Logger.getLogger("couponLogger").info(msg);
	}
	/** 余额自动充值接口的logger */
	public static void autoChargeInfo(Object msg) {
		Logger.getLogger("autochargeLogger").info(msg);
	}
	/** 查询座位信息接口的logger */
	public static void querySeatErrorInfo(Object msg) {
		Logger.getLogger("queryseatLogger").info(msg);
	}
	/**影评关键词过滤&审核接口的logger*/
	public static void commentAuditInfo(Object msg){
		Logger.getLogger("commentAuditLogger").info(msg);
	}
	
	/**发送验证码短信接口的logger*/
	public static void sendMessageInfo(Object msg){
		Logger.getLogger("sendMessageLogger").info(msg);
	}
	/** 
	 * 需人工干预的报警logger 
	 * 调用程序：1：大部分不用改，还是用LoggerUtil.alarmInfo
	 * */
	public static void alarmInfo(Object msg) {
		if(StackTraceUtil.checkIsBatchCallClass()){
			LoggerUtil.alarmInfoStrategy1(msg); 
		}else{
			LoggerUtil.alarmInfoBasic("[comn] "+msg); 			
		}
	}
	
	/**60m,10
     * 改成 LoggerUtil.alarmInfoStrategy1 或者 LoggerUtil.alarmInfoStrategy2 等
	 * 以这个为准吧，注意下 '[comn] ' '[60m,10] ' 和后面的msg之间有个空格，也是应运维的需求
	 */
	public static void alarmInfoStrategy1(Object msg) {
		LoggerUtil.alarmInfoBasic("[60m,10] "+msg); 		
	}
	/**5m,10*/
	public static void alarmInfoStrategy2(Object msg) {
		LoggerUtil.alarmInfoBasic("[5m,10] "+msg); 		
	}
	
	/**10m,1*/
	public static void alarmInfoStrategy10m1(Object msg) {
		LoggerUtil.alarmInfoBasic("[10m,1] "+msg); 		
	}
	
	/**10m,10*/
	public static void alarmInfoStrategy10m10(Object msg) {
		LoggerUtil.alarmInfoBasic("[10m,10] "+msg); 		
	}
	
	/** 需人工干预的报警logger */
	protected static void alarmInfoBasic(Object msg) {	
		Logger.getLogger("alarmLogger").info(msg); 
	}
	
	public static void alarmInfo(String msg, Exception e) {
		printAlarm(Logger.getLogger("alarmLogger"), msg, e);
	}

	
	/** 缓存logger */
	public static void cacheInfo(Object msg) {
		Logger.getLogger("cacheLogger").info(msg);
	}
	
	/**用户积分logger**/
	public static void userPointInfo(Object msg){
		Logger.getLogger("userPointLogger").info(msg);
	}

	public static void printAlarm(Logger logger, String msg, Exception e) {
		if (msg != null) {
			logger.info(msg);
		}
		if (e != null) {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			e.printStackTrace(new PrintWriter(buf, true));
			logger.error(buf.toString());
		}
	}
	
	/**errorLogger*/
	public static void error(String msg, Exception e) {
		printError(Logger.getLogger("errorLogger"), msg, e);
	}
	
	public static void printError(Logger logger, String msg, Exception e) {
		if (msg != null) {
			logger.error(msg);
		}
		if (e != null) {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			e.printStackTrace(new PrintWriter(buf, true));
			logger.error(buf.toString());
		}
	}
	/**errorLogger*/
	public static void error(String msg, Throwable e) {

		printError(Logger.getLogger("errorLogger"), msg, e);
	}
	
	public static void printError(Logger logger, String msg, Throwable e) {
		if (msg != null) {
			logger.error(msg);
		}
		if (e != null) {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			e.printStackTrace(new PrintWriter(buf, true));
			logger.error(buf.toString());
		}
	}
	
	
}
