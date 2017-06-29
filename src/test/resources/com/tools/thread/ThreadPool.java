package com.tools.thread;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class ThreadPool {

	private static final ConcurrentMap<String, ExecutorHolderMultiVersion> threadPoolCache = Maps
			.newConcurrentMap();

	private static Thread threadPoolDeamon = null;

	static {
		threadPoolDeamon = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000);
						Set<Map.Entry<String, ExecutorHolderMultiVersion>> entries = threadPoolCache.entrySet();
						for (Map.Entry<String, ExecutorHolderMultiVersion> entry : entries) {
							entry.getValue().destroy();
						}
					} catch (Exception e) {
						
					}
				}
			}
		}, "");
		threadPoolDeamon.setDaemon(true);
		threadPoolDeamon.start();
	}

	public static final int THREAD_POOL_CORE_SIZE = 16;// 线程池最少线程数
	public static final int THREAD_POOL_MAX_SIZE = 64;
	public static final int THREAD_MAX_THREAD_WAIT = 4096;// 最大线程等待数

	private ThreadPool() {

	}

	public static ExecutorService getInstance(String name, int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, int maxWaitTask) {
		if (Strings.isNullOrEmpty(name)) {
			name = "default";
		}
		return threadPoolCache.get(name).getService(name, corePoolSize, maximumPoolSize,
				keepAliveTime, unit, maxWaitTask);
	}

	public static ExecutorService getInstance(String name, int corePoolSize, int maximumPoolSize) {
		return threadPoolCache.get(name).getService(name, corePoolSize, maximumPoolSize, 5,
				TimeUnit.MINUTES, ThreadPool.THREAD_MAX_THREAD_WAIT);
	}

	public static ExecutorService getInstance(String name) {
		return ThreadPool.getInstance(name, ThreadPool.THREAD_POOL_CORE_SIZE,
				ThreadPool.THREAD_POOL_MAX_SIZE, 5, TimeUnit.MINUTES,
				ThreadPool.THREAD_MAX_THREAD_WAIT);
	}

	public static ExecutorService getInstance() {
		return getInstance("default");
	}

	public static void main(String[] arg) {
		ConcurrentMap<String, Object> map = Maps.newConcurrentMap();
		for (int i = 0; i < 10; i++) {
			map.put(i + "key", i + "value");
		}

		Set<Map.Entry<String, Object>> entries = map.entrySet();

		Set<String> keys = map.keySet();

		int count = 0;

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(map.get(entry.getKey()));
			if (count++ == 5) {
				map.remove(entry.getKey());
			}
		}
		for (Map.Entry<String, Object> entry : entries) {
			System.out.println(entry.getKey());
			System.out.println(map.get(entry.getKey()));
			if (count++ == 5) {
				map.remove(entry.getKey());
			}
		}

		for (String key : map.keySet()) {
			System.out.println(key);
			System.out.println(map.get(key));
			if (count++ == 5) {
				map.remove(key);
			}
		}

		for (String key : keys) {
			System.out.println(key);
			System.out.println(map.get(key));
			if (count++ == 5) {
				map.remove(key);
			}
		}

	}

}
