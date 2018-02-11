package cn.implement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.com.JsonUtils;
import cn.com.StockUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllDetailStockTestDao;
import cn.db.AllImportStockDao;
import cn.db.AllInformationStockTestDao;
import cn.db.AllStockDao;
import cn.db.DailyStockDao;
import cn.db.HistoryStockDao;
import cn.db.OperationDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.bean.DailyStock;
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
				} else if (className.equals(AllInformationStockTestDao.class.getSimpleName())) {
					if (allInformationStockTestDao != null) {
						allInformationStockTestDao.close();
						allInformationStockTestDao = null;
					}
				} else if (className.equals(AllDetailStockTestDao.class.getSimpleName())) {
					if (allDetailStockTestDao != null) {
						allDetailStockTestDao.close();
						allDetailStockTestDao = null;
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
	 * 获取original_stock_表中前一周的涨跌次数Json
	 * 
	 */
	protected Map<String, StatisticDetailStock> getPreOneWeekJsonInOriginalStock(Date recentDate) throws SQLException, IOException {
		
		Date[] preOneWeekStartEndDate = DateUtils.getPreOneWeek(recentDate);
		List<OriginalStock> originalStockList = originalStockDao.getOriginalStockByDateInterval(preOneWeekStartEndDate[0], preOneWeekStartEndDate[1]);
		Map<String, StatisticDetailStock> preOneWeekUpAndDownNumberMap = StockUtils.statisticUpAndDownNumber(originalStockList);
		StockUtils.setUpAndDownNumberJson(preOneWeekUpAndDownNumberMap, StatisticDetailStock.PRE_ONE_WEEK_UP_DOWN_NUM);
		return preOneWeekUpAndDownNumberMap;
	}
	
	/**
	 * 获取original_stock_表中前半月的涨跌次数Json
	 *
	 */
	protected Map<String, StatisticDetailStock> getPreHalfMonthJsonInOriginalStock(Date recentDate) throws SQLException, IOException {
		
		Date[] preHalfMonthStartEndDate = DateUtils.getPreHalfMonth(recentDate);
		List<OriginalStock> originalStockList = originalStockDao.getOriginalStockByDateInterval(preHalfMonthStartEndDate[0], preHalfMonthStartEndDate[1]);
		Map<String, StatisticDetailStock> preHalfMonthUpAndDownNumberMap = StockUtils.statisticUpAndDownNumber(originalStockList);
		StockUtils.setUpAndDownNumberJson(preHalfMonthUpAndDownNumberMap, StatisticDetailStock.PRE_HALF_MONTH_UP_DOWN_NUM);
		return preHalfMonthUpAndDownNumberMap;
	}
	
	/**
	 * 获取original_stock_表中前一月的涨跌次数Json
	 * 
	 */
	protected Map<String, StatisticDetailStock> getPreOneMonthJsonInOriginalStock(Date recentDate) throws SQLException, IOException {
		
		Date[] preOneMonthStartEndDate = DateUtils.getPreOneMonth(recentDate);
		List<OriginalStock> originalStockList = originalStockDao.getOriginalStockByDateInterval(preOneMonthStartEndDate[0], preOneMonthStartEndDate[1]);
		Map<String, StatisticDetailStock> preOneMonthUpAndDownNumberMap = StockUtils.statisticUpAndDownNumber(originalStockList);
		StockUtils.setUpAndDownNumberJson(preOneMonthUpAndDownNumberMap, StatisticDetailStock.PRE_ONE_MONTH_UP_DOWN_NUM);
		return preOneMonthUpAndDownNumberMap;
	}
	
	/**
	 * 获取original_stock_表中前二月的涨跌次数Json
	 * 
	 */
	protected Map<String, StatisticDetailStock> getPreTwoMonthJsonInOriginalStock(Date recentDate) throws SQLException, IOException {
		
		Date[] preTwoMonthStartEndDate = DateUtils.getPreTwoMonth(recentDate);
		List<OriginalStock> originalStockList = originalStockDao.getOriginalStockByDateInterval(preTwoMonthStartEndDate[0], preTwoMonthStartEndDate[1]);
		Map<String, StatisticDetailStock> preTwoMonthUpAndDownNumberMap = StockUtils.statisticUpAndDownNumber(originalStockList);
		StockUtils.setUpAndDownNumberJson(preTwoMonthUpAndDownNumberMap, StatisticDetailStock.PRE_TWO_MONTH_UP_DOWN_NUM);
		return preTwoMonthUpAndDownNumberMap;
	}
	
	/**
	 * 获取original_stock_表中前三月的涨跌次数Json
	 * 
	 */
	protected Map<String, StatisticDetailStock> getPreThreeMonthJsonInOriginalStock(Date recentDate) throws SQLException, IOException {
		
		Date[] preThreeMonthStartEndDate = DateUtils.getPreThreeMonth(recentDate);
		List<OriginalStock> originalStockList = originalStockDao.getOriginalStockByDateInterval(preThreeMonthStartEndDate[0], preThreeMonthStartEndDate[1]);
		Map<String, StatisticDetailStock> preThreeMonthUpAndDownNumberMap = StockUtils.statisticUpAndDownNumber(originalStockList);
		StockUtils.setUpAndDownNumberJson(preThreeMonthUpAndDownNumberMap, StatisticDetailStock.PRE_THREE_MONTH_UP_DOWN_NUM);
		return preThreeMonthUpAndDownNumberMap;
	}
	
	/**
	 * 获取original_stock_表中前半年的涨跌次数Json
	 *
	 */
	protected Map<String, StatisticDetailStock> getPreHalfYearJsonInOriginalStock(Date recentDate) throws SQLException, IOException {
		
		Date[] preHalfYearStartEndDate = DateUtils.getPreHalfYear(recentDate);
		List<OriginalStock> originalStockList = originalStockDao.getOriginalStockByDateInterval(preHalfYearStartEndDate[0], preHalfYearStartEndDate[1]);
		Map<String, StatisticDetailStock> preHalfYearUpAndDownNumberMap = StockUtils.statisticUpAndDownNumber(originalStockList);
		StockUtils.setUpAndDownNumberJson(preHalfYearUpAndDownNumberMap, StatisticDetailStock.PRE_HALF_YEAR_UP_DOWN_NUM);
		return preHalfYearUpAndDownNumberMap;
	}
	
	/**
	 * 获取original_stock_表中前一年的涨跌次数Json
	 *
	 */
	protected Map<String, StatisticDetailStock> getPreOneYearJsonInOriginalStock(Date recentDate) throws SQLException, IOException {
		
		Date[] preOneYearStartEndDate = DateUtils.getPreOneYear(recentDate);
		List<OriginalStock> originalStockList = originalStockDao.getOriginalStockByDateInterval(preOneYearStartEndDate[0], preOneYearStartEndDate[1]);
		Map<String, StatisticDetailStock> preOneYearUpAndDownNumberMap = StockUtils.statisticUpAndDownNumber(originalStockList);
		StockUtils.setUpAndDownNumberJson(preOneYearUpAndDownNumberMap, StatisticDetailStock.PRE_ONE_YEAR_UP_DOWN_NUM);
		return preOneYearUpAndDownNumberMap;
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
		Date[] minMaxDate = dailyStockDao.getMinMaxDate();
		String preOneWeekJson = this.getPreOneWeekJson(stockCode, minMaxDate[1]);
		String preHalfMonthJson = this.getPreHalfMonthJson(stockCode, minMaxDate[1]);
		String preOneMonthJson = this.getPreOneMonthJson(stockCode, minMaxDate[1]);
		String preTwoMonthJson = this.getPreTwoMonthJson(stockCode, minMaxDate[1]);
		String preThreeMonthJson = this.getPreThreeMonthJson(stockCode, minMaxDate[1]);
		String preHalfYearJson = this.getPreHalfYearJson(stockCode, minMaxDate[1]);
		String preOneYearJson = this.getPreOneYearJson(stockCode, minMaxDate[1]);
		statistic.setStockDate(minMaxDate[1]);
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
	protected Map<String, StatisticDetailStock> statisticUpDownNumberInOriginalStock() throws SQLException, IOException {

		Date recentDate = originalStockDao.getRecentStockDate();
		List<OriginalStock> originalStockList = originalStockDao.listOriginalData();
		Map<String, StatisticDetailStock> upAndDownNumberMap = StockUtils.statisticUpAndDownNumber(originalStockList); //总涨跌次数
		Map<String, StatisticDetailStock> preOneWeekJsonMap = getPreOneWeekJsonInOriginalStock(recentDate); //前一周涨跌次数
		Map<String, StatisticDetailStock> preHalfMonthJsonMap = getPreHalfMonthJsonInOriginalStock(recentDate); //前半月涨跌次数
		Map<String, StatisticDetailStock> PreOneMonthJsonMap = getPreOneMonthJsonInOriginalStock(recentDate); //前一月涨跌次数
		Map<String, StatisticDetailStock> preTwoMonthJsonMap = getPreTwoMonthJsonInOriginalStock(recentDate); //前二月涨跌次数
		Map<String, StatisticDetailStock> preThreeMonthJsonMap = getPreThreeMonthJsonInOriginalStock(recentDate); //前三月涨跌次数
		Map<String, StatisticDetailStock> preHalfYearJsonMap = getPreHalfYearJsonInOriginalStock(recentDate); //前半年涨跌次数
		Map<String, StatisticDetailStock> preOneYearJsonMap = getPreOneYearJsonInOriginalStock(recentDate); //前一年涨跌次数
		Map<String, StatisticDetailStock> combineUpAndDownJsonMap = StockUtils.combineUpAndDownNumberJsonMap(upAndDownNumberMap, preOneYearJsonMap,
				preHalfYearJsonMap, preThreeMonthJsonMap, preTwoMonthJsonMap, PreOneMonthJsonMap, preHalfMonthJsonMap, preOneWeekJsonMap);
		return combineUpAndDownJsonMap;
	}
}
