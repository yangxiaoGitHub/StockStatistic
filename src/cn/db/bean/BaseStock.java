package cn.db.bean;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseStock implements Serializable {
	private static final long serialVersionUID = -8509065754826682199L;

	private Long num;
	protected String stockCode;
	protected String stockCodeDES;
	protected Double turnoverRate;
	protected Date stockDate;
	protected Date inputTime;

	public final static String NUM = "NUM_";
	public final static String STOCK_CODE = "STOCK_CODE_";
	public final static String STOCK_CODE_DES = "STOCK_CODE_DES_";
	public final static String TURNOVER_RATE = "TURNOVER_RATE_";
	public final static String STOCK_DATE = "STOCK_DATE_";
	public final static String INPUT_TIME = "INPUT_TIME_";
	
	public Long getNum() {
		return num;
	}
	public void setNum(Long lineNum) {
		this.num = lineNum;
	}

	public abstract String getStockCode();
	public abstract void setStockCode(String stockCode);
	public abstract Date getStockDate();
	public abstract void setStockDate(Date stockDate);
	public abstract String getStockCodeDES();
	public abstract void setStockCodeDES(String stockCodeDES);
	public abstract Double getTurnoverRate();
	public abstract void setTurnoverRate(Double turnoverRate);

	public Date getInputTime() {
		return inputTime;
	}
	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}
}
