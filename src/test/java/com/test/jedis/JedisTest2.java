package com.test.jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;
import com.java.kj.jedis.KjJedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class JedisTest2 {
	public static void main(String[] args) throws Exception {
		int circle = 10;
		int tasks = 1000;
		if (args.length == 2) {
			circle = Integer.parseInt(args[0]);
			tasks = Integer.parseInt(args[1]);
		}

		KjJedis<Jedis> jedis1 = new KjJedis<Jedis>(new JedisSentinelPool("master",
				Sets.newHashSet("10.200.142.34:26381", "10.200.142.32:26381"), "wwfsjr_test"));
		
		jedis1.jedisCommands().del("TEST_SET");
		for (int i = 0; i < 2000; i++) {
			jedis1.jedisCommands().zadd("TEST_SET", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + i,
					"184688.SZ|博时新兴成长混合型证券投资基金" + i);
		}

		for (int k = 0; k < circle; k++) {
			long b = System.currentTimeMillis();
			ExecutorService service = Executors.newFixedThreadPool(100);
			for (int i = 0; i < tasks; i++) {
				service.submit(new Runnable() {
					@Override
					public void run() {
						jedis1.jedisCommands().zrevrange("TEST_SET", 0, 19);
					}
				});
			}
			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);
			System.out.println(
					"redis1:读:" + k + ":" + tasks + ":2000:184688.SZ|博时新兴成长混合型证券投资基金:" + (System.currentTimeMillis() - b));
		}
		System.exit(0);
	}
}
