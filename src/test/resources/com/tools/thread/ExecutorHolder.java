package com.tools.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorHolder {

	/**
	 * 线程池对象
	 */
	private volatile ThreadPoolExecutor service = null;
	/**
	 * 线程池被引用得次数
	 */
	private final AtomicInteger count = new AtomicInteger(-1);
	/**
	 *线程池在当前线程是否被引用过
	 */
	private final static Object object = new Object();
	private final ThreadLocal<Object> required = new ThreadLocal<Object>();
	/**
	 * 线程池参数
	 */
	private String name;
	private int corePoolSize;
	private int maximumPoolSize;
	private long keepAliveTime;
	private TimeUnit unit;
	private int maxWaitTask;

	public ExecutorHolder(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, int maxWaitTask) {
		super();
		this.name = name;
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.keepAliveTime = keepAliveTime;
		this.unit = unit;
		this.maxWaitTask = maxWaitTask;

	}

	public ExecutorService require() {
		if (required.get() != null) {
			return this.service;
		} else {
			while (true) {
				int rtn = -1;
				while ((rtn = this.count.get()) >= 0) {
					if (this.count.compareAndSet(rtn, rtn + 1)) {
						this.required.set(object);
						return this.service;
					}
				}
				synchronized (this) {
					rtn = this.count.get();
					if (rtn <= -2) {
						return null;
					} else if (rtn == -1) {
						this.service = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
								keepAliveTime, unit, new ArrayBlockingQueue<Runnable>(maxWaitTask),
								new ThreadRenameFactory(name),
								new ThreadPoolExecutor.CallerRunsPolicy());
						this.required.set(object);
						this.count.set(1);
						return this.service;
					}
				}
			}
		}
	}

	public boolean apply(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, int maxWaitTask) {
		if (this.name.equals(name) && this.corePoolSize == corePoolSize
				&& this.maximumPoolSize == maximumPoolSize && this.maxWaitTask == maxWaitTask
				&& unit.toSeconds(keepAliveTime) == this.unit.toSeconds(this.keepAliveTime)) {
			return true;
		}
		return false;
	}

	public void init() {
		this.count.compareAndSet(-2, -1);
	}

	public void release() {
		if (required.get() != null) {
			required.remove();
			this.count.decrementAndGet();
		}
	}

	public boolean shutdown() {
		ExecutorService service = this.service;
		if (this.count.compareAndSet(0, -1)) {
			service.shutdown();
			return true;
		}
		return false;
	}

	public boolean destroy() {
		return this.count.compareAndSet(-1, -2);
	}

}
