package com.test.string;

public class InternTest {

	public static void main(String[] args) {
		for(int i = 0;i < 10;i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(int i = 0; i < 60000000;i++) {
						System.out.println(("kuojian2121212121212121212121" + i).intern());
					}
				}
			}).start();;
		}
	}
	
}
