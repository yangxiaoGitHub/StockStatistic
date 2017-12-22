package cn.com;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DataUtils {
	
	// 整型常量
	public final static int CONSTANT_INTEGER_ZERO = 0;
	public final static int CONSTANT_INTEGER_ONE = 1;
	public final static int CONSTANT_INTEGER_TWO = 2;
	public final static int CONSTANT_INTEGER_THREE = 3;
	public final static int CONSTANT_INTEGER_FOUR = 4;
	public final static int CONSTANT_INTEGER_FIVE = 5;
	public final static int CONSTANT_INTEGER_SIX = 6;
	public final static int CONSTANT_INTEGER_SEVEN = 7;
	public final static int CONSTANT_INTEGER_EIGHT = 8;
	public final static Integer CONSTANT_LONG_DAY = 12; //A股休市时间最高限制
	public final static Double CONSTANT_MAX_CHANGE_RATE = 11.0;
	public final static Double CONSTANT_MIN_CHANGE_RATE = -11.0;
	public final static BigDecimal CONSTANT_ONE = new BigDecimal("1");
	public final static BigDecimal CONSTANT_TEN = new BigDecimal("10");
	public final static BigDecimal CONSTANT_HUNDRED = new BigDecimal("100");
	public final static BigDecimal CONSTANT_TEN_THOUSAND = new BigDecimal("10000"); //万
	public final static BigDecimal CONSTANT_HUNDRED_MILLION = new BigDecimal("100000000");  //亿
	// 字符串常量
	public final static String CONSTANT_SPACES = "   ";
	public final static String CONSTANT_BLANK = "";
	public static final String CONSTANT_SH_CAPITAL = "SH";
	public static final String CONSTANT_SZ_CAPITAL = "SZ";
	public static final String CONSTANT_SH_SMALL = "sh";
	public static final String CONSTANT_SZ_SMALL = "sz";
	public static final String CONSTANT_STRING_SIX = "6";
	public static final String CONSTANT_STRING_SEVEN = "7";
	// 小数常量
	public final static Double CONSTANT_ZERO_DOT_ONE = 0.1;
	public final static Double CONSTANT_DOUBLE_ZERO = 0.0;
	public static final Double CONSTANT_TODAY_OPEN_LIMIT = 800.00;

	public static boolean isNumeric(String value) {
		
		for (int i = 0; i < value.length(); i++) {
			if (!Character.isDigit(value.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String formatNumber(Double changeRate) {

		DecimalFormat decimal = new DecimalFormat("##.00");
		return decimal.format(changeRate);
	}

	public static boolean isStockAliasCode(String stockAliasCode) {

		if (stockAliasCode.contains("SH") || stockAliasCode.contains("SZ")) {
			String stockCode = stockAliasCode.substring(2);
			if (isNumeric(stockCode)) return true;
			else return false;
		} else {
			return false;
		}
	}

	/**
	 * 正则表达式去掉多余的.与0
	 *
	 */
	public static String subZeroAndDot(Double val){
		String value = val.toString();
	    if(value.indexOf(".") > 0){
	    	value = value.replaceAll("0+?$", ""); //去掉多余的0
	    	value = value.replaceAll("[.]$", ""); //如最后一位是.则去掉
	    }
	    return value;
	}
}
