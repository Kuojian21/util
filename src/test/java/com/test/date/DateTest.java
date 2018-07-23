package com.test.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTest {

	public static void main(String[] args) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		System.out.println(LocalDateTime.from(formatter.parse(formatter.format(LocalDateTime.now()))));
	}

}
