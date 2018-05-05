package com.test.netty;

import java.util.Map;
import java.util.Random;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Strings;
import com.java.kj.netty.Client;
import com.java.kj.netty.Server;

public class NettyTest {

	public static void main(String[] args) {
		Map<String, String> map = Maps.newHashMap();
		map.put("1", "Red");
		map.put("2", "white");
		map.put("3", "blue");
		Server.bind(8888, new Server.Action() {
			@Override
			public Object action(Object data) {
				String r = map.get(data);
				return Strings.isNullOrEmpty(r) ? "kuojian" : r;
			}
		});

		Client client = new Client("127.0.0.1", 8888);
		for (int i = 0; i < 100; i++) {
			int x = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						String data = new Random().nextInt(10) + "";
						System.out.println("线程" + x + "\t\t发送：" + data + ",返回：" + client.send(data, true));
/*						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}*/
					}
				}
			}).start();
		}
	}

}
