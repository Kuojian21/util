package com.test.jedis;

import com.java.kj.jedis.KjJedis;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

public class JedisTest3 {

	public static void main(String[] args) {
		KjJedis kjJedis = new KjJedis(new Pool<Jedis>() {
			
		});
		kjJedis.jedis().bgsave();

	}

}
