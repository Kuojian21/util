package com.tools.random;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class SecureRandomTool {
	public enum ALGORITHM {
		SHA1PRNG;
	}

	public static SecureRandom secureRandom(ALGORITHM algorithm, String provider, Long seed) {
		try {
			SecureRandom random = SecureRandom.getInstance(algorithm.name(), provider);
			if (seed != null) {
				random.setSeed(seed);
			}
			return random;
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}
}
