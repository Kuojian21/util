package com.tools.date;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 */
public final class DateUtils {

	public static Calendar getCalendar(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar getCalendar(int year, int month, int day, int hour, int minute,
			int second, int millisecond) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millisecond);
		return calendar;
	}

	public static Calendar add(Calendar calendar, int field, int num) {
		calendar.add(field, num);
		return calendar;
	}
	
	public static Date trunc(Date date, int field) {
		if (date == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		calendar.setTime(date);

		switch (field) {
		case Calendar.YEAR:
			calendar.set(Calendar.MONTH, 0);
		case Calendar.MONTH:
			calendar.set(Calendar.DAY_OF_MONTH, 0);
		case Calendar.DAY_OF_MONTH:
			calendar.set(Calendar.HOUR_OF_DAY, 0);
		case Calendar.HOUR_OF_DAY:
			calendar.set(Calendar.MINUTE, 0);
		case Calendar.MINUTE:
			calendar.set(Calendar.SECOND, 0);
		case Calendar.SECOND:
			calendar.set(Calendar.MILLISECOND, 0);
		case Calendar.MILLISECOND:
		default:
		}
		return calendar.getTime();
	}

	public static int compare(Date d1, Date d2, int field) {
		long temp = between(getCalendar(d1), getCalendar(d2), field);
		if (temp > 0) {
			return 1;
		} else if (temp == 0) {
			return 0;
		} else {
			return -1;
		}

	}

	public static long between(Calendar bg, Calendar ed, int field) {
		long bl = 0l;
		long el = 0l;
		long multiple = 1l;
		switch (field) {
		case Calendar.MILLISECOND:
			bl = bg.getTime().getTime();
			el = ed.getTime().getTime();
			break;
		case Calendar.SECOND:
			bl = bg.getTime().getTime() / 1000;
			el = ed.getTime().getTime() / 1000;
			break;
		case Calendar.MINUTE:
			bl = bg.getTime().getTime() / (1000 * 60);
			el = ed.getTime().getTime() / (1000 * 60);
			break;
		case Calendar.HOUR:
		case Calendar.HOUR_OF_DAY:
			bl = bg.getTime().getTime() / (1000 * 60 * 60);
			el = ed.getTime().getTime() / (1000 * 60 * 60);
			break;
		case Calendar.DAY_OF_MONTH:
		case Calendar.DAY_OF_WEEK:
		case Calendar.DAY_OF_WEEK_IN_MONTH:
		case Calendar.DAY_OF_YEAR:
			bl = bg.getTime().getTime() / (1000 * 60 * 60 * 24);
			el = ed.getTime().getTime() / (1000 * 60 * 60 * 24);
			break;
		case Calendar.MONTH:
			bl += bg.get(Calendar.MONTH) + 1;
			el += ed.get(Calendar.MONTH) + 1;
			multiple = 12;
		case Calendar.YEAR:
			bl += bg.get(Calendar.YEAR) * multiple;
			el += ed.get(Calendar.YEAR) * multiple;
		default:
			throw new RuntimeException("参数错误：field=" + field);
		}
		return el - bl;
	}

	public static void main(String[] args) {
		
	}
}
