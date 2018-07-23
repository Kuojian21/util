package com.test.ratelimiter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterTest {

	public static void main(String[] args) {
		RateLimiter rateLimiter = RateLimiter.create(2);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicInteger count = new AtomicInteger(0);
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						latch.await();
						int i = 0;
						while (true) {
							if (rateLimiter.tryAcquire(49, TimeUnit.SECONDS)) {
								System.out.println("Hi,World!" + i++);
							}else {
								System.out.println("限流" + count.incrementAndGet());
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		latch.countDown();

	}
}
