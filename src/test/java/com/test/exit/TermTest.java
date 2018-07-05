package com.test.exit;

import sun.misc.Signal;

@SuppressWarnings("restriction")
public class TermTest {

	public static void main(String[] args) {
		Signal.handle(new Signal("TERM"), signal ->{
			System.out.println("exit");
			new Exception().printStackTrace();
		});
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.setDaemon(false);
		thread.start();
	}

}
