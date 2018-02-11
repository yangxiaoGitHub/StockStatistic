package cn.db.bean;

import java.util.Date;

public class AllImportStock extends BaseStock {
	private static final long serialVersionUID = -6429097938382160087L;

	Double changeRate; //涨幅
	String changeRateDes;
	Double current; //现价
	Long totalHands; //总手(成交量-股)
	Double yesterdayClose; //昨收
	Double todayOpen; //开盘
	Double todayHigh; //最高
	Double todayLow; //最低
	Double quantityRatio; //量比
	String industry; //所属行业
	Double peRatio; //市盈(动)
	Double pbRatio; //市净率
	Double amplitude; //振幅
	Double turnoverRate; //换手
	String turnoverRateDes;
	Double upDown; //涨跌
	Long amount; //金额(成交量-元)
	Long outside; //外盘
	Long inside; //内盘
	Long marketCap; //总市值
	Long circulateValue; //流通值

	public final static String TABLE_NAME = "ALL_IMPORT_STOCK_";
	public final static String CHANGE_RATE ="CHANGE_RATE_";
	public final static String CHANGE_RATE_DES ="CHANGE_RATE_DES_";
	public final static String CURRENT ="CURRENT_";
	public final static String TOTAL_HANDS ="TOTAL_HANDS_";
	public final static String YESTERDAY_CLOSE ="YESTERDAY_CLOSE_";
	public final static String TODAY_OPEN ="TODAY_OPEN_";
	public final static String TODAY_HIGH ="TODAY_HIGH_";
	public final static String TODAY_LOW ="TODAY_LOW_";
	public final static String QUANTITY_RATIO ="QUANTITY_RATIO_";
	public final static String INDUSTRY ="INDUSTRY_";
	public final static String PE_RATIO ="PE_RATIO_";
	public final static String PB_RATIO ="PB_RATIO_";
	public final static String AMPLITUDE ="AMPLITUDE_";
	public final static String TURNOVER_RATE ="TURNOVER_RATE_";
	public final static String TURNOVER_RATE_DES = "TURNOVER_RATE_DES_";
	public final static String UP_DOWN ="UP_DOWN_";
	public final static String AMOUNT = "AMOUNT_";
	public final static String OUTSIDE = "OUTSIDE_";
	public final static String INSIDE = "INSIDE_";
	public final static String MARKET_CAP ="MARKET_CAP_";
	public final static String CIRCULATE_VALUE ="CIRCULATE_VALUE_";
	
	public final static String ALL_FIELDS = NUM + "," + STOCK_DATE + "," + STOCK_CODE + "," + STOCK_CODE_DES + "," + CHANGE_RATE + "," + CHANGE_RATE_DES + "," 
											+ CURRENT + "," + TOTAL_HANDS + "," + YESTERDAY_CLOSE + "," + TODAY_OPEN + "," + TODAY_HIGH + "," 
											+ TODAY_LOW + "," + QUANTITY_RATIO + "," + INDUSTRY + "," + PE_RATIO + "," + PB_RATIO + "," 
											+ AMPLITUDE + "," + TURNOVER_RATE + "," + TURNOVER_RATE_DES + "," + UP_DOWN + "," + AMOUNT + "," + OUTSIDE + "," 
											+ INSIDE + "," + MARKET_CAP + "," + CIRCULATE_VALUE + "," + INPUT_TIME;
	
	public AllImportStock() {
		
	}
	
	public AllImportStock(Date stockDate, String stockCode) {
		super.stockDate = stockDate;
		super.stockCode = stockCode;
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
		return super.stockCodeDES;
	}
	@Override
	public void setStockCodeDES(String stockCodeDES) {
		super.stockCodeDES = stockCodeDES;
	}
	public Double getChangeRate() {
		return changeRate;
	}
	public void setChangeRate(Double changeRate) {
		this.changeRate = changeRate;
	}
	public String getChangeRateDes() {
		return changeRateDes;
	}
	public void setChangeRateDes(String changeRateDes) {
		this.changeRateDes = changeRateDes;
	}
	public Double getCurrent() {
		return current;
	}
	public void setCurrent(Double current) {
		this.current = current;
	}
	public Long getTotalHands() {
		return totalHands;
	}
	public void setTotalHands(Long totalHands) {
		this.totalHands = totalHands;
	}
	public Double getYesterdayClose() {
		return yesterdayClose;
	}
	public void setYesterdayClose(Double yesterdayClose) {
		this.yesterdayClose = yesterdayClose;
	}
	public Double getTodayOpen() {
		return todayOpen;
	}
	public void setTodayOpen(Double todayOpen) {
		this.todayOpen = todayOpen;
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
	public Double getQuantityRatio() {
		return quantityRatio;
	}
	public void setQuantityRatio(Double quantityRatio) {
		this.quantityRatio = quantityRatio;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public Double getPeRatio() {
		return peRatio;
	}
	public void setPeRatio(Double peRatio) {
		this.peRatio = peRatio;
	}
	public Double getPbRatio() {
		return pbRatio;
	}
	public void setPbRatio(Double pbRatio) {
		this.pbRatio = pbRatio;
	}
	public Double getAmplitude() {
		return amplitude;
	}
	public void setAmplitude(Double amplitude) {
		this.amplitude = amplitude;
	}
	public Double getTurnoverRate() {
		return turnoverRate;
	}
	public void setTurnoverRate(Double turnoverRate) {
		this.turnoverRate = turnoverRate;
	}
	public Double getUpDown() {
		return upDown;
	}
	public void setUpDown(Double upDown) {
		this.upDown = upDown;
	}
	public Long getMarketCap() {
		return marketCap;
	}
	public void setMarketCap(Long marketCap) {
		this.marketCap = marketCap;
	}
	public Long getCirculateValue() {
		return circulateValue;
	}
	public void setCirculateValue(Long circulateValue) {
		this.circulateValue = circulateValue;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getOutside() {
		return outside;
	}

	public void setOutside(Long outside) {
		this.outside = outside;
	}

	public Long getInside() {
		return inside;
	}

	public void setInside(Long inside) {
		this.inside = inside;
	}

	public String getTurnoverRateDes() {
		return turnoverRateDes;
	}

	public void setTurnoverRateDes(String turnoverRateDes) {
		this.turnoverRateDes = turnoverRateDes;
	}
}
