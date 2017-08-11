package com.tools.code.config.java;

public class JavaTypeModel extends JavaType{
	@Override
	public String tpl() {
		return "/velocity/template/javacode/model.tpl";
	}

	@Override
	public String subPkg() {
		return "model";
	}

	@Override
	public String nameSuffix() {
		return "";
	}
}
