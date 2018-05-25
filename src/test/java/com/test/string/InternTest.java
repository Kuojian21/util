package com.test.string;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

public class InternTest {

	public static Integer count = 0;

	public static void main(String[] args) throws InterruptedException {

		int circle = 10;
		int tasks = 100000000;
		if (args.length == 2) {
			circle = Integer.parseInt(args[0]);
			tasks = Integer.parseInt(args[1]);
		}

		for (int k = 0; k < circle; k++) {
			long b = System.currentTimeMillis();
			ExecutorService service = Executors.newFixedThreadPool(100);
			for (int i = 0; i < tasks; i++) {
				int j = i;
				service.submit(new Runnable() {
					@Override
					public void run() {
						synchronized (("kuojian" + j).intern()) {
							count++;
						}
						// System.out.println(++count);
					}
				});
			}
			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);
			System.out.println("StringTableSize:" + k + ":" + tasks + ":" + (System.currentTimeMillis() - b));

			b = System.currentTimeMillis();
			service = Executors.newFixedThreadPool(100);
			ConcurrentMap<String, Object> map = Maps.newConcurrentMap();
			Object obj = new Object();
			for (int i = 0; i < tasks; i++) {
				int j = i;
				service.submit(new Runnable() {
					@Override
					public void run() {
						String key = "kuojian" + j;
						map.putIfAbsent(key, obj);
						count++;
						map.remove(key);
						// System.out.println(++count);
					}
				});
			}
			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);
			System.out.println("HashMap:" + k + ":" + tasks + ":" + (System.currentTimeMillis() - b));
		}
	}

}
