package com.test.cglib;

import java.lang.reflect.Method;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.BinaryJedis;

public class CglibTest {

	
	
	public static void main(String[] args) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(BinaryJedis.class);
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
					throws Throwable {
				return null;
			}
		});
		BinaryJedis jedis = (BinaryJedis) enhancer.create();
		System.out.println(jedis.bgsave());
		
	}

}
