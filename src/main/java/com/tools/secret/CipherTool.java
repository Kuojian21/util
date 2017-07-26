package com.tools.secret;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.pdf.codec.Base64;
import com.tools.io.IOTool;

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

	public PublicKey publicKey(KeyFactory keyFactory,byte[] key) {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
			return keyFactory.generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public PrivateKey privateKey(KeyFactory keyFactory,byte[] key) {
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
			return keyFactory.generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public KeyFactory keyFactory(String algorithm, String provider){
		try {
			return KeyFactory.getInstance(algorithm,provider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static KeyPair key(KeyPairGenerator kgen) {
		return kgen.generateKeyPair();
	}

	public byte[] loadKey(Reader reader) {
		try {
			BufferedReader br = IOTool.buffer(reader);
			StringBuilder sb = new StringBuilder();
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			return Base64.decode(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			IOTool.close(reader);
		}
	}

	public static KeyGenerator keyGenerator(String algorithm, String provider, AlgorithmParameterSpec params,
			SecureRandom random) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm, provider);
			kgen.init(params, random);
			return kgen;
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static KeyGenerator keyGenerator(String algorithm, String provider, int keysize, SecureRandom random) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm, provider);
			kgen.init(keysize, random);
			return kgen;
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static KeyPairGenerator keyPairGenerator(String algorithm, String provider, AlgorithmParameterSpec params,
			SecureRandom random) {
		try {
			KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm, provider);
			kgen.initialize(params, random);
			return kgen;
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static KeyPairGenerator keyPairGenerator(String algorithm, String provider, int keysize, SecureRandom random)
			throws Exception {
		try {
			KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm, provider);
			kgen.initialize(keysize, random);
			return kgen;
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static AlgorithmParameterSpec ivSpec(byte[] iv) {
		return new IvParameterSpec(iv);
	}

	public static byte[] doFinal(Cipher cipher, byte[] data) throws Exception {
		return cipher.doFinal(data);
	}

}
