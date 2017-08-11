package com.tools.code.config.java;

public class JavaTypeCron extends JavaType{
	@Override
	public String tpl() {
		return "/velocity/template/javacode/cron.tpl";
	}

	@Override
	public String subPkg() {
		return "cron";
	}

	@Override
	public String nameSuffix() {
		return "Cron";
	}
}
