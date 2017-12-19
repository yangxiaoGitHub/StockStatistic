package cn.implement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.JsonUtils;
import cn.com.MD5Utils;
import cn.com.PropertiesUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllInformationStockDao;
import cn.db.AllStockDao;
import cn.db.DailyStockDao;
import cn.db.HistoryStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllInformationStock;
import cn.db.bean.AllStock;
import cn.db.bean.DailyStock;
import cn.db.bean.HistoryStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticStock;

public class OtherData extends OperationData {

	/**
	 * 对原始股票(original_stock_)数据进行MD5加密
	 * 
	 */
	public void handleOriginalMD5() {

		originalStockDao = new OriginalStockDao();
		int codesMD5 = 0;
		int ratesMD5 = 0;
		try {
			List<OriginalStock> originalList = originalStockDao.listOriginalData();
			for (OriginalStock stock : originalList) {
				Date stockDate = stock.getStockDate();
				String stockCodesMD5 = stock.getStockCodesMD5();
				String changeRatesMD5 = stock.getChangeRatesMD5();
				String stockCodesMD5_ = MD5Utils.getEncryptedPwd(stock.getStockCodes());
				String changeRatesMD5_ = MD5Utils.getEncryptedPwd(stock.getChangeRates());
				if (CommonUtils.isBlank(stockCodesMD5) && CommonUtils.isBlank(changeRatesMD5)) {
					originalStockDao.updateCodesRatesMD5(stockDate, stockCodesMD5_, changeRatesMD5_);
					codesMD5++;
					ratesMD5++;
				} else if (CommonUtils.isBlank(stock.getChangeRatesMD5())) {
					originalStockDao.updateChangeRatesMD5(stockDate, changeRatesMD5_);
					ratesMD5++;
				} else if (CommonUtils.isBlank(stock.getStockCodesMD5())) {
					originalStockDao.updateStockCodesMD5(stockDate, stockCodesMD5_);
					codesMD5++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(originalStockDao);
			System.out.println("原始股票数据表中更新了" + codesMD5 + "条stockCodeMD5记录和" + ratesMD5 + "条changeRatesMD5记录！");
			log.loger.info("原始股票数据表中更新了" + codesMD5 + "条stockCodeMD5记录和" + ratesMD5 + "条changeRatesMD5记录！");
		}
	}

	/**
	 * 统计每日股票数据到statistic_stock_表中
	 * 
	 */
	public void statisticDailyStock() {

		dailyStockDao = new DailyStockDao();
		statisticStockDao = new StatisticStockDao();
		int number = 0;
		List<StatisticStock> saveList = new ArrayList<StatisticStock>();
		try {
			List<DailyStock> dailyStockList = dailyStockDao.statisticDailyStock();
			Long maxNum = statisticStockDao.getMaxNumFromStatisticStock();
			for (DailyStock dailyStock : dailyStockList) {
				String stockCode = dailyStock.getStockCode();
				boolean existFlg = statisticStockDao.isExistInStatisticStock(stockCode);
				if (!existFlg) {
					Integer upDownNumber = dailyStock.getCount();
					if (upDownNumber > 1) {
						System.out.println(DateUtils.dateTimeToString(new Date()) + " 每日股票信息表(daily_stock_)中股票统计次数为:" + dailyStock.getCount()
								+ "的股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")没有增加到统计股票信息表(statistic_stock_)中！");
						continue;
					}
					if (!existFlg) {
						Date firstStockDate = dailyStock.getStockDate();
						String stockCodeDES = DESUtils.encryptToHex(stockCode);
						StatisticStock statisticStock = new StatisticStock(stockCode, firstStockDate, stockCodeDES);
						statisticStock.setNum(++maxNum);
						// 初始化涨跌次数
						initializeStatisticStock(statisticStock);
						DailyStock newDailyStock = dailyStockDao.getDailyStockByKey(dailyStock.getRecentStockDate(), stockCode);
						// 计算股票的涨跌次数
						calculateUpDownNumber(statisticStock, newDailyStock.getChangeFlg());
						statisticStock.setNote(DataUtils.CONSTANT_BLANK);
						statisticStock.setInputTime(new Date());
						boolean saveFlg = statisticStockDao.saveStatisticStock(statisticStock);
						if (saveFlg) {
							saveList.add(statisticStock);
							++number;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(ex);
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			String strStock = getStocks(saveList);
			System.out.println("统计股票信息表(statistic_stock_)中增加了" + number + "条记录：" + strStock);
			log.loger.info("统计股票信息表(statistic_stock_)中增加了" + number + "条记录：" + strStock);
		}
	}

	private void initializeStatisticStock(StatisticStock statisticStock) {
		//初始化总涨跌次数
		statisticStock.setUpDownNumber(DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setUpNumber(DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setDownNumber(DataUtils.CONSTANT_INTEGER_ZERO);
		//初始化一周涨跌次数
		Map<String, Integer> jsonMap = new HashMap<String, Integer>();
		jsonMap.put(StatisticStock.PRE_ONE_WEEK_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_WEEK_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_WEEK_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setOneWeek(JsonUtils.getJsonByMap(jsonMap));
		//初始化半月涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_HALF_MONTH_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_HALF_MONTH_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_HALF_MONTH_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setHalfMonth(JsonUtils.getJsonByMap(jsonMap));
		//初始化一月涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_ONE_MONTH_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_MONTH_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_MONTH_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setOneMonth(JsonUtils.getJsonByMap(jsonMap));
		//初始化二月涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_TWO_MONTH_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_TWO_MONTH_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_TWO_MONTH_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setTwoMonth(JsonUtils.getJsonByMap(jsonMap));
		//初始化三月涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_THREE_MONTH_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_THREE_MONTH_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_THREE_MONTH_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setThreeMonth(JsonUtils.getJsonByMap(jsonMap));
		//初始化半年涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_HALF_YEAR_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_HALF_YEAR_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_HALF_YEAR_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setHalfYear(JsonUtils.getJsonByMap(jsonMap));
		//初始化一年涨跌次数
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_ONE_YEAR_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_YEAR_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_YEAR_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setOneYear(JsonUtils.getJsonByMap(jsonMap));
	}

	private String getStocks(List<StatisticStock> saveList) {

		if (saveList == null || saveList.size() == 0)
			return DataUtils.CONSTANT_BLANK;
		String result = "";
		for (StatisticStock stock : saveList) {
			result += "," + stock.getStockCode() + "(" + PropertiesUtils.getProperty(stock.getStockCode()) + ")";
		}
		return result.substring(1, result.length());
	}

	/**
	 * 增加或更新所有股票数据到all_stock_表中
	 * 
	 */
	public void handleAllStock() {
		
		int saveNum = 0;
		int updateNum = 0;
		try {
			allStockDao = new AllStockDao(); //AllStockDao.getInstance();
			Long maxNum = allStockDao.getMaxNumFromAllStock();
			Properties properties = PropertiesUtils.getStockNameProperties();
			Iterator<Entry<Object, Object>> itr = properties.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<Object, Object> entry = itr.next();
				String stockCode = entry.getKey().toString();
				String stockName = entry.getValue().toString();
				AllStock stock = new AllStock(stockCode, stockName);
				stock.setNum(++maxNum);
				stock.setInputTime(new Date());
				// 计算股票的流通股
				this.setCirculationInAllStock(stock);
				AllStock allStock = allStockDao.getAllStockByStockCode(stockCode);
				if (allStock == null) {
					boolean saveFlg = allStockDao.saveAllStock(stock);
					if (saveFlg) {
						++saveNum;
						System.out.println(DateUtils.dateTimeToString(new Date()) + " 所有股票表(all_stock_)中增加了股票" + stock.getStockCode() + "("
								+ stockName + ")");
					}
				} else {
					if (!stockName.equals(allStock.getStockName())) {
						boolean updateFlg = allStockDao.updateAllStockName(stock);
						if (updateFlg) {
							++updateNum;
							System.out.println(DateUtils.dateTimeToString(new Date()) + " 所有股票表(all_stock_)中更新了股票" + stock.getStockCode() + "("
									+ stockName + ")");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + " 所有股票表(all_stock_)中增加了" + saveNum + "条记录！");
			log.loger.info("所有股票表(all_stock_)中增加了" + saveNum + "条记录！");
			System.out.println(DateUtils.dateTimeToString(new Date()) + " 所有股票表(all_stock_)中更新了" + updateNum + "条记录！");
			log.loger.info("所有股票表(all_stock_)中更新了" + updateNum + "条记录！");
		}
	}

	/**
	 * 更新所有股票信息表(all_information_stock_)中的num_字段
	 * 
	 */
	public void updateNumOfAllInformationStock() {

		long num = 1;
		allInformationStockDao = new AllInformationStockDao();
		try {
			Date startDate = DateUtils.stringToDate("2017-11-06");
			Date nowTime = new Date();
			while (startDate.compareTo(nowTime) < 0) {
				List<AllInformationStock> allInformationStockList = allInformationStockDao.getAllInformationStockByStockDate(startDate);
				// 按顺序更新所有股票信息表(all_information_stock_)中的num_字段
				Properties properties = PropertiesUtils.getStockNameProperties();
				Iterator<Entry<Object, Object>> itr = properties.entrySet().iterator();
				while (itr.hasNext()) {
					Entry<Object, Object> entry = itr.next();
					String stockCode = entry.getKey().toString();
					AllInformationStock allInformationStock = getAllInformationStock(stockCode, allInformationStockList);
					if (allInformationStock != null) {
						boolean updateFlg = allInformationStockDao.updateNum(allInformationStock, num);
						if (updateFlg)
							num++;
					}
				}
				startDate = DateUtils.addOneDay(startDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allInformationStockDao);
			System.out.println("所有股票详细信息表(all_detail_stock_)批量更新了" + num + "条记录的num_字段！");
			log.loger.info("所有股票详细信息表(all_detail_stock_)批量增加了" + num + "条记录的num_字段！");
		}
	}

	private AllInformationStock getAllInformationStock(String stockCode, List<AllInformationStock> allInformationStockList) {

		AllInformationStock stock = null;
		for (AllInformationStock allInformationStock : allInformationStockList) {
			if (allInformationStock.getStockCode().equals(stockCode)) {
				stock = allInformationStock;
				break;
			}
		}
		return stock;
	}

	/**
	 * 统计每日股票数据的涨跌次数到statistic_stock_表中(一般不使用)
	 * 
	 */
	public void statisticUpAndDownToStatisticStock() {

		dailyStockDao = new DailyStockDao();
		statisticStockDao = new StatisticStockDao();
		int upDownNum = 0;
		int upNum = 0;
		int downNum = 0;
		int jsonNum = 0;
		try {
			// 更新字段up_down_number_
			Map<String, StatisticStock> dailyStockUpDownMap = dailyStockDao.statisticUpDownInDailyStock();
			Set<Entry<String, StatisticStock>> upDownEntries = dailyStockUpDownMap.entrySet();
			Iterator<Entry<String, StatisticStock>> upDownIterator = upDownEntries.iterator();
			while (upDownIterator.hasNext()) {
				Entry<String, StatisticStock> entry = upDownIterator.next();
				String stockCode = entry.getKey();
				StatisticStock statisticStock = entry.getValue();
				boolean updateFlg = statisticStockDao.updateUpDownNumber(stockCode, statisticStock);
				if (updateFlg) {
					upDownNum++;
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + upDownNum + "--->统计股票信息表更新了股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")的up_down_number_字段！");
				}
			}
			// 更新字段up_number_
			Map<String, StatisticStock> dailyStockUpMap = dailyStockDao.statisticUpInDailyStock();
			Set<Entry<String, StatisticStock>> upEntries = dailyStockUpMap.entrySet();
			Iterator<Entry<String, StatisticStock>> upIterator = upEntries.iterator();
			while (upIterator.hasNext()) {
				Entry<String, StatisticStock> entry = upIterator.next();
				String stockCode = entry.getKey();
				StatisticStock statisticStock = entry.getValue();
				boolean updateFlg = statisticStockDao.updateUpNumber(stockCode, statisticStock);
				if (updateFlg) {
					upNum++;
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + upNum + "--->统计股票信息表更新了股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")的up_number_字段！");
				}
			}
			// 更新字段down_number_
			Map<String, StatisticStock> dailyStockDownMap = dailyStockDao.statisticDownInDailyStock();
			Set<Entry<String, StatisticStock>> downEntries = dailyStockDownMap.entrySet();
			Iterator<Entry<String, StatisticStock>> downIterator = downEntries.iterator();
			while (downIterator.hasNext()) {
				Entry<String, StatisticStock> entry = downIterator.next();
				String stockCode = entry.getKey();
				StatisticStock statisticStock = entry.getValue();
				boolean downdateFlg = statisticStockDao.updateDownNumber(stockCode, statisticStock);
				if (downdateFlg) {
					downNum++;
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + downNum + "--->统计股票信息表更新了股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")的down_number_字段！");
				}
			}
			// 更新字段one_week_,half_month_,one_month_,two_month_,three_month_,half_year_,one_year_
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			for (StatisticStock statisticStock : statisticStockList) {
				String stockCode = statisticStock.getStockCode();
				StatisticStock statisticStock_ = new StatisticStock(stockCode);
				// 前一周的涨跌次数
				String preOneWeekUpAndDownJson = getPreOneWeekJson(stockCode);
				statisticStock_.setOneWeek(preOneWeekUpAndDownJson);
				// 前半月的涨跌次数
				String preHalfMonthUpAndDownJson = getPreHalfMonthJson(stockCode);
				statisticStock_.setHalfMonth(preHalfMonthUpAndDownJson);
				// 前一月的涨跌次数
				String preOneMonthUpAndDownJson = getPreOneMonthJson(stockCode);
				statisticStock_.setOneMonth(preOneMonthUpAndDownJson);
				// 前二月的涨跌次数
				String preTwoMonthUpAndDownJson = getPreTwoMonthJson(stockCode);
				statisticStock_.setTwoMonth(preTwoMonthUpAndDownJson);
				// 前三月的涨跌次数
				String preThreeMonthUpAndDownJson = getPreThreeMonthJson(stockCode);
				statisticStock_.setThreeMonth(preThreeMonthUpAndDownJson);
				// 前半年的涨跌次数
				String preHalfYearUpAndDownJson = getPreHalfYearJson(stockCode);
				statisticStock_.setHalfYear(preHalfYearUpAndDownJson);
				// 前一年的涨跌次数
				String preOneYearUpAndDownJson = getPreOneYearJson(stockCode);
				statisticStock_.setOneYear(preOneYearUpAndDownJson);
				statisticStock_.setInputTime(new Date());
				boolean updateFlg = statisticStockDao.updateAllJsonRow(statisticStock_);
				if (updateFlg) {
					jsonNum++;
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + jsonNum + "--->统计股票信息表(statistic_stock_)更新了股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")的json字段！");
					log.loger.info(" " + jsonNum + "--->统计股票信息表(statistic_stock_)更新了股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")的json字段！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			System.out.println("统计股票信息表(statistic_stock_)中更新了" + upDownNum + "条记录的字段(up_down_number_), " + upNum + "条记录的字段(up_number_), " + downNum
					+ "条记录的字段(down_number_), " + jsonNum + "条记录的Json字段！");
			log.loger.info("统计股票信息表(statistic_stock_)中更新了" + upDownNum + "条记录的字段(up_down_number_), " + upNum + "条记录的字段(up_number_), " + downNum
					+ "条记录的字段(down_number_), " + jsonNum + "条记录的Json字段！");
		}
	}

	/**
	 * 计算所有股票(all_stock_)的流通股
	 * 
	 */
	public void handleAllCirculationStock(String handleFlg) {

		int updateNum = 0;
		try {
			allStockDao = new AllStockDao(); // AllStockDao.getInstance();
			Properties properties = PropertiesUtils.getNowPriceAndCirculValueProperties();
			Iterator<Entry<Object, Object>> itrator = properties.entrySet().iterator();
			while (itrator.hasNext()) {
				Entry<Object, Object> entry = itrator.next();
				String stockAliasCode = entry.getKey().toString();
				String stockCode = CommonUtils.getStockCodeByAliasCode(stockAliasCode);
				AllStock newAllStock = new AllStock(stockCode, null);
				newAllStock.setInputTime(new Date());
				// 计算股票的流通股
				this.setCirculationInAllStock(newAllStock);

				/*
				 * String[] circulationArray = stockCirculation.split(","); if
				 * (circulationArray[0].equals("--") ||
				 * circulationArray[1].equals("--")) continue; Double nowPrice =
				 * Double.valueOf(circulationArray[0]); Long circulationValue =
				 * Long.valueOf(circulationArray[1]); BigDecimal
				 * complexCirculationStock = new
				 * BigDecimal(circulationValue.doubleValue()/nowPrice.
				 * doubleValue()).setScale(0, BigDecimal.ROUND_HALF_UP);
				 * newAllStock.setCirculationValue(circulationValue);
				 * newAllStock.setCirculationStockComplex(
				 * complexCirculationStock.longValue());
				 * newAllStock.setCirculationStockSimple(
				 * getCirculationStockSimple(complexCirculationStock));
				 */
				AllStock oldAllStock = allStockDao.getAllStockByStockCode(stockCode);
				if (oldAllStock != null) {
					boolean updateFlg = false;
					String messages = "";
					int equalFlg = isEqualAllStock(oldAllStock, newAllStock); // 0:都不等,
																				// 1:简单流通股相等,其它不等,
																				// 2:都相等
					if (equalFlg == 0) { // 提示更新
						if (handleFlg.equals("1")) { // 更新
							updateFlg = allStockDao.updateAllStockCirculation(newAllStock);
							messages = "流通值和流通股都不相等时，";
						} else {
							System.out.println("---------------------------股票" + newAllStock.getStockCode() + "("
									+ PropertiesUtils.getProperty(newAllStock.getStockCode()) + ") 流通股和流通值都不相等: ");
							log.loger.info("---------------------------股票" + newAllStock.getStockCode() + "("
									+ PropertiesUtils.getProperty(newAllStock.getStockCode()) + ") 流通股和流通值都不相等: ");
							System.out.println("---------旧流通值(circulation_value_):" + oldAllStock.getCirculationValue()
									+ " 旧流通股(circulation_stock_complex_):" + oldAllStock.getCirculationStockComplex()
									+ " 旧流通股(简)(circulation_stock_simple_):" + oldAllStock.getCirculationStockSimple_NoJson());
							log.loger.info("---------旧流通值(circulation_value_):" + oldAllStock.getCirculationValue()
									+ " 旧流通股(circulation_stock_complex_):" + oldAllStock.getCirculationStockComplex()
									+ " 旧流通股(简)(circulation_stock_simple_):" + oldAllStock.getCirculationStockSimple_NoJson());
							System.out.println("---------新流通值(circulation_value_):" + newAllStock.getCirculationValue()
									+ " 新流通股(circulation_stock_complex_):" + newAllStock.getCirculationStockComplex()
									+ " 新流通股(简)(circulation_stock_simple_):" + newAllStock.getCirculationStockSimple_NoJson());
							log.loger.info("---------新流通值(circulation_value_):" + newAllStock.getCirculationValue()
									+ " 新流通股(circulation_stock_complex_):" + newAllStock.getCirculationStockComplex()
									+ " 新流通股(简)(circulation_stock_simple_):" + newAllStock.getCirculationStockSimple_NoJson());
						}
					} else if (equalFlg == 1) { // 更新
						updateFlg = allStockDao.updateAllStockCirculation(newAllStock);
						messages = "流通股(简)相等时，";
					}
					if (updateFlg) {
						++updateNum;
						System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + updateNum + "---" + messages + "所有股票表(all_stock_)更新了股票"
								+ newAllStock.getStockCode() + "(" + PropertiesUtils.getProperty(newAllStock.getStockCode()) + ")的流通股和流通值字段");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + " 所有股票表(all_stock_)中更新了" + updateNum + "条流通股和流通市值记录！");
			log.loger.info(" 所有股票表(all_stock_)中更新了" + updateNum + "条流通股和流通市值记录！");
		}
	}

	/**
	 * 导入下载的股票数据到股票历史数据表(history_stock_)中
	 * 
	 */
	public void handleDownloadDailyData() {

		long saveNum = 0;
		historyStockDao = new HistoryStockDao();
		try {
			Long maxNum = historyStockDao.getMaxNumFromHistoryStock();
			Properties properties = PropertiesUtils.getStockNameProperties();
			Enumeration<? extends Object> keyEnumeration = properties.propertyNames();
			while (keyEnumeration.hasMoreElements()) {
				String stockCode = keyEnumeration.nextElement().toString();
				Date recentDateInHistoryStock = getMaxStockDateByStockCodeInHistoryStock(stockCode);
				if (fileExistInHistoryStock(recentDateInHistoryStock))
					continue;
				String fileName = stockCode + ".txt";
				final String filePath = "E:\\txd\\" + fileName;
				String encoding = "GBK";
				try {
					File file = new File(filePath);
					// 判断文件是否存在
					if (file.isFile() && file.exists()) {
						InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
						BufferedReader bufferedReader = new BufferedReader(read);
						String lineTxt = null;
						while ((lineTxt = bufferedReader.readLine()) != null) {
							String[] lineArray = getLineArrayFromLineTxt(lineTxt);
							if (lineArray.length > 0) {
								if (!CommonUtils.isValidDate(lineArray[0]) || !CommonUtils.isLegalDate(lineArray[0], recentDateInHistoryStock))
									continue;
								boolean saveFlg = saveHistoryStock(stockCode, lineArray, maxNum);
								if (saveFlg) {
									saveNum++;
									System.out.println(saveNum + "---" + DateUtils.dateTimeToString(new Date()) + " 历史股票信息表(history_stock_)增加了"
											+ lineArray[0] + "的股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")");
								}
							}
						}
						read.close();
					} else {
						System.out.println(DateUtils.dateTimeToString(new Date()) + " 读取文件时找不到指定的文件：" + filePath);
						log.loger.info(" 读取文件时找不到指定的文件：" + filePath);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					log.loger.error(ex);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(historyStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + " 历史股票信息表(history_stock_)增加了" + saveNum + "条记录！");
			log.loger.info(" 历史股票信息表(history_stock_)增加了" + saveNum + "条记录！");
		}
	}

	/**
	 * 填补每日股票信息表(daily_stock_)中的涨跌幅(change_rate_)和换手率(turnover_rate_)
	 * 
	 */
	public void handleChangeRateAndTurnoverRateInDailyStock() {

		int updateNum = 0;
		try {
			dailyStockDao = new DailyStockDao();
			allDetailStockDao = new AllDetailStockDao(); // AllDetailStockDao.getInstance();
			List<DailyStock> dailyStockList = dailyStockDao.listDailyStockByChangeRateOrTurnoverRate();
			for (DailyStock dailyStock : dailyStockList) {
				String stockCode = dailyStock.getStockCode();
				Date stockDate = dailyStock.getStockDate();
				int fieldFlg = 0; // 0:changeRate和turnoverRate, 1:changeRate,
									// 2:turnoverRate
				AllDetailStock allDetailStock = allDetailStockDao.getAllDetailStockByKey(stockDate, stockCode);
				if (null == allDetailStock)
					continue;
				if (CommonUtils.isZeroOrNull(dailyStock.getChangeRate()) || CommonUtils.isMinMaxValue(dailyStock.getChangeRate())) {
					Double changeRateInAllDetailStock = allDetailStock.getChangeRate();
					dailyStock.setChangeRate(changeRateInAllDetailStock);
					String changeRateDES = DESUtils.encryptToHex(changeRateInAllDetailStock.toString());
					dailyStock.setEncryptChangeRate(changeRateDES);
					fieldFlg = 1;
				}
				if (CommonUtils.isZeroOrNull(dailyStock.getTurnoverRate())) {
					Double turnoverRateInAllDetailStock = allDetailStock.getTurnoverRate();
					dailyStock.setTurnoverRate(turnoverRateInAllDetailStock);
					String turnoverRateDES = DESUtils.encryptToHex(turnoverRateInAllDetailStock.toString());
					dailyStock.setEncryptTurnoverRate(turnoverRateDES);
					fieldFlg = 2;
				}
				boolean updateFlg = dailyStockDao.updateChangeRateAndTurnoverRate(dailyStock, fieldFlg);
				if (updateFlg) {
					++updateNum;
					String sField = (fieldFlg == 0 ? "change_rate_字段和turnover_rate_字段" : (fieldFlg == 1 ? "change_rate_字段" : "turnover_rate_字段"));
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + updateNum + "---每日股票信息表(daily_stock_)更新了日期为："
							+ DateUtils.dateToString(stockDate) + "，股票代码为：" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")的"
							+ sField);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao, allDetailStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + " 每日股票信息表(daily_stock_)更新了" + updateNum + "条涨跌幅和换手率记录！");
			log.loger.info(" 每日股票信息表(daily_stock_)更新了" + updateNum + "条涨跌幅和换手率记录！");
		}
	}

	private boolean saveHistoryStock(String stockCode, String[] lineArray, Long lineNum) throws SQLException {

		boolean saveFlg = false;
		// Date stockDate = DateUtils.String2Date(lineArray[0]);
		// if (!historyStockDao.isExistInHistoryStock(stockCode, stockDate)) {
		HistoryStock historyStock = getHistoryStockByArray(stockCode, lineArray);
		// Long maxNum = historyStockDao.getMaxNumFromHistoryStock();
		historyStock.setNum(++lineNum);
		saveFlg = historyStockDao.saveHistoryStock(historyStock);
		// }
		return saveFlg;
	}

	private HistoryStock getHistoryStockByArray(String stockCode, String[] lineArray) {

		Date stockDate = DateUtils.stringToDate(lineArray[0]);
		Double openPrice = Double.parseDouble(lineArray[1]);
		Double highPrice = Double.parseDouble(lineArray[2]);
		Double lowPrice = Double.parseDouble(lineArray[3]);
		Double closePrice = Double.parseDouble(lineArray[4]);
		Long tradedStockNum = Long.parseLong(lineArray[5]);
		Float tradedAmount = Float.parseFloat(lineArray[6]);
		HistoryStock historyStock = new HistoryStock(stockCode, stockDate);
		historyStock.setOpenPrice(openPrice);
		historyStock.setHighPrice(highPrice);
		historyStock.setLowPrice(lowPrice);
		historyStock.setClosePrice(closePrice);
		historyStock.setTradedStockNumber(tradedStockNum);
		historyStock.setTradedAmount(tradedAmount);
		historyStock.setInputTime(new Date());
		return historyStock;
	}

	private int isEqualAllStock(AllStock oldAllStock, AllStock newAllStock) {

		int flg = 1; // 0:都不等, 1:简单流通股相等,其它不等, 2:都相等
		Long oldCirculationValue = oldAllStock.getCirculationValue();
		Long oldCirculationStockComplex = oldAllStock.getCirculationStockComplex();
		String oldCirculationStockSimple = oldAllStock.getCirculationStockSimple_NoJson();
		Long newCirculationValue = newAllStock.getCirculationValue();
		Long newCirculationStockComplex = newAllStock.getCirculationStockComplex();
		String newCirculationStockSimple = newAllStock.getCirculationStockSimple_NoJson();

		if (oldCirculationValue.compareTo(newCirculationValue) == 0 && oldCirculationStockComplex.compareTo(newCirculationStockComplex) == 0
				&& newCirculationStockSimple.equals(oldCirculationStockSimple)) {
			flg = 2;
		} else if (oldCirculationValue.compareTo(newCirculationValue) == 0 && oldCirculationStockComplex.compareTo(newCirculationStockComplex) == 0
				&& newCirculationStockSimple.equals(oldCirculationStockSimple)) {
			flg = 1;
		} else if (oldCirculationValue.compareTo(newCirculationValue) == 0 && oldCirculationStockComplex.compareTo(newCirculationStockComplex) == 0
				&& !newCirculationStockSimple.equals(oldCirculationStockSimple)) {
			flg = 0;
		}
		return flg;
	}

	private boolean fileExistInHistoryStock(Date recentDateInHistoryStock) throws SQLException {

		Date recentDate = DateUtils.stringToDate("2017-12-08");
		if (CommonUtils.compareDate(recentDateInHistoryStock, recentDate))
			return true;
		else
			return false;
	}

	private Date getMaxStockDateByStockCodeInHistoryStock(String stockCode) throws SQLException {

		Date[] minMaxDate = historyStockDao.getMinMaxDate(stockCode);
		return minMaxDate[1];
	}

	private String[] getLineArrayFromLineTxt(String lineTxt) {

		List<String> lineList = new ArrayList<String>();
		String[] lineArray = lineTxt.split("\t");
		for (String value : lineArray) {
			if (!CommonUtils.isBlank(value))
				lineList.add(value.trim());
		}
		return lineList.toArray(new String[lineList.size()]);
	}
}
