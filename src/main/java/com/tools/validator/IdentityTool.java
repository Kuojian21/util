package com.tools.validator;


import org.apache.commons.lang.StringUtils;

import com.tools.date.DateTool;

public class IdentityTool {
	
	/** 身份证前2位。 */
	private static final String[] IDNO_CITY = new String[] { "11", "12", "13", "14", "15", "21",
			"22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45",
			"46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71", "81", "82",
			"91" };

	/** 18位身份证号的最后一位 */
	private static final int[] IDNO_IWEIGHT = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10,
			5, 8, 4, 2, 1 };

	private static final String[] IDNO_CCHECK = new String[] { "1", "0", "X", "9", "8", "7", "6",
			"5", "4", "3", "2" };

	/** 身份证号码的正则表达式 */
	private static final String ID_NO_REGEXP = "^((\\d{17}|\\d{14})(\\d|x|X))$";
	
	
	/**
	 * 校验身份证号码。
	 */
	public static boolean isIdentityNo(String identityNo) {

		if (RegexpTool.match(identityNo, ID_NO_REGEXP)) {
			return false;
		}

		int length = identityNo.length();
		if (length != 15 && length != 18) {
			return false;
		}

		// 校验身份证号码的前2位。
		String city = identityNo.substring(0, 2);
		if (StringUtils.indexOfAny(city, IDNO_CITY) < 0) {
			return false;
		}

		// 日期校验
		if (length == 18) {
			String birth = identityNo.substring(6, 14);
			if(!DateTool.isDate(birth, "yyyyMMdd")){
				return false;
			}
			int total = 0;
			for (int i = 0; i < 17; i++) {
				total += Integer.valueOf(identityNo.substring(i, i + 1)) * IDNO_IWEIGHT[i];
			}
			int mo = total % 11;
			String lastOne = IDNO_CCHECK[mo];
			return identityNo.substring(17).equalsIgnoreCase(lastOne);
		} else {
			String birth = "19" + identityNo.substring(6,12);
			if(!DateTool.isDate(birth, "yyyyMMdd")){
				return false;
			}
			return true;
		}
	}
}
