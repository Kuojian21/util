package com.tools.code.config.java;

public class JavaTypeService extends JavaType{
	@Override
	public String tpl() {
		return "/velocity/template/javacode/service.tpl";
	}

	@Override
	public String subPkg() {
		return "service";
	}

	@Override
	public String nameSuffix() {
		return "Service";
	}
	
}
