package com.java.kj.jedis;

import java.io.Closeable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import com.java.kj.base.KjPool;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.util.Pool;

public class KjJedis<T extends Closeable> extends KjPool<T> {

	private final Action<T, Object> COMMAND = new Action<T, Object>() {
		@Override
		public Object action(T obj, Object... objs) throws Exception {
			Method method = (Method) objs[0];
			Object[] args = (Object[]) objs[1];
			return method.invoke(obj, args);
		}

	};

	private final JedisCommands JEDISCOMMANDS = (JedisCommands) Proxy.newProxyInstance(this.getClass().getClassLoader(),
			new Class<?>[] { JedisCommands.class }, new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					return KjJedis.this.execute(COMMAND, method, args);
				}
			});

	private final Jedis JEDIS;

	private final Pool<T> pool;

	public KjJedis(Pool<T> pool) {
		this.pool = pool;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(Jedis.class);
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				return KjJedis.super.execute(COMMAND, method, args);
			}
		});
		JEDIS = (Jedis) enhancer.create();
	}

	public JedisCommands jedisCommands() {
		return JEDISCOMMANDS;
	}

	public Jedis jedis() {
		return JEDIS;
	}

	@Override
	protected void close() throws Exception {
		KjJedis.this.pool.close();
	}

	@Override
	protected void returnObject(T obj) throws Exception {
		if (obj != null) {
			obj.close();
		}
	}

	@Override
	protected T borrowObject() throws Exception {
		return pool.getResource();
	}

}