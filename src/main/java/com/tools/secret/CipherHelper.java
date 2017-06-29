package com.tools.secret;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CipherHelper {

	static {
		try {
			Security.addProvider(new BouncyCastleProvider());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] encrypt(byte[] datas, byte[] key, byte[] ivDatas) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
		// SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		// Cipher cipher = Cipher.getInstance("AES");
		// SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		// Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		// SecretKey skeySpec = new SecretKeySpec(key, "DESede");
		// Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		// SecretKey skeySpec = new SecretKeySpec(key, "DESede");
		// Cipher cipher = Cipher.getInstance("DESede");
		// SecretKey skeySpec = new SecretKeySpec(key, "DESede");
		// Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(ivDatas);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		return cipher.doFinal(datas);
	}

	public static byte[] decrypt(byte[] datas, byte[] key, byte[] ivDatas) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
		// SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		// Cipher cipher = Cipher.getInstance("AES");
		// SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		// Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		// SecretKey skeySpec = new SecretKeySpec(key, "DESede");
		// Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		// SecretKey skeySpec = new SecretKeySpec(key, "DESede");
		// Cipher cipher = Cipher.getInstance("DESede");
		// SecretKey skeySpec = new SecretKeySpec(key, "DESede");
		// Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(ivDatas);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		return cipher.doFinal(datas);
	}

	public static byte[] encrypt1(byte[] key) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(key);
		kgen.init(128, random);
		SecretKey secretKey = kgen.generateKey();
		return secretKey.getEncoded();
	}

}
