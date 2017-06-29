package com.netease.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期有关的操作工具类，主要包括：<br/>
 * 日期的创建、加减、比较、格式化、获取当前日期、计算两个日期之间的差值、取日期局部信息等操作。<br/>
 * 
 * @author 开发支持中心
 */
public class DateUtil{
	/**
	 * 加、减天数。
	 * 
	 * @param date
	 *         基准日期。
	 * @param addedDays
	 *         如果>0，则增加天数；否则，会减天数。 
	 * @return
	 *         计算后的日期。
	 */
	public static Date addDays(Date date, int addedDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, addedDays);
		return cal.getTime();
	}	

	/**
	 * 加、减月数。
	 * 
	 * @param date
	 *         基准日期。
	 * @param addedMonths
	 *         如果>0，则增加月数；否则，会减月数。 
	 * @return
	 *         计算后的日期。
	 */
	public static Date addMonths(Date date, int addedMonths) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, addedMonths);
		return c.getTime();
	}

	/**
	 * 根据生日计算年龄。
	 * 
	 * @param birth
	 *            字符串格式的生日。
	 * @param format
	 *            日期格式。
	 * @return
	 *            年龄。
	 * @throws ParseException
	 */
	public static int calculateAge(String birth,String format) 
			throws ParseException {
	
		Date birthday = getDate(birth, format);
		return calculateAge(birthday);
	}
	
	/**
	 * 根据生日计算年龄。
	 * 
	 * @param birth
	 *        生日。
	 * @return
	 *        年龄。
	 */
	public static int calculateAge(Date birth) {
	
		Calendar c = Calendar.getInstance();
		Calendar b = Calendar.getInstance();
		b.setTime(birth);
		
		int thisYear = c.get(Calendar.YEAR);
		int thisMonth = c.get(Calendar.MONTH);
		int thisDate = c.get(Calendar.DATE);
		
		int birthYear = b.get(Calendar.YEAR);
		int birthMonth = b.get(Calendar.MONTH);
		int birthDate = b.get(Calendar.DAY_OF_MONTH);
		
		if (thisMonth < birthMonth) // 今年还没过生日
		{
			return thisYear - birthYear - 1;
		} else if (thisMonth > birthMonth) {
			return thisYear - birthYear;
		} else{
			if (thisDate < birthDate) // 在生日月份没到具体日子
			{
				return thisYear - birthYear - 1;
			} else {
				return thisYear - birthYear;
			}
		}
	}

	/**
	 * 时间比较。
	 * 
	 * @param date1 日期1
	 * @param date2 日期2
	 * @return 0 : 相同；<br/>
	 *        小于0的值：date1小于date2.<br/>
	 *        大于0的值：date1大于date2. <br/>
	 */
	public static int compareDate(Date date1, Date date2) {
	
		Calendar c1 = Calendar.getInstance() ;
		c1.setTime(date1);
		
		Calendar c2 = Calendar.getInstance() ;
		c2.setTime(date2);
		
		return c1.compareTo(c2);
		
	}
		
	/**
	 * 按指定格式把Date对象格化为字符串。
	 * 
	 * @param d
	 *        待格式化的日期。
	 * @param format 
	 *        日期格式。例如：“yyyy-MM-dd”。参见：
	 *          <a href="http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
	 * @return 
	 *        格式化后的日期字符串，如果传入的日期对象为NULL，返回null。
	 */
	public static String format(Date d, String format)
	{
		return d==null? null : new SimpleDateFormat(format).format(d);
	}

	/**
	 * 按指定格式把Timestamp对象格化为字符串。
	 * 
	 * @param time
	 *         带格式化的Timestamp对象。
	 * @param format
	 *          日期格式。例如：“yyyy-MM-dd”。参见：
	 *          <a href="http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
	 * @return  格式化后的日期字符串，如果传入的日期对象为NULL，返回空字符串。
	 */
	public static String format(Timestamp time, String format)
	{
		return time==null? null: new SimpleDateFormat(format).format(time);
	}
	
	/**
	 * 将字符串格式化为日期对象
	 * @param date
	 * @param format
	 * @return 如果date为空或格式不标准，返回NULL，否则返回对应的日期对象
	 */
	public static Date formatToDate(String date, String format)
	{
		try
		{
			if (null==date || "".equalsIgnoreCase(date))
			{
				return null;
			}

			SimpleDateFormat sorceFmt = new SimpleDateFormat(format);
			return new Date(sorceFmt.parse(date).getTime());
		}
		catch (ParseException e)
		{
			LoggerUtil.debug("invalid date :" + date);
			return null;
		}
	}

	/**
	 * 将字符串格式化为日期对象
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Timestamp formatToTimestamp(String dateStr, String format)
	{
		try
		{
			SimpleDateFormat sorceFmt = new SimpleDateFormat(format);
			return new Timestamp(sorceFmt.parse(dateStr).getTime()); // 一天的时间24*3600*1000
		}
		catch (ParseException e)
		{
			return null;
		}
	}

	/**
	 * 判断date 是否在start 和 end之间
	 * 
	 * @param date 待比较的日期对象。
	 * @param start 日期区间的开始处。
	 * @param end   日期区间的结束处。
	 * @return true：date在[start,end]区间内；false：在区间外。
	 */
	public static boolean isBetween(Date date, Date start, Date end) {
		
		if ( date == null || start == null || end == null )
			return false;
		
		return date.after(start) && date.before(end);
	}

	/**
	 * 判断是否是当月最后一天。
	 * 
	 * @param date 待判断的日期。
	 * @return true：是当月最后一天；false：不是。
	 */
	public static boolean isLastDayOfMonth(Date date)
	{
		if (date == null)
		{
			return false;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return day == last;
	}

	/**
	 * 判断传入日期是否是今天
	 * @param date
	 * @return true:是今天。< br/>
	 *         false:不是今天。
	 */
	public static boolean isToday(Date date)
	{
		if (date == null)
		{
			return false;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		return sdf.format(Calendar.getInstance().getTime())
				.equals(sdf.format(date.getTime()));
			
	}



	/**
	 * 根据年、月、日创建日期对象。
	 * 
	 * @param year  年
	 * @param month 月
	 * @param day   日
	 * @return  Date对象。 
	 */
	public static Date getDate(int year, int month, int day) {
		
		return getDate(year, month, day, 0, 0, 0);
	}
	
	/**
	 * 根据年、月、日、小时、分钟、秒创建 Date对象。
	 * 
	 * @param year  年
	 * @param month 月
	 * @param day   日
	 * @param hour  小时
	 * @param minute 分钟
	 * @param second 秒
	 * @return Date对象。
	 */
	public static Date getDate(int year, int month, int day, 
			int hour, int minute, int second) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, hour, minute, second);
		return calendar.getTime();
	}

	/**
	 * 将字符串格式化为日期对象
	 * @param date
	 * @param format
	 *          日期格式，例如“yyyy-MM-dd”。参见：
	 *          <a href="http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
	 * @return 如果date为空或格式不正确，返回NULL，否则返回对应的日期对象
	 * @throws ParseException 
	 */
	public static Date getDate(String date, String format) throws ParseException{
		
		SimpleDateFormat sorceFmt = new SimpleDateFormat(format);
		return new Date(sorceFmt.parse(date).getTime());
	}
	
	/**
	 * 将字符串格式化为Timestamp对象。
	 * 
	 * @param str    待转换的字符串。
	 * @param format 格式
	 * @return  转换或的Timestamp对象。
	 * @throws ParseException
	 */
	public static Timestamp getTimestamp(String str, String format) 
	throws ParseException {
	
		SimpleDateFormat sorceFmt = new SimpleDateFormat(format);
		return new Timestamp(sorceFmt.parse(str).getTime()); // 一天的时间24*3600*1000
	}

	/**
	 * 获得begin-end的天数值。
	 * 
	 * @param begin 开始的Date对象。
	 * @param end  结束的Date对象。
	 * @return begin-end的天数值。
	 */
	public static int getDiffDays(Date begin, Date end) {
	
		return (int) (getDiffMinutes(begin, end) / 1440);
	}
	
	/**
	 * 获得begin-end的分钟值。
	 * 
	 * @param begin 开始的Date对象。
	 * @param end  结束的Date对象。
	 * @return begin-end的分钟值
	 */
	public static long getDiffMinutes(Date begin, Date end) {
	
		return getDiffMsecs(begin, end) / (60 * 1000);
	}
	
	/**
	 * 获得begin-end的毫秒值。
	 * 
	 * @param begin 开始的Date对象。
	 * @param end  结束的Date对象。
	 * @return begin-end的毫秒值。
	 */
	public static long getDiffMsecs(Date begin, Date end) {
	
		return end.getTime() - begin.getTime();
	}
	
	/**
	 * 取Date对象的局部信息“年”。
	 * @param date Date对象。
	 * @return 年。
	 */
	public static int getYear(Date date) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * 取Date对象的局部信息“月”。
	 * 
	 * @param date Date对象。
	 * @return 月
	 */
	public static int getMonth(Date date) {
	
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return  cal.get(Calendar.MONTH) + 1;
	}
	
	/**
	 * 取Date对象的局部信息“天”。
	 * 
	 * @param date Date对象。
	 * @return  天.
	 */
	public static int getDay(Date date) {
	
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return  cal.get(Calendar.DAY_OF_MONTH);
	}	

	/**
	 * 得到Date对象的星期。
	 * 
	 * @param date Date对象。
	 * @return  1:周日、2：周一....、7:周六。
	 */
	public static int getWeek(Date date) {
	
		Calendar current = Calendar.getInstance();
		current.setTime(date);
		return  current.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * 得到昨天。
	 * 
	 * @return 昨天的同一时间点。
	 */
	public static Date getYesterday() {
		return addDays(new Date(), -1);
	}

	public static void main(String[] args) throws ParseException {
		//valueOf("2007-02-05","yyyy-MM-dd");
		//System.out.println("2007-02-01,age = "+calculateAge("2007-02-01","yyyy-MM-dd"));
		//System.out.println("2007-02-02,age = "+calculateAge("2007-02-02","yyyy-MM-dd"));
		//System.out.println("2007-02-03,age = "+calculateAge("2007-02-03","yyyy-MM-dd"));
		//System.out.println("2007-02-05,age = "+calculateAge("2007-02-05","yyyy-MM-dd"));
		//System.out.println("2007-02-10,age = "+calculateAge("2007-02-10","yyyy-MM-dd"));
		//System.out.println("2007-01-31,age = "+calculateAge("2007-01-31","yyyy-MM-dd"));
		
		Calendar c = Calendar.getInstance() ;
//		c.set(2007, 10, 20);
		System.out.println( c.getTime() + " is today ? " +  isToday(c.getTime() ));
		System.out.println(DateUtil.getWeek(new Date()));
		String display_time = DateUtil.format(DateUtil.getDate("2012-11-17 16:00:00", "yyyy-MM-dd hh:mm:ss"),"yyyy-MM-dd hh:mm");
		System.out.println(display_time);
		String display_time2 = DateUtil.format(DateUtil.getDate("2012-11-17 16:00:00", "yyyy-MM-dd hh:mm:ss"),"yyyy-MM-dd HH:mm");
		System.out.println(display_time2);
		
		System.out.println(DateUtil.format(new Date(), "yyyy年MM月dd日"));
		
		System.out.println(DateUtil.getDiffMinutes(new Date(), DateUtil.addDays(new Date(), 1)));
		
		System.out.println(DateUtil.format(new Date(), "MM月dd日HH时mm分"));
	}
}



