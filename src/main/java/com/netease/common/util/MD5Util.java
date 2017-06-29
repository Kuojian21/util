package com.netease.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

/**
 * 计算md5值，并根据合作方分配的密钥串，完成MD5校验<br/>
 * 该工具具有两种功能：<br/>
 * （1）计算并提供MD5摘要值<br/>
 * （2）通过引入密钥，与原始串一起计算摘要，一定程度上保证通信的安全性<br/>
 * 
 * @author 开发支持中心
 * 
 */
public class MD5Util {
	/**
	 * 获取字符串S的MD5摘要串 
	 * @param s	
	 * 			源串
	 * @param encode 
	 *			编码类型，根据合作方的约定来定，例如"GBK"，"UTF-8"等 
	 * @return  MD5摘要串
	 */
	public static String get(String s, String encode) {
		byte abyte0[] = null;
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("MD5");
			messagedigest.update(s.getBytes(encode));			
		} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
			throw new IllegalArgumentException("no md5 support");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("不支持" + encode + "编码");
		}
		abyte0 = messagedigest.digest();
		return CodecUtil.hexEncode(abyte0);
	}

	/**
	 * 使用 MD5 哈希函数计算基于哈希值的消息验证代码 (HMAC)。 HMACMD5 是从 MD5
	 * 哈希函数构造的一种键控哈希算法，被用作基于哈希的消息验证代码 (HMAC)。 此 HMAC
	 * 进程将密钥与消息数据混合，使用哈希函数对混合结果进行哈希计算，将所得哈希值与该密钥混合，然后再次应用哈希函数。 输出的哈希值长度为 128 位。
	 * 
	 * @param ssn
	 * @param seed
	 * @return 摘要串
	 */
	public static String hmacmd5(String ssn, int seed) {
		try {
			byte[] plainText = ssn.getBytes();

			KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
			SecureRandom sr = new SecureRandom();

			// LoggerUtil.debug("seed=" + seed, "SecurityUtil", "hmacmd5");

			sr.setSeed(seed);
			keyGen.init(64, sr);
			SecretKey MD5key = keyGen.generateKey();
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(MD5key);
			mac.update(plainText);

			return URLEncoder.encode(new String(mac.doFinal()), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    /**
     * MD5摘要值校验，根据合作方传来的摘要串和原始串，校验摘要值的合法性
     * @param password
     * 				摘要串
     * @param inputString
     * 				原始串
     * @param codeType
     * 				编码类型
     * @return MD5校验结果
     */
	public static boolean validate(String password, String inputString,
			String codeType) {
		System.out.println("sign:"+password+"，根据源串："+inputString+"，生成的签名串:"+digest(inputString, codeType));
		if (password.equalsIgnoreCase(digest(inputString, codeType))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 计算字符串originString的MD5摘要串 
	 * @param originString
	 * 					源串
	 * @param codeType	编码类型，根据合作方的约定来定，例如"GBK"，"UTF-8"等 
	 * @return MD5摘要串
	 */
	private static String digest(String originString, String codeType) {
		if (originString != null) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
				// LoggerUtil.debug("源串："+originString);
				byte[] results = md.digest(originString.getBytes(codeType));
				// 将得到的字节数组变成字符串返回
				String resultString = CodecUtil.hexEncode(results);
				// MD5结果转换成大写字母
				String pass = resultString.toUpperCase();
				// LoggerUtil.debug("结果串："+ pass);
				return pass;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		String pwd1 = "123";
		String pwd2 = "1";
		System.out.println("未加密的密码:" + pwd1);
		// 将123加密
		pwd2 = MD5Util.digest(pwd1, "UTF-8");
		System.out.println("加密后的密码:" + pwd2);

		System.out.print("验证密码是否下确:");
		if (MD5Util.validate(pwd2, pwd1, "UTF-8")) {
			System.out.println("正确");
		} else {
			System.out.println("错误");
		}
		System.out.println(MD5Util.get("http://movie.mall.163.com/order/confirm.html?order_id=2012102514GORDER00149540E2tPkfwK9bg8R8pw", "UTF-8"));
		System.out.println(MD5Util.validate("a9d134563a43f8178ef55036b4da42fb", "9:02|9:010000059595wy_seat_c4c3abda71010eE2tPkfwK9bg8R8pw", "UTF-8"));
	    System.out.println(MD5Util.get("movie_client", "UTF-8"));
	    
	    String sign="3C4CC858B18F93E8F0C999DF80CA8079";
	    String source ="1:01000087376915810437404wy_seat_f833988f9c4014EdxbQduTLj6s6S3X";
	    System.out.println(MD5Util.validate(sign, source,"utf-8"));
	    System.out.println(MD5Util.get(source,"utf-8"));
	    
	    System.out.println(DateUtil.format(new Date(), "yyyy年MM月dd日"));
	}
}
