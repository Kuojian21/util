package com.test.snowflake;

import java.util.concurrent.CountDownLatch;

import com.java.kj.snowflake.KjSnowflake;

public class IdWorkerTest {
	public static void main(String[] args) throws InterruptedException {
//		KjSnowflake worker = new KjSnowflake(1);
//		CountDownLatch latch = new CountDownLatch(100);
//		long s = System.currentTimeMillis();
//		for(int i = 0 ;i < 100;i++) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					while(true) {
//						System.out.println(Long.toHexString(worker.nextId()));
//					}
//				}
//			}).start();
//		}
//		latch.await();
//		System.out.println(System.currentTimeMillis() - s);
//		System.out.println(Long.toHexString(365 * 24 * 3600 * 1000));
		System.out.println(new Long(792656933252235265L).intValue()&( -1 ^ (-1 << 10)));
	}
}
