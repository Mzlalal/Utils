package com.a.ds.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
/**
 * 日期处理工具类.
 * 
 */
public final class DateUtil {

	/**
	 * 构造函数.
	 */
	private DateUtil() {

	}

	/**
	 * 日期类型 yyyy-MM-dd.
	 */
	public final static String DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * 日期类型yyyy-MM-dd HH:mm:ss.
	 */
	public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private final static Long DATE_NUMBER = 86400000L;

	private final static Long MINUTE_NUMBER = 60000L;
	
	private static final String[] DATE_PARSE_PATTENS = new String[] {"yyyyMMddHHmmss",
		"yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss","yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss.s",
		"yyyy/MM/dd HH:mm", "yyyyMMdd HHmmss.s", "yyyy-MM-dd","yyyyMMdd"};
	
	private static final String DATE_FORMAT1 = "yyyy-MM-dd HH:mm:ss";

	private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();
	
	public static Date parseDate(String date) {
		try {
			return DateUtils.parseDate(date, DATE_PARSE_PATTENS);
		} catch (ParseException e) {
			try {
				return DateUtils.parseDate("19000101", DATE_PARSE_PATTENS);
			} catch (ParseException e1) {
				e1.printStackTrace();
				return new Date();
			}
		}
	}
	
	/**
	 * 
	 * <p>
	 * 日期格式转换
	 * 
	 * @param date
	 * @return
	 * @return String author: LinKang
	 */
	public static String formatDate(Date date) {
		if(date==null){
			return null;
		}
		return getDateFormat().format(date);
	}
	
	// 获取线程的变量副本，如果不覆盖initialValue，第一次get返回null，故需要初始化一个SimpleDateFormat，并set到threadLocal中
	public static SimpleDateFormat getDateFormat() {
		SimpleDateFormat df = (SimpleDateFormat) threadLocal.get();
		if (df == null) {
			df = new SimpleDateFormat(DATE_FORMAT1);
			threadLocal.set(df);
		}
		return df;
	}
	
	/**
	 * 字符串转换成日期,如果需转换的字符串为null,则返回为null.
	 * 
	 * @param value
	 *            String 需转换的字符串 格式为yyyy-MM-dd
	 * @return Date 日期
	 */
	public static Date string2date(String value) {
		if (value == null || ("").equals(value.trim())) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try {
			return sdf.parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 字符串转换成日期,如果需转换的字符串为null,则返回为null.
	 * 
	 * @param value
	 *            String 需转换的字符串 格式为"yyyy-MM-dd HH:mm:ss"
	 * @return Date 日期
	 */
	public static Date string2dateTime(String value) {
		if (value == null || ("").equals(value.trim())) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		try {
			Date parsedDate = sdf.parse(value);
			return parsedDate;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 字符串转换成日期,如果需转换的字符串为null,则返回为null.
	 * 
	 * @param value
	 *            String 需转换的字符串
	 * @param format
	 *            String 转换成字符型的日期格式
	 * @return Date 日期
	 */
	public static Date string2date(String value, String format) {
		if (value == null || ("").equals(value.trim())) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date parsedDate = sdf.parse(value);
			return parsedDate;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 日期转换成字符串,如果需转换的日期为null,则返回为null.
	 * 
	 * @param date
	 *            Date 需转换的日期 格式为yyyy-MM-dd
	 * @return String 日期字符串
	 */
	public static String date2string(Date date) {
		if (date == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String formatedDate = sdf.format(date);
		return formatedDate;
	}

	/**
	 * 日期转换成字符串,如果需转换的日期为null,则返回为null.
	 * 
	 * @param date
	 *            Date 需转换的日期
	 * @param format
	 *            String 转换成字符型的日期格式
	 * @return String 日期字符串
	 */
	public static String date2string(Date date, String format) {
		if (date == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String formatedDate = sdf.format(date);
		return formatedDate;
	}

	/**
	 * 取得系统当前时间.
	 * 
	 * @param format
	 *            yyyy-MM-dd HH:mm:ss:S 年月日时分秒毫杪.
	 * @return 时间
	 */
	public static String getNowDate(String format) {
		String dateTime = "";
		try {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			dateTime = sdf.format(now);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTime;
	}

	/**
	 * 
	 * @return 以 yyyy-mm-dd 形式返回当前日期
	 */
	public static String getNowDate() {
		return getNowDate(DATE_FORMAT);
	}

	/**
	 * 
	 * @return 以 yyyy-MM-dd HH:mm:ss 形式返回当前日期时间
	 */
	public static String getNowDateTime() {
		return getNowDate(DATE_TIME_FORMAT);
	}

	/**
	 * 获取两个日期相隔的天数.
	 * 
	 * @param date
	 *            日期1
	 * @param date2
	 *            日期2
	 * @return date2-date 相隔的天数
	 */
	public static long getDaysInterval(Date date, Date date2) {
		return (date2.getTime() - date.getTime()) / DATE_NUMBER;
	}

	/**
	 * 获取两个日期相隔的分钟数.
	 * 
	 * @param date
	 *            日期1
	 * @param date2
	 *            日期2
	 * @return date2-date 相隔的分钟数
	 */
	public static long getMinutesInterval(Date date, Date date2) {
		return (date2.getTime() - date.getTime()) / (MINUTE_NUMBER);

	}

	/**
	 * 获取Timestamp类型的日期.
	 * 
	 * @return 日期
	 */
	public static java.sql.Timestamp getTimestamp() {
		return java.sql.Timestamp.valueOf(getNowDateTime());
	}

	/**
	 * 获取和当前日期days天的日期.
	 * 
	 * @param days
	 *            天数
	 * @return 和当前相差days天的日期
	 */
	public static String getDate(int days) {
		Date date = new Date();
		date.setTime(date.getTime() + DATE_NUMBER * days);
		return date2string(date);
	}

	/**
	 * 返回与指定日期相差days天的日期.
	 * 
	 * @param date
	 *            指定日期
	 * @param days
	 *            天数
	 * @return 和当前相差days天的日期 YYYY-MM-DD
	 */
	public static String getDaydiffDate(Date date, int days) {
		Date result = new Date();
		result.setTime(date.getTime() + DATE_NUMBER * days);
		return date2string(result);
	}

	/**
	 * 返回与指定日期相差days天的日期.
	 * 
	 * @param date
	 *            指定的日期
	 * @param days
	 *            天数
	 * @return 和当前相差days天的日期 YYYY-MM-DD
	 */
	public static String getDaydiffDate(String date, int days) {
		Date result = new Date();
		Date temp = string2date(date);
		result.setTime(temp.getTime() + DATE_NUMBER * days);
		return date2string(result);
	}

	/**
	 * 获取日期对应的年度.
	 * 
	 * @param date
	 *            日期
	 * @return date 对应的年度
	 */
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取当前日期对应的年度.
	 * 
	 * @return 年度
	 */
	public static int getNowYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取指定日期所在的月份.
	 * 
	 * @param date
	 *            日期
	 * @return date 对应的月
	 */
	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当前日期所在的月份.
	 * 
	 * @return 对应的月份
	 */
	public static int getNowMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取指定日期在月份中的天.
	 * 
	 * @param date
	 *            日期
	 * @return 对应的天
	 */
	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取当前日期在月份中的天.
	 * 
	 * @return 对应的天
	 */
	public static int getNowDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 根据日期算年龄.
	 * 
	 * @param birthDate
	 *            出生日期
	 * @return 年龄
	 */
	public static String yearOLD(Date birthDate) {

		if (birthDate == null) {
			return null;
		}

		String age = null;
		Date nowDate = new Date();
		if (nowDate.before(birthDate)) {
			// 当前日期早于出生日期
			age = "0";
		} else {
			GregorianCalendar s = new GregorianCalendar();
			s.setTime(birthDate);
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(nowDate);

			// 计算两个年份之差
			int yDiff = c.get(Calendar.YEAR) - s.get(Calendar.YEAR);
			int mDiff = c.get(Calendar.MONTH) - s.get(Calendar.MONTH);
			int dDiff = c.get(Calendar.DATE) - s.get(Calendar.DATE);

			if (mDiff < 0 || (mDiff == 0 && dDiff < 0)) {
				yDiff--;
			}

			// 大于1岁直接显示岁
			if (yDiff >= 1) {
				age = yDiff + "岁";
			} else {
				if (dDiff < 0) {
					mDiff--;
				}

				// 大于3个月则显示月
				if (mDiff >= 3) {
					age = mDiff + "月";
				}
				// find day diff
				else {
					int s_doy = s.get(Calendar.DAY_OF_YEAR);
					int c_doy = c.get(Calendar.DAY_OF_YEAR);
					int dayDiff = c_doy - s_doy;
					if (dayDiff < 0)
						dayDiff += (s.isLeapYear(s.get(Calendar.YEAR)) ? 366
								: 365) - s_doy;

					age = dayDiff + "天";
				}
			}
		}

		return age;
	}

	/**
	 * 根据日期算年龄.
	 * 
	 * @param birthDate
	 *            出生日期
	 * @return 年龄
	 */
	public static int getYearOldByBirthday(Date birthDate) {

		int age = 0;

		if (birthDate == null) {
			return age;
		}

		Date nowDate = new Date();
		if (nowDate.before(birthDate)) {
			// 当前日期早于出生日期
			return age;
		} else {
			GregorianCalendar s = new GregorianCalendar();
			s.setTime(birthDate);
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(nowDate);

			// 计算两个年份之差
			int yDiff = c.get(Calendar.YEAR) - s.get(Calendar.YEAR);
			int mDiff = c.get(Calendar.MONTH) - s.get(Calendar.MONTH);
			int dDiff = c.get(Calendar.DATE) - s.get(Calendar.DATE);

			if (mDiff < 0 || (mDiff == 0 && dDiff < 0)) {
				yDiff--;
			}

			// 大于1岁直接显示岁
			if (yDiff >= 1) {
				age = yDiff;
			} else {
				age = 1;
			}
		}

		return age;
	}

	/**
	 * 当月第一天
	 * 
	 * @return
	 */
	public static String getFirstDay(String theDate) {
		Calendar cDay = Calendar.getInstance();
		cDay.setTime(string2date(theDate));
		final int firstDay = cDay.getActualMinimum(Calendar.DAY_OF_MONTH);
		Date firstDate = cDay.getTime();
		firstDate.setDate(firstDay);
		return DateUtil.date2string(firstDate);
	}

	/**
	 * 当月最后一天
	 * 
	 * @return
	 */
	public static String getLastDay(String theDate) {
		Calendar cDay = Calendar.getInstance();
		cDay.setTime(string2date(theDate));
		final int lastDay = cDay.getActualMaximum(Calendar.DAY_OF_MONTH);
		Date lastDate = cDay.getTime();
		lastDate.setDate(lastDay);
		return DateUtil.date2string(lastDate);
	}

	/**
	 * 依据给定的开始时间、结束时间按求出相应的日期集合
	 * 
	 * @param beginDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return 日期集合
	 */
	public static List<String> getBetweenDate(String beginDate, String endDate) {

		List<String> dateList = new ArrayList<String>();
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date begin = sdf.parse(beginDate);
			Date end = sdf.parse(endDate);
			if (begin.after(end)) { // 开始时间不能大于结束时间
				System.out.println(beginDate + "不能大于" + endDate);
				return dateList;
			}
			Calendar beginCalendar = GregorianCalendar.getInstance();
			beginCalendar.setTime(begin);
			Calendar endCalendar = GregorianCalendar.getInstance();
			endCalendar.setTime(end);
			int beginYear = beginCalendar.get(Calendar.YEAR); // 开始年份
			int beginMonth = beginCalendar.get(Calendar.MONTH) + 1; // 开始月份
			int beginDay = beginCalendar.get(Calendar.DAY_OF_MONTH); // 开始天
			int endYear = endCalendar.get(Calendar.YEAR);
			int endMonth = endCalendar.get(Calendar.MONTH) + 1; // 结束月份
			int endDay = endCalendar.get(Calendar.DAY_OF_MONTH); // 结束天
			for (int i = beginYear; i <= endYear; i++) {
				int tempEndMonth = 12;
				int tempBeginMonth = 1;
				if (i == endYear)
					tempEndMonth = endMonth;
				if (i == beginYear)
					tempBeginMonth = beginMonth;
				for (int j = tempBeginMonth; j <= tempEndMonth; j++) {
					int tempDays = getDays(i, j);
					int tempBeginDays = 1;
					if (i == endYear && j == tempEndMonth)
						tempDays = endDay;
					if (i == beginYear && j == tempBeginMonth)
						tempBeginDays = beginDay;
					for (int k = tempBeginDays; k <= tempDays; k++) {
						dateList.add(i + "-" + (j < 10 ? "0" + j : j) + "-"
								+ (k < 10 ? "0" + k : k));
					}
				}
			}

		} catch (Exception e) {
			dateList.clear();
			e.printStackTrace();
		}

		return dateList;
	}

	// 判断是否闰年
	public static boolean isRunNian(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
			return true;
		return false;
	}

	// 根据年度，月份求出当月的天数
	public static int getDays(int year, int month) {
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12)
			return 31;
		else if (month == 2) {
			if (isRunNian(year))
				return 29;
			else
				return 28;
		} else
			return 30;
	}

	// 判断是否是周末
	public static boolean isWeekend(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date time = null;
		try {
			time = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param d1
	 * @param d2
	 * @return  两个日期相差的天数
	 */
	public static long getDateDiff(Date d1,Date d2){
	   return  (d1.getTime()   -   d2.getTime())   /   (24   *   60   *   60   *   1000); 
	}
	
	public static int getHour(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	public static String addHour(String strDate,int hours){
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.string2date(strDate));
		cal.add(Calendar.HOUR_OF_DAY, hours);
		return DateUtil.date2string(cal.getTime(),"yyyy-MM-dd HH:mm:ss");
	}
	
	public static String addDay(String strDate,int days){
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.string2date(strDate));
		cal.add(Calendar.DAY_OF_MONTH, days);
		return DateUtil.date2string(cal.getTime());
	}

}
