package com.java.kj.crypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Crypt {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	private SecretKey key;
	private IvParameterSpec ivp;
	private String algorithm;
	public Crypt(String algorithm, String padding, String keyAlgorithm, String keyValue) {
		try {
			this.algorithm = algorithm;
			this.ivp = new IvParameterSpec(padding.getBytes("UTF-8"));
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			this.key = SecretKeyFactory.getInstance(keyAlgorithm)
					.generateSecret(new DESedeKeySpec(keyValue.getBytes("UTF-8")));
			config.setMinIdle(10);
			config.setMaxTotal(100);
			config.setMaxWaitMillis(30000);
		} catch (UnsupportedEncodingException | InvalidKeyException | InvalidKeySpecException
				| NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public SecretKey getKey() {
		return key;
	}

	public IvParameterSpec getIvp() {
		return ivp;
	}

	public String getAlgorithm() {
		return algorithm;
	}

}
