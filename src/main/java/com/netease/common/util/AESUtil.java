package com.netease.common.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.netease.common.exception.DecryptException;
import com.netease.common.exception.EncryptException;
import com.netease.common.exception.ParameterException;
import com.netease.common.util.LogUtil;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

/**
 * AES（Advanced Encryption Standard，对称加密的一种 ）加解密算法封装，主要包括：<br />
 * 1. 基于AES的加密算法实现方法；<br />
 * 2. 基于AES的解密算法实现方法；<br />
 * 3. 提供了基于main函数的简单测试。<br />
 * 
 * @author 开发支持中心
 * 
 */

public class AESUtil {
//	public static byte[] IV_BYTES = { 0x12, 0x34, 0x56, 0x78, 0x90, 0xAB, 0xCD,
//			0xEF, 0x12, 0x34, 0x56, 0x78, 0x90, 0xAB, 0xCD, 0xEF };

	/**
	 * 加密算法：基于明文串，密钥对明文信息进行加密
	 * 
	 * @param sSrc
	 *            待加密的字符串。
	 * @param sKey
	 *            密钥。长度必须=16个字节(长度为128位),why?
	 * @return 加密后的经base64编码的字符串。
	 * @throws EncryptException
	 *             加密过程出现异常。
	 * @throws ParameterException
	 *             入口参数异常。源串为null或空，密钥不符合要求等。
	 * @throws GeneralSecurityException
	 * @throws NoSuchAlgorithmException
	 */
	public static String encrypt(String sSrc, String sKey)
			throws ParameterException, EncryptException,
			NoSuchAlgorithmException, GeneralSecurityException {
		if (sSrc == null || sSrc.trim().length() == 0 || sKey == null
				|| sKey.trim().length() != 16) {
			LogUtil.debug("入口参数不正确", LogUtil.LOG_BASIC_FRAMEWORK);

			throw new ParameterException();
		}

		try {
			// 构造密钥。
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"

			IvParameterSpec iv = new IvParameterSpec(
					"0102030405060708".getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度

			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes());
			// return new
			// BASE64Encoder().encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
			return CodecUtil.base64Encode(encrypted);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
		} catch (Exception e) {
			throw new EncryptException("[AES加密异常]", e);
		}
	}

	/**
	 * 解密 算法：根据加密串和密钥对密文进行解密.
	 * 
	 * @param encryptedString
	 *            待解密的加密串。
	 * @param sKey
	 *            密钥。
	 * @return 解密后的字符串。
	 * @throws ParameterException
	 * @throws DecryptException
	 */
	public static String decrypt(String encryptedString, String sKey)
			throws ParameterException, DecryptException {
		if (encryptedString == null || encryptedString.trim().length() == 0
				|| sKey == null || sKey.trim().length() != 16) {
			throw new ParameterException();
		}

		try {
			// 为什么是ASCII？ by wxy.
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());// 
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			// byte[] encrypted1 = new
			// BASE64Decoder().decodeBuffer(encryptedString);//先用base64解密
			byte[] encrypted1 = CodecUtil.base64Decode(encryptedString);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			throw new DecryptException("[AES解密异常]", e);
		}
	}

	/**
	 * 然后 在对KEY上 他们要求的是6 16 或24位，以往的做法是使用随机强加密随机数生成器！~ 因为这个很难做到互通
	 * 所以我采用了一种很简单的方式就是使用 MD5 短16位： c#默认运算模式为CBCjava默认为ECB所以我们第一步算法是要 修改为一致的运算模式
	 * 我们采用 ECB 因为 ECB 不需要 IV
	 */

	/**
	 * AES + Base64加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密钥
	 * @param charsetName
	 *            编码类型 null或""则默认UTF-8
	 * @return
	 */
	public static String encryptForCMode(String content, String password,
			String charsetName) {
		try {
			if (charsetName == null || "".equals(charsetName.trim()))
				charsetName = "UTF-8";
			// KeyGenerator kgen =
			// KeyGenerator.getInstance("AES/CBC/NoPadding");
			// kgen.init(128, new SecureRandom(password.getBytes()));
			// SecretKey secretKey = kgen.generateKey();
			// byte[] enCodeFormat = secretKey.getEncoded();
			// 为了和.NET加密一致修改为
			byte[] enCodeFormat = password.getBytes(charsetName);//MD5Util.get(password, charsetName)
			// ///////////////////////以上已经修改/////////////////////////
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes(charsetName);
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return new sun.misc.BASE64Encoder().encode(result); // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * AES + Base64加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密钥
	 * @param charsetName
	 *            编码类型 null或""则默认UTF-8
	 * @return
	 */
	public static String decryptForCMode(String encryptedString, String password,
			String charsetName) {
		try {
			if (charsetName == null || "".equals(charsetName.trim()))
				charsetName = "UTF-8";
			// KeyGenerator kgen =
			// KeyGenerator.getInstance("AES/CBC/NoPadding");
			// kgen.init(128, new SecureRandom(password.getBytes()));
			// SecretKey secretKey = kgen.generateKey();
			// byte[] enCodeFormat = secretKey.getEncoded();
			// 为了和.NET加密一致修改为
			byte[] enCodeFormat = password.getBytes(
					charsetName);//MD5Util.get(password, charsetName)
			// ///////////////////////以上已经修改/////////////////////////
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			//----------
			byte[] encrypted1 = CodecUtil.base64Decode(encryptedString);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
//			//-----------
//			byte[] byteContent = encryptedString.getBytes(charsetName);			
//			byte[] result = cipher.doFinal(byteContent);
//			return new sun.misc.BASE64Encoder().encode(result); // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) throws Exception {
		/*
		 * 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
		 * 此处使用AES-128-CBC加密模式，key需要为16位。
		 */

		String cKey = "f1Cnw1CAyKt79buz";
		// 需要加密的字串
		String cSrc = "18072850850";
		System.out.println(cSrc);
		// 加密
		long lStart = System.currentTimeMillis();
		String enString = AESUtil.encrypt(cSrc, cKey);
		System.out.println("加密后的字串是：" + enString);

		long lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("加密耗时：" + lUseTime + "毫秒");
		// 解密
		lStart = System.currentTimeMillis();
		String DeString = AESUtil.decrypt(enString, cKey);
		System.out.println("解密后的字串是：" + DeString);
		lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("解密耗时：" + lUseTime + "毫秒");
		// 18610246002
		// 18610246002
		String phone = "15810437404";
		String key = "E2tPkfwK9bg8R8pw";

		String miwen = AESUtil.encryptForCMode(phone, key,"UTF-8");
		System.out.println("加密串：" + miwen);
		// pCP2SGdLYFM58MDVXK69sw==
		System.out.println("解密串："
				+ AESUtil.decryptForCMode(miwen, key,"UTF-8"));

	}
}
