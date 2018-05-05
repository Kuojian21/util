package com.java.kj.netty;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResponseFuture {

	private final Lock lock = new ReentrantLock();
	private final Condition cond = lock.newCondition();
	private volatile boolean done = false;
	private Response<?> response;
	private Exception exception;

	public Response<?> get() throws Exception {
		if (this.done) {
			if (this.response == null) {
				throw exception;
			}
			return response;
		}
		try {
			lock.lock();
			while (!this.done) {
				cond.await();
			}
			if (this.response == null) {
				throw exception;
			}
			return response;
		} finally {
			lock.unlock();
		}
	}

	public void set(Response<?> response) {
		if (this.done) {
			return;
		}
		try {
			lock.lock();
			if (this.done) {
				return;
			}
			this.response = response;
			this.done = true;
			cond.signalAll();
		} finally {
			lock.unlock();
		}
	}

}