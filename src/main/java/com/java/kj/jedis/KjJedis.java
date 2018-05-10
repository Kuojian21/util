package com.java.kj.jedis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import com.java.kj.base.KjPool;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.util.Pool;

public class KjJedis<T extends AutoCloseable> extends KjPool<T, Object> {

	private final Action<T, Object> COMMAND = new Action<T, Object>() {
		@Override
		public Object action(T obj, Object... objs) throws Exception {
			Method method = (Method) objs[0];
			Object[] args = (Object[]) objs[1];
			return method.invoke(obj, args);
		}

	};

	private final Action<T, Object> BINARYCOMMAND = new Action<T, Object>() {
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

	@SuppressWarnings("unused")
	private final BinaryJedis BINARYJEDIS;

	private Pool<T> pool;

	public KjJedis(Pool<T> pool) {
		this.pool = pool;
		Type type = pool.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			Type[] types = ((ParameterizedType) type).getActualTypeArguments();
			if (((Class<?>) types[0]).isAssignableFrom(BinaryJedis.class)) {
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(BinaryJedis.class);
				enhancer.setCallback(new MethodInterceptor() {
					@Override
					public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
							throws Throwable {
						return KjJedis.this.execute(BINARYCOMMAND, method, args);
					}
				});
				BINARYJEDIS = (BinaryJedis) enhancer.create();
			} else {
				BINARYJEDIS = null;
			}
		} else {
			BINARYJEDIS = null;
		}

	}

	public JedisCommands jedisCommands() {
		return JEDISCOMMANDS;
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