package com.tools.validator;

import java.util.regex.Pattern;


/**
 * 参数校验工具类。主要校验request中的参数是否正确，包括：<br />
 * 1.校验字符串是否为null或trim后的长度为0<br />
 * 2.校验是否是数字、整数、正整数、浮点数、正浮点数、long型。<br />
 * 3.校验Email、URL、邮件编码、手机号码、电话号码、身份证号、护照号、机动车行驶证、驾驶证、生日、银行卡号等。<br />
 * 4.校验字符串里的字母是否递增（连续）。<br />
 *
 */
public class RegexpTool {

	/** email的正则表达式 */
	private static final String EMAIL_REGEXP = "^([\\w-\\.]+)@[\\w-.]+(\\.?[a-zA-Z]{2,4}$)";

	/** URL的正则表达式 */
	private static final String URL_REGEXP = "^(http|https)://.*";

	/** 邮政编码的正则表达式 */
	private static final String POSTCODE_REGEXP = "[0-9]{6}";

	/** 手机号码的正则表达式 */
	private static final String PHONE_NO_REGEXP = "[1]{1}[3|4|5|8]{1}[0-9]{9}";

	/** 固定电话的正则表达式 */
	private static final String TOLEPHONE_NO_REGEXP = "^([\\d]{3}-)?([\\d]{3,4}-)?[\\d]{7,8}(-[\\d]{1,4})?$";

	/** 汉语名字的正则表达式 */
	private static final String CHINESE_NAME_REGEXP = "^[\u0391-\uFFE5]{2,10}$";

	/** 护照号码的正则表达式 */
	private static final String PASSPORT_REGEXP = "^[Pp]([0-9]{7})$|^[Gg]([0-9]{8})$";

	/** 汽车牌照的正则表达式 */
	private static final String VEHICLE_LICENCE_NO_REGEXP = "^[\u4e00-\u9fa5]{1}[A-Za-z0-9]{6}$";

	/** 汽车行驶证照的正则表达式 */
	private static final String DRIVING_LICENSE_NO_REGEXP = "^[^\u0391-\uFFE5]{5,64}$";

	/** 银行卡号的正则表达式 */
	private static final String BANK_CARD_NO_REGEXP = "^[\\d]{13,20}$";

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
	 * 判断传入的参数是否是Email
	 */
	public static boolean isEmail(String str) {
		return match(str, EMAIL_REGEXP);
	}

	/**
	 * 校验URL
	 */
	public static boolean isURL(String str) {
		return match(str, URL_REGEXP);
	}

	/**
	 * 校验邮件编码
	 */
	public static boolean isPostCode(String str) {
		return match(str, POSTCODE_REGEXP);
	}

	/**
	 * 校验手机号码
	 */
	public static boolean isPhoneNo(String str) {
		return match(str, PHONE_NO_REGEXP);
	}

	/**
	 * 校验固定电话号码。格式为“3位国家代码-3或4位区号-7或8位电话号码-1到4位分机号码",
	 * （注：中间用分隔符“-”隔开），除了7或8位电话号码，其余几处都可为空。
	 */
	public static boolean isTelephoneNo(String str) {
		return match(str, TOLEPHONE_NO_REGEXP);
	}
	
	/**
	 * 校验护照号码。
	 */
	public static boolean isPassportNo(String str) {
		return match(str, PASSPORT_REGEXP);
	}

	/**
	 * 校验中文姓名。
	 */
	public static boolean isChineseName(String name) {
		return match(name, CHINESE_NAME_REGEXP);
	}

	/**
	 * 校验车牌号是否合法.
	 */
	public static boolean isVehicleLicenseNo(String str) {
		return match(str, VEHICLE_LICENCE_NO_REGEXP);

	}

	/**
	 * 校验车辆行驶证型号是否合法
	 */
	public static boolean isDrivingLicenseNo(String str) {
		return match(str, DRIVING_LICENSE_NO_REGEXP);
	}

	/**
	 * 校验银行卡号（13-20位数字）。
	 */
	public static boolean isBankCardNo(String backCardNo) {
		return match(backCardNo, BANK_CARD_NO_REGEXP);
	}

}
