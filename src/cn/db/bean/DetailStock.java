package cn.db.bean;

import java.util.Date;

public class DetailStock extends BaseStock {
	private static final long serialVersionUID = 928675550402133001L;
	
	private String stockName;
	private Double todayOpen;
	private Double current;
	private Double yesterdayClose;
	private Double todayHigh;
	private Double todayLow;
	private Long tradedStockNumber;
	private Float tradedAmount;
	private Double changeRate;
	private String changeRateDES;
	private Double turnoverRate;
	private String turnoverRateDES;
	private Date tradedTime;
	
	public final static String TABLE_NAME = "DETAIL_STOCK_";
	public final static String STOCK_NAME = "STOCK_NAME_";
	public final static String TODAY_OPEN = "TODAY_OPEN_";
	public final static String YESTERDAY_CLOSE = "YESTERDAY_CLOSE_";
	public final static String CURRENT = "CURRENT_";
	public final static String TODAY_HIGH = "TODAY_HIGH_";
	public final static String TODAY_LOW = "TODAY_LOW_";
	public final static String TRADED_STOCK_NUMBER = "TRADED_STOCK_NUMBER_";
	public final static String TRADED_AMOUNT = "TRADED_AMOUNT_";
	public final static String CHANGE_RATE = "CHANGE_RATE_";
	public final static String CHANGE_RATE_DES = "CHANGE_RATE_DES_";
	public final static String TURNOVER_RATE = "TURNOVER_RATE_";
	public final static String TURNOVER_RATE_DES = "TURNOVER_RATE_DES_";
	public final static String TRADED_TIME = "TRADED_TIME_";
	public final static String ALL_FIELDS = NUM + "," + STOCK_DATE + "," + STOCK_CODE + "," + STOCK_CODE_DES + "," + STOCK_NAME + "," + TODAY_OPEN + "," 
											+ YESTERDAY_CLOSE + "," + CURRENT + "," + TODAY_HIGH + "," + TODAY_LOW + "," + TRADED_STOCK_NUMBER + "," + TRADED_AMOUNT + "," 
											+ CHANGE_RATE + "," + CHANGE_RATE_DES + "," + TURNOVER_RATE + "," + TURNOVER_RATE_DES + "," + TRADED_TIME + "," + INPUT_TIME;
	@Override
	public String getStockCodeDES() {
		return stockCodeDES;
	}
	@Override
	public void setStockCodeDES(String stockCodeDES) {
		this.stockCodeDES = stockCodeDES;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public Double getTodayOpen() {
		return todayOpen;
	}
	public void setTodayOpen(Double todayOpen) {
		this.todayOpen = todayOpen;
	}
	public Double getYesterdayClose() {
		return yesterdayClose;
	}
	public void setYesterdayClose(Double yesterdayClose) {
		this.yesterdayClose = yesterdayClose;
	}
	public Double getTodayHigh() {
		return todayHigh;
	}
	public void setTodayHigh(Double todayHigh) {
		this.todayHigh = todayHigh;
	}
	public Double getTodayLow() {
		return todayLow;
	}
	public void setTodayLow(Double todayLow) {
		this.todayLow = todayLow;
	}
	public Long getTradedStockNumber() {
		return tradedStockNumber;
	}
	public void setTradedStockNumber(Long tradedStockNumber) {
		this.tradedStockNumber = tradedStockNumber;
	}
	public Float getTradedAmount() {
		return tradedAmount;
	}
	public void setTradedAmount(Float tradedAmount) {
		this.tradedAmount = tradedAmount;
	}
	public Double getChangeRate() {
		return changeRate;
	}
	public void setChangeRate(Double changeRate) {
		this.changeRate = changeRate;
	}
	public String getChangeRateDES() {
		return changeRateDES;
	}
	public void setChangeRateDES(String changeRateDES) {
		this.changeRateDES = changeRateDES;
	}
	public Double getTurnoverRate() {
		return turnoverRate;
	}
	public void setTurnoverRate(Double turnoverRate) {
		this.turnoverRate = turnoverRate;
	}
	public String getTurnoverRateDES() {
		return turnoverRateDES;
	}
	public void setTurnoverRateDES(String turnoverRateDES) {
		this.turnoverRateDES = turnoverRateDES;
	}
	public Date getTradedTime() {
		return tradedTime;
	}
	public void setTradedTime(Date tradedTime) {
		this.tradedTime = tradedTime;
	}
	public Double getCurrent() {
		return current;
	}
	public void setCurrent(Double current) {
		this.current = current;
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
}