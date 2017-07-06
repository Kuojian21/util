package com.tools.random;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class RandomTool {

	private static final ConcurrentMap<String,Random> randoms = Maps.newConcurrentMap();
	private static final ConcurrentMap<String,SecureRandom> secureRandoms = Maps.newConcurrentMap();
	
	public static Random random(Long seed){
		String key = seed == null? "DEFAULT" : seed.toString();
		Random random = new Random(seed);
		return random;
	}
	
}
