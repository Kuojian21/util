package com.tools.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

public final class CommonUtils {

	
	/**
	 * 判断文件的编码格式
	 */
	public static String charset(File file) throws Exception {
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
		int p = (bin.read() << 8) + bin.read();
		String code = null;

		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}

		bin.close();
		return code;
	}

	/**
	 * 将金额转换单位，50000转为：5万，5000转为：5千
	 * 
	 * @param num
	 * @return
	 */
	public static String changeMoneyUnit(int num) {
		String result = null;
		int integer = 0;
		int point = 0;

		if (num >= 10000) {
			integer = num / 10000;
			point = num % 10000;
			if (point == 0) {
				result = integer + "万";
			} else {
				result = 1.0 * num / 10000 + "万";
			}
		} else {
			integer = num / 1000;
			point = num % 1000;
			if (point == 0) {
				result = integer + "千";
			} else {
				result = 1.0 * num / 1000 + "千";
			}
		}

		return result;
	}

	public static boolean isInt(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Byte || obj instanceof Short || obj instanceof Integer || obj instanceof Long) {
			return true;
		} else {
			return Pattern.matches("\\d+", obj.toString().trim());
		}
	}

}
