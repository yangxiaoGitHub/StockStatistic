package cn.com;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.db.bean.AllImportStock;
import cn.db.bean.DetailStock;

public class DataUtils {
	
	// 整型常量
	public final static int _INT_ZERO = 0;
	public final static int _INT_ONE = 1;
	public final static int _INT_TWO = 2;
	public final static int _INT_THREE = 3;
	public final static int _INT_FOUR = 4;
	public final static int _INT_FIVE = 5;
	public final static int _INT_SIX = 6;
	public final static int _INT_SEVEN = 7;
	public final static int _INT_EIGHT = 8;
	public final static int _INT_TEN = 10;
	public final static int _INT_FIFTEEN = 15;
	public final static int _INT_TWENTY_FIVE = 25;
	public final static int _INT_FORTY = 40;
	public final static int _INT_FIFTY = 50;
	public final static int _INT_ONE_KB = 1024;
	public final static Integer _LONG_DAY = 12; //A股休市时间最高限制
	public final static Double _MAX_CHANGE_RATE = 11.0;
	public final static Double _MIN_CHANGE_RATE = -11.0;
	public final static BigDecimal _ONE = new BigDecimal("1");
	public final static BigDecimal _TEN = new BigDecimal("10");
	public final static BigDecimal _HUNDRED = new BigDecimal("100");
	public final static BigDecimal _TEN_THOUSAND = new BigDecimal("10000"); //万
	public final static BigDecimal _HUNDRED_MILLION = new BigDecimal("100000000");  //亿
	// 字符串常量
	public final static String _DASH = "--";
	public final static String _SPACES = "   ";
	public final static String _BLANK = "";
	public static final String _SH_CAPITAL = "SH";
	public static final String _SZ_CAPITAL = "SZ";
	public static final String _SH_SMALL = "sh";
	public static final String _SZ_SMALL = "sz";
	public static final String _ZERO = "0";
	public static final String _STRING_SIX = "6";
	public static final String _STRING_SEVEN = "7";
	// 小数常量
	public final static Double _ZERO_DOT_ONE = 0.1;
	public final static Double _DOUBLE_ZERO = 0.0;
	public static final Double _TODAY_OPEN_LIMIT = 800.00;

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
	
	/**
	 * 获得最晚(最小)日期的股票详细信息
	 *
	 */
	public static AllImportStock getLastAllImportStock(List<AllImportStock> allImportStockList) {
		
		AllImportStock lastImportStock = allImportStockList.get(0);
		for (AllImportStock allImportStock : allImportStockList) {
			Date stockDate = allImportStock.getStockDate();
			Date lastStockDate = lastImportStock.getStockDate();
			if (stockDate.compareTo(lastStockDate) > 0) lastImportStock = allImportStock;
		}
		return lastImportStock;
	}
    
	/**
	 * 获得最早(最大)日期的股票详细信息
	 *
	 */
	public static AllImportStock getFirstAllImportStock(List<AllImportStock> allImportStockList) {
		
		AllImportStock firstImportStock = allImportStockList.get(0);
		for (AllImportStock importStock : allImportStockList) {
			Date stockDate = importStock.getStockDate();
			Date firstStockDate = firstImportStock.getStockDate();
			if (stockDate.compareTo(firstStockDate) < 0) firstImportStock = importStock;
		}
		return firstImportStock;
	}
	
	/**
	 * 验证统计的Json涨跌次数和statistic_stock_表中的Json涨跌次数是否相等
	 *
	 */
	public static boolean validateJsonUpAndDownNumber(String upAndDownJson, String upAndDownStatisticJson, String periodFlg) throws IOException {

		boolean validateFlg = true;
		String[] upAndDownNumberKeys = StockUtils.getUpAndDownNumberKeysByFlg(periodFlg);
		String upDownNumberKey = upAndDownNumberKeys[0];
		String upNumberKey = upAndDownNumberKeys[1];
		String downNumberKey = upAndDownNumberKeys[2];
		Map<String, Integer> upAndDownMap = JsonUtils.getMapByJson(upAndDownJson);
		Map<String, Integer> upAndDownStatisticMap = JsonUtils.getMapByJson(upAndDownStatisticJson);
		Integer upDownNumber = upAndDownMap.get(upDownNumberKey);
		Integer upNumber = upAndDownMap.get(upNumberKey);
		Integer downNumber = upAndDownMap.get(downNumberKey);
		Integer upDownStatisticNumber = upAndDownStatisticMap.get(upDownNumberKey);
		Integer upStatisticNumber = upAndDownStatisticMap.get(upNumberKey);
		Integer downStatisticNumber = upAndDownStatisticMap.get(downNumberKey);
		if (upDownNumber.compareTo(upDownStatisticNumber)!=0
			|| upNumber.compareTo(upStatisticNumber)!=0
			|| downNumber.compareTo(downStatisticNumber)!=0) {
			validateFlg = false;
		}
		return validateFlg;
	}
	
	/**
	 * 将字符串编码成16进制数字
	 * 
	 */
	public static String stringToHex(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString().trim();
	}

	/**
	 * 16进制直接转换成为字符串
	 *
	 */
	public static String hexToString(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}
	
	/**
	 * 将bytes数组转换成十六进制
	 * 
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 将十六进制转换成bytes数组
	 * 
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals(DataUtils._BLANK)) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
	/**
	 * 数字长度不够补0
	 *
	 */
	public static String supplyNumber(int num, int size) {
		String result = "";
		int numBit = String.valueOf(num).length();
		int sizeBit = String.valueOf(size).length();
		int differ = sizeBit - numBit;
		for (int index = 0; index < differ; index++) {
			result += "0";
		}
		return result + num;
	}
	
	// 两数相加
	public static <T extends Number> BigDecimal add(T firstValue, T secondValue) {
		BigDecimal firstDecimal = new BigDecimal(firstValue.toString());
		BigDecimal secondDecimal = new BigDecimal(secondValue.toString());
		return firstDecimal.add(secondDecimal);
	}

	// 两数相减
	public static <T extends Number> T sub(T firstValue, T secondValue) {
		   BigDecimal firstDecimal = new BigDecimal(firstValue.toString());
		   BigDecimal secondDecimal = new BigDecimal(secondValue.toString());
		   return (T)firstDecimal.subtract(secondDecimal);
	}

	// 两数相乘
	public static <T extends Number> T mul(T firstValue, T secondValue) {  
		   BigDecimal firstDecimal = new BigDecimal(firstValue.toString());  
		   BigDecimal secondDecimal = new BigDecimal(secondValue.toString());   
		   return (T)firstDecimal.multiply(secondDecimal); 
	}

	// 两数相除
	public static <T extends Number> T div(T firstValue, T secondValue) {  
		   BigDecimal firstDecimal = new BigDecimal(firstValue.toString());  
		   BigDecimal secondDecimal = new BigDecimal(secondValue.toString()); 
		   return (T)firstDecimal.divide(secondDecimal);  
	}

	// 两数相除并保留n小数
	public static <T extends Number> BigDecimal div(T firstValue, T secondValue, int scale) {
		BigDecimal firstDecimal = new BigDecimal(firstValue.toString());
		BigDecimal secondDecimal = new BigDecimal(secondValue.toString());
		return firstDecimal.divide(secondDecimal, scale, BigDecimal.ROUND_HALF_UP);
	}
	
	public static <T extends Number> boolean isZeroOrNull(T value) {
		if (value == null || value.floatValue() == 0)
			return true;
		else
			return false;
	}
	
	public static boolean isMinMaxValue(Double changeRate) {

		if (_MAX_CHANGE_RATE.compareTo(changeRate)==0 
				|| _MIN_CHANGE_RATE.compareTo(changeRate)==0)
			return true;
		else
			return false;
	}
	
	public static Integer getRoundInt(double data) {

		BigDecimal bd = new BigDecimal(data).setScale(0, BigDecimal.ROUND_HALF_UP);
		return Integer.parseInt(bd.toString());
	}
}
