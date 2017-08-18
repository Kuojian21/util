package com.tools.code;

import com.google.common.base.Strings;

class Util {
	static String c2pName(String col) {
		if (Strings.isNullOrEmpty(col)) {
			return "";
		}
		StringBuilder ret = new StringBuilder();
		String[] cells = col.split("_");
		ret.append(cells[0].toLowerCase());
		for (int i = 1; i < cells.length; i++) {
			ret.append(cells[i].substring(0, 1).toUpperCase()).append(cells[i].substring(1));
		}
		return ret.toString();
	}

	static String c2pType(String ctype) {
		if (Strings.isNullOrEmpty(ctype)) {
			return "";
		}
		ctype = ctype.toLowerCase();
		if (ctype.contains("int")) {
			return "Integer";
		} else if (ctype.startsWith("decimal")) {
			return "java.math.BigDecimal";
		} else if (ctype.startsWith("varchar")) {
			return "String";
		} else if (ctype.startsWith("date")) {
			return "java.sql.Date";
		} else if (ctype.startsWith("datetime")) {
			return "java.sql.Timestamp";
		}
		return "";
	}

	static String c2mType(String ctype) {
		if (Strings.isNullOrEmpty(ctype)) {
			return "";
		}
		ctype = ctype.toLowerCase();
		if (ctype.startsWith("bigint")) {
			return "INTEGER";
		} else if (ctype.startsWith("decimal")) {
			return "DECIMAL";
		} else if (ctype.startsWith("varchar")) {
			return "VARCHAR";
		} else if (ctype.startsWith("date")) {
			return "DATE";
		} else if (ctype.startsWith("datetime")) {
			return "TIMESTAMP";
		}
		return "";
	}

}
