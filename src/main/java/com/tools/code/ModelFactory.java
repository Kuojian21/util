package com.tools.code;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.fastjson.JSON;

public class ModelFactory {

	public static Model mysql(Connection connection, String table) {
		Statement stmt = null;
		Model model = new Model();
		try {
			model.setTable(table);
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("show full columns from " + table);
			while(rs.next()){
				
			}
			System.out.println(JSON.toJSONString(model));
			return model;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return model;
	}

}
