package com.tools.thread;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Maps;

public class ExecutorHolderMultiVersion {
	
	private final ConcurrentMap<ExecutorHolder,ConcurrentMap<Thread,AtomicInteger>> holders = Maps.newConcurrentMap();
	
	private final ThreadLocal<ExecutorHolder> holder = new ThreadLocal<ExecutorHolder>();
	
	public ExecutorService getService(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, int maxWaitTask){
		ExecutorHolder holder = this.holder.get();
		if(holder != null){
			this.holders.get(this.holder.get()).get(Thread.currentThread()).incrementAndGet();
			return holder.require();
		}else{
			Set<ExecutorHolder> keys = holders.keySet();
			for(ExecutorHolder key : keys){
				if(key.apply(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, maxWaitTask)){
					ExecutorService service = key.require();
					if(service != null){
						holders.get(key).put(Thread.currentThread(),new AtomicInteger(1));
						return service;
					}else{
						do{
							key.init();
						}while(key.require() != null);
						return generate(key);
					}
				}
			}
			return generate(new ExecutorHolder(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, maxWaitTask));
		}
	}
	
	private ExecutorService generate(ExecutorHolder holder){
		holders.putIfAbsent(holder, Maps.newConcurrentMap());
		holders.get(holder).put(Thread.currentThread(),new AtomicInteger(1));
		return holder.require();
	}
	
	public void release(){
		if(this.holder.get() != null&&this.holders.get(this.holder.get()).get(Thread.currentThread()).decrementAndGet() == 0){
			this.holder.get().release();
			this.holder.remove();
		}
	}
		
	public void destroy(){
		Set<ExecutorHolder> keys = holders.keySet();
		for(ExecutorHolder key:keys){
			if(!key.shutdown()){
				if(key.destroy()){
					this.holders.remove(key);
				}
			}
		}
	}
}
