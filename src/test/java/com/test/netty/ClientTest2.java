package com.test.netty;

import java.util.Random;

import com.java.kj.netty.KjClient;

public class ClientTest2 {
	public static void main(String[] args) {
		KjClient client = new KjClient(2, "127.0.0.1", 8888);
		for (int i = 0; i < 10; i++) {
			int x = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						String data = new Random().nextInt(10) + "";
						client.send(data, true);
//						System.out.println("线程" + x + "\t\t发送：" + data + ",返回：" + client.send(data, true));

						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}

					}
				}
			}).start();
		}
	}
}
