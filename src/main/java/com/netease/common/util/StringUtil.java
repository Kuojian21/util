package com.netease.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Vector;
/**
 * 字符串工具类，主要封装了常用的字符串操作接口：<br/>
 * 1. 获取字符串长度;<br/>
 * 2. 字符串判空;<br/>
 * 3. 转换为16进制;<br/>
 * 
 * @author 开发支持中心
 *
 */
public class StringUtil {

	// 0. Some parameters
	static byte NUMBER_KEY = 48;
	static byte UPPER_KEY = 55;
	static byte LOWER_KEY = 87;
	static byte HEX_KEY = 16;
	static byte NUMBER_MAX = 57;
	static byte NUMBER_MIN = 48;
	static byte UPPER_MAX = 70;
	static byte UPPER_MIN = 65;
	static byte LOWER_MAX = 102;
	static byte LOWER_MIN = 97;

	/**
	 * 验证字符串对象是否为null或空,如果是则返回true
	 * 
	 * @param str
	 * @return 是否为空
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 * 验证字符串长度是否符合要求，一个汉字等于两个字符
	 * 
	 * @param strParameter string串
	 * @param charset 编码
	 * @return 字符串长度
	 * @throws UnsupportedEncodingException
	 */
	public static int getLength(String strParameter, String charset)
			throws UnsupportedEncodingException {
		return isEmpty(charset) ? strParameter.getBytes().length : strParameter
				.getBytes(charset).length;

	}

	/**
	 * Convert the String using the platform's default charset to Binary coded
	 * by Hex.
	 * 
	 * @param str
	 * @return 十六进制值字节数组
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] toHex(String str) throws UnsupportedEncodingException {
		return toHex(str, null);
	}

	/**
	 * Convert the String to Binary coded by Hex. Note: This convertation is an
	 * unsigned byte between string.
	 * 
	 * @param str
	 * @param charset
	 *            如果为null或空，用确实字符串。
	 * @return 十六进制字节数组
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] toHex(String str, String charset)
			throws UnsupportedEncodingException {
		byte checkByte = 0;

		if (isEmpty(str))
			return null;

		// 对字符串进行编码
		byte[] bytes = null;
		if (isEmpty(charset)) {
			bytes = str.getBytes();
		} else {
			bytes = str.getBytes(charset);
		}

		byte[] tmpBytes = new byte[2];
		Vector<Integer> v = new Vector<Integer>();

		int i = 0;
		while (i < bytes.length - 1) {
			tmpBytes[0] = 0;
			tmpBytes[1] = 0;

			// convert the ascii to hex here
			// 1. convert the high position
			tmpBytes[0] = bytes[i];
			checkByte = tmpBytes[0];

			if (tmpBytes[0] >= NUMBER_MIN && tmpBytes[0] <= NUMBER_MAX) {
				tmpBytes[0] = new Integer(tmpBytes[0] - NUMBER_KEY).byteValue();
			} else if (tmpBytes[0] >= UPPER_MIN && tmpBytes[0] <= UPPER_MAX) {
				tmpBytes[0] = new Integer(tmpBytes[0] - UPPER_KEY).byteValue();
			} else if (tmpBytes[0] >= LOWER_MIN && tmpBytes[0] <= LOWER_MAX) {
				tmpBytes[0] = new Integer(tmpBytes[0] - LOWER_KEY).byteValue();
			}

			if (checkByte == tmpBytes[0]) {
				continue;
			}

			tmpBytes[0] = new Integer(tmpBytes[0] * HEX_KEY).byteValue();

			// 2. conver the low position
			i++;
			tmpBytes[1] = bytes[i];
			checkByte = tmpBytes[1];
			if (tmpBytes[1] >= NUMBER_MIN && tmpBytes[1] <= NUMBER_MAX) {
				tmpBytes[1] = new Integer(tmpBytes[1] - NUMBER_KEY).byteValue();
			} else if (tmpBytes[1] >= UPPER_MIN && tmpBytes[1] <= UPPER_MAX) {
				tmpBytes[1] = new Integer(tmpBytes[1] - UPPER_KEY).byteValue();
			} else if (tmpBytes[1] >= LOWER_MIN && tmpBytes[1] <= LOWER_MAX) {
				tmpBytes[1] = new Integer(tmpBytes[1] - LOWER_KEY).byteValue();
			}

			// 这句是否需要？不管怎样都会 continue的吧？ by wxy.
			if (checkByte == tmpBytes[1]) {
				continue;
			}

			v.add(new Integer(tmpBytes[0] + tmpBytes[1]));
		}

		Object[] intList = v.toArray();

		byte[] result = new byte[intList.length];

		for (int li = 0; li < intList.length; li++) {
			result[li] = ((Integer) intList[li]).byteValue();
		}

		return result;
	}
	
    /**
     * 获取字节数组对应的字符串值
     * @param pBytes
     * @return 字节数组对应的字符串
     */
	public static String valueOf(byte[] pBytes) {
		String result = "";
		for (int i = 0; i < pBytes.length; i++) {
			int tmpInt = new Byte(pBytes[i]).intValue();
			if (tmpInt < 0)
				tmpInt = tmpInt + 256;
			byte[] strList = new byte[2];
			strList[1] = new Integer(tmpInt % 16).byteValue();
			strList[0] = new Integer((tmpInt / 16) % 16).byteValue();

			if (strList[1] > 9 && strList[1] < 16)
				strList[1] += UPPER_KEY;
			if (strList[1] >= 0 && strList[1] < 10)
				strList[1] += NUMBER_KEY;

			if (strList[0] > 9 && strList[0] < 16)
				strList[0] += UPPER_KEY;
			if (strList[0] >= 0 && strList[0] < 10)
				strList[0] += NUMBER_KEY;

			result = result + new String(strList);

		}
		return result;
	}
    /**
     * 校验字符串长度
     * @param input_str
     * @return 字符串长度校验结果
     */
	public static boolean checklen(String input_str) {
		String str = input_str;
		char[] b = new char[str.length()];
		str.getChars(0, str.length(), b, 0);
		int cs = 0;
		int es = 0;
		for (int i = 0; i < b.length; i++) {
			char c = b[i];
			if (c > 128) {
				cs++;
				i++;
			} else
				es++;
		}

		if (cs > 0) {
			if ((es + cs) > (140 / 2))
				return true;
		} else {
			if (es > 160)
				return true;
		}
		return false;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(toHex(null, null));
		System.out.println(toHex("  ", null));
		System.out.println(toHex("ABC", null));
		System.out.println(toHex("123", null));
		System.out.println(toHex("ABC123", null));
		String str = "11111123333";
		str = str.replaceAll("(?s)(.)(?=.*\\1)", "");
		System.out.println(str);
		
		String rebateCodeValues = "abc|2334|123|";
		String[] rebateCodes = rebateCodeValues.split("\\|");
		for(int i=0;i<rebateCodes.length;i++){
			System.out.println(rebateCodes[i]);
		}
		
		rebateCodeValues = rebateCodeValues.substring(0, rebateCodeValues.lastIndexOf("|"));
		System.out.println(rebateCodeValues);
		
		Integer intTest = 2;
		System.out.println(intTest ==2 );
	}

}
