package cn.db.bean;

import java.util.Date;

public class HistoryStock extends BaseStock {
	private static final long serialVersionUID = -3039150481931384357L;

	private Double openPrice;
	private Double highPrice;
	private Double lowPrice;
	private Double closePrice;
	private Long tradedStockNumber;
	private Float tradedAmount;
	
	public static final String TABLE_NAME = "HISTORY_STOCK_";
	public static final String OPEN_PRICE = "OPEN_PRICE_";
	public static final String HIGH_PRICE = "HIGH_PRICE_";
	public static final String LOW_PRICE = "LOW_PRICE_";
	public static final String CLOSE_PRICE = "CLOSE_PRICE_";
	public static final String TRADED_STOCK_NUMBER = "TRADED_STOCK_NUMBER_";
	public static final String TRADED_AMOUNT = "TRADED_AMOUNT_";
	public final static String ALL_FIELDS = NUM + "," + STOCK_DATE + "," + STOCK_CODE + "," + OPEN_PRICE + "," + HIGH_PRICE + "," + LOW_PRICE + "," + CLOSE_PRICE + "," + TRADED_STOCK_NUMBER + "," + TRADED_AMOUNT + "," +  INPUT_TIME;

	public HistoryStock() {
		
	}
	public HistoryStock(String stockCode, Date stockDate) {
		this.setStockCode(stockCode);
		this.setStockDate(stockDate);
	}
	public Double getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}
	public Double getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}
	public Double getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}
	public Double getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
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
		return null;
	}
	@Override
	public void setStockCodeDES(String stockCodeDES) {
		
	}
	@Override
	public Double getTurnoverRate() {
		return null;
	}
	@Override
	public void setTurnoverRate(Double turnoverRate) {
		
	}
}
