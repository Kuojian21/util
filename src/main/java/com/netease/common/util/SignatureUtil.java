package com.netease.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import sun.misc.BASE64Decoder;
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
	
	private static final String NETEASE_RSA_PRIKEY_FILE = "";
	private static final String NETEASE_DSA_PRIKEY_FILE = "";
	

	/**
	 * 验证签名的合法性，根据源串，签名串以及对方的公钥信息验证签名信息的合法性 
	 * @param source
	 * 			源串
	 * @param sign
	 * 			签名串
	 * @param algorithm
	 * 			算法
	 * @param publicKey
	 * 			公钥文件对象
	 * @param signCodingType
	 * 			签名编码方式
	 * @param srcEncodingType
	 * 			源串编码方式
	 * @return
	 * 			签名校验结果True、False
	 */
	private static boolean verifySignature(
			String source, String sign, String algorithm, 
			PublicKey publicKey, 
			String signCodingType, String srcEncodingType) {
		
		Signature sigEng = null;
		try{
			if ("rsa".equalsIgnoreCase(algorithm)) {
				sigEng = Signature.getInstance("SHA1withRSA");
			} else
			{
				// use params algorithm as ways to create verify with pubkey
				sigEng = Signature.getInstance(algorithm);
			}
			
			sigEng.initVerify(publicKey);
			
			sigEng.update(source.getBytes(srcEncodingType));
			
			if ("base64".equalsIgnoreCase(signCodingType)) {
				return sigEng.verify(CodecUtil.base64Decode(sign));
			} else if ("hex".equalsIgnoreCase(signCodingType)) {
				return sigEng.verify(Hex.decodeHex(sign.toCharArray()));
			}else{
				return false;
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 验证签名：根据源串，签名串以及对方的公钥字符串验证签名信息的合法性
	 * @param source
	 *            源串
	 * @param sign
	 *            签名
	 * @param algorithm
	 *            可使用RSA(SHA1withRSA/MD5withRSA) DSA 算法
	 * @param publicKeyString
	 *            公钥字符
	 * @param signCodingType
	 *            对签名结果的验签前的编码处理
	 * @param srcEncodingType
	 *            对源串验签前的编码处理
	 * @return
	 * 			签名校验结果True、False
	 */
	public static boolean verifySignature(
			String source, String sign, String algorithm, 
			String publicKeyString, String signCodingType, 
			String srcEncodingType) {

		PublicKey publicKey = getPublicKey(publicKeyString, algorithm);
		
		return publicKey == null ? false : verifySignature(source,sign,algorithm,
				publicKey,signCodingType,srcEncodingType);
		

	}

	/**
	 * 验证签名：根据源串，签名串以及对方的公钥文件路径验证签名信息的合法性
	 * 
	 * @param source
	 *            源串
	 * @param sign
	 *            签名串
	 * @param algorithm
	 *            算法
	 * @param publicKeyFile
	 *            公钥文件的路径
	 * @param publicKeyFileType
	 *            公钥文件的类型
	 * @param signCodingType
	 *            对签名结果的处理类型
	 * @param srcEncodingType
	 *            对源串取字节数组时的编码处理。
	 * @return
	 *            签名是否合法布尔值
	 */
	public static boolean verifySignature(
			String source, String sign, String algorithm, 
			String publicKeyFile, String publicKeyFileType, 
			String signCodingType, String srcEncodingType) {
	
		PublicKey publicKey = getPublicKey(publicKeyFile, 
				algorithm, publicKeyFileType);
		
		return publicKey == null ? false : verifySignature(source,sign,algorithm,
				publicKey,signCodingType,srcEncodingType);

	}
	

	/**
	 * 生成签名信息，根据源串，签名算法和自己的私钥信息生成签名信息
	 * @param source
	 * 			源串
	 * @param algorithm
	 * 			签名生成算法
	 * @param codingType
	 * 			签名编码类型
	 * @param srcEncodingType
	 * 			签名源串编码类型
	 * @param privateKeyValue
	 * 			私钥值
	 * @param type
	 * @return
	 * 			签名串
	 */
	private static String generateSignature(
			String source, 
			String algorithm, 
			String codingType, 
			String srcEncodingType,
			String privateKeyValue, 
			int type) {
	
		String result = null;
		PrivateKey privateKey = null;
		Signature sigEng = null;

		try{
			if ("RSA".equalsIgnoreCase(algorithm)) {
				sigEng = Signature.getInstance("SHA1withRSA");
			} else {
				sigEng = Signature.getInstance(algorithm);
			}
			
			if ( type == 1 ){
				privateKey = getPrivateKeyByString(algorithm, privateKeyValue);
			}else if ( type == 2 ){
				privateKey = getPrivateKeyByFile(algorithm, privateKeyValue);
			}else{
				return null;
			}
			
			sigEng.initSign(privateKey);
			
			sigEng.update(source.getBytes(srcEncodingType));
			
			byte[] signature = sigEng.sign();
			if ("hex".equalsIgnoreCase(codingType)) {
				result = CodecUtil.hexEncode(signature);
			} else if ("base64".equalsIgnoreCase(codingType)) {
				result = CodecUtil.base64Encode(signature);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 生成签名：根据源串，签名算法和自己的私钥字符串信息生成签名信息
	 * 
	 * @param source
	 *            源串
	 * @param algorithm
	 *            私钥签名算法。可使用RSA(SHA1withRSA) DSA 算法
	 * @param privateKeyString
	 *            私钥
	 * @param codingType
	 *            对签名生成字节数组的编码处理。取值为“base64”、“hex”等.
	 * @param srcEncodingType
	 *            对源串取字节数组时的编码处理
	 * @return
	 * 			签名串
	 */
	public static String generateSignatureByPrivateKeyString(
			String source, 
			String algorithm, 
			String codingType, 
			String srcEncodingType,
			String privateKeyString) {
		
		return generateSignature(
					source, 
					algorithm, 
					codingType, 
					srcEncodingType,
					privateKeyString, 
					1);

	}
	
	/**
	 * 生成签名：生成签名信息，根据源串，签名算法和自己的私钥文件信息生成签名信息
	 * 
	 * @param source
	 *            源串
	 * @param algorithm
	 *            使用的私钥签名算法可使用RSA(SHA1withRSA/MD5withRSA) DSA 算法
	 * @param privateKeyFile
	 *            私钥
	 * @param codingType
	 *            对签名生成字节数组的编码处理
	 * @param srcEncodingType
	 *            对源串取字节数组时的编码处理
	 * @return
	 * 			    生成的签名值
	 */
	public static String generateSignatureByPrivateKeyPath(
			String source, 
			String algorithm, 
			String codingType, 
			String srcEncodingType,
			String privateKeyFile) {
		
		return generateSignature(
				source,
				algorithm,
				codingType,
				srcEncodingType,
				privateKeyFile,
				2);


	}
	
	
	/**
	 * 获取私钥：算法有待检查，根据私钥文件路径获取私钥信息
	 * 
	 * @param algorithm
	 *            返回转换指定算法的 public/private关键字的 KeyFactory 对象<br />
	 *            支持RSA、SHA1withRSA、MD5withRSA、DSA
	 * @param privateKeyString
	 *            私钥字符串<br />
	 * @return
	 * 			   私钥对象
	 */
	private static PrivateKey getPrivateKeyByFile(
			String algorithm, String keyFile) {
	
		PrivateKey privateKey = null;
		try{
			if ("RSA".equalsIgnoreCase(algorithm) 
					|| "SHA1withRSA".equalsIgnoreCase(algorithm) 
					|| "MD5withRSA".equalsIgnoreCase(algorithm)) {
				
				byte[] pribyte;
				
				String privateKeyString = FileUtil.read(keyFile);
				
				pribyte = Hex.decodeHex(privateKeyString.trim().toCharArray());
				
				// 根据给定的编码密钥创建一个新的 PKCS8EncodedKeySpec
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pribyte);
				
				KeyFactory fac = KeyFactory.getInstance("RSA");
				
				privateKey = (RSAPrivateKey) fac.generatePrivate(keySpec);
				
			} else if ("DSA".equalsIgnoreCase(algorithm)) {
				
				File privateKeyFile = new File(keyFile);
				
				ObjectInputStream keyIn = null;

				keyIn = new ObjectInputStream(new FileInputStream(privateKeyFile));
				privateKey = (PrivateKey) keyIn.readObject();
				keyIn.close();
			}
		}catch (Exception e){
			e.printStackTrace();
			privateKey =null;
		}
		return privateKey;

	}
	
	 public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception{  
	        try {  
	            BASE64Decoder base64Decoder= new BASE64Decoder();  
	            byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);  
	            PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);  
	            KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
	            RSAPrivateKey privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
	            return privateKey;
	        } catch (NoSuchAlgorithmException e) {  
	            throw new Exception("无此算法");  
	        } catch (InvalidKeySpecException e) {  
	            throw new Exception("私钥非法");  
	        } catch (IOException e) {  
	            throw new Exception("私钥数据内容读取错误");  
	        } catch (NullPointerException e) {  
	            throw new Exception("私钥数据为空");  
	        }  
	    }  
	
	/**
	 * 取私钥：根据私钥字符串获取私钥信息
	 * 
	 * @param algorithm
	 *            返回转换指定算法的 public/private关键字的 KeyFactory 对象<br />
	 * @param privateKeyString
	 *            私钥字符串<br />
	 * @return
	 * 			     私钥对象信息
	 */
	private static PrivateKey getPrivateKeyByString(
			String algorithm, 
			String keyString) {
	
		PrivateKey privateKey = null;
		try{
			if ("RSA".equalsIgnoreCase(algorithm) 
					|| "SHA1withRSA".equalsIgnoreCase(algorithm) 
					|| "MD5withRSA".equalsIgnoreCase(algorithm)) {
				byte[] pribyte;
				if (ValidatorUtil.isEmpty(keyString)) {
					keyString = FileUtil.read(NETEASE_RSA_PRIKEY_FILE);
				}
				pribyte = Hex.decodeHex(keyString.trim().toCharArray());
				// 根据给定的编码密钥创建一个新的 PKCS8EncodedKeySpec
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pribyte);
				KeyFactory fac = KeyFactory.getInstance("RSA");
				privateKey = (RSAPrivateKey) fac.generatePrivate(keySpec);
			} else if ("DSA".equalsIgnoreCase(algorithm)) {
				ObjectInputStream keyIn = null;
				File privateKeyFile = new File(NETEASE_DSA_PRIKEY_FILE);
				keyIn = new ObjectInputStream(new FileInputStream(privateKeyFile));
				privateKey = (PrivateKey) keyIn.readObject();
				keyIn.close();
			}
		}catch ( Exception e){
			e.printStackTrace();
			privateKey = null;
		}
		return privateKey;
	}
	
	
	/**
	 * 根据字符串取公钥
	 * 
	 * @param keyString
	 * 			    公钥串
	 * @param algorithm 
	 *            加密算法
	 * @return
	 * 			公钥信息
	 * @throws Exception
	 */
	private static PublicKey getPublicKey(
			String keyString, 
			String algorithm)  {
		
		PublicKey pubKey = null;
	
		try {
			//把密钥由字符串转化为十六进制数组。
			byte[] keyBytes = Hex.decodeHex(keyString.trim().toCharArray());
			
			//构造X509EncodedKeySpec对象
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			
			// KEY_ALGORITHM 指定的加密算法
			KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
			
			// 取公钥匙对象
			pubKey = keyFactory.generatePublic(keySpec);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return pubKey;
	}
	
	/**
	 * 取公钥：根据公钥文件路径获取公钥信息
	 * 
	 * @param publicKeyPath
	 *            公钥文件路径
	 * @param algorithm
	 *            签名算法
	 * @param publicKeyFileType
	 *            公钥文件的文件类型。支持hex,pem,javaObject。
	 * @return
	 * 			    公钥对象
	 */
	private static PublicKey getPublicKey(
			String publicKeyPath, 
			String algorithm, 
			String publicKeyFileType) {
	
		PublicKey publicKey = null;
		
		try{
			// hexCode 16进制编码格式或pem格式
			if ("hex".equalsIgnoreCase(publicKeyFileType) 
					|| "pem".equalsIgnoreCase(publicKeyFileType)) {

				byte[] pubbyte = null;
					
				if ("hex".equalsIgnoreCase(publicKeyFileType)) {
					String publicKeyString = FileUtil.read(publicKeyPath);
					pubbyte = Hex.decodeHex(publicKeyString.trim().toCharArray());
					
				}else if ("pem".equalsIgnoreCase(publicKeyFileType)) {
					
					BufferedReader br = new BufferedReader(new FileReader(publicKeyPath));
					String s = br.readLine();
					StringBuilder str = new StringBuilder();
					s = br.readLine();
					while (s.charAt(0) != '-') {
						str.append(s);
						str.append("\r");
						s = br.readLine();
					}
					pubbyte = Base64.decodeBase64(str.toString().trim());
				}
					
				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubbyte);
				KeyFactory fac = KeyFactory.getInstance(algorithm);
				publicKey = fac.generatePublic(keySpec);
					
			}else if ("javaObject".equalsIgnoreCase(publicKeyFileType)) {
				
				ObjectInputStream keyIn = null;
				File pubKeyFile = new File(publicKeyPath);
				keyIn = new ObjectInputStream(new FileInputStream(pubKeyFile));
				publicKey = (PublicKey) keyIn.readObject();
				keyIn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
			publicKey = null; 
		}
			
		return publicKey;
	}
	
	public static String generateSHA1withRSASigature(String src,String keyPath){
        try{

                Signature sigEng = Signature.getInstance("SHA1withRSA");
                File inputKeyFile=new File(keyPath);
                byte[] key=new byte[(int)inputKeyFile.length()];
                new FileInputStream(inputKeyFile).read(key);
               
				String hexstr = bytesToHexStr(key);
                
                byte[] pribyte = hexStrToBytes(hexstr.trim());

                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pribyte);

                KeyFactory fac = KeyFactory.getInstance("RSA");
                RSAPrivateKey privateKey = (RSAPrivateKey)fac.generatePrivate(keySpec);
                sigEng.initSign(privateKey);
                sigEng.update(src.getBytes());

                byte[] signature = sigEng.sign();
                return bytesToHexStr(signature);
        }catch(Exception e){
                e.printStackTrace(System.err);
                return null;
        }
	}
	/**
	 * 生成签名
	 * @param source 源串
	 * @param algorithm 算法
	 * @return
	 */
	public static String getSignature(String source, String algorithm,String keyPath) {
		return SignatureUtil.generateSignatureByPrivateKeyPath(source, algorithm, SIGN_CODING_TYPE_BASE64,SOURCE_ENCODING_TYPE_UTF8, keyPath);
	}
	

	private static final char[] bcdLookup =
    {
            '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
    };
	public static final String bytesToHexStr(
            byte[] bcd){
            StringBuffer s = new StringBuffer(bcd.length * 2);
            for (int i = 0; i < bcd.length; i++)
            {
                    s.append(bcdLookup[(bcd[i] >>> 4) & 0x0f]);
                    s.append(bcdLookup[bcd[i] & 0x0f]);
            }

            return s.toString();
    }
	
	/**
     * 将16进制字符串还原为字节数组.
     */
    public static final byte[] hexStrToBytes(
            String	s)
    {
            byte[]	bytes;

            bytes = new byte[s.length() / 2];

            for (int i = 0; i < bytes.length; i++)
            {
                    bytes[i] = (byte)Integer.parseInt(
                                    s.substring(2 * i, 2 * i + 2), 16);
            }

            return bytes;
    }
	
	 public static void main(String[] args){
	        //加载公钥  
//	        try {  
//	           SignatureUtil.getPrivateKeyByFile(SignatureUtil.ALGORITHM_RSA, keyFile); 
//	            System.out.println("加载公钥成功");  
//	        } catch (Exception e) {  
//	            System.err.println(e.getMessage());  
//	            System.err.println("加载公钥失败");  
//	        }  
	  
	        //加载私钥  
	        try {  
	        	
	            System.out.println(SignatureUtil.generateSHA1withRSASigature("测试", "F:\\keys\\netease.der"));  
	        } catch (Exception e) {  
	            System.err.println(e.getMessage());  
	            System.err.println("加载私钥失败");  
	        }  
	  
	        //测试字符串  
	        String encryptStr= "Test String chaijunkun";  
	  
	        try {  
	        } catch (Exception e) {  
	            System.err.println(e.getMessage());  
	        }  
	    }  
}
