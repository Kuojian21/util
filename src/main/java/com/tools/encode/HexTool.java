package com.tools.encode;

import java.io.UnsupportedEncodingException;

public class HexTool {
	
	public static String STR = "0123456789ABCDEF";
	
	public static String toHex(String str) {
		try {
			StringBuilder sb = new StringBuilder();
			byte[] bytes = str.getBytes("UTF-8");
			for (byte b : bytes) {
				int c = b >> 4;
				sb.append(Integer.toHexString(c));
				int d = b & 0xF;
				sb.append(Integer.toHexString(d));
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 16进制转字符串
	 */
	public static String fromHex(String hex) {
		try {
			char[] hexs = hex.toUpperCase().toCharArray();
			byte[] bytes = new byte[hex.length() / 2];
			for (int i = 0; i < bytes.length; i++) {
				int n = STR.indexOf(hexs[2 * i]) * 16;
				n += STR.indexOf(hexs[2 * i + 1]);
				bytes[i] = (byte) (n & 0xff);
			}
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
}
