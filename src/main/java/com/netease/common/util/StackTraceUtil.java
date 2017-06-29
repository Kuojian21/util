package com.netease.common.util;

/**
 * 获取方法调用栈，已经判断当前调用是否是批处理调用
 * @author bjjpdu
 *
 */
public class StackTraceUtil {
	/**获取当前线程的调用栈*/
	public static StackTraceElement[] obtainCurrentStackTraceByThread(){
		StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
		return stackElements;
	}
	
	/**获取当前线程的调用栈通过Throwable*/
	public static StackTraceElement[] obtainCurrentStackTraceByThrowable(){
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		return stackElements;
	}
	
	/**判断是否批处理调用，根据类名，类名其实可以作为参数*/
	public static boolean checkIsBatchCallClass(){
		boolean isBatchCall = false;
		StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
		if(stackElements != null){
	        for(int i = 0; i < stackElements.length; i++){
	        	//批处理订单重发ResendAcion.resendOrder
	        	if(stackElements[i].getClassName().equalsIgnoreCase("com.netease.movie.controller.schedule.ResendController")){
	        		isBatchCall =true;
	        		return isBatchCall;
	        	}
//	        	LoggerUtil.debug(stackElements[i].getClassName());
//	        	LoggerUtil.debug(stackElements[i].getMethodName());
	        }
	    }
		return isBatchCall;
	}
	
	/**判断是否批处理调用，根据类名，类名其实可以作为参数，并直接记录日志*/
	public static boolean checkIsBatchCallClassAndMakeAlarmLog(String alarmInfo){
		boolean isBatchCall = false;
		StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
		if(stackElements != null){
	        for(int i = 0; i < stackElements.length; i++){
	        	//批处理订单重发ResendAcion.resendOrder
	        	if(stackElements[i].getClassName().equalsIgnoreCase("com.netease.movie.controller.schedule.ResendController")){
	        		isBatchCall =true;
	        		alarmInfo="[批处理]"+alarmInfo;
	        		break;
	        	}
//	        	LoggerUtil.debug(stackElements[i].getClassName());
//	        	LoggerUtil.debug(stackElements[i].getMethodName());
	        }
	    }
		LoggerUtil.alarmInfo(alarmInfo);
		return isBatchCall;
	}
	
	/**通过类名，方法重载*/
	public static boolean checkIsBatchCallClass(String className){
		boolean isBatchCall = false;
		StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
		if(stackElements != null){
	        for(int i = 0; i < stackElements.length; i++){
	        	//批处理订单重发ResendAcion.resendOrder
	        	if(stackElements[i].getClassName().equalsIgnoreCase(className)){
	        		isBatchCall =true;
	        		return isBatchCall;
	        	}
//	        	LoggerUtil.debug(stackElements[i].getClassName());
//	        	LoggerUtil.debug(stackElements[i].getMethodName());
	        }
	    }
		return isBatchCall;
	}
	
	/**判断是否批处理，根据类名及方法名*/
	public static boolean checkIsBatchCallMethod(String className,String method){
		boolean isBatchCall = false;
		StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
		if(stackElements != null){
	        for(int i = 0; i < stackElements.length; i++){
	        	//批处理订单重发ResendAcion.resendOrder
	        	if(stackElements[i].getClassName().equalsIgnoreCase(className)
	        			&&stackElements[i].getMethodName().equalsIgnoreCase(method)){
	        		isBatchCall =true;
	        		return isBatchCall;
	        	}
//	        	LoggerUtil.debug(stackElements[i].getClassName());
//	        	LoggerUtil.debug(stackElements[i].getMethodName());
	        }
	    }
		return isBatchCall;
	}
}
