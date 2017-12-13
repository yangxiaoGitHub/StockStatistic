package cn.db.bean;

import java.io.Serializable;
import java.util.Date;

public class BaseStock implements Serializable {
	private static final long serialVersionUID = -8509065754826682199L;

	protected Long num;
	protected String stockCode;
	protected Date stockDate;
	protected Date inputTime;
	
	public final static String NUM = "NUM_";
	public final static String STOCK_CODE = "STOCK_CODE_";
	public final static String STOCK_DATE = "STOCK_DATE_";
	public final static String INPUT_TIME = "INPUT_TIME_";
	
	public Long getNum() {
		return num;
	}
	public void setNum(Long lineNum) {
		this.num = lineNum;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public Date getStockDate() {
		return stockDate;
	}
	public void setStockDate(Date stockDate) {
		this.stockDate = stockDate;
	}
	public Date getInputTime() {
		return inputTime;
	}
	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}
	
}
