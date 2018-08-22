package com.test.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class StreamTest {

	public static void main(String[] args) {
		List<String> stringList = Arrays
				.asList(Lists.newArrayList("1", "2", "3"), Lists.newArrayList("1", "2", "3"),
						Lists.newArrayList("1", "2", "3"))
				.stream().flatMap(t -> t.stream()).collect(Collectors.toList());
		System.out.println(stringList);
	}

}
