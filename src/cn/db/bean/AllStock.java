package cn.db.bean;

import java.util.Date;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.JsonUtils;

public class AllStock extends BaseStock {
	private static final long serialVersionUID = -8320836713125893535L;
	
	private String stockName;
	private Long circulationValue;
	private Long circulationStockComplex;
	private String circulationStockSimple;
	private String industry;
	//Json数据
	public final static String JSON_VALUE = "value";
	public final static String JSON_UNIT = "unit";
	public final static String UNIT_HUNDRED_MILLION = "亿";
	public final static String UNIT_TEN_THOUSAND = "万";

	public final static String TABLE_NAME = "ALL_STOCK_";
	public final static String STOCK_NAME = "STOCK_NAME_";
	public final static String CIRCULATION_VALUE = "CIRCULATION_VALUE_";
	public final static String CIRCULATION_STOCK_COMPLEX = "CIRCULATION_STOCK_COMPLEX_";
	public final static String CIRCULATION_STOCK_SIMPLE = "CIRCULATION_STOCK_SIMPLE_";
	public final static String INDUSTRY = "INDUSTRY_";
	public final static String ALL_FIELDS = NUM + "," + STOCK_CODE + "," + STOCK_NAME + "," + CIRCULATION_VALUE + "," + CIRCULATION_STOCK_COMPLEX + "," 
											+ CIRCULATION_STOCK_SIMPLE + "," + INDUSTRY + "," + INPUT_TIME;
	
	public AllStock() {
		
	}
	
	public AllStock(String stockCode, String stockName) {
		this.setStockCode(stockCode);
		this.stockName = stockName;
	}

	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public Long getCirculationValue() {
		return circulationValue;
	}
	public void setCirculationValue(Long circulationValue) {
		this.circulationValue = circulationValue;
	}
	public Long getCirculationStockComplex() {
		return circulationStockComplex;
	}
	public void setCirculationStockComplex(Long circulationStockComplex) {
		this.circulationStockComplex = circulationStockComplex;
	}

	public String getCirculationStockSimple() {
		return circulationStockSimple;
	}
	
	public String getCirculationStockSimple_NoJson() {
		
		if (CommonUtils.isBlank(circulationStockSimple)) return DataUtils._BLANK; 
		System.out.println("json value: " + circulationStockSimple);
		Map<String, String> map = JsonUtils.getMapByJson(circulationStockSimple);
		String value = map.get(AllStock.JSON_VALUE);
		String unit = map.get(AllStock.JSON_UNIT);
		return value + unit;
	}

	public void setCirculationStockSimple(String circulationStockSimple) {
		this.circulationStockSimple = circulationStockSimple;
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