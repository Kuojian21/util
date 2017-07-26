package com.tools.secret;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.KeyGenerator;

public class KeyGeneratorTool {
	public enum ALGORITHM {
		AES("AES ", 128), DES("DES", 56), DESede("DESede", 168), HmacSHA1("HmacSHA1", 0), HmacSHA256("", 0);
		private ALGORITHM(String name, int keysize) {
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
}
