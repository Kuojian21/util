package com.tool.kj.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.CaseFormat;

public class MakeModel {

	public static void mysql(DataSource dataSource, String table) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<Model> models = jdbcTemplate.query("show full columns from " + table, new RowMapper<Model>() {
			@Override
			public Model mapRow(ResultSet rs, int rowNum) throws SQLException {
				/*
				 * ResultSetMetaData rsmd = rs.getMetaData(); for (int i = 1; i <=
				 * rsmd.getColumnCount(); i++) { System.out.println(rsmd.getColumnName(i) + "\t"
				 * + rs.getObject(i)); }
				 */
				Model model = new Model();
				model.setName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, rs.getString("Field")));
				if (rs.getString("Type").startsWith("bigint") || rs.getString("Type").startsWith("tinyint")) {
					model.setType("Integer");
				} else if (rs.getString("Type").startsWith("varchar")) {
					model.setType("String");
				}

				model.setNul("NO".equals(rs.getString("Null")));
				model.setPrimary("PRI".equals(rs.getString("Key")));
				model.setDef(rs.getString("Default"));
				model.setComment(rs.getString("Comment"));
				return model;
			}
		});
		model(models);
	}

	public static void model(List<Model> models) {
		for (Model model : models) {
			System.out.println("/*" + model.getComment() + "*/");
			System.out.println("private " + model.getType() + " " + model.getName() + ";");
		}
	}

}
