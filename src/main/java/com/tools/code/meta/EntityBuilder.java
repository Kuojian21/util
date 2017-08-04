package com.tools.code.meta;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.fastjson.JSON;

public class EntityBuilder {

	public static Entity build(Connection connection, String table) {
		Statement stmt = null;
		try {
			Entity entity = new Entity();
			entity.setTableName(table);
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " where 1 = 2");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				Property p = new Property();
				p.setColumn(rsmd.getColumnName(i).toLowerCase(),rsmd.getColumnTypeName(i).toUpperCase());
				entity.addProperties(p);
			}
			System.out.println(JSON.toJSONString(entity));
			return entity;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} 
	}

}
