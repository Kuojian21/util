package com.java.kj.slide;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Maps;

public class KjSlide {

	private static final long EPOCH = 1344322705519L;

	private final ConcurrentMap<Long, AtomicInteger> windows = Maps.newConcurrentMap();
	private final AtomicInteger count = new AtomicInteger(0);

	private final AtomicLong index;
	private final AtomicLong last;

	private TimeUnit unit;
	private long grain;
	private int size;
	private long rate;

	public KjSlide(int period, TimeUnit unit, long rate) {
		this.unit = unit;
		this.rate = rate;
		this.grain = this.nextUnit().toMillis(1);
		this.last = new AtomicLong((System.currentTimeMillis() - EPOCH) / this.grain);
		this.size = (int) (unit.toMillis(1) / this.grain);
		this.index = new AtomicLong(this.last);
		this.windows.putIfAbsent(this.index.get(), new AtomicInteger(0));
		for (int i = 1; i < size; i++) {
			this.windows.putIfAbsent(this.index.get(), new AtomicInteger(0));
			this.index.decrementAndGet();
		}
	}

	public boolean add() {
		long last = (System.currentTimeMillis() - EPOCH) / this.grain;
		
		while(true) {
			
		}
	}

	public TimeUnit nextUnit() {
		TimeUnit l = TimeUnit.MILLISECONDS;
		for (TimeUnit u : TimeUnit.values()) {
			if (u.equals(this.unit)) {
				return l;
			} else {
				l = u;
			}
		}
		return l;
	}

}
