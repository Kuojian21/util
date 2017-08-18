package com.tools.code;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.tools.database.JdbcTool;

public class ModelFactory {

	public static Model mModel(Connection conn, String table) {
		Model model = new Model();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			model.setTable(table);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("show full columns from " + table);
			while (rs.next()) {
				Property p = new Property();
				String cname = rs.getString("Field");
				p.setCname(cname);
				p.setName(Util.c2pName(cname));
				String ctype = rs.getString("Type");
				p.setCtype(ctype);
				p.setType(Util.c2pType(ctype));
				p.setMtype(Util.c2mType(ctype));
				if ("PRI".equals(rs.getString("Key"))) {
					p.setCkey(true);
				} else {
					p.setCkey(false);
				}
				p.setCcomment(rs.getString("Comment"));
				model.addProperties(p);
			}
			return model;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcTool.close(rs);
			JdbcTool.close(stmt);
		}
		return model;
	}

}
