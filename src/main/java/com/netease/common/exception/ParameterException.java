package com.netease.common.exception;
/**
 * 基础框架定义的参数异常类，继承异常类Exception<br />
 * 工具类中如果收到非法的参数，将对外抛出该异常<br /> 
 * 
 * @author 开发支持中心 *
 */
public class ParameterException extends Exception {
	private static final long serialVersionUID = 1061848946235758710L;

	public ParameterException() {
		super();
	}
	
	public ParameterException(String msg) {
		super(msg);
	}
	
	public ParameterException(Exception e) {
		super(e);
	}

	public ParameterException(String msg, Exception e) {
		super(msg,e);
	}

}
