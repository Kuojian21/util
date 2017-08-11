package com.tools.code.config.java;

public class JavaTypeDaoImpl extends JavaType{
	@Override
	public String tpl() {
		return "/velocity/template/javacode/dao_impl.tpl";
	}

	@Override
	public String subPkg() {
		return "dao.impl";
	}

	@Override
	public String nameSuffix() {
		return "DaoImpl";
	}
}
