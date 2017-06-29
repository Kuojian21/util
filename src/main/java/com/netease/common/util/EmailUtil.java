package com.netease.common.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

/**
 * 本类方法发送邮件调用的是邮件平台接口 需要提前申请相应产品<br/>
 * 发送邮箱和ip许可等 使用及申请方法可以咨询fjtan@corp.netease.com <br/>
 * 
 * @author 开发支持中心 
 * 
 */
public class EmailUtil {
	private static String EMAIL_URL = "http://mail.admin.netease.com/service/addTask.do";

	private static String CHARSET_UTF_8 = "UTF-8";

	/**
	 * 发送普通邮件
	 * 
	 * @param productName
	 *            产品名
	 * @param to
	 *            收件人邮箱
	 * @param subject
	 *            标题
	 * @param html
	 *            邮件内容
	 * @param startTime
	 *            发送时间 格式:yyyyMMddHHmmss 用于异步发送邮件
	 * @param sendArgs
	 *            发送参数 格式:to,id,name;test@163.com,test,测试;.....
	 *            其中to为收件人邮箱的默认参数值，其他参数为邮件内容中需要替换的参数值
	 * @return 发送成功返回ok 其他情况会返回相应的提示
	 * 
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static String sendEmail(String productName, String to,
			String subject, String html, String startTime, String sendArgs)
			throws MalformedURLException, IOException {
		String result = null;
		if (StringUtils.isBlank(productName) || StringUtils.isBlank(subject)
				|| StringUtils.isBlank(html)) {
			return "参数产品名，标题或者邮件内容为空！";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			sdf.parse(startTime);
		} catch (ParseException e) {
			return "发送时间格式不正确！";
		}

		if (StringUtils.isBlank(to) && StringUtils.isBlank(sendArgs)) {
			return "收件人邮箱为空！";
		}

		// 不用校验参数 远端接口会对参数验证并返回相应值
		StringBuffer sb = new StringBuffer("productName=").append(productName)
				.append("&to=").append(to).append("&subject=").append(subject)
				.append("&html=").append(html).append("&startTime=")
				.append(startTime);
		if (StringUtils.isNotBlank(sendArgs)) {
			sb.append("&sendArgs=").append(sendArgs);
		}
		result = new String(HttpUtil.sendPostRequest(EMAIL_URL, sb.toString(),
				CHARSET_UTF_8));
		return result;
	}

	/**
	 * 发送模板邮件
	 * 
	 * @param productName
	 *            产品名
	 * @param templateName
	 *            模板名
	 * @param startTime
	 *            发送时间 格式:yyyyMMddHHmmss 用于异步发送邮件
	 * @param sendArgs
	 *            发送参数 格式:to,id,name;test@163.com,test,测试;.....
	 *            其中to为收件人邮箱的默认参数值，其他参数为邮件内容中需要替换的参数值
	 * @return 发送成功返回ok 其他情况会返回相应的提示
	 * 
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static String sendEmailByTemplate(String productName,
			String templateName, String startTime, String sendArgs)
			throws MalformedURLException, IOException {
		String result = null;
		if (StringUtils.isBlank(productName)
				|| StringUtils.isBlank(templateName)) {
			return "参数产品名或者模板名为空！";
		}

		if (StringUtils.isNotBlank(startTime)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				sdf.parse(startTime);
			} catch (ParseException e) {
				return "发送时间格式不正确！";
			}
		}

		if (StringUtils.isBlank(sendArgs)) {
			return "发送参数为空！";
		}

		// 不用校验参数 远端接口会对参数验证并返回相应值
		StringBuffer sb = new StringBuffer("productName=").append(productName)
				.append("&templateName=").append(templateName)
				.append("&startTime=").append(startTime);
		if (StringUtils.isNotBlank(sendArgs)) {
			sb.append("&sendArgs=").append(sendArgs);
		}
		result = new String(HttpUtil.sendPostRequest(EMAIL_URL, sb.toString(),
				CHARSET_UTF_8));
		return result;
	}

	public static void main(String[] args) throws Exception {
		String result = sendEmail(
				"mailcenter",
				"fjtan@corp.netease.com",
				"测试邮件",
				"<em><strong><span style='font-family:Microsoft YaHei;'>test</span></strong></em><br />",
				"20120207104600", null);
		System.out.println("简单邮件：" + result);
		result = sendEmailByTemplate("mailcenter", "mail_test_tmp", null,
				"to,nickName,identityNo,mobile;fjtan@corp.netease.com,谭富佳,230804,1381010");
		System.out.println("模板邮件：" + result);
	}
}