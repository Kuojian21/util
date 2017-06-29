package com.netease.common.exception;
/**
 * 基础框架定义的解密异常类，继承异常类AppException<br />
 * DESUtil等解密如果产生异常将对外抛出该异常<br /> 
 * 
 * @author 开发支持中心 *
 */
public class DecryptException extends AppException {
	private static final long serialVersionUID = 7291789260653914087L;

	public DecryptException() {
		super();
	}

	public DecryptException(String msg) {
		super(msg);
	}
	
	public DecryptException(Exception e) {
		super(e);
	}
	
	public DecryptException(String msg, Exception e) {
		super(msg,e);
	}

}
