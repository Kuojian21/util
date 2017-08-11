package com.tools.code.config.java;

public class JavaTypeDao extends JavaType{
	@Override
	public String tpl() {
		return "/velocity/template/javacode/dao.tpl";
	}

	@Override
	public String subPkg() {
		return "dao";
	}

	@Override
	public String nameSuffix() {
		return "Dao";
	}
}
