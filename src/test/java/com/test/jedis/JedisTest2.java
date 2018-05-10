package com.test.jedis;

import com.google.common.collect.Sets;
import com.java.kj.base.KjPool.Action;
import com.java.kj.jedis.KjJedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class JedisTest2 {
	public static void main(String[] args) throws Exception {
		KjJedis<Jedis> jedis = new KjJedis<Jedis>(new JedisSentinelPool("master",
				Sets.newHashSet("10.200.142.34:26381", "10.200.142.32:26381"), "wwfsjr_test"));
		
		jedis.execute(new Action<Jedis,Object>(){
			@Override
			public Object action(Jedis jedis, Object... objs) throws Exception {
				jedis.pipelined();
				return null;
			}
		});
	}
}
