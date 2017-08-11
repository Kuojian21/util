package com.tools.code.config.java;

import com.tools.code.config.Type;

public abstract class JavaType implements Type{
	
	public abstract String subPkg();
	public abstract String nameSuffix();
	
	public String fullPkg(){
		return null;
	}
	
	public String fullName(){
		return null;
	}
	
	@Override
	public String path(String base){
		return base + "/" + this.subPkg();
	}
	@Override
	public String name(String base){
		return base + this.nameSuffix();
	}
	@Override
	public String ext(){
		return ".java";
	}
	
}
