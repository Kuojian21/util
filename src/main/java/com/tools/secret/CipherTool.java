package com.tools.secret;


import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class CipherTool {

	static {
		try {
			Security.addProvider(new BouncyCastleProvider());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum TRANSFORMATION {
		AES_CBC_NoPadding_128("AESCBC/NoPadding", 128), 
		AES_CBC_PKCS5Padding_128("AES/CBC/PKCS5Padding", 128), 
		AES_ECB_NoPadding_128(
				"AES/ECB/NoPadding", 128), 
				AES_ECB_PKCS5Padding_128("AES/ECB/PKCS5Padding", 128), DES_CBC_NoPadding_56(
				"DES/CBC/NoPadding", 56), DES_CBC_PKCS5Padding_56("DES/CBC/PKCS5Padding", 56), DES_ECB_NoPadding_56(
				"DES/ECB/NoPadding", 56), DES_ECB_PKCS5Padding_56("DES/ECB/PKCS5Padding", 56), DESede_CBC_NoPadding_168(
				"DESede/CBC/NoPadding", 168), DESede_CBC_PKCS5Padding_168("DESede/CBC/PKCS5Padding", 168), DESede_ECB_NoPadding_168(
				"DESede/ECB/NoPadding", 168), DESede_ECB_PKCS5Padding_168("DESede/ECB/PKCS5Padding", 168), RSA_ECB_PKCS1Padding_1024(
				"RSA/ECB/PKCS1Padding", 1024), RSA_ECB_OAEPWithSHA_1AndMGF1Padding_1024(
				"RSA/ECB/OAEPWithSHA-1AndMGF1Padding", 1024), RSA_ECB_OAEPWithSHA_256AndMGF1Padding_1024(
				"RSA/ECB/OAEPWithSHA-256AndMGF1Padding", 1024), RSA_ECB_PKCS1Padding_2048("RSA/ECB/PKCS1Padding", 2048), RSA_ECB_OAEPWithSHA_1AndMGF1Padding_2048(
				"RSA/ECB/OAEPWithSHA-1AndMGF1Padding", 2048), RSA_ECB_OAEPWithSHA_256AndMGF1Padding_2048(
				"RSA/ECB/OAEPWithSHA-256AndMGF1Padding", 2048);

		private TRANSFORMATION(String name, int keysize) {
			this.name = name;
			this.keysize = keysize;
		}
		private final String name;
		private final int keysize;
		public String getName() {
			return name;
		}
		public int getKeysize() {
			return keysize;
		}
		
	}

	public static Cipher cipher(String transformation, String provider, int mode, Certificate certificate,
			SecureRandom random) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, provider);
			cipher.init(mode, certificate, random);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static Cipher cipher(String transformation, String provider, int mode, Key key, AlgorithmParameters params,
			SecureRandom random) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, provider);
			cipher.init(mode, key, params, random);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static Cipher cipher(String transformation, String provider, int mode, Key key,
			AlgorithmParameterSpec params, SecureRandom random) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, provider);
			cipher.init(mode, key, params, random);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static Cipher encrypt(String transformation, String provider, Certificate certificate, SecureRandom random) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, provider);
			cipher.init(Cipher.ENCRYPT_MODE, certificate, random);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static Cipher encrypt(String transformation, String provider, Key key, AlgorithmParameters params,
			SecureRandom random) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, provider);
			cipher.init(Cipher.ENCRYPT_MODE, key, params, random);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static Cipher encrypt(String transformation, String provider, Key key, AlgorithmParameterSpec params,
			SecureRandom random) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, provider);
			cipher.init(Cipher.ENCRYPT_MODE, key, params, random);// Cipher.ENCRYPT_MODE
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static Cipher decrypt(String transformation, String provider, int mode, Certificate certificate,
			SecureRandom random) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, provider);
			cipher.init(Cipher.DECRYPT_MODE, certificate, random);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static Cipher decrypt(String transformation, String provider, int mode, Key key, AlgorithmParameters params,
			SecureRandom random) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, provider);
			cipher.init(Cipher.DECRYPT_MODE, key, params, random);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static Cipher decrypt(String transformation, String provider, int mode, Key key,
			AlgorithmParameterSpec params, SecureRandom random) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, provider);
			cipher.init(Cipher.DECRYPT_MODE, key, params, random);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public static Key key(byte[] key, String algorithm) {
		return new SecretKeySpec(key, algorithm);
	}

	public static Key key(KeyGenerator kgen) {
		return kgen.generateKey();
	}


	public static AlgorithmParameterSpec ivSpec(byte[] iv) {
		return new IvParameterSpec(iv);
	}

	public static byte[] doFinal(Cipher cipher, byte[] data) throws Exception {
		return cipher.doFinal(data);
	}

}
