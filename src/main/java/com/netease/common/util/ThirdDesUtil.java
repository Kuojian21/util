package com.netease.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 格瓦拉3DES加解密工具类
 * @author bjjpdu
 *
 */
public class ThirdDesUtil {
	   /**
	    * @param keyStr 24字节，base64编码
	    * @param plainText 待加密的字符
	    * @param encoding
	    * @return 加密后用base64编码的字符串
	 * @throws Exception 
	    */
	   public static String encryptWithThiDES(String keyStr, String plainText, String encoding) throws Exception {
	      try{
	         byte[] key = new sun.misc.BASE64Decoder().decodeBuffer(keyStr);
	         byte[] src = plainText.getBytes(encoding);
	         byte[] result = encryptWithThiDES(key, src);
	         return new sun.misc.BASE64Encoder().encode(result).replaceAll("\r\n", "");
	      }catch(Exception e){
	    	  throw e;
	      }
	   }
	   /**
	    * @param keyStr 24字节，base64编码
	    * @param encrypt 加密后的字节用base64编码的字符串
	    * @param encoding 编码
	    * @return
	    * @throws Exception
	    */
	   public static String decryptWithThiDES(String keyStr, String encrypt, String encoding) throws Exception {
	      try {
	         byte[] key = new sun.misc.BASE64Decoder().decodeBuffer(keyStr);
	         byte[] src = new sun.misc.BASE64Decoder().decodeBuffer(encrypt);
	         byte[] result = decryptWithThiDES(key, src);
	         return new String(result, encoding);
	      }catch (Exception e) {
	    	  throw e;
	      }

	   }
	   
	   public static String encryptWithThiDES(byte[] key, String plainText,String encoding) throws Exception{
		   	  byte[] src = plainText.getBytes(encoding);
		      SecretKey deskey = new SecretKeySpec(key, "DESede");
		      Cipher cipher = Cipher.getInstance("DESede");
		      cipher.init(Cipher.ENCRYPT_MODE, deskey);
		      byte[] result = cipher.doFinal(src);
		      return new sun.misc.BASE64Encoder().encode(result).replaceAll("\r\n", "");
	  }
	   
	  public static String decryptWithThiDES(byte[] key, String encrypt,String encoding) throws Exception {
		  	  byte[] encryptedBytes = new sun.misc.BASE64Decoder().decodeBuffer(encrypt);
		      //生成密钥
		      SecretKey deskey = new SecretKeySpec(key, "DESede");
		      //解密
		      Cipher c1 = Cipher.getInstance("DESede");
		      c1.init(Cipher.DECRYPT_MODE, deskey);
		      byte[] result = c1.doFinal(encryptedBytes);
		      return new String(result,encoding);
	  }
	   
	   public static byte[] encryptWithThiDES(byte[] key, byte[] src) throws Exception{
	      SecretKey deskey = new SecretKeySpec(key, "DESede");
	      Cipher cipher = Cipher.getInstance("DESede");
	      cipher.init(Cipher.ENCRYPT_MODE, deskey);
	      byte[] result = cipher.doFinal(src);
	      return result;
	   }
	   
	   public static byte[] decryptWithThiDES(byte[] key, byte[] encrypted) throws Exception {
	      //生成密钥
	      SecretKey deskey = new SecretKeySpec(key, "DESede");
	      //解密
	      Cipher c1 = Cipher.getInstance("DESede");
	      c1.init(Cipher.DECRYPT_MODE, deskey);
	      byte[] result = c1.doFinal(encrypted);
	      return result;
	   }
	   
	   public static void main(String[] args) throws Exception {
	      String key = "wCsAEEN1QIT2jd7AJE9+f0BMeny8yu0L";
	      byte[] keyBytesTW = { 0x7A, (byte) 0x92, (byte) 0xC9, 0x3B, 0x27, (byte) 0xE9,
	    	     (byte) 0xCD, 0x67, 0x16, 0x3C,  0x34, 0x20, 0x02, 0x76, 0x36,
	    	     0x52, 0x20, 0x2C,  0x74, 0x54,  (byte) 0xF2, 0x3D, (byte) 0xA2, (byte) 0xEA}; 
	      
	      System.out.println(key);
	      String result = encryptWithThiDES(key, "中文1234abc", "utf-8");
	      System.out.println(result);//sKTaNMF8Y8qrv4jSahqhPQ==
	      String de = decryptWithThiDES(key, result, "utf-8");
	      System.out.println(de);
	      
	      String encResult = new sun.misc.BASE64Encoder().encode(ThirdDesUtil.encryptWithThiDES(keyBytesTW, ("a中文1234abc").getBytes("utf-8"))).replaceAll("\r\n", "");
	      System.out.println(encResult);//sKTaNMF8Y8qrv4jSahqhPQ==
	      String deResult = new String(ThirdDesUtil.decryptWithThiDES(keyBytesTW, new sun.misc.BASE64Decoder().decodeBuffer(encResult)),"UTF-8");
	      System.out.println(deResult);
	      
	      String encResult2 = ThirdDesUtil.encryptWithThiDES(keyBytesTW, "a中文1234abc","UTF-8");
	      System.out.println(encResult2);//sKTaNMF8Y8qrv4jSahqhPQ==
	      String deResult2 = ThirdDesUtil.decryptWithThiDES(keyBytesTW, encResult2,"UTF-8");
	      System.out.println(deResult2);
	   }

}
