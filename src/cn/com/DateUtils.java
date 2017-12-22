package cn.com;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.implement.ComData;

public class DateUtils {

	public final static String TIME_FORMAT = "HH:mm:ss";
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_TIME_MSEC_FORMAT = "yyyy-MM-dd HH:mm:ss.fff";

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