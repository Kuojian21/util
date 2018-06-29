package com.test.buffertrigger;

import java.util.concurrent.TimeUnit;

import com.github.phantomthief.collection.BufferTrigger;

public class BufferTriggerTest {

	public static void main(String[] args) {
		BufferTrigger.simple().interval(1, TimeUnit.SECONDS).consumer((t) -> {
			System.out.println("1");
		}).build();
	}

}
