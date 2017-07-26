package com.tools.secret;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public class KeyPairGeneratorTool {

	public enum ALGORITHM {
		DiffieHellman("DiffieHellman", 1024), DSA("DSA", 1024), RSA_1024("RSA", 1024), RSA_2048(
				"RSA", 2048);
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

	public static KeyPair key(KeyPairGenerator kgen) {
		return kgen.generateKeyPair();
	}

	public static KeyPairGenerator keyPairGenerator(ALGORITHM algorithm, String provider,
			AlgorithmParameterSpec params, SecureRandom random) {
		try {
			KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm.getName(), provider);
			kgen.initialize(params, random);
			return kgen;
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static KeyPairGenerator keyPairGenerator(ALGORITHM algorithm, String provider,
			SecureRandom random) throws Exception {
		try {
			KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm.getName(), provider);
			kgen.initialize(algorithm.getKeysize(), random);
			return kgen;
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}
}
