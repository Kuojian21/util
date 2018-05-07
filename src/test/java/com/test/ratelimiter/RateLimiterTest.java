package com.test.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterTest {

	public static void main(String[] args) {
		RateLimiter rateLimiter = RateLimiter.create(500000);
		while (true) {
			if (rateLimiter.tryAcquire()) {
				System.out.println("Hi,World!");
			}else {
				System.out.println("限流");
			}
		}
	}
}
