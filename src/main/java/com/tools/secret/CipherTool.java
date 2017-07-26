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
