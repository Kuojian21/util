package com.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLock {

	public static void main(String[] args) {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		Lock w = lock.writeLock();
		Lock r = lock.readLock();
//		w.lock();
		r.lock();
		w.lock();
		System.out.println();
		
	}
	
}
