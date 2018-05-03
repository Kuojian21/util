package com.java.kj.jedis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import com.google.common.collect.Sets;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;
import redis.clients.util.Pool;

public class JedisTemplate {

	private Pool<Jedis> pool;

	public JedisTemplate(Pool<Jedis> pool) {
		this.pool = pool;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				JedisTemplate.this.pool.close();
			}
		});
	}

	public interface JedisAction<T> {
		T action(Jedis jedis);
	}

	public interface JedisActionNoResult {
		void action(Jedis jedis);
	}

	public interface PipelineAction {
		List<Object> action(Pipeline Pipeline);
	}

	public interface PipelineActionNoResult {
		void action(Pipeline Pipeline);
	}

	public <T> T execute(JedisAction<T> jedisAction) {
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			return jedisAction.action(jedis);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public void execute(JedisActionNoResult jedisAction) {
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			jedisAction.action(jedis);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<Object> execute(PipelineAction pipelineAction) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			Pipeline pipeline = jedis.pipelined();
			pipelineAction.action(pipeline);
			return pipeline.syncAndReturnAll();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public void execute(PipelineActionNoResult pipelineAction) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			Pipeline pipeline = jedis.pipelined();
			pipelineAction.action(pipeline);
			pipeline.sync();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public JedisCommands jedisCommands() {
		return (JedisCommands) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class<?>[] { JedisCommands.class }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return JedisTemplate.this.execute(new JedisAction<Object>() {
							@Override
							public Object action(Jedis jedis) {
								try {
									return method.invoke(jedis, args);
								} catch (Exception e) {
									return null;
								}
							}
						});
					}
				});
	}
	
	public static void main(String[] args) {
		JedisTemplate jedisTemplate = new JedisTemplate(new JedisSentinelPool("master",
				Sets.newHashSet("10.200.142.34:26381", "10.200.142.32:26381"), "wwfsjr_test"));
		long b = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			System.out.println(jedisTemplate.jedisCommands().incr("incr"));;
		}
		System.out.println(System.currentTimeMillis() - b);

		for (int i = 0; i < 10000; i++) {
			System.out.println(jedisTemplate.jedisCommands().lpush("queue", "queue" + i));
		}
		b = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			System.out.println(jedisTemplate.jedisCommands().rpop("queue"));
		}
		System.out.println(System.currentTimeMillis() - b);
	}

}