package cn.db.bean;

import java.util.Date;

public class StatisticStock extends BaseStock {
	private static final long serialVersionUID = -2355807492821409015L;

	private Date firstDate;
	private String note;
	
	public final static String TABLE_NAME = "STATISTIC_STOCK_";
	public final static String STOCK_CODE_DES = "STOCK_CODE_DES_";
	public final static String FIRST_DATE = "FIRST_DATE_";
	public final static String NOTE = "NOTE_";
	public final static String ALL_FIELDS = NUM + "," + STOCK_CODE + "," + STOCK_CODE_DES + "," + FIRST_DATE + "," + INPUT_TIME + "," + NOTE;
	
	public StatisticStock() {
	}
	
	public StatisticStock(String stockCode) {
		this.setStockCode(stockCode);
	}
	
	public StatisticStock(String stockCode, Date firstDate, String stockCodeDES) {
		this.setStockCode(stockCode);
		this.firstDate = firstDate;
		this.stockCodeDES = stockCodeDES;
	}

	public Date getFirstDate() {
		return firstDate;
	}
	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
	}
	public String getStockCodeDES() {
		return stockCodeDES;
	}
	public void setStockCodeDES(String stockCodeDES) {
		this.stockCodeDES = stockCodeDES;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
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
		return null;
	}
	@Override
	public void setStockDate(Date stockDate) {
		
	}
}