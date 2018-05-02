package com.test.snowflake;

import java.util.concurrent.CountDownLatch;

import com.snowfake.IdWorker;

public class IdWorkerTest {
	public static void main(String[] args) throws InterruptedException {
		IdWorker worker = new IdWorker(1);
		CountDownLatch latch = new CountDownLatch(100);
		long s = System.currentTimeMillis();
		for(int i = 0 ;i < 100;i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(int i = 0;i < 10000;i++) {
						System.out.println(Long.toHexString(worker.nextId()));
					}
					latch.countDown();
				}
			}).start();
		}
		latch.await();
		System.out.println(System.currentTimeMillis() - s);
	}
}
