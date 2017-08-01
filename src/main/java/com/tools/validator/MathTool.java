package com.tools.validator;

import java.util.regex.Pattern;

public class MathTool {
	
	/**
	 * 判断str与正则表达式regexp是否匹配。
	 */
	public static boolean match(String str, String regexp) {
		if (isEmpty(str)) {
			return false;
		}
		return Pattern.matches(regexp, str);
	}

	/**
	 * 判断字符串是否为null或trim()后的长度为0.
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}


	/**
	 * 判断传入的字符串内容是否是int类型的数据。
	 */
	public static boolean isInt(String str) {
		if (isEmpty(str)){
			return false;
		}
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断传入的字符串内容是否是正整数。
	 */
	public static boolean isPositiveInt(String str) {
		if (isEmpty(str)){
			return false;
		}
		try {
			int tmp = Integer.valueOf(str);
			return tmp >= 0;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断传入的字符串里的内容是否是浮点数（float）。
	 */
	public static boolean isFloat(String str) {
		if (isEmpty(str)){
			return false;
		}
		try {
			Float.parseFloat(str);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * 判断传入的字符串里的内容是否是正浮点数（positive float）。
	 */
	public static boolean isPositiveFloat(String str) {
		if (isEmpty(str)){
			return false;
		}
		try {
			float num = Float.parseFloat(str);
			return (num >= 0);
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * 校验long类型的数据。
	 */
	public static boolean isLong(String str) {
		try {
			new Long(str);
		} catch (Exception e) {

			return false;
		}

		return true;
	}

	/**
	 * 判断字符串里的字符是否都是数字。。
	 */
	public static boolean isNumber(String str) {
		if (isEmpty(str)){
			return false;
		}
		return match(str,"\\d+");
	}
}
