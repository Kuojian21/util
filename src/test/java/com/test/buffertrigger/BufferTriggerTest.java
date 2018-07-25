package com.test.buffertrigger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.phantomthief.collection.BufferTrigger;
import com.google.common.collect.Lists;

public class BufferTriggerTest {

	public static void main(String[] args) throws InterruptedException {
		BufferTrigger<String> bufferTrigger = BufferTrigger.<String, List<String>>simple().interval(3, TimeUnit.SECONDS)
				.setContainer(Lists::newArrayList, (c, e) -> {
					return c.add(e);
				}).consumer(t -> {
					t.forEach(e -> {
						System.out.println(e);
					});
				}).build();

		for (int i = 0; i < 1000; i++) {
			bufferTrigger.enqueue("" + i);
		}
		Thread.sleep(5000);
		for (int i = 0; i < 1000; i++) {
			bufferTrigger.enqueue("xxx");
		}

		Thread.sleep(1000000);
	}

}
