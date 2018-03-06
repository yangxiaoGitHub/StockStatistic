package cn.implement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.JsonUtils;
import cn.com.StockUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllImportStockDao;
import cn.db.AllStockDao;
import cn.db.DailyStockDao;
import cn.db.HistoryStockDao;
import cn.db.OperationDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.bean.BaseStock;
import cn.db.bean.DailyStock;
import cn.db.bean.HistoryStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticDetailStock;
import cn.log.Log;

public class OperationData extends BaseData {
	protected Log log = Log.getLoger();

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
				} else if (className.equals(StatisticDetailStockDao.class.getSimpleName())) {
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
				} else if (className.equals(AllImportStockDao.class.getSimpleName())) {
					if (allImportStockDao != null) {
						allImportStockDao.close();
						allDetailStockDao = null;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
	}

	/**
	 * 获取前半年的涨跌次数Json
	 *
	 */
	protected String getPreHalfYearJson(String stockCode, Date recentDate) throws SQLException {

		Date[] preHalfYearStartEndDate = DateUtils.getPreHalfYear(recentDate);
		Map<String, StatisticDetailStock> preHalfYearUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preHalfYearStartEndDate);
		Map<String, StatisticDetailStock> preHalfYearUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preHalfYearStartEndDate);
		Map<String, StatisticDetailStock> preHalfYearDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preHalfYearStartEndDate);
		final String statisticDate = DateUtils.dateToString(preHalfYearStartEndDate[1]);
		final Integer preHalfYearUpDownNumber = preHalfYearUpDownNumberMap.get(stockCode) == null ? 0
				: preHalfYearUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preHalfYearUpNumber = preHalfYearUpNumberMap.get(stockCode) == null ? 0
				: preHalfYearUpNumberMap.get(stockCode).getUpNumber();
		final Integer preHalfYearDownNumber = preHalfYearDownNumberMap.get(stockCode) == null ? 0
				: preHalfYearDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preHalfYearUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticDetailStock.PRE_HALF_YEAR_UP_DOWN_NUM, preHalfYearUpDownNumber);
				put(StatisticDetailStock.PRE_HALF_YEAR_UP_NUM, preHalfYearUpNumber);
				put(StatisticDetailStock.PRE_HALF_YEAR_DOWN_NUM, preHalfYearDownNumber);
			}
		};
		String preHalfYearUpAndDownJson = JsonUtils.getJsonByMap(preHalfYearUpAndDownMap);
		return preHalfYearUpAndDownJson;
	}

	/**
	 * 获取前一周的涨跌次数Json
	 *
	 */
	protected String getPreHalfMonthJson(String stockCode, Date recentDate) throws SQLException {
		
		Date[] preHalfMonthStartEndDate = DateUtils.getPreHalfMonth(recentDate);
		Map<String, StatisticDetailStock> preHalfMonthUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preHalfMonthStartEndDate);
		Map<String, StatisticDetailStock> preHalfMonthUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preHalfMonthStartEndDate);
		Map<String, StatisticDetailStock> preHalfMonthDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preHalfMonthStartEndDate);
		final Integer preHalfMonthUpDownNumber = preHalfMonthUpDownNumberMap.get(stockCode) == null ? 0
				: preHalfMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preHalfMonthUpNumber = preHalfMonthUpNumberMap.get(stockCode) == null ? 0
				: preHalfMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preHalfMonthDownNumber = preHalfMonthDownNumberMap.get(stockCode) == null ? 0
				: preHalfMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preHalfMonthUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticDetailStock.PRE_HALF_MONTH_UP_DOWN_NUM, preHalfMonthUpDownNumber);
				put(StatisticDetailStock.PRE_HALF_MONTH_UP_NUM, preHalfMonthUpNumber);
				put(StatisticDetailStock.PRE_HALF_MONTH_DOWN_NUM, preHalfMonthDownNumber);
			}
		};
		String preHalfMonthUpAndDownJson = JsonUtils.getJsonByMap(preHalfMonthUpAndDownMap);
		return preHalfMonthUpAndDownJson;
	}

	/**
	 * 获取前一月的涨跌次数Json
	 *
	 */
	protected String getPreOneMonthJson(String stockCode, Date recentDate) throws SQLException {

		Date[] preOneMonthStartEndDate = DateUtils.getPreOneMonth(recentDate);
		Map<String, StatisticDetailStock> preOneMonthUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preOneMonthStartEndDate);
		Map<String, StatisticDetailStock> preOneMonthUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preOneMonthStartEndDate);
		Map<String, StatisticDetailStock> preOneMonthDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preOneMonthStartEndDate);
		final Integer preOneMonthUpDownNumber = preOneMonthUpDownNumberMap.get(stockCode) == null ? 0
				: preOneMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preOneMonthUpNumber = preOneMonthUpNumberMap.get(stockCode) == null ? 0
				: preOneMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preOneMonthDownNumber = preOneMonthDownNumberMap.get(stockCode) == null ? 0
				: preOneMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preOneMonthUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticDetailStock.PRE_ONE_MONTH_UP_DOWN_NUM, preOneMonthUpDownNumber);
				put(StatisticDetailStock.PRE_ONE_MONTH_UP_NUM, preOneMonthUpNumber);
				put(StatisticDetailStock.PRE_ONE_MONTH_DOWN_NUM, preOneMonthDownNumber);
			}
		};
		String preOneMonthUpAndDownJson = JsonUtils.getJsonByMap(preOneMonthUpAndDownMap);
		return preOneMonthUpAndDownJson;
	}

	/**
	 * 获取前二月的涨跌次数Json
	 *
	 */
	protected String getPreTwoMonthJson(String stockCode, Date recentDate) throws SQLException {

		Date[] preTwoMonthStartEndDate = DateUtils.getPreTwoMonth(recentDate);
		Map<String, StatisticDetailStock> preTwoMonthUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preTwoMonthStartEndDate);
		Map<String, StatisticDetailStock> preTwoMonthUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preTwoMonthStartEndDate);
		Map<String, StatisticDetailStock> preTwoMonthDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preTwoMonthStartEndDate);
		final Integer preTwoMonthUpDownNumber = preTwoMonthUpDownNumberMap.get(stockCode) == null ? 0
				: preTwoMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preTwoMonthUpNumber = preTwoMonthUpNumberMap.get(stockCode) == null ? 0
				: preTwoMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preTwoMonthDownNumber = preTwoMonthDownNumberMap.get(stockCode) == null ? 0
				: preTwoMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preTwoMonthUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticDetailStock.PRE_TWO_MONTH_UP_DOWN_NUM, preTwoMonthUpDownNumber);
				put(StatisticDetailStock.PRE_TWO_MONTH_UP_NUM, preTwoMonthUpNumber);
				put(StatisticDetailStock.PRE_TWO_MONTH_DOWN_NUM, preTwoMonthDownNumber);
			}
		};
		String preTwoMonthUpAndDownJson = JsonUtils.getJsonByMap(preTwoMonthUpAndDownMap);
		return preTwoMonthUpAndDownJson;
	}

	/**
	 * 获取前三月的涨跌次数Json
	 * 
	 */
	protected String getPreThreeMonthJson(String stockCode, Date recentDate) throws SQLException {

		Date[] preThreeMonthStartEndDate = DateUtils.getPreThreeMonth(recentDate);
		Map<String, StatisticDetailStock> preThreeMonthUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preThreeMonthStartEndDate);
		Map<String, StatisticDetailStock> preThreeMonthUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preThreeMonthStartEndDate);
		Map<String, StatisticDetailStock> preThreeMonthDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preThreeMonthStartEndDate);
		final Integer preThreeMonthUpDownNumber = preThreeMonthUpDownNumberMap.get(stockCode) == null ? 0
				: preThreeMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preThreeMonthUpNumber = preThreeMonthUpNumberMap.get(stockCode) == null ? 0
				: preThreeMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preThreeMonthDownNumber = preThreeMonthDownNumberMap.get(stockCode) == null ? 0
				: preThreeMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preThreeMonthUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticDetailStock.PRE_THREE_MONTH_UP_DOWN_NUM, preThreeMonthUpDownNumber);
				put(StatisticDetailStock.PRE_THREE_MONTH_UP_NUM, preThreeMonthUpNumber);
				put(StatisticDetailStock.PRE_THREE_MONTH_DOWN_NUM, preThreeMonthDownNumber);
			}
		};
		String preThreeMonthUpAndDownJson = JsonUtils.getJsonByMap(preThreeMonthUpAndDownMap);
		return preThreeMonthUpAndDownJson;
	}

	/**
	 * 获取前一周的涨跌次数Json
	 *
	 */
	protected String getPreOneWeekJson(String stockCode, Date recentDate) throws SQLException {

		Date[] preWeekStartEndDate = DateUtils.getPreOneWeek(recentDate);
		Map<String, StatisticDetailStock> preOneWeekUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preWeekStartEndDate);
		Map<String, StatisticDetailStock> preOneWeekUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preWeekStartEndDate);
		Map<String, StatisticDetailStock> preOneWeekDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preWeekStartEndDate);
		final Integer preOneWeekUpDownNumber = preOneWeekUpDownNumberMap.get(stockCode) == null ? 0
				: preOneWeekUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preOneWeekUpNumber = preOneWeekUpNumberMap.get(stockCode) == null ? 0 : preOneWeekUpNumberMap.get(stockCode).getUpNumber();
		final Integer preOneWeekDownNumber = preOneWeekDownNumberMap.get(stockCode) == null ? 0
				: preOneWeekDownNumberMap.get(stockCode).getDownNumber();
		Map<String, ? extends Object> preOneWeekUpAndDownMap = new HashMap<String, Object>() {
			{
				put(StatisticDetailStock.PRE_ONE_WEEK_UP_DOWN_NUM, preOneWeekUpDownNumber);
				put(StatisticDetailStock.PRE_ONE_WEEK_UP_NUM, preOneWeekUpNumber);
				put(StatisticDetailStock.PRE_ONE_WEEK_DOWN_NUM, preOneWeekDownNumber);
			}
		};
		String preOneWeekUpAndDownJson = JsonUtils.getJsonByMap(preOneWeekUpAndDownMap);
		return preOneWeekUpAndDownJson;
	}
	
	/**
	 * 获得总涨跌次数
	 * 
	 */
	protected Map<String, Integer> getUpAndDownNumber(String stockCode) throws SQLException {
		
		final Integer upDownNumber = dailyStockDao.getUpDownNumberByStockCode(stockCode);
		final Integer upNumber = dailyStockDao.getUpNumberByStockCode(stockCode);
		final Integer downNumber = dailyStockDao.getDownNumberByStockCode(stockCode);
		Map<String, Integer> upDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticDetailStock.UP_DOWN_KEY, upDownNumber);
				put(StatisticDetailStock.UP_KEY, upNumber);
				put(StatisticDetailStock.DOWN_KEY, downNumber);
			}
		};
		return upDownMap;
	}

	/**
	 * 获取前一年的涨跌次数Json
	 *
	 */
	protected String getPreOneYearJson(String stockCode, Date recentDate) throws SQLException {

		Date[] preOneYearStartEndDate = DateUtils.getPreOneYear(recentDate);
		Map<String, StatisticDetailStock> preOneYearUpDownNumberMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, preOneYearStartEndDate);
		Map<String, StatisticDetailStock> preOneYearUpNumberMap = dailyStockDao.statisticUpInDailyStock(stockCode, preOneYearStartEndDate);
		Map<String, StatisticDetailStock> preOneYearDownNumberMap = dailyStockDao.statisticDownInDailyStock(stockCode, preOneYearStartEndDate);
		final Integer preOneYearUpDownNumber = preOneYearUpDownNumberMap.get(stockCode) == null ? 0
				: preOneYearUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preOneYearUpNumber = preOneYearUpNumberMap.get(stockCode) == null ? 0 : preOneYearUpNumberMap.get(stockCode).getUpNumber();
		final Integer preOneYearDownNumber = preOneYearDownNumberMap.get(stockCode) == null ? 0
				: preOneYearDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preOneYearUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticDetailStock.PRE_ONE_YEAR_UP_DOWN_NUM, preOneYearUpDownNumber);
				put(StatisticDetailStock.PRE_ONE_YEAR_UP_NUM, preOneYearUpNumber);
				put(StatisticDetailStock.PRE_ONE_YEAR_DOWN_NUM, preOneYearDownNumber);
			}
		};
		String preOneYearUpAndDownJson = JsonUtils.getJsonByMap(preOneYearUpAndDownMap);
		return preOneYearUpAndDownJson;
	}
	
	/**
	 * 计算股票的总涨跌次数和统计股票Json涨跌次数
	 *
	 */
	protected void calculateUpDownNumber(StatisticDetailStock statisticDetailStock, String changeFlg) throws SQLException {
		// 计算总涨跌次数
		Integer upDownNumber = statisticDetailStock.getUpDownNumber();
		statisticDetailStock.setUpDownNumber(++upDownNumber); // 总涨跌次数
		if (DailyStock.CHANGE_FLG_ONE.equals(changeFlg)) {
			Integer upNumber = statisticDetailStock.getUpNumber();
			statisticDetailStock.setUpNumber(++upNumber); // 总涨次数
		} else {
			Integer downNumber = statisticDetailStock.getDownNumber();
			statisticDetailStock.setDownNumber(++downNumber); // 总跌次数
		}
		// 统计Json涨跌次数
		statisticUpAndDownJsonInDailyStock(statisticDetailStock);
	}
	
	/**
	 * 统计daily_detail_stock_表中所有股票Json涨跌次数(不包括总涨跌次数)
	 *
	 */
	protected void statisticUpAndDownJsonInDailyStock(StatisticDetailStock statistic) throws SQLException {

		String stockCode = statistic.getStockCode();
		//Date[] minMaxDate = dailyStockDao.getMinMaxDate();
		Date recentDate = statistic.getStockDate();
		String preOneWeekJson = this.getPreOneWeekJson(stockCode, recentDate);
		String preHalfMonthJson = this.getPreHalfMonthJson(stockCode, recentDate);
		String preOneMonthJson = this.getPreOneMonthJson(stockCode, recentDate);
		String preTwoMonthJson = this.getPreTwoMonthJson(stockCode, recentDate);
		String preThreeMonthJson = this.getPreThreeMonthJson(stockCode, recentDate);
		String preHalfYearJson = this.getPreHalfYearJson(stockCode, recentDate);
		String preOneYearJson = this.getPreOneYearJson(stockCode, recentDate);
		statistic.setStockDate(recentDate);
		statistic.setOneWeek(preOneWeekJson);
		statistic.setHalfMonth(preHalfMonthJson);
		statistic.setOneMonth(preOneMonthJson);
		statistic.setTwoMonth(preTwoMonthJson);
		statistic.setThreeMonth(preThreeMonthJson);
		statistic.setHalfYear(preHalfYearJson);
		statistic.setOneYear(preOneYearJson);
	}

	/**
	 * 统计original_stock_表中所有股票总涨跌次数和Json涨跌次数
	 *
	 */
	protected Map<String, StatisticDetailStock> statisticUpDownNumberInOriginalStock(List<StatisticDetailStock> statisticDetailStockList) throws SQLException, IOException {

		List<OriginalStock> originalStockList = originalStockDao.listOriginalData();
		// 根据statisticDetailStockList，获得总涨跌次数
		Map<String, StatisticDetailStock> upAndDownNumberMap = StockUtils.statisticUpAndDownNumber(statisticDetailStockList, originalStockList, DataUtils._INT_ZERO);
		// 根据statisticDetailStockList，获得前一周涨跌次数
		Map<String, StatisticDetailStock> preOneWeekJsonMap = StockUtils.statisticUpAndDownNumber(statisticDetailStockList, originalStockList, DataUtils._INT_ONE);
		StockUtils.setUpAndDownNumberJson(preOneWeekJsonMap, StatisticDetailStock.PRE_ONE_WEEK_UP_DOWN_NUM);
		// 获得前半月涨跌次数
		Map<String, StatisticDetailStock> preHalfMonthJsonMap = StockUtils.statisticUpAndDownNumber(statisticDetailStockList, originalStockList, DataUtils._INT_TWO);
		StockUtils.setUpAndDownNumberJson(preHalfMonthJsonMap, StatisticDetailStock.PRE_HALF_MONTH_UP_DOWN_NUM);
		// 获得前一月涨跌次数
		Map<String, StatisticDetailStock> preOneMonthJsonMap = StockUtils.statisticUpAndDownNumber(statisticDetailStockList, originalStockList, DataUtils._INT_THREE);
		StockUtils.setUpAndDownNumberJson(preOneMonthJsonMap, StatisticDetailStock.PRE_ONE_MONTH_UP_DOWN_NUM);
		// 获得前二月涨跌次数
		Map<String, StatisticDetailStock> preTwoMonthJsonMap = StockUtils.statisticUpAndDownNumber(statisticDetailStockList, originalStockList, DataUtils._INT_FOUR);
		StockUtils.setUpAndDownNumberJson(preTwoMonthJsonMap, StatisticDetailStock.PRE_TWO_MONTH_UP_DOWN_NUM);
		// 获得前三月涨跌次数
		Map<String, StatisticDetailStock> preThreeMonthJsonMap = StockUtils.statisticUpAndDownNumber(statisticDetailStockList, originalStockList, DataUtils._INT_FIVE);
		StockUtils.setUpAndDownNumberJson(preThreeMonthJsonMap, StatisticDetailStock.PRE_THREE_MONTH_UP_DOWN_NUM);
		// 获得前半年涨跌次数
		Map<String, StatisticDetailStock> preHalfYearJsonMap = StockUtils.statisticUpAndDownNumber(statisticDetailStockList, originalStockList, DataUtils._INT_SIX);
		StockUtils.setUpAndDownNumberJson(preHalfYearJsonMap, StatisticDetailStock.PRE_HALF_YEAR_UP_DOWN_NUM);
		// 获得前一年涨跌次数 
		Map<String, StatisticDetailStock> preOneYearJsonMap = StockUtils.statisticUpAndDownNumber(statisticDetailStockList, originalStockList, DataUtils._INT_SEVEN);
		StockUtils.setUpAndDownNumberJson(preOneYearJsonMap, StatisticDetailStock.PRE_ONE_YEAR_UP_DOWN_NUM);
		
		Map<String, StatisticDetailStock> combineUpAndDownJsonMap = StockUtils.combineUpAndDownNumberJsonMap(upAndDownNumberMap, preOneYearJsonMap, preHalfYearJsonMap, 
																	preThreeMonthJsonMap, preTwoMonthJsonMap, preOneMonthJsonMap, preHalfMonthJsonMap, preOneWeekJsonMap);
		return combineUpAndDownJsonMap;
	}
	
	/**
	 * according to the key(stockCode, stockDate), select the stock of the previous stock opening day
	 * 
	 */
	protected HistoryStock getHistoryStockByKey(String stockCode, Date stockDate) throws SQLException {

		Date preDate = stockDate;
		HistoryStock historyStock = null;
		for (int index = 0; index < DataUtils._LONG_DAY; index++) {
			preDate = DateUtils.minusOneDay(preDate);
			historyStock = historyStockDao.getHistoryStockByKey(stockCode, preDate);
			if (historyStock != null)
				break;
		}
		return historyStock;
	}
}
