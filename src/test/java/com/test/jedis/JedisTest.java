package com.test.jedis;

import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.google.common.collect.Lists;
import com.java.kj.jedis.KjJedis;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class JedisTest {
	public static void main(String[] args) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(-1);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		List<JedisShardInfo> infos = Lists.newArrayList();
		JedisShardInfo info = new JedisShardInfo("10.200.142.34",6387);
		info.setPassword("eQLABUKW");
		infos.add(info);
		KjJedis<ShardedJedis> jedis = new KjJedis<ShardedJedis>(new ShardedJedisPool(config,infos));
			
		int l1 = 10;
		int l2 = 10000;
		if(args.length == 2) {
			l1 = Integer.parseInt(args[0]);
			l2 = Integer.parseInt(args[1]);
		}
		
		for(int i = 0;i < l1;l1++) {
			long b = System.currentTimeMillis();
			for (int j = 0; j < l2; j++) {
				jedis.jedisCommands().incr("incr");
			}
			System.out.println(System.currentTimeMillis() - b);

			for (int j = 0; j < l2; j++) {
				jedis.jedisCommands().lpush("queue", "queue" + i);
			}
			b = System.currentTimeMillis();
			for (int j = 0; j < l2; j++) {
				jedis.jedisCommands().rpop("queue");
			}
			System.out.println(System.currentTimeMillis() - b);
		}
		
	}
}
