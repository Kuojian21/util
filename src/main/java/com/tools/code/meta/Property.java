package com.tools.code.meta;

public class Property {
	private String columnName;
	private String columnType;
	private String propertyName;
	private String propertyType;
	public String getColumnName() {
		return columnName;
	}
	public String getColumnType() {
		return columnType;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setColumn(String columnName,String columnType) {
		this.columnName = columnName;
		this.columnType = columnType;
	}
	public void setProperty(String propertyName,String propertyType) {
		this.propertyName = propertyName;
		this.propertyType = propertyType;
	}
}
