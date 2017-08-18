package com.tools.code;

import java.util.List;

import com.beust.jcommander.internal.Lists;

public class Model {
	private String base;
	private String pkg;
	private String name;
	private String table;
	private List<Property> properties = Lists.newArrayList();
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	
	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public void addProperties(Property... properties){
		for(Property property : properties){
			this.properties.add(property);
		}
	}
}
