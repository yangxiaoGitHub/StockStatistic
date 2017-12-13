package cn.implement;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DateUtils;
import cn.com.JsonUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllStockDao;
import cn.db.DailyStockDao;
import cn.db.HistoryStockDao;
import cn.db.OperationDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllStock;
import cn.db.bean.DetailStock;
import cn.db.bean.StatisticStock;
import cn.log.Log;

public class OperationData extends BaseData {
	Log log = Log.getLoger();
	
	@Override
	protected void closeDao(OperationDao... daoList) {
		try {
			for (OperationDao dao : daoList) {
				String className = dao.getClass().getSimpleName();
				if (className.equals(DailyStockDao.class.getSimpleName())) {
					if (dailyStockDao != null) {
						dailyStockDao.close();
						dailyStockDao = null;
					}
				} else if (className.equals(StatisticStockDao.class.getSimpleName())) {
					if (statisticStockDao != null) {
						statisticStockDao.close();
						statisticStockDao = null;
					}
				} else if (className.equals(OriginalStockDao.class.getSimpleName())) {
					if (originalStockDao != null) {
						originalStockDao.close();
						originalStockDao = null;
					}
				} else if (className.equals(AllDetailStockDao.class.getSimpleName())) {
					if (allDetailStockDao != null) {
						allDetailStockDao.close();
						allDetailStockDao = null;
					}
				} else if (className.equals(AllDetailStockDao.class.getSimpleName())) {
					if (allInformationStockDao != null) {
						allInformationStockDao.close();
						allInformationStockDao = null;
					}
				} else if (className.equals(AllStockDao.class.getSimpleName())) {
					if (allStockDao != null) {
						allStockDao.close();
						allStockDao = null;
					}
				} else if (className.equals(HistoryStockDao.class.getSimpleName())) {
					if (historyStockDao != null) {
						historyStockDao.close();
						historyStockDao = null;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(ex);
		}
	}
	
	protected AllDetailStock getDetailStockFromArray(String[] stockInfoArray) {

		AllDetailStock detailStock = new AllDetailStock();
		detailStock.setStockName(stockInfoArray[0]);
		detailStock.setTodayOpen(Double.valueOf(stockInfoArray[1]));
		detailStock.setYesterdayClose(Double.valueOf(stockInfoArray[2]));
		detailStock.setCurrent(Double.valueOf(stockInfoArray[3]));
		detailStock.setTodayHigh(Double.valueOf(stockInfoArray[4]));
		detailStock.setTodayLow(Double.valueOf(stockInfoArray[5]));
		detailStock.setTradedStockNumber(Long.valueOf(stockInfoArray[8]));
		detailStock.setTradedAmount(Float.valueOf(stockInfoArray[9]));
		detailStock.setStockDate(DateUtils.String2Date(stockInfoArray[30]));
		detailStock.setTradedTime(DateUtils.StringToDateTime(stockInfoArray[30] + " " + stockInfoArray[31]));
		detailStock.setStockCode(stockInfoArray[33]);
		String stockCodeDES = DESUtils.encryptToHex(stockInfoArray[33]);
		detailStock.setStockCodeDES(stockCodeDES);
	    // 计算涨跌幅
	    calculateStockChangeRate(detailStock);
		return detailStock;
	}

	/**
	 * 获取前半年的涨跌次数Json
	 *
	 */
	protected String getPreHalfYearJson(String stockCode) throws SQLException {
		
		Date[] preHalfYearStartEndDate = DateUtils.getPreHalfYear();
		Map<String, StatisticStock> preHalfYearUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preHalfYearStartEndDate);
		Map<String, StatisticStock> preHalfYearUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preHalfYearStartEndDate);
		Map<String, StatisticStock> preHalfYearDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preHalfYearStartEndDate);
		final Integer preHalfYearUpDownNumber = preHalfYearUpDownNumberMap.get(stockCode)==null?0:preHalfYearUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preHalfYearUpNumber = preHalfYearUpNumberMap.get(stockCode)==null?0:preHalfYearUpNumberMap.get(stockCode).getUpNumber();
		final Integer preHalfYearDownNumber = preHalfYearDownNumberMap.get(stockCode)==null?0:preHalfYearDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preHalfYearUpAndDownMap = new HashMap<String, Integer>(){{put(StatisticStock.PRE_HALF_YEAR_UP_DOWN_NUM, preHalfYearUpDownNumber); 
																					   put(StatisticStock.PRE_HALF_YEAR_UP_NUM, preHalfYearUpNumber); 
																					   put(StatisticStock.PRE_HALF_YEAR_DOWN_NUM, preHalfYearDownNumber);}};
		String preHalfYearUpAndDownJson = JsonUtils.mapToJson(preHalfYearUpAndDownMap);
		return preHalfYearUpAndDownJson;
	}

	/**
	 * 获取前一周的涨跌次数Json
	 *
	 */
	protected String getPreHalfMonthJson(String stockCode) throws SQLException {
		
		Date[] preHalfMonthStartEndDate = DateUtils.getPreHalfMonth();
		Map<String, StatisticStock> preHalfMonthUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preHalfMonthStartEndDate);
		Map<String, StatisticStock> preHalfMonthUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preHalfMonthStartEndDate);
		Map<String, StatisticStock> preHalfMonthDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preHalfMonthStartEndDate);
		final Integer preHalfMonthUpDownNumber = preHalfMonthUpDownNumberMap.get(stockCode)==null?0:preHalfMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preHalfMonthUpNumber = preHalfMonthUpNumberMap.get(stockCode)==null?0:preHalfMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preHalfMonthDownNumber = preHalfMonthDownNumberMap.get(stockCode)==null?0:preHalfMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preHalfMonthUpAndDownMap = new HashMap<String, Integer>(){{put(StatisticStock.PRE_HALF_MONTH_UP_DOWN_NUM, preHalfMonthUpDownNumber); 
		   																			 put(StatisticStock.PRE_HALF_MONTH_UP_NUM, preHalfMonthUpNumber); 
		   																			 put(StatisticStock.PRE_HALF_MONTH_DOWN_NUM, preHalfMonthDownNumber);}};
		String preHalfMonthUpAndDownJson = JsonUtils.mapToJson(preHalfMonthUpAndDownMap);
		return preHalfMonthUpAndDownJson;
	}
	
	/**
	 * 获取前一月的涨跌次数Json
	 *
	 */
	protected String getPreOneMonthJson(String stockCode) throws SQLException {
		
		Date[] preOneMonthStartEndDate = DateUtils.getPreOneMonth();
		Map<String, StatisticStock> preOneMonthUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preOneMonthStartEndDate);
		Map<String, StatisticStock> preOneMonthUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preOneMonthStartEndDate);
		Map<String, StatisticStock> preOneMonthDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preOneMonthStartEndDate);
		final Integer preOneMonthUpDownNumber = preOneMonthUpDownNumberMap.get(stockCode)==null?0:preOneMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preOneMonthUpNumber = preOneMonthUpNumberMap.get(stockCode)==null?0:preOneMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preOneMonthDownNumber = preOneMonthDownNumberMap.get(stockCode)==null?0:preOneMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preOneMonthUpAndDownMap = new HashMap<String, Integer>(){{put(StatisticStock.PRE_ONE_MONTH_UP_DOWN_NUM, preOneMonthUpDownNumber); 
																					   put(StatisticStock.PRE_ONE_MONTH_UP_NUM, preOneMonthUpNumber);
																					   put(StatisticStock.PRE_ONE_MONTH_DOWN_NUM, preOneMonthDownNumber);}};
		String preOneMonthUpAndDownJson = JsonUtils.mapToJson(preOneMonthUpAndDownMap);
		return preOneMonthUpAndDownJson;
	}
	
	/**
	 * 获取前二月的涨跌次数Json
	 *
	 */
	protected String getPreTwoMonthJson(String stockCode) throws SQLException {
		
		Date[] preTwoMonthStartEndDate = DateUtils.getPreTwoMonth();
		Map<String, StatisticStock> preTwoMonthUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preTwoMonthStartEndDate);
		Map<String, StatisticStock> preTwoMonthUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preTwoMonthStartEndDate);
		Map<String, StatisticStock> preTwoMonthDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preTwoMonthStartEndDate);
		final Integer preTwoMonthUpDownNumber = preTwoMonthUpDownNumberMap.get(stockCode)==null?0:preTwoMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preTwoMonthUpNumber = preTwoMonthUpNumberMap.get(stockCode)==null?0:preTwoMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preTwoMonthDownNumber = preTwoMonthDownNumberMap.get(stockCode)==null?0:preTwoMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preTwoMonthUpAndDownMap = new HashMap<String, Integer>(){{put(StatisticStock.PRE_TWO_MONTH_UP_DOWN_NUM, preTwoMonthUpDownNumber); 
																					   put(StatisticStock.PRE_TWO_MONTH_UP_NUM, preTwoMonthUpNumber);
																					   put(StatisticStock.PRE_TWO_MONTH_DOWN_NUM, preTwoMonthDownNumber);}};
		String preTwoMonthUpAndDownJson = JsonUtils.mapToJson(preTwoMonthUpAndDownMap);
		return preTwoMonthUpAndDownJson;
	}

	/**
	 * 获取前三月的涨跌次数Json
	 * 
	 */
	protected String getPreThreeMonthJson(String stockCode) throws SQLException {
		
		Date[] preThreeMonthStartEndDate = DateUtils.getPreThreeMonth();
		Map<String, StatisticStock> preThreeMonthUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preThreeMonthStartEndDate);
		Map<String, StatisticStock> preThreeMonthUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preThreeMonthStartEndDate);
		Map<String, StatisticStock> preThreeMonthDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preThreeMonthStartEndDate);
		final Integer preThreeMonthUpDownNumber = preThreeMonthUpDownNumberMap.get(stockCode)==null?0:preThreeMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preThreeMonthUpNumber = preThreeMonthUpNumberMap.get(stockCode)==null?0:preThreeMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preThreeMonthDownNumber = preThreeMonthDownNumberMap.get(stockCode)==null?0:preThreeMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preThreeMonthUpAndDownMap = new HashMap<String, Integer>(){{put(StatisticStock.PRE_THREE_MONTH_UP_DOWN_NUM, preThreeMonthUpDownNumber); 
																						 put(StatisticStock.PRE_THREE_MONTH_UP_NUM, preThreeMonthUpNumber);
																					   	 put(StatisticStock.PRE_THREE_MONTH_DOWN_NUM, preThreeMonthDownNumber);}};
		String preThreeMonthUpAndDownJson = JsonUtils.mapToJson(preThreeMonthUpAndDownMap);
		return preThreeMonthUpAndDownJson;
	}
	
	/**
	 * 获取前一周的涨跌次数Json
	 *
	 */
	protected String getPreOneWeekJson(String stockCode) throws SQLException {

		Date[] preWeekStartEndDate = DateUtils.getPreOneWeek();
		Map<String, StatisticStock> preOneWeekUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preWeekStartEndDate);
		Map<String, StatisticStock> preOneWeekUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preWeekStartEndDate);
		Map<String, StatisticStock> preOneWeekDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preWeekStartEndDate);
		final Integer preOneWeekUpDownNumber = preOneWeekUpDownNumberMap.get(stockCode)==null?0:preOneWeekUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preOneWeekUpNumber = preOneWeekUpNumberMap.get(stockCode)==null?0:preOneWeekUpNumberMap.get(stockCode).getUpNumber();
		final Integer preOneWeekDownNumber = preOneWeekDownNumberMap.get(stockCode)==null?0:preOneWeekDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preOneWeekUpAndDownMap = new HashMap<String, Integer>(){{put(StatisticStock.PRE_ONE_WEEK_UP_DOWN_NUM, preOneWeekUpDownNumber); 
																				   	  put(StatisticStock.PRE_ONE_WEEK_UP_NUM, preOneWeekUpNumber); 
																				      put(StatisticStock.PRE_ONE_WEEK_DOWN_NUM, preOneWeekDownNumber);}};
		String preOneWeekUpAndDownJson = JsonUtils.mapToJson(preOneWeekUpAndDownMap);
		return preOneWeekUpAndDownJson;
	}
	
	/**
	 * 获取前一年的涨跌次数Json
	 *
	 */
	protected String getPreOneYearJson(String stockCode) throws SQLException {
		
		Date[] preOneYearStartEndDate = DateUtils.getPreOneYear();
		Map<String, StatisticStock> preOneYearUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preOneYearStartEndDate);
		Map<String, StatisticStock> preOneYearUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preOneYearStartEndDate);
		Map<String, StatisticStock> preOneYearDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preOneYearStartEndDate);
		final Integer preOneYearUpDownNumber = preOneYearUpDownNumberMap.get(stockCode)==null?0:preOneYearUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preOneYearUpNumber = preOneYearUpNumberMap.get(stockCode)==null?0:preOneYearUpNumberMap.get(stockCode).getUpNumber();
		final Integer preOneYearDownNumber = preOneYearDownNumberMap.get(stockCode)==null?0:preOneYearDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preOneYearUpAndDownMap = new HashMap<String, Integer>(){{put(StatisticStock.PRE_ONE_YEAR_UP_DOWN_NUM, preOneYearUpDownNumber); 
		   																			   put(StatisticStock.PRE_ONE_YEAR_UP_NUM, preOneYearUpNumber);
		   																			   put(StatisticStock.PRE_ONE_YEAR_DOWN_NUM, preOneYearDownNumber);}};
		String preOneYearUpAndDownJson = JsonUtils.mapToJson(preOneYearUpAndDownMap);
		return preOneYearUpAndDownJson;
	}
	
	/**
	 * 计算股票的涨跌幅
	 * @return 
	 * 
	 */
	protected <T extends DetailStock> void calculateStockChangeRate(T stock) {

		Double yesterdayClose = stock.getYesterdayClose();
		if (!CommonUtils.isZeroOrNull(yesterdayClose)) {
			double current = stock.getCurrent().doubleValue();
			double changeRate = ((current-yesterdayClose)*100)/yesterdayClose;
			BigDecimal bigValue = new BigDecimal(changeRate);
			double dValue = bigValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			stock.setChangeRate(dValue);
			String changeRateDES = DESUtils.encryptToHex(String.valueOf(dValue));
			stock.setChangeRateDES(changeRateDES);
		}
	}
	
	/**
	 * 根据流通股和交易股计算股票的换手率
	 *
	 */
	protected <T extends DetailStock> void calculateTurnoverRate(T stock) throws SQLException {
		
		String stockCode = stock.getStockCode();
		AllStock allStock = allStockDao.getAllStockByStockCode(stockCode);
		Long circulationStock = null;
		if (allStock != null) circulationStock = allStock.getCirculationStockComplex();
		if (circulationStock!=null && circulationStock!=0) {
			Long tradedStockNum = stock.getTradedStockNumber();
			Double turnoverRate = (tradedStockNum.doubleValue()/circulationStock.doubleValue())*100;
			turnoverRate = new BigDecimal(turnoverRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			String turnoverRateDES = DESUtils.encryptToHex(turnoverRate.toString());
			stock.setTurnoverRate(turnoverRate);
			stock.setTurnoverRateDES(turnoverRateDES);
		}
	}
	
	/**
	 * 设置StatisticStock对象的涨跌次数和涨跌次数Json字符串
	 *
	 */
	protected void setUpDownNumberAndUpDownJson(StatisticStock statistic) throws SQLException {
		
		String stockCode = statistic.getStockCode();
		Integer upNumber = dailyStockDao.getUpNumberByStockCode(stockCode);
		Integer downNumber = dailyStockDao.getDownNumberByStockCode(stockCode);
		Integer upDownNumber = dailyStockDao.getUpDownNumberByStockCode(stockCode);
		String preOneWeekJson = this.getPreOneWeekJson(stockCode);
		String preHalfMonthJson = this.getPreHalfMonthJson(stockCode);
		String preOneMonthJson = this.getPreOneMonthJson(stockCode);
		String preTwoMonthJson = this.getPreTwoMonthJson(stockCode);
		String preThreeMonthJson = this.getPreThreeMonthJson(stockCode);
		String preHalfYearJson = this.getPreHalfYearJson(stockCode);
		String preOneYearJson = this.getPreOneYearJson(stockCode);
		statistic.setUpDownNumber(upDownNumber);
		statistic.setUpNumber(upNumber);
		statistic.setDownNumber(downNumber);
		statistic.setOneWeek(preOneWeekJson);
		statistic.setHalfMonth(preHalfMonthJson);
		statistic.setOneMonth(preOneMonthJson);
		statistic.setTwoMonth(preTwoMonthJson);
		statistic.setThreeMonth(preThreeMonthJson);
		statistic.setHalfYear(preHalfYearJson);
		statistic.setOneYear(preOneYearJson);
	}
}
