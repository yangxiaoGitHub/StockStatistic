package cn.db.bean;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DailyStock  extends BaseStock {
	private static final long serialVersionUID = -461181756005215413L;
	
	private Double changeRate;
	private String encryptChangeRate;
	private String turnoverRateEncrypt;
	private String changeFlg;
	private String note;

	private String decryptChangeRate;
	private int count; //统计的次数
	private String stockDates; //该股票出现的日期
	private Date recentStockDate; //该股票出现的最近日期
	
	private int dailyCount; //每日股票数据统计的次数
	private int originalCount; //原始股票数据统计的次数
	
	public static final String CHANGE_FLG_ZERO = "0"; //跌
	public static final String CHANGE_FLG_ONE = "1"; //涨

	public static final String TABLE_NAME = "DAILY_STOCK_";
	public static final String STOCK_CODE_DES = "STOCK_CODE_DES_";
	public static final String CHANGE_RATE = "CHANGE_RATE_";
	public static final String CHANGE_RATE_ENCRYPT = "CHANGE_RATE_ENCRYPT_";
	public static final String TURNOVER_RATE = "TURNOVER_RATE_";
	public static final String TURNOVER_RATE_ENCRYPT = "TURNOVER_RATE_ENCRYPT_";
	public static final String CHANGE_FLG = "CHANGE_FLG_";
	public static final String NOTE = "NOTE_";
	public static final String ALL_FIELDS = NUM + "," + STOCK_DATE + "," + STOCK_CODE + "," + STOCK_CODE_DES + "," + CHANGE_RATE + "," + CHANGE_RATE_ENCRYPT + "," 
											+ TURNOVER_RATE + "," + TURNOVER_RATE_ENCRYPT + "," + CHANGE_FLG + "," + NOTE + "," + INPUT_TIME;

	public DailyStock() {

	}
	
	public DailyStock(String stockCode, Date stockDate) {
		this.stockCode = stockCode;
		this.stockDate = stockDate;
	}
	
	public Double getChangeRate() {
		return changeRate;
	}
	public void setChangeRate(Double changeRate) {
		this.changeRate = changeRate;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getChangeFlg() {
		return changeFlg;
	}
	public void setChangeFlg(String changeFlg) {
		this.changeFlg = changeFlg;
	}
	public String getEncryptChangeRate() {
		return encryptChangeRate;
	}
	public void setEncryptChangeRate(String encryptChangeRate) {
		this.encryptChangeRate = encryptChangeRate;
	}
	public String getDecryptChangeRate() {
		return decryptChangeRate;
	}
	public void setDecryptChangeRate(String decryptChangeRate) {
		this.decryptChangeRate = decryptChangeRate;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void setDates(List<Date> dateList) {
		String strDate = StringUtils.join(dateList.toArray(), ",");
		stockDates = strDate;
	}
	public String getStockDates() {
		return stockDates;
	}
	public String getTurnoverRateEncrypt() {
		return turnoverRateEncrypt;
	}
	public Date getRecentStockDate() {
		return recentStockDate;
	}
	public void setRecentStockDate(Date recentStockDate) {
		this.recentStockDate = recentStockDate;
	}
	public void setTurnoverRateEncrypt(String turnoverRateEncrypt) {
		this.turnoverRateEncrypt = turnoverRateEncrypt;
	}
	public int getDailyCount() {
		return dailyCount;
	}
	public void setDailyCount(int dailyCount) {
		this.dailyCount = dailyCount;
	}
	public int getOriginalCount() {
		return originalCount;
	}
	public void setOriginalCount(int originalCount) {
		this.originalCount = originalCount;
	}
	@Override
	public String getStockCode() {
		return super.stockCode;
	}
	@Override
	public void setStockCode(String stockCode) {
		super.stockCode = stockCode;
	}
	@Override
	public Date getStockDate() {
		return super.stockDate;
	}
	@Override
	public void setStockDate(Date stockDate) {
		super.stockDate = stockDate;
	}
	@Override
	public String getStockCodeDES() {
		return stockCodeDES;
	}
	@Override
	public void setStockCodeDES(String stockCodeDES) {
		this.stockCodeDES = stockCodeDES;
	}
	@Override
	public Double getTurnoverRate() {
		return super.turnoverRate;
	}
	@Override
	public void setTurnoverRate(Double turnoverRate) {
		super.turnoverRate = turnoverRate;
	}
}