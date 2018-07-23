package com.test.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

	public static void main(String[] args) {
		Matcher matcher = Pattern.compile("\\d+$").matcher("Khhhhhwai Go-Papaya-01");
		System.out.println(matcher.find());
		System.out.println(matcher.group());
	}

}
