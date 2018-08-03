package com.tool.kj.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.CaseFormat;

public class MakeModel {

	public static void mysql(DataSource dataSource, String table) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<Cell> cells = jdbcTemplate.query("show full columns from " + table, new RowMapper<Cell>() {
			@Override
			public Cell mapRow(ResultSet rs, int rowNum) throws SQLException {

//				ResultSetMetaData rsmd = rs.getMetaData();
//				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//					System.out.println(rsmd.getColumnName(i) + "\t" + rs.getObject(i));
//				}

				Cell model = new Cell();
				model.setName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, rs.getString("Field")));
				if (rs.getString("Type").startsWith("tinyint") || rs.getString("Type").startsWith("int")) {
					model.setType("Integer");
				} else if (rs.getString("Type").startsWith("bigint")) {
					model.setType("Long");
				} else if (rs.getString("Type").startsWith("varchar") || rs.getString("Type").endsWith("text")) {
					model.setType("String");
				}

				model.setNul("NO".equals(rs.getString("Null")));
				model.setPrimary("PRI".equals(rs.getString("Key")));
				model.setDef(rs.getString("Default"));
				model.setComment(rs.getString("Comment"));
				return model;
			}
		});
		Model model = new Model();
		model.setName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, table));
		model.setCells(cells);
		model(model);
	}

	public static void model(Model model) {
		System.out.println("public class " + model.getName() + "{");
		for (Cell cell : model.getCells()) {
			System.out.println("\t/*" + cell.getComment() + "*/");
			System.out.println("\tprivate " + cell.getType() + " " + cell.getName() + ";");
		}
		System.out.println("}");
	}

}
