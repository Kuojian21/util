package com.tools.code;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.tools.code.meta.Entity;

public class EntityBuilder {

	public static Entity build(Connection connection, String table) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String columnName = rsmd.getColumnName(i).toLowerCase();
				String typeName = rsmd.getColumnTypeName(i).toUpperCase();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

		}

		return null;

	}

}
