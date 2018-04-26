package com.test.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockTest {

	public static void main(String[] args) {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		Lock w = lock.writeLock();
		Lock r = lock.readLock();
		new Thread(new Runnable() {
			@Override
			public void run() {
				r.lock();
				w.lock();
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				r.lock();
				w.lock();
			}

		}).start();

		Object x = new Object();
		Object y = new Object();
		CountDownLatch lx = new CountDownLatch(1);
		CountDownLatch ly = new CountDownLatch(1);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (x) {
						lx.countDown();
						ly.await();
						synchronized (y) {

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "x").start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (y) {
						ly.countDown();
						lx.await();
						synchronized (x) {

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "y").start();
		
		ReentrantLock x1 = new ReentrantLock();
		ReentrantLock y1 = new ReentrantLock();
		CountDownLatch lx1 = new CountDownLatch(1);
		CountDownLatch ly1 = new CountDownLatch(1);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					x1.lock();
					lx1.countDown();
					ly1.await();
					y1.lock();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "x1").start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					y1.lock();
					ly1.countDown();
					lx1.await();
					x1.lock();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "y1").start();
		
		ReentrantLock x2 = new ReentrantLock();
		ReentrantLock y2 = new ReentrantLock();
		CountDownLatch lx2 = new CountDownLatch(1);
		CountDownLatch ly2 = new CountDownLatch(1);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized(x2) {
						lx2.countDown();
						ly2.await();
						y2.lock();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "x2").start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					y2.lock();
					ly2.countDown();
					lx2.await();
					synchronized(x2) {
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "y2").start();
		
		ReentrantLock x3 = new ReentrantLock();
		ReentrantLock y3 = new ReentrantLock();
		ReentrantLock z3 = new ReentrantLock();
		CountDownLatch lx3 = new CountDownLatch(1);
		CountDownLatch ly3 = new CountDownLatch(1);
		CountDownLatch lz3 = new CountDownLatch(1);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized(x3) {
						lx3.countDown();
						ly3.await();
						y3.lock();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "x3").start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					y3.lock();
					ly3.countDown();
					lz3.await();
					z3.lock();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "y3").start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					z3.lock();
					lz3.countDown();
					lx3.await();
					synchronized(x3) {
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "z3").start();
		
	}
}
