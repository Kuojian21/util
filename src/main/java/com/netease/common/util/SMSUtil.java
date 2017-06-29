package com.netease.common.util;

import org.apache.commons.lang.StringUtils;

import com.netease.common.constant.BaseConstant;
import com.netease.common.exception.ParameterException;

/**
 * 使用本类方法发送短信需提前申请IP许可及相应的成本中心代码<br/>
 *  @author 开发支持中心
 */
public class SMSUtil {
	// 企信通接口url。
	public static String SMS_URL = "http://smsknl.163.com:8089/servlet/CorpIdentifyNotCheck";

	// 填写的验证码和下发的验证码是否一致。
	private static String SMS_CHECKCODE_URL = "http://co.sms.163.com:8080/check_code.jsp?";

	static boolean DEBUG = true;

	/**
	 * 通过无线接口验证操作码，仅适用于绑定手机的操作
	 * 
	 * @param mobile
	 *            手机号
	 * @param checkCode
	 *            验证码
	 * @param msgprop
	 *            无线给产品分配的代码
	 * @return 验证结果
	 */
	public static boolean veryCheckCode(String mobile, String checkCode,
			String msgprop) {
		boolean result = false;
		String wirelessUrl = new StringBuffer(SMS_CHECKCODE_URL)
				.append("phone=").append(mobile).append("&code=")
				.append(checkCode).append("&msgprop=").append(msgprop)
				.toString();

		LogUtil.debug(wirelessUrl, LogUtil.LOG_BASIC_FRAMEWORK);

		try {
			String resultstr = StringUtil.valueOf(HttpUtil
					.sendGetRequest(wirelessUrl));
			String[] tmp = resultstr.split("\n");
			return tmp != null && tmp.length > 1
					&& "1".equalsIgnoreCase(tmp[0].trim());
		} catch (Exception e) {
			// throw exception.
			// log.error("validateOperateCodeRemote out of time: " +
			// e.getMessage());
		}
		return result;
	}

	/**
	 * 回复用户短信验证码,根据用户接收短信，所使用不同的msgprop
	 * 
	 * @param mobile
	 *            信息接收方的手机号码。
	 * @param opcode
	 *            未经编码的短信内容。
	 * @param msgprop
	 *            增值业务的内部代号，决定信息的业务属性。
	 * @return 发送结果
	 */
	public static String sendMessage(String mobile, String opcode,
			String msgprop) throws Exception {
		String resultstr = null;

		// 入口参数校验
		if (StringUtils.isBlank(mobile) || StringUtils.isBlank(opcode)
				|| StringUtils.isBlank(msgprop)) {
			LogUtil.debug("入口参数不正确", LogUtil.LOG_BASIC_FRAMEWORK);
			throw new ParameterException("入口参数不正确");
		}

		// 短信长度检验
//		if (StringUtil.checklen(opcode)) {
//			LogUtil.debug("发送短信字符数超过70", LogUtil.LOG_BASIC_FRAMEWORK);
//			throw new ParameterException("发送短信字符数超过70");
//		}

		/*
		 * if(!(IniConstant.getIniByName("qxtmsgprop02").iniValue.equals(msgprop)
		 * )) { msgprop=IniConstant.getIniByName("qxtmsgprop").iniValue; }
		 */

		try {
			// 对短信内容进行编码。
			String msg = CodecUtil.hexEncode(opcode.getBytes("GBK"));

			// url 网易企信通的地址
			String url = new StringBuffer(BaseConstant.getValue("smsSendURL")).append("?phone=")
					.append(mobile).append("&frmphone=").append(mobile)
					.append("&msgprop=").append(msgprop).append("&message=")
					.append(msg).append("&corpinfo=1").toString();

			// log.debug("subUrl" + url);

			resultstr = new String(HttpUtil.sendGetRequest(url));

		} catch (Exception e) {
			// throw exception
		}

		return resultstr;
	}

	public static void main(String[] args) throws Exception {
		String result = sendMessage("13800138000", "test", "61125");
		System.out.println(result);
	}
}
