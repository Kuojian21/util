package com.resort;

public class Test {

	private static int x, y, m, n;// 测试用的信号变量

	public static void main(String[] args) throws InterruptedException {
		m = 1;
		x = n;
		int count = 10000;
		for (int i = 0; i < count; ++i) {
			x = y = m = n = 0;
			Thread one = new Thread() {
				public void run() {
					m = 1;
					x = n;
				};
			};
			Thread two = new Thread() {
				public void run() {
					n = 1;
					y = m;
				};
			};
			one.start();
			two.start();
			one.join();
			two.join();
			System.out.println("index:" + i + " {x:" + x + ",y:" + y + "}");
		}
	}
}