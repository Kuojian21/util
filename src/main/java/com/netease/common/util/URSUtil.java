package com.netease.common.util;

import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.netease.common.constant.BaseConstant;
import com.netease.common.util.IPUtil;
import com.netease.urs.ntescode;

/**
 * URS身份认证类，主要封装如下接口：<br/>
 * 1. 校验URS的登录cookie<br/>
 * 2. 比较cookie中的ip段和request中的ip段是否一样<br/>
 * 3. 调用URS接口验证用户在URS的手机状态<br/>
 * 
 * @author 开发支持中心
 */
public class URSUtil {

	static Logger log = Logger.getLogger(URSUtil.class);
	
    //Cookie名称
	public static String NTES_COOKIE = "NTES_SESS";   //URS内存Cookie
	public static String PASSPORT_COOKIE = "NTES_PASSPORT";  //URS持久Cookie
	public static String S_INFO_COOKIE = "S_INFO";

	//Cookie缺省有效时间为40分钟。
	public static long COOKIE_DEFAULT_EXPIRE_TIME = 40 * 60 ; // cookie有效时间为8小时
	

	//登录状态
	public static final String LOG_STATUS_NOT = "0";  //未登录
	public static final String LOG_STATUS_TIMEOUT = "-1";  //登录超时
	public static final String LOG_STATUS_LOGGED = "1"; //已登录
	
	//URS用户名格式
	public static final String URS_SSN_REGEXP = "^[a-zA-Z]([a-zA-Z]|[0-9]|_){4,16}([a-zA-Z]|[0-9])$";
	
	/**
	 * 校验是否符合URS帐号注册规范
	 * 
	 * @param ssn
	 * @return true:符合，false:不符合
	 */
	public static boolean isUrsSsn(String ssn) {
	
		return Pattern.matches(URS_SSN_REGEXP, ssn);
	}
	
	
	/**
	 * 获取Cookie里有效的URS帐号。timeout如果为0，取默认的超时时间（40分钟，2400妙）
	 * 
	 * @param request
	 * @param timeout Cookie超时时间。如果为0，取默认的超时时间（40分钟，2400妙）。
	 * @return cookie有效时的用户名，用户名带域名后缀；cookie失效或无cookie时，返回null。
	 */
	public static String getSsnFromCookie(HttpServletRequest request,long timeout) {
		String username = null;
		ntescode n = null;

		//如果 cookie有效时间为0，取缺少的 cookie有效时间。
	    if ( timeout == 0 ){
	    	timeout = COOKIE_DEFAULT_EXPIRE_TIME;
	    }
	    
	    String ntesCookieValue = getNeteaseCookie(request);
	    
	    if (ntesCookieValue != null 
	    		&& ntesCookieValue.length() > 0) {
	    	
	    	n = new ntescode();
	    	
			// cookie是否有效
			int isCookieValidate = n.validate_cookie(
					ntesCookieValue.getBytes(), 
					8, 
					timeout, 
					false);
			
			if (isCookieValidate >= 0) {
				username = new String(n.ssn);
				String[] ssnDomain = cutSsnDomain(username);
				username = ssnDomain[2];
			}
		}
		return username;
	}

	/**
	 * 从cookie中获取用户信息。timeout如果为0，取默认的超时时间（40分钟，2400妙）。
	 * 
	 * @param request
	 *            ：HTTP请求
	 * @param timeout Cookie超时时间。如果为0，取默认的超时时间（40分钟，2400妙）。
	 * @return 字符串数组。{登录状态,用户帐号,手机号,cookie创建时间,创建cookie时用户的ip}。
	 *         登录状态取值： “-1”： 登录超时；“0”: 没有登录 ；“1”：已登录。在已登录的情况下，返回其余内容。
	 *         用户帐号带域名后缀。
	 */
	public static String[] getUserInfoFromCookie(
			HttpServletRequest request, long timeout)
	{
		String[] retStr = new String[5];
		
		String ntesCookieValue = getNeteaseCookie(request);
		
		if (ntesCookieValue == null)
		{
			retStr[0] = LOG_STATUS_NOT;// 没有登陆
		}
		else
		{
			//如果 cookie有效时间为0，取缺少的 cookie有效时间。
		    if ( timeout == 0 ){
		    	timeout = COOKIE_DEFAULT_EXPIRE_TIME;
		    }

			ntescode n = new ntescode();
			
			int ret = n.validate_cookie(ntesCookieValue.getBytes(), 8, timeout, false);
			
			if (ret < 0 || "1".equals(new String(n.autologin)))
			{
				retStr[0] = LOG_STATUS_TIMEOUT;// 登陆超时
			}
			else
			{
				retStr[0] = LOG_STATUS_LOGGED; //登录成功
				
				//retStr[1]:cookie里的用户名
				String username = new String(n.ssn);
				String[] ssnDomain = cutSsnDomain(username);
				retStr[1] = ssnDomain[2];
				
				//retStr[2]:绑定的手机号
				String phoneStr = new String(n.mobile);
				if (phoneStr != null && phoneStr.length() > 0)
				{
					retStr[2] = phoneStr.substring(1); //URS绑定手机号，URS返回的手机号前面带一个"O"字符，这里去掉
				}
				
				//retStr[3]:cookie创建时间
				try
				{
					retStr[3] = DateUtil.format(
							new Date(n.cookieCreateTime*1000L), 
							"yyyy-MM-dd HH:mm:ss");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				//retStr[4]:创建cookie时用户的ip
				retStr[4]=new String(n.p_uid);
			}
		}
		return retStr;
	}

	/**
	 * 从cookie中获取用户登录信息(默认40分钟超时（2400秒）)
	 * 
	 * @param request
	 *            ：HTTP请求
	 * @return “0”: 没有登录 ； “1”： 登录超时；SSN（登录用户名）
	 */
	public static String[] getUserInfoFromCookie(HttpServletRequest request)
	{
		return getUserInfoFromCookie(request, 2400L);
	}

	
	/**
	 * 取URS内存Cookie值。
	 * @param request
	 * @return 如果Cookie存在，返回Cookie值，否则，返回null。
	 */
	public static String getNeteaseCookie(HttpServletRequest request) {
	
		try {
			String ntesCookieValue = null;
			Cookie[] cookies = request.getCookies();
			if(null == cookies){
				return null;
			}
			for (int i = 0; i < cookies.length; i++) {
				if ((cookies[i].getName()).equals(NTES_COOKIE)) {
					ntesCookieValue = cookies[i].getValue();
					break;
				}
			}
			return ntesCookieValue;
		} catch (Exception e) {
			return null;
		}
	}
	
    /**
     * 取URS持久Cookie。
     * @param request
     * @return 如果Cookie存在，返回Cookie值，否则，返回null。
     */
	public String getNeteasePersistentCookie(HttpServletRequest request) {
		String ntesCookieValue = null;
	
		try {
			Cookie[] cookies = request.getCookies();
			if(null == cookies){
				return null;
			}
			for (int i = 0; i < cookies.length; i++) {
				if ((cookies[i].getName()).equals(PASSPORT_COOKIE)) {
					ntesCookieValue = cookies[i].getValue();
					break;
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return ntesCookieValue;
	}
	

	/**
	 * 是否游戏用户。
	 * 
	 * @param ssn 用户名
	 * @return true ：是游戏用户；否则，不是。
	 */
	public static boolean isGameUser(String ssn) {
	
		if (ssn == null || ssn.length() < 4) {
			return false;
		}
		return (ssn.substring(0, 3).equals("8wy") 
				|| ssn.substring(0, 3).equals("xyq") 
				|| ssn.substring(0, 3).equals("yys") 
				|| ssn.substring(0, 3).equals("tx2")) 
				&& ValidatorUtil.isNumber(ssn.substring(3, ssn.length()));
	}


	/**
	 * 比较cookie里的帐号与传入的帐号是否相同。
	 * 
	 * @param request
	 * @param accountId
	 * @return true：相同；false：不相同。
	 */
	public static boolean compareAccount(
			HttpServletRequest request,String accountId)
	{
		String[] cookieSsn = getUserInfoFromCookie(request);
		if (LOG_STATUS_NOT.equals(cookieSsn[0]) 
				|| LOG_STATUS_TIMEOUT.equals(cookieSsn[0]))
		{
			return false;
		}
		
		return accountId != null 
				&& cookieSsn[1] != null 
				&& accountId.equals(cookieSsn[1]);
	}


	/**
	 * 判断cookie里的用户ip与当前用户的ip是否一致。
	 * @param request
	 * @return true：相同；false：不相同。
	 */
    public static boolean compareIP(HttpServletRequest request)
    {
            String[] ssn = getUserInfoFromCookie(request);
            
            return compareIP(ssn[4],IPUtil.getRemoteAddr(request));
    }
    
    /**
     * 比较IPV4前三段Ip是否相等
     * @param str1
     * @param str2
     * @return true：相同，false：不同
     */
    private static boolean compareIP(String str1,String str2)    {
    	if(str1 == null || str2 == null)
    	{
    		return false;
    	}
    	if(str1.lastIndexOf(".")<=0 || str2.lastIndexOf(".")<=0)
    	{
    		return false;
    	}
    	
    	if(!str1.substring(0, str1.lastIndexOf(".")).equals(str2.substring(0,str2.lastIndexOf("."))))
    	{
    		return false;
    	}else{
    		return true;
    	}
    }
    
	/**
	 * 切分用户名
	 * 
	 * @param username
	 * @return 字符串数组{不带域的帐号名,域名,带域的完整的用户名}
	 */
	public static String[] cutSsnDomain(String username) {
	
		String[] ssnDomain = { "", "", "" };
		
		//如果为空，直接返回空串
		if (ValidatorUtil.isEmpty(username)) {
			return ssnDomain;
		}
		
		int indexAt = username.lastIndexOf("@");
		
		if (indexAt == -1) { // 如果username中不含有@字符，则认定为163帐号
			
			ssnDomain[0] = username; // username
			ssnDomain[1] = BaseConstant.DOMAIN_163;// domain
			ssnDomain[2] = username + BaseConstant.DOMAIN_163; // 带域的完整用户名。
			
		} else {// 如果username中含有@字符
			
			int indexAt2 = username.indexOf("@");
			
			if (indexAt2 < indexAt) { // 用户名中带有多个"@"
				
				String temp = username.substring(indexAt2 + 1);

				String domainTemp = "@" + temp.substring(0, temp.indexOf("@"));
				
				ssnDomain[0] = username.substring(0, indexAt2);
				
				if (BaseConstant.DOMAIN_163.equals(domainTemp)) { // 163
					ssnDomain[1] = BaseConstant.DOMAIN_163;
					ssnDomain[2] = ssnDomain[0] + BaseConstant.DOMAIN_163;
				} else {
					ssnDomain[1] = domainTemp;
					ssnDomain[2] = ssnDomain[0] + domainTemp;
				}
			} else {
				
				ssnDomain[0] = username.substring(0, indexAt);// ssn
				ssnDomain[1] = username.substring(indexAt);// domain
				ssnDomain[2] = username; 
			}
		}
		return ssnDomain;
	}
	
	/**
	 * 调用URS接口验证用户在URS的手机状态
	 * 
	 * @param accountId
	 *            帐户Id
	 * @param mobile
	 *            手机号码
	 * @return true:urs已经激活且与当前用户欲在网易宝绑定的手机号一致 false:其他所有情形（包括验证时连接异常）
	 */
	public static boolean isMobileActiveInURS(String accountId, String mobile) {

		try {
			String validateUrl = new StringBuffer("http://reg.163.com/services/userinfo/getmobile?").append("username=").append(accountId).append("&getMobStatus=1").toString();
			String result = new String(HttpUtil.sendGetRequest(validateUrl)).trim();
			log.info("validate urs mobile result:" + result);
			String[] ret = result.split(";");
			// 验证正确的返回格式为，ex.201Ok and more information
			// returned.mobile=O13800138000;0
			// 正确的情形需要：返回头201，验证返回手机号与用户填写一致，验证urs手机状态为1
			if (ret.length > 1 && ret[0].startsWith("201") 
					&& ret[1].equals("1") 
					&& ret[0].endsWith(mobile)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.fatal("connect urs to validate mobile failed: out of time");
			return false;
		}
	}
	
    public static void main(String args[])
    {
    }
}