package cn.db.bean;

import java.util.Date;

public class OriginalStock extends BaseStock{
	private static final long serialVersionUID = 689612712421043217L;

	private Integer stockNumber;
	private String stockCodes;
	private String stockCodesEncrypt;
	private String stockCodesMD5;
	private String changeRates;
	private String changeRatesEncrypt;
	private String changeRatesMD5;
	private String turnoverRates;
	private String turnoverRatesEncrypt;
	private String turnoverRatesMD5;
	
	private String decryptStockCodes;
	private String decryptChangeRates;
	
	public static final String TABLE_NAME = "ORIGINAL_STOCK_";
	public static final String STOCK_NUMBER = "STOCK_NUMBER_";
	public static final String STOCK_CODES = "STOCK_CODES_";
	public static final String STOCK_CODES_ENCRYPT = "STOCK_CODES_ENCRYPT_";
	public static final String STOCK_CODES_MD5 = "STOCK_CODES_MD5_";
	public static final String CHANGE_RATES = "CHANGE_RATES_";
	public static final String CHANGE_RATES_ENCRYPT = "CHANGE_RATES_ENCRYPT_";
	public static final String CHANGE_RATES_MD5 = "CHANGE_RATES_MD5_";
	public static final String TURNOVER_RATES = "TURNOVER_RATES_";
	public static final String TURNOVER_RATES_ENCRYPT = "TURNOVER_RATES_ENCRYPT_";
	public static final String TURNOVER_RATES_MD5 = "TURNOVER_RATES_MD5_";
	public static final String ALL_FIELDS = NUM + "," + STOCK_DATE + "," + STOCK_NUMBER + "," + STOCK_CODES + "," + STOCK_CODES_ENCRYPT + "," 
											+ STOCK_CODES_MD5 + "," + CHANGE_RATES + "," + CHANGE_RATES_ENCRYPT + "," + CHANGE_RATES_MD5 + "," 
											+ TURNOVER_RATES + "," + TURNOVER_RATES_ENCRYPT + "," + TURNOVER_RATES_MD5 + "," + INPUT_TIME;
	
	public Integer getStockNumber() {
		return stockNumber;
	}
	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}
	public String getStockCodes() {
		return stockCodes;
	}
	public void setStockCodes(String stockCodes) {
		this.stockCodes = stockCodes;
	}
	public String getStockCodesEncrypt() {
		return stockCodesEncrypt;
	}
	public void setStockCodesEncrypt(String stockCodesEncrypt) {
		this.stockCodesEncrypt = stockCodesEncrypt;
	}
	public String getChangeRates() {
		return changeRates;
	}
	public void setChangeRates(String changeRates) {
		this.changeRates = changeRates;
	}
	public String getChangeRatesEncrypt() {
		return changeRatesEncrypt;
	}
	public void setChangeRatesEncrypt(String changeRatesEncrypt) {
		this.changeRatesEncrypt = changeRatesEncrypt;
	}
	public String getDecryptStockCodes() {
		return decryptStockCodes;
	}
	public void setDecryptStockCodes(String decryptStockCodes) {
		this.decryptStockCodes = decryptStockCodes;
	}
	public String getDecryptChangeRates() {
		return decryptChangeRates;
	}
	public void setDecryptChangeRates(String decryptChangeRates) {
		this.decryptChangeRates = decryptChangeRates;
	}
	public String getStockCodesMD5() {
		return stockCodesMD5;
	}
	public void setStockCodesMD5(String stockCodesMD5) {
		this.stockCodesMD5 = stockCodesMD5;
	}
	public String getChangeRatesMD5() {
		return changeRatesMD5;
	}
	public void setChangeRatesMD5(String changeRatesMD5) {
		this.changeRatesMD5 = changeRatesMD5;
	}
	public String getTurnoverRates() {
		return turnoverRates;
	}
	public void setTurnoverRates(String turnoverRates) {
		this.turnoverRates = turnoverRates;
	}
	public String getTurnoverRatesEncrypt() {
		return turnoverRatesEncrypt;
	}
	public void setTurnoverRatesEncrypt(String turnoverRatesEncrypt) {
		this.turnoverRatesEncrypt = turnoverRatesEncrypt;
	}
	public String getTurnoverRatesMD5() {
		return turnoverRatesMD5;
	}
	public void setTurnoverRatesMD5(String turnoverRatesMD5) {
		this.turnoverRatesMD5 = turnoverRatesMD5;
	}
}
