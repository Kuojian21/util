package com.tools.logger;

public class LogConstant {

	private LogConstant(){}
	
	public static final UserLogger runLog = UserLogger.getLogger("run.log");
	
	private static final ThreadLocal<Long> businessHolder = new ThreadLocal<Long>();
	
	public static Long generate(){
		businessHolder.set(System.currentTimeMillis());
		return businessHolder.get();
	}
	
	public static Long destroy(){
		Long r = businessHolder.get();
		businessHolder.set(null);
		return r;
	}
	
	public static Long get(){
		return businessHolder.get();
	}
	
}
