package com.tools.code.meta;

import java.util.List;

import com.beust.jcommander.internal.Lists;

public class Entity {
	private String entityName;
	private String tableName;
	private List<Property> properties = Lists.newArrayList();
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
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
