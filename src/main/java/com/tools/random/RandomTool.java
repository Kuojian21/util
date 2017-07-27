package com.tools.random;

import java.util.Random;

public class RandomTool {


	public static Random random(Long seed) {
		return new Random(seed);
	}

}
