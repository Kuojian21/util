package com.test.perf;

import java.security.SecureRandom;

public class Test {

	public static void main(String[] args) {
		Integer[] datas = new Integer[1024 * 1024 * 10];
		SecureRandom random = new SecureRandom();
		
		for(int i = 0,len = datas.length;i < len;i++) {
			datas[i] = random.nextInt(100000000);
		}
		
		long b = System.currentTimeMillis();
		int j = 0;
		for(int i = 0;i < 10000000;i++) {
			j += datas[random.nextInt(datas.length)];
			j /= datas.length;
			j &= 0xFFFFFFFF;
			j += 10;
		}
		System.out.println(System.currentTimeMillis() - b);
		System.out.println(j);
	}

}
