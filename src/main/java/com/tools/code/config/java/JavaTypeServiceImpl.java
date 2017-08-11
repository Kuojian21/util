package com.tools.code.config.java;

public class JavaTypeServiceImpl extends JavaType{
	@Override
	public String tpl() {
		return "/velocity/template/javacode/service_impl.tpl";
	}

	@Override
	public String subPkg() {
		return "service.impl";
	}

	@Override
	public String nameSuffix() {
		return "ServiceImpl";
	}
}
