package com.tools.random;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class RandomTool {

	private static final ConcurrentMap<String, Random> randoms = Maps.newConcurrentMap();
	private static final ConcurrentMap<String, SecureRandom> secureRandoms = Maps.newConcurrentMap();

	public static Random random(Long seed) {
		String key = seed == null ? "DEFAULT" : seed.toString();
		if (!randoms.containsKey(key)) {
			if (seed == null) {
				randoms.putIfAbsent(key, new Random());
			} else {
				randoms.putIfAbsent(key, new Random(seed));
			}
		}
		return randoms.get(key);
	}

	public static SecureRandom secureRandom(String algorithm, String provider, Long seed) {
		String key = algorithm + "@" + provider + "@" + (seed == null ? "DEFAULT" : seed.toString());
		try {
			if (!secureRandoms.containsKey(key)) {
				SecureRandom random = SecureRandom.getInstance(algorithm, provider);
				if (seed != null) {
					random.setSeed(seed);
				}
				secureRandoms.putIfAbsent(key, random);
			}
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		return secureRandoms.get(key);
	}

}
