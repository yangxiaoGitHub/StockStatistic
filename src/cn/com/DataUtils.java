package cn.com;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DataUtils {
	
	// ���ͳ���
	public final static int CONSTANT_LONG_DAY = 12; //A������ʱ���������
	public final static BigDecimal CONSTANT_ONE = new BigDecimal("1");
	public final static BigDecimal CONSTANT_TEN = new BigDecimal("10");
	public final static BigDecimal CONSTANT_HUNDRED = new BigDecimal("100");
	public final static BigDecimal CONSTANT_TEN_THOUSAND = new BigDecimal("10000"); //��
	public final static BigDecimal CONSTANT_HUNDRED_MILLION = new BigDecimal("100000000");  //��
	public final static String CONSTANT_SPACES = "   ";
	
	// С������
	public final static Double CONSTANT_ZERO_DOT_ONE = 0.1;

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
	 * ������ʽȥ�������.��0
	 *
	 */
	public static String subZeroAndDot(Double val){
		String value = val.toString();
	    if(value.indexOf(".") > 0){
	    	value = value.replaceAll("0+?$", ""); //ȥ�������0
	    	value = value.replaceAll("[.]$", ""); //�����һλ��.��ȥ��
	    }
	    return value;
	}
}
