package com.tools.code.config.java;

public class JavaTypeController extends JavaType{
	@Override
	public String tpl() {
		return "/velocity/template/javacode/controller.tpl";
	}

	@Override
	public String subPkg() {
		return "controller";
	}

	@Override
	public String nameSuffix() {
		return "Controller";
	}
}
