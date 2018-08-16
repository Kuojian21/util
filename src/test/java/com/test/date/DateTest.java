package com.test.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTest {

	public static void main(String[] args) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		System.out.println(LocalDateTime.from(formatter.parse(formatter.format(LocalDateTime.now()))));

		// long beginTime = Instant.from()
		// .toEpochMilli();
		System.out.println(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		System.out.println(LocalDateTime.parse("20180724000000", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
				.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		// System.out.println(beginTime);

		System.out.println(new Date().getTime());
		// long endTime = Instant.from(
		// LocalDate.now()/*from(DateTimeFormatter.ofPattern("yyyyMMdd").parse("20180718")).plusDays(7)*/)
		// .toEpochMilli();
		System.out.println(LocalDateTime.parse("20180719", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
				.plusDays(7).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
	}

}
