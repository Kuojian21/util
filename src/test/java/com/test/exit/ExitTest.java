package com.test.exit;

public class ExitTest {

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						System.out.println("testx");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.setDaemon(false);
		thread.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						System.out.println("testy");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		Thread.sleep(1000);
//		System.exit(0);
	}

}
