package com.test.classloader;

public class ClassLoaderTest {

	public static void main(String[] args) throws InterruptedException {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while(true) {
						Thread.sleep(1000);
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		Thread.sleep(3000);
		
		ClassLoaderTest.class.getClassLoader();
	}

}
