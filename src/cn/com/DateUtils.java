package cn.com;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public final static String TIME_FORMAT = "HH:mm:ss";
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String YEAR_MONTH_FORMAT = "yyyy年MM月";
	public final static String MONTH_FORMAT = "yyyy-MM";
	public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_TIME_MSEC_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	public final static String MIN_DATE_ORIGINAL_STOCK = "2017-09-11";
	public final static String _YEAR = "年";
	public final static String _MONTH = "月";

	public static Date stringToDateTime(String sDate) {
		Date date = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
			date = formatter.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date[] getPreOneWeek(Date recentDate) {

		Date[] preWeek = new Date[2];
		try {
			Calendar calender = Calendar.getInstance();
			calender.setTime(recentDate);
			calender.add(Calendar.DATE, -7);
			preWeek[0] = calender.getTime();
			preWeek[1] = recentDate;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return preWeek;
	}

	public static Date[] getPreHalfMonth(Date recentDate) {

		Date[] preHalfMonth = new Date[2];
		try {
			Calendar calender = Calendar.getInstance();
			calender.setTime(recentDate);
			calender.add(Calendar.DATE, -15);
			preHalfMonth[0] = calender.getTime();
			preHalfMonth[1] = recentDate;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return preHalfMonth;
	}

	public static Date[] getPreOneMonth(Date recentDate) {

		Date[] preOneMonth = new Date[2];
		try {
			Calendar calender = Calendar.getInstance();
			calender.setTime(recentDate);
			calender.add(Calendar.MONTH, -1);
			preOneMonth[0] = calender.getTime();
			preOneMonth[1] = recentDate;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return preOneMonth;
	}

	public static Date[] getPreTwoMonth(Date recentDate) {

		Date[] preTwoMonth = new Date[2];
		try {
			Calendar calender = Calendar.getInstance();
			calender.setTime(recentDate);
			calender.add(Calendar.MONTH, -2);
			preTwoMonth[0] = calender.getTime();
			preTwoMonth[1] = recentDate;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return preTwoMonth;
	}

	public static Date[] getPreThreeMonth(Date recentDate) {

		Date[] preThreeMonth = new Date[2];
		try {
			Calendar calender = Calendar.getInstance();
			calender.setTime(recentDate);
			calender.add(Calendar.MONTH, -3);
			preThreeMonth[0] = calender.getTime();
			preThreeMonth[1] = recentDate;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return preThreeMonth;
	}

	public static Date[] getPreHalfYear(Date recentDate) {

		Date[] preHalfYear = new Date[2];
		try {
			Calendar calender = Calendar.getInstance();
			calender.setTime(recentDate);
			calender.add(Calendar.MONTH, -6);
			preHalfYear[0] = calender.getTime();
			preHalfYear[1] = recentDate;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return preHalfYear;
	}

	public static Date[] getPreOneYear(Date recentDate) {

		Date[] preOnefYear = new Date[2];
		try {
			Calendar calender = Calendar.getInstance();
			calender.setTime(recentDate);
			calender.add(Calendar.YEAR, -1);
			preOnefYear[0] = calender.getTime();
			preOnefYear[1] = recentDate;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return preOnefYear;
	}
	
	public static String dateTimeToString(Date date) {
		String sDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
			sDate = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sDate;
	}

	public static String dateTimeToString(Date date, final String format) {
		String sDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sDate = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sDate;
	}

	public static String dateTimeMsecToString(Date date) {
		String sDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_MSEC_FORMAT);
			sDate = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sDate;
	}

	public static Date stringToDate(String sDate) {
		Date date = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			date = formatter.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String dateToString(Date date) {
		String sDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			sDate = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sDate;
	}
	
	public static String dateToMonth(Date date) {
		String sDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(MONTH_FORMAT);
			sDate = sdf.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sDate;
	}
	
	public static Date monthToDate(String sDate) {
		Date date = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(MONTH_FORMAT);
			date = formatter.parse(sDate);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return date;
	}
	
	//获得某月的第一天
	public static Date getFirstDayOfMonth(int year, int month) {
		
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			cal.set(Calendar.DAY_OF_MONTH, firstDay);
			return cal.getTime();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	//获得某月的最后一天
	public static Date getLastDayOfMonth(int year, int month) {

		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			cal.set(Calendar.DAY_OF_MONTH, lastDay);
			return cal.getTime();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	//获得给定日期的年
	public static int getYearFromDate(Date date) {
		
		Instant instant = date.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
		return localDateTime.getYear();
	}

	//获得给定日期的月
	public static int getMonthFromDate(Date date) {
		
		Instant instant = date.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
		return localDateTime.getMonth().getValue();
	}

	/**
	 * 增加一天
	 * 
	 */
	public static Date addOneDay(Date date) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1); // +1天
		return c.getTime();
	}

	/**
	 * 增加一个月
	 * 
	 */
	public static Date addOneMonth(Date date) {
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		return c.getTime();
	}
	
	/**
	 * 减少一天
	 * 
	 */
	public static Date minusOneDay(Date date) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, -1); // -1天
		return c.getTime();
	}
	
	/** 
	 * 判断时间格式必须为"YYYY-MM-dd"或"YYYY-MM"
	 * 
	 */
	public static boolean isValidDate(String sDate) {

		try {
			if (sDate.length() <= DataUtils._INT_SEVEN) {
				DateFormat monthFormatter = new SimpleDateFormat(MONTH_FORMAT);
				Date month = monthFormatter.parse(sDate);
				return sDate.equals(monthFormatter.format(month));
			} else {
				DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
				Date date = dateFormatter.parse(sDate);
				return sDate.equals(dateFormatter.format(date));
			}
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 根据输入的时间，获取时间段
	 *
	 */
	public static String getTimeInterval(Date[] preOneWeek) {
		
		String startDate = DateUtils.dateToString(preOneWeek[0]);
		String endDate = DateUtils.dateToString(preOneWeek[1]);
		return startDate + "至" + endDate;
	}
	
	public static String checkDate(String startDate, String endDate) {
		if (null != startDate && !DateUtils.isValidDate(startDate)) {
			return "输入的开始日期格式不正确！";
		}
		if (null != endDate && !DateUtils.isValidDate(endDate)) {
			return "输入的结束日期格式不正确！";
		}
		return null;
	}
	
	public static String inspectDate(String startDate, String endDate) {

		if (startDate.trim().equals(DataUtils._BLANK) && endDate.trim().equals(DataUtils._BLANK)) {
			return null;
		}
		if ((startDate.trim().equals(DataUtils._BLANK) && !endDate.trim().equals(DataUtils._BLANK))
				|| !startDate.trim().equals(DataUtils._BLANK) && endDate.trim().equals(DataUtils._BLANK)) {
			return "输入的开始日期和结束日期同时为空，或者同时不为空！";
		}
		if (!DateUtils.isValidDate(startDate)) {
			return "输入的开始日期格式不正确！";
		}
		if (!DateUtils.isValidDate(endDate)) {
			return "输入的结束日期格式不正确！";
		}
		return null;
	}
	
	public static boolean isEqualsTime(Date date1, Date date2) {

		if (date1 == null || date2 == null)
			return false;
		if (date1.getTime() == date2.getTime()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isLegalDate(String sDate, Date maxStockDate) {

		Date limitDate = DateUtils.stringToDate("2015-01-05");
		if (maxStockDate != null)
			limitDate = maxStockDate;
		Date stockDate = DateUtils.stringToDate(sDate);
		if (stockDate.compareTo(limitDate) > 0)
			return true;
		else
			return false;
	}

	/**
	 * 毫秒转化时分秒毫秒
	 * 
	 */
	public static String msecToTime(Long ms) {
		Integer ss = 1000;
		Integer mi = ss * 60;
		Integer hh = mi * 60;
		Integer dd = hh * 24;

		Long day = ms / dd;
		Long hour = (ms - day * dd) / hh;
		Long minute = (ms - day * dd - hour * hh) / mi;
		Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

		StringBuffer sb = new StringBuffer();
		if (day > 0) {
			sb.append(day + "天");
		}
		if (hour > 0) {
			sb.append(hour + "小时");
		}
		if (minute > 0) {
			sb.append(minute + "分");
		}
		if (second > 0) {
			sb.append(second + "秒");
		}
		if (milliSecond > 0) {
			sb.append(milliSecond + "毫秒");
		}
		return sb.toString();
	}
}