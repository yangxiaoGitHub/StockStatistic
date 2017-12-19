package cn.db.bean;

import java.util.Date;
import java.util.Map;

import cn.com.JsonUtils;

public class StatisticStock extends BaseStock {
	private static final long serialVersionUID = 7751504139176766814L;

	private Date firstDate;
	private Integer upDownNumber;
	private Integer upNumber;
	private Integer downNumber;
	private String oneWeek;
	private String halfMonth;
	private String oneMonth;
	private String twoMonth;
	private String threeMonth;
	private String halfYear;
	private String oneYear;
	private String stockCodeDES;
	private String decryptStockCode;
	private String note;
	private Integer errorUpDownNumber;
	private Integer errorUpNumber;
	private Integer errorDownNumber;
	// 涨跌次数标识：1:跌  2:涨跌 3:涨
//	private Integer upDownFlg = 0;
	public final static Integer DOWN_FLG = 1; // 跌
	public final static Integer UP_DOWN_FLG = 2; // 涨跌
	public final static Integer UP_FLG = 3; // 涨
	public final static Integer ERROR_SUM_FLG = 4; //涨跌次数!=涨次数+跌次数
	// 常量
	public final static String CH_UP_DOWN = "涨跌";
	public final static String CH_UP = "涨";
	public final static String CH_DOWN = "跌";
	
	//前一周涨跌次数常量
	public final static String PRE_ONE_WEEK_UP_DOWN_NUM = "preOneWeekUpDownNum"; //前一周涨跌次数
	public final static String PRE_ONE_WEEK_UP_NUM = "preOneWeekUpNum"; //前一周涨次数
	public final static String PRE_ONE_WEEK_DOWN_NUM = "preOneWeekDownNum"; //前一周跌次数
	//前半月涨跌次数常量
	public final static String PRE_HALF_MONTH_UP_DOWN_NUM = "preHalfMonthUpDownNum"; //前半月涨跌次数
	public final static String PRE_HALF_MONTH_UP_NUM = "preHalfMonthUpNum"; //前半月涨次数
	public final static String PRE_HALF_MONTH_DOWN_NUM = "preHalfMonthDownNum"; //前半月跌次数
	//前一月涨跌次数常量
	public final static String PRE_ONE_MONTH_UP_DOWN_NUM = "preOneMonthUpDownNum"; //前一月涨跌次数
	public final static String PRE_ONE_MONTH_UP_NUM = "preOneMonthUpNum"; //前一月涨次数
	public final static String PRE_ONE_MONTH_DOWN_NUM = "preOneMonthDownNum"; //前一月跌次数
	//前二月涨跌次数常量
	public final static String PRE_TWO_MONTH_UP_DOWN_NUM = "preTwoMonthUpDownNum"; //前一月涨跌次数
	public final static String PRE_TWO_MONTH_UP_NUM = "preTwoMonthUpNum"; //前一月涨次数
	public final static String PRE_TWO_MONTH_DOWN_NUM = "preTwoMonthDownNum"; //前一月跌次数
	//前三月涨跌次数常量
	public final static String PRE_THREE_MONTH_UP_DOWN_NUM = "preThreeMonthUpDownNum"; //前一月涨跌次数
	public final static String PRE_THREE_MONTH_UP_NUM = "preThreeMonthUpNum"; //前一月涨次数
	public final static String PRE_THREE_MONTH_DOWN_NUM = "preThreeMonthDownNum"; //前一月跌次数
	//前半年涨跌次数常量
	public final static String PRE_HALF_YEAR_UP_DOWN_NUM = "preHalfYearUpDownNum"; //前半年涨跌次数
	public final static String PRE_HALF_YEAR_UP_NUM = "preHalfYearUpNum"; //前半年涨次数
	public final static String PRE_HALF_YEAR_DOWN_NUM = "preHalfYearDownNum"; //前半年跌次数
	//前一年涨跌次数常量
	public final static String PRE_ONE_YEAR_UP_DOWN_NUM = "preOneYearUpDownNum"; //前一年涨跌次数
	public final static String PRE_ONE_YEAR_UP_NUM = "preOneYearUpNum"; //前一年涨次数
	public final static String PRE_ONE_YEAR_DOWN_NUM = "preOneYearDownNum"; //前一年跌次数
	
	public final static String TABLE_NAME = "STATISTIC_STOCK_";
	public final static String FIRST_DATE = "FIRST_DATE_";
	public final static String UP_DOWN_NUMBER = "UP_DOWN_NUMBER_";
	public final static String UP_NUMBER = "UP_NUMBER_";
	public final static String DOWN_NUMBER = "DOWN_NUMBER_";
	public final static String ONE_WEEK = "ONE_WEEK_";
	public final static String HALF_MONTH = "HALF_MONTH_";
	public final static String ONE_MONTH = "ONE_MONTH_";
	public final static String TWO_MONTH = "TWO_MONTH_";
	public final static String THREE_MONTH = "THREE_MONTH_";
	public final static String HALF_YEAR = "HALF_YEAR_";
	public final static String ONE_YEAR = "ONE_YEAR_";
	public final static String STOCK_CODE_DES = "STOCK_CODE_DES_";
	public final static String NOTE = "NOTE_";
	public final static String ALL_FIELDS = NUM + "," + STOCK_CODE + "," + FIRST_DATE + ","  + UP_DOWN_NUMBER + "," + UP_NUMBER + "," + DOWN_NUMBER + "," 
							 			  + ONE_WEEK + "," + HALF_MONTH + "," + ONE_MONTH + "," + TWO_MONTH + "," + THREE_MONTH + "," + HALF_YEAR + "," 
							 			  + ONE_YEAR + "," + STOCK_CODE_DES + "," + INPUT_TIME + "," + NOTE;

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

	public String getDecryptStockCode() {
		return decryptStockCode;
	}

	public void setDecryptStockCode(String decryptStockCode) {
		this.decryptStockCode = decryptStockCode;
	}

	public Integer getUpDownNumber() {
		return upDownNumber;
	}

	public void setUpDownNumber(Integer upDownNumber) {
		this.upDownNumber = upDownNumber;
	}

	public Integer getUpNumber() {
		return upNumber;
	}

	public void setUpNumber(Integer upNumber) {
		this.upNumber = upNumber;
	}

	public Integer getDownNumber() {
		return downNumber;
	}

	public void setDownNumber(Integer downNumber) {
		this.downNumber = downNumber;
	}
	
	//获得总涨跌次数
	public String getAllUpDownNumber() {
		return CH_UP_DOWN + ":" + upDownNumber + "," + CH_UP + ":" + upNumber + "," + CH_DOWN + ":" + downNumber;
	}

	public Integer getErrorDownNumber() {
		return errorDownNumber;
	}

	public void setErrorDownNumber(Integer errorDownNumber) {
		this.errorDownNumber = errorDownNumber;
	}

	public Integer getErrorUpNumber() {
		return errorUpNumber;
	}

	public void setErrorUpNumber(Integer errorUpNumber) {
		this.errorUpNumber = errorUpNumber;
	}

	public Integer getErrorUpDownNumber() {
		return errorUpDownNumber;
	}

	public void setErrorUpDownNumber(Integer errorUpDownNumber) {
		this.errorUpDownNumber = errorUpDownNumber;
	}

	/*public Integer getUpDownFlg() {
		return upDownFlg;
	}

	public void setUpDownFlg(Integer upDownFlg) {
		this.upDownFlg = upDownFlg;
	}*/

	public String getOneWeek() {
		return oneWeek;
	}
	
	//解析前一周涨跌次数
	public String getOneWeek_NoJson() {
		Map<String, Integer> jsonMap = JsonUtils.getMapByJson(oneWeek);
		Integer upDownNum = jsonMap.get(PRE_ONE_WEEK_UP_DOWN_NUM);
		Integer upNum = jsonMap.get(PRE_ONE_WEEK_UP_NUM);
		Integer downNum = jsonMap.get(PRE_ONE_WEEK_DOWN_NUM);
		return CH_UP_DOWN + ":" + upDownNum + "," + CH_UP + ":" + upNum + "," + CH_DOWN + ":" + downNum;
	}

	public void setOneWeek(String oneWeek) {
		this.oneWeek = oneWeek;
	}

	public String getHalfMonth() {
		return halfMonth;
	}
	
	//解析前半月涨跌次数
	public String getHalfMonth_NoJson() {
		Map<String, Integer> jsonMap = JsonUtils.getMapByJson(halfMonth);
		Integer upDownNum = jsonMap.get(PRE_HALF_MONTH_UP_DOWN_NUM);
		Integer upNum = jsonMap.get(PRE_HALF_MONTH_UP_NUM);
		Integer downNum = jsonMap.get(PRE_HALF_MONTH_DOWN_NUM);
		return CH_UP_DOWN + ":" + upDownNum + "," + CH_UP + ":" + upNum + "," + CH_DOWN + ":" + downNum;
	}

	public void setHalfMonth(String halfMonth) {
		this.halfMonth = halfMonth;
	}

	public String getOneMonth() {
		return oneMonth;
	}

	//解析前一月涨跌次数
	public String getOneMonth_NoJson() {
		Map<String, Integer> jsonMap = JsonUtils.getMapByJson(oneMonth);
		Integer upDownNum = jsonMap.get(PRE_ONE_MONTH_UP_DOWN_NUM);
		Integer upNum = jsonMap.get(PRE_ONE_MONTH_UP_NUM);
		Integer downNum = jsonMap.get(PRE_ONE_MONTH_DOWN_NUM);
		return CH_UP_DOWN + ":" + upDownNum + "," + CH_UP + ":" + upNum + "," + CH_DOWN + ":" + downNum;
	}
	
	public void setOneMonth(String oneMonth) {
		this.oneMonth = oneMonth;
	}

	public String getTwoMonth() {
		return twoMonth;
	}
	
	//解析前二月涨跌次数
	public String getTwoMonth_NoJson() {
		Map<String, Integer> jsonMap = JsonUtils.getMapByJson(twoMonth);
		Integer upDownNum = jsonMap.get(PRE_TWO_MONTH_UP_DOWN_NUM);
		Integer upNum = jsonMap.get(PRE_TWO_MONTH_UP_NUM);
		Integer downNum = jsonMap.get(PRE_TWO_MONTH_DOWN_NUM);
		return CH_UP_DOWN + ":" + upDownNum + "," + CH_UP + ":" + upNum + "," + CH_DOWN + ":" + downNum;
	}

	public void setTwoMonth(String twoMonth) {
		this.twoMonth = twoMonth;
	}

	public String getThreeMonth() {
		return threeMonth;
	}
	
	//解析前三月涨跌次数
	public String getThreeMonth_NoJson() {
		Map<String, Integer> jsonMap = JsonUtils.getMapByJson(threeMonth);
		Integer upDownNum = jsonMap.get(PRE_THREE_MONTH_UP_DOWN_NUM);
		Integer upNum = jsonMap.get(PRE_THREE_MONTH_UP_NUM);
		Integer downNum = jsonMap.get(PRE_THREE_MONTH_DOWN_NUM);
		return CH_UP_DOWN + ":" + upDownNum + "," + CH_UP + ":" + upNum + "," + CH_DOWN + ":" + downNum;
	}

	public void setThreeMonth(String threeMonth) {
		this.threeMonth = threeMonth;
	}

	public String getHalfYear() {
		return halfYear;
	}
	
	//解析前半年涨跌次数
	public String getHalfYear_NoJson() {
		Map<String, Integer> jsonMap = JsonUtils.getMapByJson(halfYear);
		Integer upDownNum = jsonMap.get(PRE_HALF_YEAR_UP_DOWN_NUM);
		Integer upNum = jsonMap.get(PRE_HALF_YEAR_UP_NUM);
		Integer downNum = jsonMap.get(PRE_HALF_YEAR_DOWN_NUM);
		return CH_UP_DOWN + ":" + upDownNum + "," + CH_UP + ":" + upNum + "," + CH_DOWN + ":" + downNum;
	}


	public void setHalfYear(String halfYear) {
		this.halfYear = halfYear;
	}

	public String getOneYear() {
		return oneYear;
	}
	
	//解析前一周涨跌次数
	public String getOneYear_NoJson() {
		Map<String, Integer> jsonMap = JsonUtils.getMapByJson(oneYear);
		Integer upDownNum = jsonMap.get(PRE_ONE_YEAR_UP_DOWN_NUM);
		Integer upNum = jsonMap.get(PRE_ONE_YEAR_UP_NUM);
		Integer downNum = jsonMap.get(PRE_ONE_YEAR_DOWN_NUM);
		return CH_UP_DOWN + ":" + upDownNum + "," + CH_UP + ":" + upNum + "," + CH_DOWN + ":" + downNum;
	}

	public void setOneYear(String oneYear) {
		this.oneYear = oneYear;
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