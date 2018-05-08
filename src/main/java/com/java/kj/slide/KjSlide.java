package com.java.kj.slide;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Maps;

public class KjSlide {

	private static final ConcurrentMap<String,AtomicInteger> WINDOWS = Maps.newConcurrentMap();
	private static final long IDEPOCH = 1344322705519L; 
	
}
