package com.netease.common.exception;
/**
 * 基础框架定义的加密异常类，继承异常类AppException<br />
 * DESUtil等加密如果产生异常将对外抛出该异常<br /> 
 * 
 * @author 开发支持中心 *
 */
public class EncryptException extends AppException {
	
	private static final long serialVersionUID = 3424864071960800551L;

	public EncryptException() {
		super();
	}
	
	public EncryptException(String msg) {
		super(msg);
	}
	public EncryptException(Exception e) {
		super(e);
	}

	public EncryptException(String msg, Exception e) {
		super(msg,e);
	}

}
