package com.netease.common.exception;
/**
 * 基础框架定义的应用异常类，具体业务可以继承或者直接使用该业务异常<br />
 * 主要针对业务系统非运行时异常<br /> 
 * 
 * @author 开发支持中心 *
 */
public class AppException extends Exception {
	private static final long serialVersionUID = 2185426810977277130L;

	public AppException()
	{
		super();
	}

	public AppException(String msg)
	{
		super(msg);
	}

	public AppException(Exception e)
	{
		super(e);
	}

	public AppException(String msg, Exception e)
	{
		super(msg, e);
	}

}
