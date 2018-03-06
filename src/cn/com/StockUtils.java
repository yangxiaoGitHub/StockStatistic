package cn.com;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.db.OriginalStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllStock;
import cn.db.bean.DailyStock;
import cn.db.bean.DetailStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticDetailStock;
import cn.db.bean.StatisticStock;

public class StockUtils {

	/**
	 * 计算股票的涨跌幅
	 *
	 */
	public static double getChangeRate(double yesterdayClose, double current) {
		
		if (DataUtils.isZeroOrNull(current) || DataUtils.isZeroOrNull(yesterdayClose)) 
			return DataUtils._DOUBLE_ZERO;
		double rate = ((current - yesterdayClose) * 100) / yesterdayClose;
		BigDecimal bigValue = new BigDecimal(rate);
		double changeRate = bigValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return changeRate;
	}

	/**
	 * 计算股票的流通股
	 * 
	 */
	public static void setCirculationInAllStock(AllStock allStock) {

		String aliasCode = StockUtils.getAliasCodeByStockCode(allStock.getStockCode());
		String stockCirculation = PropertiesUtils.getProperty(aliasCode);
		String[] circulationArray = stockCirculation.split(",");
		if (!circulationArray[0].contains(DataUtils._DASH) && !circulationArray[1].contains(DataUtils._DASH)) {
			Double nowPrice = Double.valueOf(circulationArray[0]);
			Long circulationValue = Long.valueOf(circulationArray[1]);
			BigDecimal complexCirculationStock = new BigDecimal(circulationValue.doubleValue() / nowPrice.doubleValue()).setScale(0,
					BigDecimal.ROUND_HALF_UP);
			allStock.setCirculationValue(circulationValue);
			allStock.setCirculationStockComplex(complexCirculationStock.longValue());
			allStock.setCirculationStockSimple(getCirculationStockSimple(complexCirculationStock));
		} else {
			allStock.setCirculationValue(0L);
			allStock.setCirculationStockComplex(0L);
			allStock.setCirculationStockSimple(DataUtils._BLANK);
		}
	}
	
	/**
	 * 把流通股转换成Json数据(包括流通股和单位) 
	 * 0.3321 亿 <1 (取4位整数) -->3321万 
	 * 1.3653 亿 >=1 <10 (取小数点2位)-->1.37亿 
	 * 23.265 亿 >=10 <100 (取小数点1位)-->23.3亿 
	 * 362.02 亿 >=100 <1000(取整) -->362亿 
	 * 2235.1 亿 >1000 -->2235亿
	 */
	private static String getCirculationStockSimple(BigDecimal complexCirculationStock) {

		String unit = AllStock.UNIT_HUNDRED_MILLION;
		Double reValue = 0.0;
		BigDecimal complexCirculationValue = complexCirculationStock.divide(DataUtils._HUNDRED_MILLION);
		if (complexCirculationValue.compareTo(DataUtils._ONE) < 0) {
			unit = AllStock.UNIT_TEN_THOUSAND;
			BigDecimal tempValue = complexCirculationStock.divide(DataUtils._TEN_THOUSAND);
			reValue = getPreFourNumber(tempValue);
		} else if (complexCirculationValue.compareTo(DataUtils._ONE) >= 0
				&& complexCirculationValue.compareTo(DataUtils._TEN) < 0) {
			reValue = complexCirculationValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); // 取2位小数
		} else if (complexCirculationValue.compareTo(DataUtils._TEN) >= 0
				&& complexCirculationValue.compareTo(DataUtils._HUNDRED) < 0) {
			reValue = complexCirculationValue.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue(); // 取1位小数
		} else if (complexCirculationValue.compareTo(DataUtils._HUNDRED) >= 0) {
			reValue = complexCirculationValue.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue(); // 取整
		}
		// {"value":2.05,"unit":"亿"}
		Map<String, String> circulationSimpleMap = new HashMap<String, String>();
		circulationSimpleMap.put(AllStock.JSON_VALUE, DataUtils.subZeroAndDot(reValue));
		circulationSimpleMap.put(AllStock.JSON_UNIT, unit);
		String curculationJson = JsonUtils.getJsonByMap(circulationSimpleMap);
		return curculationJson;
	}
	
	/**
	 * 3365.96 <10000 (取整) -->3366 
	 * 55628.65 >=10000 (取前4位整数) -->5563
	 */
	private static Double getPreFourNumber(BigDecimal value) {

		Double reValue = 0.0;
		if (value.compareTo(DataUtils._TEN_THOUSAND) < 0) {
			reValue = value.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			Double doubleFive = Double.valueOf(value.toString().substring(0, 5));
			reValue = new BigDecimal(doubleFive / 10).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return reValue;
	}
	
	/**
	 * 根据流通股和成交量计算股票的换手率
	 *
	 */
	public static <T extends DetailStock> void calculateTurnoverRate(T stock, AllStock allStock) throws SQLException {

//		String stockCode = stock.getStockCode();
//		AllStock allStock = allStockDao.getAllStockByStockCode(stockCode);
		Long circulationStock = null;
		if (allStock != null)
			circulationStock = allStock.getCirculationStockComplex();
		if (circulationStock != null && circulationStock != 0) {
			Long tradedStockNum = stock.getTradedStockNumber();
			Double turnoverRate = (tradedStockNum.doubleValue() / circulationStock.doubleValue()) * 100;
			turnoverRate = new BigDecimal(turnoverRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			String turnoverRateDES = DESUtils.encryptToHex(turnoverRate.toString());
			stock.setTurnoverRate(turnoverRate);
			stock.setTurnoverRateDES(turnoverRateDES);
		} else {
			stock.setTurnoverRate(DataUtils._DOUBLE_ZERO);
			stock.setTurnoverRateDES(DataUtils._BLANK);
		}
	}
	
	/**
	 * 计算股票的涨跌幅
	 * 
	 */
	public static <T extends DetailStock> void calculateChangeRate(T stock) {

		Double yesterdayClose = stock.getYesterdayClose();
		if (!DataUtils.isZeroOrNull(yesterdayClose)) {
			double current = stock.getCurrent().doubleValue();
			double changeRate = getChangeRate(yesterdayClose, current);
//			double changeRate = ((current - yesterdayClose) * 100) / yesterdayClose;
//			BigDecimal bigValue = new BigDecimal(changeRate);
//			double dValue = bigValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			stock.setChangeRate(changeRate);
			String changeRateDES = DESUtils.encryptToHex(String.valueOf(changeRate));
			stock.setChangeRateDES(changeRateDES);
		} else {
			stock.setChangeRate(DataUtils._DOUBLE_ZERO);
			stock.setChangeRateDES(DataUtils._BLANK);
		}
	}
	
	/**
	 * 根据股票信息串获得股票详细信息对象
	 *
	 */
	public static AllDetailStock getDetailStockFromArray(String[] stockInfoArray) {

		AllDetailStock detailStock = new AllDetailStock();
		detailStock.setStockName(stockInfoArray[0]);
		detailStock.setTodayOpen(Double.valueOf(stockInfoArray[1]));
		detailStock.setYesterdayClose(Double.valueOf(stockInfoArray[2]));
		detailStock.setCurrent(Double.valueOf(stockInfoArray[3]));
		detailStock.setTodayHigh(Double.valueOf(stockInfoArray[4]));
		detailStock.setTodayLow(Double.valueOf(stockInfoArray[5]));
		detailStock.setTradedStockNumber(Long.valueOf(stockInfoArray[8]));
		detailStock.setTradedAmount(Float.valueOf(stockInfoArray[9]));
		detailStock.setStockDate(DateUtils.stringToDate(stockInfoArray[30]));
		detailStock.setTradedTime(DateUtils.stringToDateTime(stockInfoArray[30] + " " + stockInfoArray[31]));
		detailStock.setStockCode(stockInfoArray[33]);
		String stockCodeDES = DESUtils.encryptToHex(stockInfoArray[33]);
		detailStock.setStockCodeDES(stockCodeDES);
		// 计算涨跌幅
		calculateChangeRate(detailStock);
		return detailStock;
	}
	
	/**
	 * 统计OriginalStock列表中股票的stockCode和firstDate
	 *
	 */
	public static Map<String, DailyStock> statisticStockCodeAndFirstDate(List<OriginalStock> originalStockList) {
		
		Map<String, DailyStock> dailyStockMap = new HashMap<String, DailyStock>();
		for (OriginalStock originalStock : originalStockList) {
			Date stockDate = originalStock.getStockDate();
			String stockCodes = originalStock.getStockCodes();
			String changeRates = originalStock.getChangeRates();
			String turnoverRates = originalStock.getTurnoverRates();
			String[] codeArray = stockCodes.split(",");
			String[] changeRateArray = changeRates.split(",");
			String[] turnoverRateArray = turnoverRates.split(",");
			for (int index = 0; index < codeArray.length; index++) {
				// 对数据进行转换
				DailyStock dailyStock = CommonUtils.getDailyStockFromArray(index, codeArray, changeRateArray, turnoverRateArray, DateUtils.dateToString(stockDate));
				String stockCode = dailyStock.getStockCode();
				boolean existFlg = dailyStockMap.containsKey(stockCode);
				if (!existFlg) {
					dailyStockMap.put(stockCode, dailyStock);
				}
			}
		}
		return dailyStockMap;
	}
	
	/**
	 * 根据statisticDetailStockList，统计OriginalStock列表中股票的涨跌次数
	 * periodFlg: 0-全部涨跌次数，1-前一周的涨跌次数，2-前半月的涨跌次数，3-前一月的涨跌次数，4-前二月的涨跌次数，5-前三月的涨跌次数，6-前半年的涨跌次数，7-前一年的涨跌次数
	 */
	public static Map<String, StatisticDetailStock> statisticUpAndDownNumber(List<StatisticDetailStock> statisticDetailStockList, 
																			 List<OriginalStock> originalStockList,
																			 int periodFlg) {
		
		Map<String, StatisticDetailStock> statisticUpAndDownMap = new HashMap<String, StatisticDetailStock>();
		for (StatisticDetailStock statisticDetailStock : statisticDetailStockList) {
			String detailStockCode = statisticDetailStock.getStockCode();
			Date detailStockDate = statisticDetailStock.getStockDate();
			Date[] startEndDate = getStartEndDate(periodFlg, detailStockDate);
			String mapKey = detailStockCode + DateUtils.dateToString(detailStockDate);
			for (OriginalStock originalStock : originalStockList) {
				Date stockDate = originalStock.getStockDate();
				if (stockDate.compareTo(startEndDate[0])<0 || stockDate.compareTo(startEndDate[1])>0) continue;
				String stockCodes = originalStock.getStockCodes();
				String changeRates = originalStock.getChangeRates();
				String turnoverRates = originalStock.getTurnoverRates();
				String[] codeArray = stockCodes.split(",");
				String[] changeRateArray = changeRates.split(",");
				String[] turnoverRateArray = turnoverRates.split(",");
				for (int index = 0; index < codeArray.length; index++) {
					// 对数据进行转换
					DailyStock dailyStock = CommonUtils.getDailyStockFromArray(index, codeArray, changeRateArray, turnoverRateArray, DateUtils.dateToString(stockDate));
					String stockCode = dailyStock.getStockCode();
					if (!stockCode.equals(detailStockCode)) continue;
					String changeFlg = dailyStock.getChangeFlg();
					boolean existFlg = statisticUpAndDownMap.containsKey(mapKey);
					if (existFlg) {
						StatisticDetailStock staDetailStock = statisticUpAndDownMap.get(mapKey);
						Integer upDownNumber = staDetailStock.getUpDownNumber();
						++upDownNumber;
						staDetailStock.setUpDownNumber(upDownNumber);
						if (changeFlg.equals(DailyStock.CHANGE_FLG_ONE)) {
							Integer upNumber = staDetailStock.getUpNumber();
							++upNumber;
							staDetailStock.setUpNumber(upNumber);
						} else {
							Integer downNumber = staDetailStock.getDownNumber();
							++downNumber;
							staDetailStock.setDownNumber(downNumber);
						}
						statisticUpAndDownMap.put(mapKey, staDetailStock);
					} else {
						StatisticDetailStock staDetailStock = new StatisticDetailStock(stockCode, stockDate);
						staDetailStock.setUpDownNumber(DataUtils._INT_ONE);
						if (changeFlg.equals(DailyStock.CHANGE_FLG_ONE)) {
							staDetailStock.setUpNumber(DataUtils._INT_ONE);
							staDetailStock.setDownNumber(DataUtils._INT_ZERO);
						} else {
							staDetailStock.setUpNumber(DataUtils._INT_ZERO);
							staDetailStock.setDownNumber(DataUtils._INT_ONE);
						}
						statisticUpAndDownMap.put(mapKey, staDetailStock);
					}
				}
			}
		}
		return statisticUpAndDownMap;
	}

	/**
	 * 0-全部涨跌次数，1-前一周的涨跌次数，2-前半月的涨跌次数，3-前一月的涨跌次数，4-前二月的涨跌次数，5-前三月的涨跌次数，6-前半年的涨跌次数，7-前一年的涨跌次数
	 *
	 */
	private static Date[] getStartEndDate(int periodFlg, Date stockDate) {

		Date[] startEndDate = new Date[2];
		switch(periodFlg) {
		case 0:
			startEndDate[0] = DateUtils.stringToDate(DateUtils.MIN_DATE_ORIGINAL_STOCK);
			startEndDate[1] = stockDate;
			break;
		case 1:
			startEndDate = DateUtils.getPreOneWeek(stockDate);
			break;
		case 2:
			startEndDate = DateUtils.getPreHalfMonth(stockDate);
			break;
		case 3:
			startEndDate = DateUtils.getPreOneMonth(stockDate);
			break;
		case 4:
			startEndDate = DateUtils.getPreTwoMonth(stockDate);
			break;
		case 5:
			startEndDate = DateUtils.getPreThreeMonth(stockDate);
			break;
		case 6:
			startEndDate = DateUtils.getPreHalfYear(stockDate);
			break;
		case 7:
			startEndDate = DateUtils.getPreOneYear(stockDate);
			break;
		default:
			startEndDate = DateUtils.getPreOneYear(stockDate);
			break;
		}
		return startEndDate;
	}

	/**
	 * 统计OriginalStock列表中股票的涨跌次数
	 *
	 */
	public static Map<String, StatisticDetailStock> statisticUpAndDownNumber(List<OriginalStock> originalStockList) {
		
		Map<String, StatisticDetailStock> statisticUpAndDownMap = new HashMap<String, StatisticDetailStock>();
		for (OriginalStock originalStock : originalStockList) {
			Date stockDate = originalStock.getStockDate();
			String stockCodes = originalStock.getStockCodes();
			String changeRates = originalStock.getChangeRates();
			String turnoverRates = originalStock.getTurnoverRates();
			String[] codeArray = stockCodes.split(",");
			String[] changeRateArray = changeRates.split(",");
			String[] turnoverRateArray = turnoverRates.split(",");
			for (int index = 0; index < codeArray.length; index++) {
				// 对数据进行转换
				DailyStock dailyStock = CommonUtils.getDailyStockFromArray(index, codeArray, changeRateArray, turnoverRateArray, DateUtils.dateToString(stockDate));
				String stockCode = dailyStock.getStockCode();
				String changeFlg = dailyStock.getChangeFlg();
				boolean existFlg = statisticUpAndDownMap.containsKey(stockCode);
				if (existFlg) {
					StatisticDetailStock statisticDetailStock = statisticUpAndDownMap.get(stockCode);
					Integer upDownNumber = statisticDetailStock.getUpDownNumber();
					++upDownNumber;
					statisticDetailStock.setUpDownNumber(upDownNumber);
					if (changeFlg.equals(DailyStock.CHANGE_FLG_ONE)) {
						Integer upNumber = statisticDetailStock.getUpNumber();
						++upNumber;
						statisticDetailStock.setUpNumber(upNumber);
					} else {
						Integer downNumber = statisticDetailStock.getDownNumber();
						++downNumber;
						statisticDetailStock.setDownNumber(downNumber);
					}
					statisticUpAndDownMap.put(stockCode, statisticDetailStock);
				} else {
					StatisticDetailStock statisticDetailStock = new StatisticDetailStock(stockCode, stockDate);
					statisticDetailStock.setUpDownNumber(DataUtils._INT_ONE);
					if (changeFlg.equals(DailyStock.CHANGE_FLG_ONE)) {
						statisticDetailStock.setUpNumber(DataUtils._INT_ONE);
						statisticDetailStock.setDownNumber(DataUtils._INT_ZERO);
					} else {
						statisticDetailStock.setUpNumber(DataUtils._INT_ZERO);
						statisticDetailStock.setDownNumber(DataUtils._INT_ONE);
					}
					statisticUpAndDownMap.put(stockCode, statisticDetailStock);
				}
			}
		}
		return statisticUpAndDownMap;
	}
	
	/**
	 * 初始化StatisticStock的涨跌次数(包括Json字段)
	 *
	 */
	public static void initializeStatisticDetailStock(StatisticDetailStock statisticDetailStock) {
		//初始化总涨跌次数
		statisticDetailStock.setUpDownNumber(DataUtils._INT_ZERO);
		statisticDetailStock.setUpNumber(DataUtils._INT_ZERO);
		statisticDetailStock.setDownNumber(DataUtils._INT_ZERO);
		//初始化一周涨跌次数
		Map<String, Integer> jsonMap = new HashMap<String, Integer>();
		jsonMap.put(StatisticDetailStock.PRE_ONE_WEEK_UP_DOWN_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_ONE_WEEK_UP_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_ONE_WEEK_DOWN_NUM, DataUtils._INT_ZERO);
		statisticDetailStock.setOneWeek(JsonUtils.getJsonByMap(jsonMap));
		//初始化半月涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticDetailStock.PRE_HALF_MONTH_UP_DOWN_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_HALF_MONTH_UP_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_HALF_MONTH_DOWN_NUM, DataUtils._INT_ZERO);
		statisticDetailStock.setHalfMonth(JsonUtils.getJsonByMap(jsonMap));
		//初始化一月涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticDetailStock.PRE_ONE_MONTH_UP_DOWN_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_ONE_MONTH_UP_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_ONE_MONTH_DOWN_NUM, DataUtils._INT_ZERO);
		statisticDetailStock.setOneMonth(JsonUtils.getJsonByMap(jsonMap));
		//初始化二月涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticDetailStock.PRE_TWO_MONTH_UP_DOWN_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_TWO_MONTH_UP_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_TWO_MONTH_DOWN_NUM, DataUtils._INT_ZERO);
		statisticDetailStock.setTwoMonth(JsonUtils.getJsonByMap(jsonMap));
		//初始化三月涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticDetailStock.PRE_THREE_MONTH_UP_DOWN_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_THREE_MONTH_UP_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_THREE_MONTH_DOWN_NUM, DataUtils._INT_ZERO);
		statisticDetailStock.setThreeMonth(JsonUtils.getJsonByMap(jsonMap));
		//初始化半年涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticDetailStock.PRE_HALF_YEAR_UP_DOWN_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_HALF_YEAR_UP_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_HALF_YEAR_DOWN_NUM, DataUtils._INT_ZERO);
		statisticDetailStock.setHalfYear(JsonUtils.getJsonByMap(jsonMap));
		//初始化一年涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticDetailStock.PRE_ONE_YEAR_UP_DOWN_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_ONE_YEAR_UP_NUM, DataUtils._INT_ZERO);
		jsonMap.put(StatisticDetailStock.PRE_ONE_YEAR_DOWN_NUM, DataUtils._INT_ZERO);
		statisticDetailStock.setOneYear(JsonUtils.getJsonByMap(jsonMap));
	}
	
	/**
	 * 合并前一周、前二周、前三周、前半年、前一年的Json涨跌次数和总涨跌次数
	 *
	 */
	public static Map<String, StatisticDetailStock> combineUpAndDownNumberJsonMap(Map<String, StatisticDetailStock> upAndDownNumberMap,
			Map<String, StatisticDetailStock> preOneYearJsonMap, Map<String, StatisticDetailStock> preHalfYearJsonMap,
			Map<String, StatisticDetailStock> preThreeMonthJsonMap, Map<String, StatisticDetailStock> preTwoMonthJsonMap,
			Map<String, StatisticDetailStock> preOneMonthJsonMap, Map<String, StatisticDetailStock> preHalfMonthJsonMap,
			Map<String, StatisticDetailStock> preOneWeekJsonMap) {

		Map<String, StatisticDetailStock> upAndDownJsonMap = new HashMap<String, StatisticDetailStock>();
		//for (StatisticDetailStock upAndDownStatisticStock : upAndDownNumberMap.values()) {
		for (String mapKey : upAndDownNumberMap.keySet()) {
			StatisticDetailStock upAndDownStatisticStock = upAndDownNumberMap.get(mapKey);
			String stockCode = upAndDownStatisticStock.getStockCode();
			Date stockDate = upAndDownStatisticStock.getStockDate();
			StatisticDetailStock statisticDetailStock = new StatisticDetailStock(stockCode, stockDate);
			initializeStatisticDetailStock(statisticDetailStock);
			//设置总涨跌次数
			statisticDetailStock.setUpDownNumber(upAndDownStatisticStock.getUpDownNumber());
			statisticDetailStock.setUpNumber(upAndDownStatisticStock.getUpNumber());
			statisticDetailStock.setDownNumber(upAndDownStatisticStock.getDownNumber());
			//设置一周涨跌次数Json
			StatisticDetailStock preOneWeekStatisticDetailStock = preOneWeekJsonMap.get(mapKey);
			if (preOneWeekStatisticDetailStock != null)
				statisticDetailStock.setOneWeek(preOneWeekStatisticDetailStock.getOneWeek());
			//设置半月涨跌次数Json
			StatisticDetailStock preHalfMonthStatisticStock = preHalfMonthJsonMap.get(mapKey);
			if (preHalfMonthStatisticStock != null)
				statisticDetailStock.setHalfMonth(preHalfMonthStatisticStock.getHalfMonth());
			//设置一月涨跌次数Json
			StatisticDetailStock preOneMonthStatisticStock = preOneMonthJsonMap.get(mapKey);
			if (preOneMonthStatisticStock != null)
				statisticDetailStock.setOneMonth(preOneMonthStatisticStock.getOneMonth());
			//设置二月涨跌次数Json
			StatisticDetailStock preTwoMonthStatisticStock = preTwoMonthJsonMap.get(mapKey);
			if (preTwoMonthStatisticStock != null)
				statisticDetailStock.setTwoMonth(preTwoMonthStatisticStock.getTwoMonth());
			//设置三月涨跌次数Json
			StatisticDetailStock preThreeMonthStatisticStock = preThreeMonthJsonMap.get(mapKey);
			if (preThreeMonthStatisticStock != null)
				statisticDetailStock.setThreeMonth(preThreeMonthStatisticStock.getThreeMonth());
			//设置半年涨跌次数Json
			StatisticDetailStock preHalfYearStatisticStock = preHalfYearJsonMap.get(mapKey);
			if (preHalfYearStatisticStock != null)
				statisticDetailStock.setHalfYear(preHalfYearStatisticStock.getHalfYear());
			//设置一年涨跌次数Json
			StatisticDetailStock preOneYearStatisticStock = preOneYearJsonMap.get(mapKey);
			if (preOneYearStatisticStock != null)
				statisticDetailStock.setOneYear(preOneYearStatisticStock.getOneYear());
			//增加到Map中
			upAndDownJsonMap.put(mapKey, statisticDetailStock);
		}
		return upAndDownJsonMap;
	}
	
	/**
	 * 把涨跌次数转换成Json涨跌次数
	 *
	 */
	public static void setUpAndDownNumberJson(Map<String, StatisticDetailStock> upAndDownNumberMap, String periodFlg) throws IOException {
		
		String[] upAndDownNumberKeys = getUpAndDownNumberKeysByFlg(periodFlg);
		String upDownNumberKey = upAndDownNumberKeys[0];
		String upNumberKey = upAndDownNumberKeys[1];
		String downNumberKey = upAndDownNumberKeys[2];
		for (StatisticDetailStock statisticDetailStock : upAndDownNumberMap.values()) {
			Integer upDownNumber = statisticDetailStock.getUpDownNumber();
			Integer upNumber = statisticDetailStock.getUpNumber();
			Integer downNumber = statisticDetailStock.getDownNumber();
			Map<String, Integer> upAndDownNumberJsonMap = new HashMap<String, Integer>();
			upAndDownNumberJsonMap.put(upDownNumberKey, upDownNumber);
			upAndDownNumberJsonMap.put(upNumberKey, upNumber);
			upAndDownNumberJsonMap.put(downNumberKey, downNumber);
			String upAndDownNumberJson = JsonUtils.getJsonByMap(upAndDownNumberJsonMap);
			switch (periodFlg) {
				case StatisticDetailStock.PRE_ONE_WEEK_UP_DOWN_NUM: // 一周涨跌次数
					statisticDetailStock.setOneWeek(upAndDownNumberJson);
					break;
				case StatisticDetailStock.PRE_HALF_MONTH_UP_DOWN_NUM: // 半月涨跌次数
					statisticDetailStock.setHalfMonth(upAndDownNumberJson);
					break;
				case StatisticDetailStock.PRE_ONE_MONTH_UP_DOWN_NUM: // 一月涨跌次数
					statisticDetailStock.setOneMonth(upAndDownNumberJson);
					break;
				case StatisticDetailStock.PRE_TWO_MONTH_UP_DOWN_NUM: // 二月涨跌次数
					statisticDetailStock.setTwoMonth(upAndDownNumberJson);
					break;
				case StatisticDetailStock.PRE_THREE_MONTH_UP_DOWN_NUM: // 三月涨跌次数
					statisticDetailStock.setThreeMonth(upAndDownNumberJson);
					break;
				case StatisticDetailStock.PRE_HALF_YEAR_UP_DOWN_NUM: // 半年涨跌次数
					statisticDetailStock.setHalfYear(upAndDownNumberJson);
					break;
				case StatisticDetailStock.PRE_ONE_YEAR_UP_DOWN_NUM: // 一年涨跌次数
					statisticDetailStock.setOneYear(upAndDownNumberJson);
					break;
				default:
					IOException ioException = new IOException("周期标识(periodFlg)不正确: " + periodFlg);
					throw ioException;
			}
		}
	}
	
	/**
	 * 根据周期标识获得Json字符串键值
	 *
	 */
	public static String[] getUpAndDownNumberKeysByFlg(String periodFlg) throws IOException {

		String upDownNumberKey = "";
		String upNumberKey = "";
		String downNumberKey = "";
		switch (periodFlg) {
			case StatisticDetailStock.PRE_ONE_WEEK_UP_DOWN_NUM: // 一周涨跌次数
				upDownNumberKey = StatisticDetailStock.PRE_ONE_WEEK_UP_DOWN_NUM;
				upNumberKey = StatisticDetailStock.PRE_ONE_WEEK_UP_NUM;
				downNumberKey = StatisticDetailStock.PRE_ONE_WEEK_DOWN_NUM;
				break;
			case StatisticDetailStock.PRE_HALF_MONTH_UP_DOWN_NUM: // 半月涨跌次数
				upDownNumberKey = StatisticDetailStock.PRE_HALF_MONTH_UP_DOWN_NUM;
				upNumberKey = StatisticDetailStock.PRE_HALF_MONTH_UP_NUM;
				downNumberKey = StatisticDetailStock.PRE_HALF_MONTH_DOWN_NUM;
				break;
			case StatisticDetailStock.PRE_ONE_MONTH_UP_DOWN_NUM: // 一月涨跌次数
				upDownNumberKey = StatisticDetailStock.PRE_ONE_MONTH_UP_DOWN_NUM;
				upNumberKey = StatisticDetailStock.PRE_ONE_MONTH_UP_NUM;
				downNumberKey = StatisticDetailStock.PRE_ONE_MONTH_DOWN_NUM;
				break;
			case StatisticDetailStock.PRE_TWO_MONTH_UP_DOWN_NUM: // 二月涨跌次数
				upDownNumberKey = StatisticDetailStock.PRE_TWO_MONTH_UP_DOWN_NUM;
				upNumberKey = StatisticDetailStock.PRE_TWO_MONTH_UP_NUM;
				downNumberKey = StatisticDetailStock.PRE_TWO_MONTH_DOWN_NUM;
				break;
			case StatisticDetailStock.PRE_THREE_MONTH_UP_DOWN_NUM: // 三月涨跌次数
				upDownNumberKey = StatisticDetailStock.PRE_THREE_MONTH_UP_DOWN_NUM;
				upNumberKey = StatisticDetailStock.PRE_THREE_MONTH_UP_NUM;
				downNumberKey = StatisticDetailStock.PRE_THREE_MONTH_DOWN_NUM;
				break;
			case StatisticDetailStock.PRE_HALF_YEAR_UP_DOWN_NUM: // 半年涨跌次数
				upDownNumberKey = StatisticDetailStock.PRE_HALF_YEAR_UP_DOWN_NUM;
				upNumberKey = StatisticDetailStock.PRE_HALF_YEAR_UP_NUM;
				downNumberKey = StatisticDetailStock.PRE_HALF_YEAR_DOWN_NUM;
				break;
			case StatisticDetailStock.PRE_ONE_YEAR_UP_DOWN_NUM: // 一年涨跌次数
				upDownNumberKey = StatisticDetailStock.PRE_ONE_YEAR_UP_DOWN_NUM;
				upNumberKey = StatisticDetailStock.PRE_ONE_YEAR_UP_NUM;
				downNumberKey = StatisticDetailStock.PRE_ONE_YEAR_DOWN_NUM;
				break;
			default:
				IOException ioException = new IOException("周期标识(periodFlg)不正确: " + periodFlg);
				throw ioException;
		}
		String[] upAndDownKeys = {upDownNumberKey, upNumberKey, downNumberKey};
		return upAndDownKeys;
	}
	
	/**
	 * 根据股票别代码，获得股票代码
	 *
	 */
	public static String getStockCodeByAliasCode(String stockAliasCode) {

		String stockCode = null;
		if (stockAliasCode.contains(DataUtils._SH_CAPITAL) || stockAliasCode.contains(DataUtils._SZ_CAPITAL))
			stockCode = stockAliasCode.substring(2);
		else
			stockCode = stockAliasCode;
		return stockCode;
	}
	
	/**
	 * 根据股票代码得到代码别名 
	 *
	 */
	public static String getAliasCodeByStockCode(String stockCode) {

		String aliasCode = "";
		String firstChar = stockCode.substring(0, 1);
		if (firstChar.equals(DataUtils._STRING_SIX) || firstChar.equals(DataUtils._STRING_SEVEN)) {
			aliasCode = DataUtils._SH_CAPITAL + stockCode;
		} else {
			aliasCode = DataUtils._SZ_CAPITAL + stockCode;
		}
		return aliasCode;
	}
}
