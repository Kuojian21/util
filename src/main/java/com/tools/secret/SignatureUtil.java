package com.tools.secret;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;


/**
 * 
 * 基于非对称加密RSA/DSA的签名及签名校验算法实现相关工具类，主要封装了包括：<br/>
 * 1.签名实施：根据源串、私钥等信息生成签名值；<br/>
 * 2.私钥获取：根据文件或者字符串获取私钥值；<br/>
 * 3.公钥获取：获取公钥值；<br/>
 * 4.签名校验：根据合作方签名值，并用其源串和公钥对其签名值进行校验<br/>
 * 
 * @author 开发支持中心
 *
 */
public class SignatureUtil {

	/** 签名算法 */
	public static final String ALGORITHM_RSA = "RSA";
	public static final String ALGORITHM_DSA = "DSA";
	public static final String ALGORITHM_MD5WITHRSA = "MD5withRSA";
	public static final String ALGORITHM_SHA1WITHMD5 = "SHA1withMD5";

	/** 签名结果的处理类垄 */
	public static final String SIGN_CODING_TYPE_BASE64 = "base64";
	public static final String SIGN_CODING_TYPE_HEX = "hex";
	public static final String SIGN_CODING_TYPE_HEXCODE = "hexCode";
	/** 公钥文件的文件类垄 */
	public static final String PUBLIC_KEY_FILE_TYPE_PEM = "pem";
	public static final String PUBLIC_KEY_FILE_TYPE_HEXCODE = "hexCode";
	public static final String PUBLIC_KEY_FILE_TYPE_JAVAOBJECT = "javaObject";

	/** 签名、验签时，对源串取字节数组时的编码处琄 */
	public static final String SOURCE_ENCODING_TYPE_UTF8 = "UTF-8";
	public static final String SOURCE_ENCODING_TYPE_GBK = "GBK";



	/**
	 * 验证签名的合法性，根据源串，签名串以及对方的公钥信息验证签名信息的合法性
	 */
	public static boolean verify(String algorithm, PublicKey publicKey, byte[] datas, byte[] sign) {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initVerify(publicKey);
			signature.update(datas);
			return signature.verify(sign);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 生成签名信息，根据源串，签名算法和自己的私钥信息生成签名信息
	 */
	public static byte[] sign(String algorithm,PrivateKey privateKey, byte[] datas) throws Exception {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(privateKey);
			signature.update(datas);
			return signature.sign();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("",e);
		}

	}
	
}
