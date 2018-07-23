package com.test.executor;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import com.java.kj.executor.KjExecutorService;

public class ExecutorServiceTest {

	public abstract static class Task implements Runnable {
		Integer dependCount = 1;
		private AtomicInteger dependFinish = new AtomicInteger(0);

		public void runTask() {
			if (this.dependFinish.incrementAndGet() == this.dependCount) {
				this.run();
			}
		}
	}

	static void merge(int[] data, int s, int mid, int e) {
		try {
			int[] x = new int[mid - s + 1];
			int[] y = new int[e - mid];
			for (int i = 0, len = x.length; i < len; i++) {
				x[i] = data[s + i];
			}
			for (int i = 0, len = y.length; i < len; i++) {
				y[i] = data[i + mid + 1];
			}
			int i = 0, j = 0, z = s;
			int xlen = x.length, ylen = y.length;
			while (i < xlen && j < ylen) {
				if (x[i] <= y[j]) {
					data[z++] = x[i++];
				} else {
					data[z++] = y[j++];
				}
			}
			while (i < xlen) {
				data[z++] = x[i++];
			}
			while (j < ylen) {
				data[z++] = y[j++];
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

	static void insert(int[] data, int s, int e) {
		for (int i = s + 1; i <= e; i++) {
			int t = data[i];
			for (int j = i - 1; j >= s; j--) {
				if (t < data[j]) {
					data[j + 1] = data[j];
					if (j == s) {
						data[j] = t;
					}
				} else {
					data[j + 1] = t;
					break;
				}
			}
		}
	}

	static void sort(int[] data, int s, int e) {
		if (s == e) {
			return;
		}
		int mid = (s + e) / 2;
		sort(data, s, mid);
		sort(data, mid + 1, e);
		merge(data, s, mid, e);
	}

	static void sort(KjExecutorService kjService, int[] data, int s, int e, Task task) {
		if (e - s == 31) {
			sort(data, s, e);
			task.runTask();
			return;
		}
		int mid = (s + e) / 2;
		Task t = new Task() {
			@Override
			public void run() {
				merge(data, s, mid, e);
				// System.out.println("s=" + s + " e=" + e);
				// for (int n = s; n <= e; n++) {
				// System.out.println(data[n]);
				// }
				task.runTask();
			}
		};
		t.dependCount = 2;

		kjService.submit(new KjExecutorService.Task<?>[] { new KjExecutorService.Task<Void>() {
			@Override
			public Void runTask(Object[] objs) {
				sort(kjService, data, s, mid, t);
				return null;
			}
		}, new KjExecutorService.Task<Void>() {
			@Override
			public Void runTask(Object[] objs) {
				sort(kjService, data, mid + 1, e, t);
				return null;
			}
		} }, null, true);

	}

	public static void main(String[] args) throws InterruptedException {
		int[] data = new int[536870912/16];
		// int[] data2 = new int[100000000];
		for (int i = 0, len = data.length; i < len; i++) {
			data[i] = Math.abs(new Random().nextInt(100));
			// data2[i] = data[i];
			// System.out.println(data[i]);
		}
		System.out.println("sort");
		// long s2 = System.currentTimeMillis();
		// sort(data, 0, data.length - 1);
		// System.out.println("escape2:" + (System.currentTimeMillis() - s2));
		long s = System.currentTimeMillis();
		KjExecutorService kjService = new KjExecutorService();
		sort(kjService, data, 0, data.length - 1, new Task() {
			@Override
			public void run() {
				// for (int x : data) {
				// System.out.println(x);
				// }
				System.out.println("escape:" + (System.currentTimeMillis() - s));
				kjService.shutdown();
			}
		});

		// s2 = System.currentTimeMillis();
		// sort(data, 0, data.length - 1);
		// System.out.println("escape2:" + (System.currentTimeMillis() - s2));
	}

}
