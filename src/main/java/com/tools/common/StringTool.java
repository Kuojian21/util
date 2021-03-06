package com.tools.common;

public class StringTool {

	private StringTool() {

	}

	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static String toString(Object obj, String def) {
		if (obj == null) {
			return def;
		}
		return obj.toString();
	}

	public static String toString(Object obj) {
		return toString(obj, "");
	}
	
}
