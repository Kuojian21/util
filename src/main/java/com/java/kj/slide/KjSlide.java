package com.java.kj.slide;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Maps;

public class KjSlide {

	private static final long EPOCH = 102530117577364L;
	private final ConcurrentMap<Long, AtomicInteger> windows = Maps.newConcurrentMap();
	private final AtomicLong count = new AtomicLong(0);
	private final AtomicLong index;
	private TimeUnit unit;
	private long grain;
	private int size;
	private long rate;

	public KjSlide(int period, TimeUnit unit, long rate) {
		this.unit = unit;
		this.rate = rate;
		TimeUnit grainUnit = TimeUnit.NANOSECONDS;
		for (TimeUnit u : TimeUnit.values()) {
			if (u.equals(this.unit)) {
				break;
			} else {
				grainUnit = u;
			}
		}
		this.grain = grainUnit.toNanos(1);
		this.size = (int) (unit.toNanos(1) / this.grain * period);
		this.index = new AtomicLong((System.nanoTime() - EPOCH) / this.grain - this.size + 1);
	}

	public boolean tryAcquire() {
		long last = (System.nanoTime() - EPOCH) / this.grain;
		
		long upper = last - this.size;
		long i = this.index.get();
		while (i <= upper) {
			if (this.index.compareAndSet(i, ++i)) {
				AtomicInteger t = this.windows.remove(i);
				if (t != null) {
					this.count.addAndGet(-t.get());
				}
			}
		}
		
		if (this.count.get() >= this.rate) {
			return false;
		}
		
		AtomicInteger c = this.windows.get(last);
		if (c == null) {
			this.windows.putIfAbsent(last, new AtomicInteger(0));
			c = this.windows.get(last);
		}

		c.incrementAndGet();
		return this.count.incrementAndGet() <= this.rate;
	}

}
