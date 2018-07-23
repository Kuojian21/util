package com.test.model;

import java.sql.SQLException;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.mysql.cj.jdbc.Driver;
import com.tool.kj.model.MakeModel;

public class ModelTest {

	public static void main(String[] args) throws SQLException {
		if (args.length != 4) {
			return;
		}
		MakeModel.mysql(new SimpleDriverDataSource(new Driver(), args[0], args[1], args[2]), args[3]);
		
	}

}
