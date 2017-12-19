package cn.implement;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.JsonUtils;
import cn.com.PropertiesUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllDetailStockTestDao;
import cn.db.AllInformationStockTestDao;
import cn.db.AllStockDao;
import cn.db.DailyStockDao;
import cn.db.HistoryStockDao;
import cn.db.OperationDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllStock;
import cn.db.bean.DailyStock;
import cn.db.bean.DetailStock;
import cn.db.bean.StatisticStock;
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
		detailStock.setStockDate(DateUtils.stringToDate(stockInfoArray[30]));
		detailStock.setTradedTime(DateUtils.stringToDateTime(stockInfoArray[30] + " " + stockInfoArray[31]));
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
		final Integer preHalfYearUpDownNumber = preHalfYearUpDownNumberMap.get(stockCode) == null ? 0
				: preHalfYearUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preHalfYearUpNumber = preHalfYearUpNumberMap.get(stockCode) == null ? 0
				: preHalfYearUpNumberMap.get(stockCode).getUpNumber();
		final Integer preHalfYearDownNumber = preHalfYearDownNumberMap.get(stockCode) == null ? 0
				: preHalfYearDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preHalfYearUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticStock.PRE_HALF_YEAR_UP_DOWN_NUM, preHalfYearUpDownNumber);
				put(StatisticStock.PRE_HALF_YEAR_UP_NUM, preHalfYearUpNumber);
				put(StatisticStock.PRE_HALF_YEAR_DOWN_NUM, preHalfYearDownNumber);
			}
		};
		String preHalfYearUpAndDownJson = JsonUtils.getJsonByMap(preHalfYearUpAndDownMap);
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
		final Integer preHalfMonthUpDownNumber = preHalfMonthUpDownNumberMap.get(stockCode) == null ? 0
				: preHalfMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preHalfMonthUpNumber = preHalfMonthUpNumberMap.get(stockCode) == null ? 0
				: preHalfMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preHalfMonthDownNumber = preHalfMonthDownNumberMap.get(stockCode) == null ? 0
				: preHalfMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preHalfMonthUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticStock.PRE_HALF_MONTH_UP_DOWN_NUM, preHalfMonthUpDownNumber);
				put(StatisticStock.PRE_HALF_MONTH_UP_NUM, preHalfMonthUpNumber);
				put(StatisticStock.PRE_HALF_MONTH_DOWN_NUM, preHalfMonthDownNumber);
			}
		};
		String preHalfMonthUpAndDownJson = JsonUtils.getJsonByMap(preHalfMonthUpAndDownMap);
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
		final Integer preOneMonthUpDownNumber = preOneMonthUpDownNumberMap.get(stockCode) == null ? 0
				: preOneMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preOneMonthUpNumber = preOneMonthUpNumberMap.get(stockCode) == null ? 0
				: preOneMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preOneMonthDownNumber = preOneMonthDownNumberMap.get(stockCode) == null ? 0
				: preOneMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preOneMonthUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticStock.PRE_ONE_MONTH_UP_DOWN_NUM, preOneMonthUpDownNumber);
				put(StatisticStock.PRE_ONE_MONTH_UP_NUM, preOneMonthUpNumber);
				put(StatisticStock.PRE_ONE_MONTH_DOWN_NUM, preOneMonthDownNumber);
			}
		};
		String preOneMonthUpAndDownJson = JsonUtils.getJsonByMap(preOneMonthUpAndDownMap);
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
		final Integer preTwoMonthUpDownNumber = preTwoMonthUpDownNumberMap.get(stockCode) == null ? 0
				: preTwoMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preTwoMonthUpNumber = preTwoMonthUpNumberMap.get(stockCode) == null ? 0
				: preTwoMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preTwoMonthDownNumber = preTwoMonthDownNumberMap.get(stockCode) == null ? 0
				: preTwoMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preTwoMonthUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticStock.PRE_TWO_MONTH_UP_DOWN_NUM, preTwoMonthUpDownNumber);
				put(StatisticStock.PRE_TWO_MONTH_UP_NUM, preTwoMonthUpNumber);
				put(StatisticStock.PRE_TWO_MONTH_DOWN_NUM, preTwoMonthDownNumber);
			}
		};
		String preTwoMonthUpAndDownJson = JsonUtils.getJsonByMap(preTwoMonthUpAndDownMap);
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
		final Integer preThreeMonthUpDownNumber = preThreeMonthUpDownNumberMap.get(stockCode) == null ? 0
				: preThreeMonthUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preThreeMonthUpNumber = preThreeMonthUpNumberMap.get(stockCode) == null ? 0
				: preThreeMonthUpNumberMap.get(stockCode).getUpNumber();
		final Integer preThreeMonthDownNumber = preThreeMonthDownNumberMap.get(stockCode) == null ? 0
				: preThreeMonthDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preThreeMonthUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticStock.PRE_THREE_MONTH_UP_DOWN_NUM, preThreeMonthUpDownNumber);
				put(StatisticStock.PRE_THREE_MONTH_UP_NUM, preThreeMonthUpNumber);
				put(StatisticStock.PRE_THREE_MONTH_DOWN_NUM, preThreeMonthDownNumber);
			}
		};
		String preThreeMonthUpAndDownJson = JsonUtils.getJsonByMap(preThreeMonthUpAndDownMap);
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
		final Integer preOneWeekUpDownNumber = preOneWeekUpDownNumberMap.get(stockCode) == null ? 0
				: preOneWeekUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preOneWeekUpNumber = preOneWeekUpNumberMap.get(stockCode) == null ? 0 : preOneWeekUpNumberMap.get(stockCode).getUpNumber();
		final Integer preOneWeekDownNumber = preOneWeekDownNumberMap.get(stockCode) == null ? 0
				: preOneWeekDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preOneWeekUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticStock.PRE_ONE_WEEK_UP_DOWN_NUM, preOneWeekUpDownNumber);
				put(StatisticStock.PRE_ONE_WEEK_UP_NUM, preOneWeekUpNumber);
				put(StatisticStock.PRE_ONE_WEEK_DOWN_NUM, preOneWeekDownNumber);
			}
		};
		String preOneWeekUpAndDownJson = JsonUtils.getJsonByMap(preOneWeekUpAndDownMap);
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
		final Integer preOneYearUpDownNumber = preOneYearUpDownNumberMap.get(stockCode) == null ? 0
				: preOneYearUpDownNumberMap.get(stockCode).getUpDownNumber();
		final Integer preOneYearUpNumber = preOneYearUpNumberMap.get(stockCode) == null ? 0 : preOneYearUpNumberMap.get(stockCode).getUpNumber();
		final Integer preOneYearDownNumber = preOneYearDownNumberMap.get(stockCode) == null ? 0
				: preOneYearDownNumberMap.get(stockCode).getDownNumber();
		Map<String, Integer> preOneYearUpAndDownMap = new HashMap<String, Integer>() {
			{
				put(StatisticStock.PRE_ONE_YEAR_UP_DOWN_NUM, preOneYearUpDownNumber);
				put(StatisticStock.PRE_ONE_YEAR_UP_NUM, preOneYearUpNumber);
				put(StatisticStock.PRE_ONE_YEAR_DOWN_NUM, preOneYearDownNumber);
			}
		};
		String preOneYearUpAndDownJson = JsonUtils.getJsonByMap(preOneYearUpAndDownMap);
		return preOneYearUpAndDownJson;
	}

	/**
	 * 计算股票的涨跌幅
	 * 
	 */
	protected <T extends DetailStock> void calculateStockChangeRate(T stock) {

		Double yesterdayClose = stock.getYesterdayClose();
		if (!CommonUtils.isZeroOrNull(yesterdayClose)) {
			double current = stock.getCurrent().doubleValue();
			double changeRate = ((current - yesterdayClose) * 100) / yesterdayClose;
			BigDecimal bigValue = new BigDecimal(changeRate);
			double dValue = bigValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			stock.setChangeRate(dValue);
			String changeRateDES = DESUtils.encryptToHex(String.valueOf(dValue));
			stock.setChangeRateDES(changeRateDES);
		} else {
			stock.setChangeRate(DataUtils.CONSTANT_ZERO_DOT_ZERO);
			stock.setChangeRateDES(DataUtils.CONSTANT_BLANK);
		}
	}

	/**
	 * 根据流通股和成交量计算股票的换手率
	 *
	 */
	protected <T extends DetailStock> void calculateTurnoverRate(T stock) throws SQLException {

		String stockCode = stock.getStockCode();
		AllStock allStock = allStockDao.getAllStockByStockCode(stockCode);
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
			stock.setTurnoverRate(DataUtils.CONSTANT_ZERO_DOT_ZERO);
			stock.setTurnoverRateDES(DataUtils.CONSTANT_BLANK);
		}
	}

	/**
	 * 计算股票的涨跌次数
	 *
	 */
	protected void calculateUpDownNumber(StatisticStock statisticStock, String changeFlg) throws Exception {

		Integer upDownNumber = statisticStock.getUpDownNumber();
		statisticStock.setUpDownNumber(++upDownNumber); // 总涨跌次数
		if (DailyStock.CHANGE_FLG_ONE.equals(changeFlg)) {
			Integer upNumber = statisticStock.getUpNumber();
			statisticStock.setUpNumber(++upNumber); // 总涨次数
		} else {
			Integer downNumber = statisticStock.getDownNumber();
			statisticStock.setDownNumber(++downNumber); // 总跌次数
		}
		// 一周涨跌次数
		String oneWeekJson = statisticStock.getOneWeek();
		oneWeekJson = modifyJsonByChangeFlg(oneWeekJson, changeFlg, StatisticStock.PRE_ONE_WEEK_UP_DOWN_NUM);
		statisticStock.setOneWeek(oneWeekJson);
		// 半月涨跌次数
		String halfMonthJson = statisticStock.getHalfMonth();
		halfMonthJson = modifyJsonByChangeFlg(halfMonthJson, changeFlg, StatisticStock.PRE_HALF_MONTH_UP_DOWN_NUM);
		statisticStock.setHalfMonth(halfMonthJson);
		// 一月涨跌次数
		String oneMonthJson = statisticStock.getOneMonth();
		oneMonthJson = modifyJsonByChangeFlg(oneMonthJson, changeFlg, StatisticStock.PRE_ONE_MONTH_UP_DOWN_NUM);
		statisticStock.setOneMonth(oneMonthJson);
		// 二月涨跌次数
		String twoMonthJson = statisticStock.getTwoMonth();
		twoMonthJson = modifyJsonByChangeFlg(twoMonthJson, changeFlg, StatisticStock.PRE_TWO_MONTH_UP_DOWN_NUM);
		statisticStock.setTwoMonth(twoMonthJson);
		// 三月涨跌次数
		String threeMonthJson = statisticStock.getThreeMonth();
		threeMonthJson = modifyJsonByChangeFlg(threeMonthJson, changeFlg, StatisticStock.PRE_THREE_MONTH_UP_DOWN_NUM);
		statisticStock.setThreeMonth(threeMonthJson);
		// 半年涨跌次数
		String halfYearJson = statisticStock.getHalfYear();
		halfYearJson = modifyJsonByChangeFlg(halfYearJson, changeFlg, StatisticStock.PRE_HALF_YEAR_UP_DOWN_NUM);
		statisticStock.setHalfYear(halfYearJson);
		// 一年涨跌次数
		String oneYearJson = statisticStock.getOneYear();
		oneYearJson = modifyJsonByChangeFlg(oneYearJson, changeFlg, StatisticStock.PRE_ONE_YEAR_UP_DOWN_NUM);
		statisticStock.setOneYear(oneYearJson);
	}

	/**
	 * 涨跌次数自动增加1
	 *
	 */
	private String modifyJsonByChangeFlg(String numberJson, String changeFlg, String periodFlg) throws Exception {

		String upDownNumberKey = "";
		String upNumberKey = "";
		String downNumberKey = "";
		switch (periodFlg) {
		case StatisticStock.PRE_ONE_WEEK_UP_DOWN_NUM: // 一周涨跌次数
			upDownNumberKey = StatisticStock.PRE_ONE_WEEK_UP_DOWN_NUM;
			upNumberKey = StatisticStock.PRE_ONE_WEEK_UP_NUM;
			downNumberKey = StatisticStock.PRE_ONE_WEEK_DOWN_NUM;
			break;
		case StatisticStock.PRE_HALF_MONTH_UP_DOWN_NUM: // 半月涨跌次数
			upDownNumberKey = StatisticStock.PRE_HALF_MONTH_UP_DOWN_NUM;
			upNumberKey = StatisticStock.PRE_HALF_MONTH_UP_NUM;
			downNumberKey = StatisticStock.PRE_HALF_MONTH_DOWN_NUM;
			break;
		case StatisticStock.PRE_ONE_MONTH_UP_DOWN_NUM: // 一月涨跌次数
			upDownNumberKey = StatisticStock.PRE_ONE_MONTH_UP_DOWN_NUM;
			upNumberKey = StatisticStock.PRE_ONE_MONTH_UP_NUM;
			downNumberKey = StatisticStock.PRE_ONE_MONTH_DOWN_NUM;
			break;
		case StatisticStock.PRE_TWO_MONTH_UP_DOWN_NUM: // 二月涨跌次数
			upDownNumberKey = StatisticStock.PRE_TWO_MONTH_UP_DOWN_NUM;
			upNumberKey = StatisticStock.PRE_TWO_MONTH_UP_NUM;
			downNumberKey = StatisticStock.PRE_TWO_MONTH_DOWN_NUM;
			break;
		case StatisticStock.PRE_THREE_MONTH_UP_DOWN_NUM: // 三月涨跌次数
			upDownNumberKey = StatisticStock.PRE_THREE_MONTH_UP_DOWN_NUM;
			upNumberKey = StatisticStock.PRE_THREE_MONTH_UP_NUM;
			downNumberKey = StatisticStock.PRE_THREE_MONTH_DOWN_NUM;
			break;
		case StatisticStock.PRE_HALF_YEAR_UP_DOWN_NUM: // 半年涨跌次数
			upDownNumberKey = StatisticStock.PRE_HALF_YEAR_UP_DOWN_NUM;
			upNumberKey = StatisticStock.PRE_HALF_YEAR_UP_NUM;
			downNumberKey = StatisticStock.PRE_HALF_YEAR_DOWN_NUM;
			break;
		case StatisticStock.PRE_ONE_YEAR_UP_DOWN_NUM: // 一年涨跌次数
			upDownNumberKey = StatisticStock.PRE_ONE_YEAR_UP_DOWN_NUM;
			upNumberKey = StatisticStock.PRE_ONE_YEAR_UP_NUM;
			downNumberKey = StatisticStock.PRE_ONE_YEAR_DOWN_NUM;
			break;
		default:
			IOException ioException = new IOException("周期标识(periodFlg)不正确(应该为:0～6): " + periodFlg);
			throw ioException;
		}

		Map<String, Integer> jsonMap = JsonUtils.getMapByJson(numberJson);
		Integer upDownNumberValue = jsonMap.get(upDownNumberKey);
		jsonMap.put(upDownNumberKey, ++upDownNumberValue);
		if (DailyStock.CHANGE_FLG_ONE.equals(changeFlg)) {
			Integer upNumberValue = jsonMap.get(upNumberKey);
			jsonMap.put(upNumberKey, ++upNumberValue);
		} else {
			Integer downNumberValue = jsonMap.get(downNumberKey);
			jsonMap.put(downNumberKey, ++downNumberValue);
		}
		return JsonUtils.getJsonByMap(jsonMap);
	}

	/**
	 * 统计statistic_stock_表中股票的涨跌次数
	 *
	 */
	protected void statisticUpDownNumber(StatisticStock statistic) throws SQLException {

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

	/**
	 * 计算股票的流通股
	 * 
	 */
	public void setCirculationInAllStock(AllStock allStock) {

		String aliasCode = CommonUtils.getAliasCodeByStockCode(allStock.getStockCode());
		String stockCirculation = PropertiesUtils.getProperty(aliasCode);
		String[] circulationArray = stockCirculation.split(",");
		if (!circulationArray[0].equals("--") && !circulationArray[1].equals("--")) {
			Double nowPrice = Double.valueOf(circulationArray[0]);
			Long circulationValue = Long.valueOf(circulationArray[1]);
			BigDecimal complexCirculationStock = new BigDecimal(circulationValue.doubleValue() / nowPrice.doubleValue()).setScale(0,
					BigDecimal.ROUND_HALF_UP);
			allStock.setCirculationValue(circulationValue);
			allStock.setCirculationStockComplex(complexCirculationStock.longValue());
			allStock.setCirculationStockSimple(getCirculationStockSimple(complexCirculationStock));
		}
	}

	/**
	 * 验证网上获取的股票信息与实际股票信息是否一致(股票名称和开盘价)
	 *
	 */
	protected boolean validateStockData(DetailStock detailStock) {

		boolean validateFlg = true;
		String realStockName_first = PropertiesUtils.getProperty(detailStock.getStockCode()).substring(0, 1);
		String detailStockName_first = detailStock.getStockName().substring(0, 1);
		if (!realStockName_first.equals(detailStockName_first))
			validateFlg = false;
		Double todayOpen = detailStock.getTodayOpen();
		if (todayOpen > DataUtils.CONSTANT_TODAY_OPEN_LIMIT)
			validateFlg = false;
		return validateFlg;
	}

	/**
	 * 把流通股转换成Json数据(包括流通股和单位) 0.3321 亿 <1 (取4位整数) -->3321万 1.3653 亿 >=1 <10
	 * (取小数点2位)-->1.37亿 23.265 亿 >=10 <100 (取小数点1位)-->23.3亿 362.02 亿 >=100 <1000
	 * (取整) -->362亿 2235.1 亿 >1000 -->2235亿
	 */
	private String getCirculationStockSimple(BigDecimal complexCirculationStock) {

		String unit = AllStock.UNIT_HUNDRED_MILLION;
		Double reValue = 0.0;
		BigDecimal complexCirculationValue = complexCirculationStock.divide(DataUtils.CONSTANT_HUNDRED_MILLION);
		if (complexCirculationValue.compareTo(DataUtils.CONSTANT_ONE) < 0) {
			unit = AllStock.UNIT_TEN_THOUSAND;
			BigDecimal tempValue = complexCirculationStock.divide(DataUtils.CONSTANT_TEN_THOUSAND);
			reValue = getPreFourNumber(tempValue);
		} else if (complexCirculationValue.compareTo(DataUtils.CONSTANT_ONE) >= 0
				&& complexCirculationValue.compareTo(DataUtils.CONSTANT_TEN) < 0) {
			reValue = complexCirculationValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); // 取2位小数
		} else if (complexCirculationValue.compareTo(DataUtils.CONSTANT_TEN) >= 0
				&& complexCirculationValue.compareTo(DataUtils.CONSTANT_HUNDRED) < 0) {
			reValue = complexCirculationValue.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue(); // 取1位小数
		} else if (complexCirculationValue.compareTo(DataUtils.CONSTANT_HUNDRED) >= 0) {
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
	 * 3365.96 <10000 (取整) -->3366 55628.65 >=10000 (取前4位整数) -->5563
	 */
	private Double getPreFourNumber(BigDecimal value) {

		Double reValue = 0.0;
		if (value.compareTo(DataUtils.CONSTANT_TEN_THOUSAND) < 0) {
			reValue = value.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			Double doubleFive = Double.valueOf(value.toString().substring(0, 5));
			reValue = new BigDecimal(doubleFive / 10).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return reValue;
	}
}
