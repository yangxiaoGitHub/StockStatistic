package cn.implement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.db.AllImportStockDao;
import cn.db.DailyStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllImportStock;
import cn.db.bean.BaseStock;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticStock;
import cn.db.bean.ext.ExtDailyStock;

public class AnalysisTurnoverRateData extends OperationData {

	/**
	 * 统计分析一段时间内股票平均换手率和其分布情况
	 *
	 */
	public void analysisTurnoverRate(String[] startEndDate, String changeFlg) {

		dailyStockDao = new DailyStockDao();
		statisticStockDao = new StatisticStockDao();
		allImportStockDao = new AllImportStockDao();
		try {
			switch(Integer.valueOf(changeFlg)) {
			case DataUtils._INT_ZERO:
				analysisDailyStockTurnoverRate(startEndDate);
				break;
			case DataUtils._INT_ONE:
				analysisAllSelectedStockTurnoverRate(startEndDate);
				break;
			case DataUtils._INT_TWO:
				analysisAllStockTurnoverRate(startEndDate);
				break;
			default: 
				System.out.println("统计标识值：" + changeFlg + " 不正确！");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, statisticStockDao, allImportStockDao);
		}
	}
    
	/**
	 * 统计一段时间内每日选择股票(daily_stock_)的换手率
	 *
	 */
	private void analysisDailyStockTurnoverRate(String[] startEndDate) throws SQLException, InstantiationException, IllegalAccessException {

		Date startDate = DateUtils.stringToDate(startEndDate[0]);
		Date endDate = DateUtils.stringToDate(startEndDate[1]);
		List<DailyStock> dailyStockList = dailyStockDao.queryStockByTime(startDate, endDate);
		Map<String, DailyStock> averageStockMap = getAverageTurnoverRate(dailyStockList, DailyStock.class);
		List<DailyStock> averageStockList = new ArrayList<DailyStock>(averageStockMap.values());
		Map<String, List<String>> turnoverMap = new HashMap<String, List<String>>();
		if (averageStockList.size() > 0) {
			System.out.println("------------------------" + startEndDate[0] + "至" + startEndDate[1] + " 每日选择股票平均换手率统计------------------------");
			log.loger.warn("------------------------" + startEndDate[0] + "至" + startEndDate[1] + " 每日选择股票平均换手率统计------------------------");
			long count = 0;
			for (DailyStock dailyStock : averageStockList) {
				String stockCode = dailyStock.getStockCode();
				Double turnoverRate = dailyStock.getTurnoverRate();
				calculateAllSelectedStockTurnoverRate(stockCode, turnoverRate, turnoverMap);
				count++;
			}
			System.out.println(">>>>>>>>>统计每日选择股票(daily_stock_)的换手率方法(calculateAllSelectedStockTurnoverRate)执行次数：" + count);
			printTurnoverMap(turnoverMap);
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->此时间段(" + startEndDate[0] + "至" + startEndDate[1] + ")，表(daily_stock_)没有每日股票数据！");
			log.loger.warn("------>此时间段(" + startEndDate[0] + "至" + startEndDate[1] + ")，表(daily_stock_)没有每日股票数据！");
		}
	}

	/**
	 * 统计一段时间内所有选择股票(statistic_stock_)的换手率
	 *
	 */
	private void analysisAllSelectedStockTurnoverRate(String[] startEndDate) throws SQLException, InstantiationException, IllegalAccessException {

		Date startDate = DateUtils.stringToDate(startEndDate[0]);
		Date endDate = DateUtils.stringToDate(startEndDate[1]);
		List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
		String stockCodes = getStockCodesFromList(statisticStockList);
		List<AllImportStock> allImportStockList = allImportStockDao.getAllImportStockByCodesAndDate(stockCodes, new Date[]{startDate, endDate});
		Map<String, AllImportStock> averageStockMap = getAverageTurnoverRate(allImportStockList, AllImportStock.class);
		List<AllImportStock> averageStockList = new ArrayList<AllImportStock>(averageStockMap.values());
		Map<String, List<String>> turnoverMap = new HashMap<String, List<String>>();

		if (averageStockList.size() > 0) {
			System.out.println("----------------------" + startEndDate[0] + "至" + startEndDate[1] + " 所有选择股票平均换手率统计-----------------------");
			log.loger.warn("----------------------" + startEndDate[0] + "至" + startEndDate[1] + " 所有选择股票平均换手率统计-----------------------");
			long count = 0;
			for (AllImportStock allImportStock : averageStockList) {
				String stockCode = allImportStock.getStockCode();
				Double turnoverRate = allImportStock.getTurnoverRate();
				calculateAllSelectedStockTurnoverRate(stockCode, turnoverRate, turnoverMap);
				count++;
			}
			System.out.println(">>>>>>>>>统计所有选择股票(statistic_stock_)的换手率方法(calculateAllSelectedStockTurnoverRate)执行次数：" + count);
			printTurnoverMap(turnoverMap);
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->此时间段(" + startEndDate[0] + "至" + startEndDate[1] + ")，表(all_import_stock_)没有每日股票数据！");
			log.loger.warn("------>此时间段(" + startEndDate[0] + "至" + startEndDate[1] + ")，表(all_import_stock_)没有每日股票数据！");
		}
	}

	/**
	 * 统计一段时间内所有导入股票(all_import_stock_)的换手率
	 *
	 */
	private void analysisAllStockTurnoverRate(String[] startEndDate) throws SQLException, InstantiationException, IllegalAccessException {

		Date startDate = DateUtils.stringToDate(startEndDate[0]);
		Date endDate = DateUtils.stringToDate(startEndDate[1]);
		List<AllImportStock> allImportStockList = allImportStockDao.getAllImportStockByStockDate(new Date[]{startDate, endDate});
		Map<String, AllImportStock> averageStockMap = getAverageTurnoverRate(allImportStockList, AllImportStock.class);
		List<AllImportStock> averageStockList = new ArrayList<AllImportStock>(averageStockMap.values());
		Map<String, List<String>> turnoverMap = new HashMap<String, List<String>>();
		if (averageStockList.size() > 0) {
			System.out.println("---------------------" + startEndDate[0] + "至" + startEndDate[1] + " 所有导入股票平均换手率统计----------------------");
			log.loger.warn("---------------------" + startEndDate[0] + "至" + startEndDate[1] + " 所有导入股票平均换手率统计----------------------");
			long count = 0;
			for (AllImportStock allImportStock : averageStockList) {
				String stockCode = allImportStock.getStockCode();
				Double turnoverRate = allImportStock.getTurnoverRate();
				calculateAllSelectedStockTurnoverRate(stockCode, turnoverRate, turnoverMap);
				count++;
			}
			System.out.println(">>>>>>>>>统计所有导入股票(all_import_stock_)的换手率方法(calculateAllSelectedStockTurnoverRate)的执行次数：" + count);
			printTurnoverMap(turnoverMap);
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->此时间段(" + startEndDate[0] + "至" + startEndDate[1] + ")，表(all_import_stock_)没有导入股票数据！");
			log.loger.warn("------>此时间段(" + startEndDate[0] + "至" + startEndDate[1] + ")，表(all_import_stock_)没有导入股票数据！");
		}
	}

	private Map<String, List<String>> calculateAllSelectedStockTurnoverRate(String stockCode, Double turnoverRate, Map<String, List<String>> turnoverRateMap) {

		String turnoverFlg = CommonUtils.getTurnoverFlg(turnoverRate);
		switch(turnoverFlg) {
			case ExtDailyStock.LESS_ONE:
				if (turnoverRateMap.containsKey(ExtDailyStock.LESS_ONE)) {
					List<String> stockCodeList = turnoverRateMap.get(ExtDailyStock.LESS_ONE);
					List<String> codeList = new ArrayList<String>(stockCodeList);
					codeList.add(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.LESS_ONE, codeList);
				} else {
					List<String> stockCodeList = Arrays.asList(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.LESS_ONE, stockCodeList);
				}
				break;
			case ExtDailyStock.ONE_TO_TWO:
				if (turnoverRateMap.containsKey(ExtDailyStock.ONE_TO_TWO)) {
					List<String> stockCodeList = turnoverRateMap.get(ExtDailyStock.ONE_TO_TWO);
					List<String> codeList = new ArrayList<String>(stockCodeList);
					codeList.add(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.ONE_TO_TWO, codeList);
				} else {
					List<String> stockCodeList = Arrays.asList(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.ONE_TO_TWO, stockCodeList);
				}
				break;
			case ExtDailyStock.TWO_TO_THREE:
				if (turnoverRateMap.containsKey(ExtDailyStock.TWO_TO_THREE)) {
					List<String> stockCodeList = turnoverRateMap.get(ExtDailyStock.TWO_TO_THREE);
					List<String> codeList = new ArrayList<String>(stockCodeList);
					codeList.add(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.TWO_TO_THREE, codeList);
				} else {
					List<String> stockCodeList = Arrays.asList(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.TWO_TO_THREE, stockCodeList);
				}
				break;
			case ExtDailyStock.THREE_TO_FIVE:
				if (turnoverRateMap.containsKey(ExtDailyStock.THREE_TO_FIVE)) {
					List<String> stockCodeList = turnoverRateMap.get(ExtDailyStock.THREE_TO_FIVE);
					List<String> codeList = new ArrayList<String>(stockCodeList);
					codeList.add(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.THREE_TO_FIVE, codeList);
				} else {
					List<String> stockCodeList = Arrays.asList(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.THREE_TO_FIVE, stockCodeList);
				}
				break;
			case ExtDailyStock.FIVE_TO_EIGHT:
				if (turnoverRateMap.containsKey(ExtDailyStock.FIVE_TO_EIGHT)) {
					List<String> stockCodeList = turnoverRateMap.get(ExtDailyStock.FIVE_TO_EIGHT);
					List<String> codeList = new ArrayList<String>(stockCodeList);
					codeList.add(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.FIVE_TO_EIGHT, codeList);
				} else {
					List<String> stockCodeList = Arrays.asList(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.FIVE_TO_EIGHT, stockCodeList);
				}
				break;
			case ExtDailyStock.EIGHT_TO_FIFTEEN:
				if (turnoverRateMap.containsKey(ExtDailyStock.EIGHT_TO_FIFTEEN)) {
					List<String> stockCodeList = turnoverRateMap.get(ExtDailyStock.EIGHT_TO_FIFTEEN);
					List<String> codeList = new ArrayList<String>(stockCodeList);
					codeList.add(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.EIGHT_TO_FIFTEEN, codeList);
				} else {
					List<String> stockCodeList = Arrays.asList(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.EIGHT_TO_FIFTEEN, stockCodeList);
				}
				break;
			case ExtDailyStock.FIFTY_TO_TWENTY_FIVE:
				if (turnoverRateMap.containsKey(ExtDailyStock.FIFTY_TO_TWENTY_FIVE)) {
					List<String> stockCodeList = turnoverRateMap.get(ExtDailyStock.FIFTY_TO_TWENTY_FIVE);
					List<String> codeList = new ArrayList<String>(stockCodeList);
					codeList.add(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.FIFTY_TO_TWENTY_FIVE, codeList);
				} else {
					List<String> stockCodeList = Arrays.asList(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.FIFTY_TO_TWENTY_FIVE, stockCodeList);
				}
				break;
			case ExtDailyStock.LARGER_TWENTY_FIVE:
				if (turnoverRateMap.containsKey(ExtDailyStock.LARGER_TWENTY_FIVE)) {
					List<String> stockCodeList = turnoverRateMap.get(ExtDailyStock.LARGER_TWENTY_FIVE);
					List<String> codeList = new ArrayList<String>(stockCodeList);
					codeList.add(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.LARGER_TWENTY_FIVE, codeList);
				} else {
					List<String> stockCodeList = Arrays.asList(stockCode + "(" + turnoverRate + "%)");
					turnoverRateMap.put(ExtDailyStock.LARGER_TWENTY_FIVE, stockCodeList);
				}
				break;
			default:
				System.out.println("换手率标识值：" + turnoverFlg + " 不正确！");
				break;
		}
		return turnoverRateMap;
	}

	private String getStockCodesFromList(List<StatisticStock> statisticStockList) {
		
		List<String> stockCodeList = new ArrayList<String>();
		for (StatisticStock stock : statisticStockList) {
			String stockCode = stock.getStockCode();
			stockCodeList.add(stockCode);
		}
		return StringUtils.join(stockCodeList.toArray(), ",");
	}

	private void printTurnoverMap(Map<String, List<String>> turnoverMap) {
		
		List<String> lessOneList = turnoverMap.get(ExtDailyStock.LESS_ONE);
		if (lessOneList==null) lessOneList = new ArrayList<String>();
		List<String> oneToTwoList = turnoverMap.get(ExtDailyStock.ONE_TO_TWO);
		if (oneToTwoList==null) oneToTwoList = new ArrayList<String>();
		List<String> twoToThreeList = turnoverMap.get(ExtDailyStock.TWO_TO_THREE);
		if (twoToThreeList==null) twoToThreeList = new ArrayList<String>();
		List<String> threeToFiveList = turnoverMap.get(ExtDailyStock.THREE_TO_FIVE);
		if (threeToFiveList==null) threeToFiveList = new ArrayList<String>();
		List<String> fiveToEightList = turnoverMap.get(ExtDailyStock.FIVE_TO_EIGHT);
		if (fiveToEightList==null) fiveToEightList = new ArrayList<String>();
		List<String> eightToFifteenList = turnoverMap.get(ExtDailyStock.EIGHT_TO_FIFTEEN);
		if (eightToFifteenList==null) eightToFifteenList = new ArrayList<String>();
		List<String> fifteenToTwentyFiveList = turnoverMap.get(ExtDailyStock.LARGER_TWENTY_FIVE);
		if (fifteenToTwentyFiveList==null) fifteenToTwentyFiveList = new ArrayList<String>();
		List<String> largerTwentyFiveList = turnoverMap.get(ExtDailyStock.LARGER_TWENTY_FIVE);
		if (largerTwentyFiveList==null) largerTwentyFiveList = new ArrayList<String>();

		System.out.println("绝对低量(小于1%)：" + lessOneList.size() + "----->股票：" + StringUtils.join(lessOneList.toArray(), ","));
		System.out.println("成交低靡(1%-2%)：" + oneToTwoList.size() + "----->股票：" + StringUtils.join(oneToTwoList.toArray(), ","));
		System.out.println("成交温和(2%-3%)：" + twoToThreeList.size() + "----->股票：" + StringUtils.join(twoToThreeList.toArray(), ","));
		System.out.println("成交活跃(3%-5%)：" + threeToFiveList.size() + "----->股票：" + StringUtils.join(threeToFiveList.toArray(), ","));
		System.out.println("带量(5%-8%)：" + fiveToEightList.size() + "----->股票：" + StringUtils.join(fiveToEightList.toArray(), ","));
		System.out.println("放量(8%-15%)：" + eightToFifteenList.size() + "----->股票：" + StringUtils.join(eightToFifteenList.toArray(), ","));
		System.out.println("巨量(15%-25%)：" + fifteenToTwentyFiveList.size() + "----->股票：" + StringUtils.join(fifteenToTwentyFiveList.toArray(), ","));
		System.out.println("成交怪异(大于25%)：" + largerTwentyFiveList.size() + "----->股票：" + StringUtils.join(largerTwentyFiveList.toArray(), ","));

		log.loger.warn("绝对低量(小于1%)：" + lessOneList.size() + "----->股票：" + StringUtils.join(lessOneList.toArray(), ","));
		log.loger.warn("成交低靡(1%-2%)：" + oneToTwoList.size() + "----->股票：" + StringUtils.join(oneToTwoList.toArray(), ","));
		log.loger.warn("成交温和(2%-3%)：" + twoToThreeList.size() + "----->股票：" + StringUtils.join(twoToThreeList.toArray(), ","));
		log.loger.warn("成交活跃(3%-5%)：" + threeToFiveList.size() + "----->股票：" + StringUtils.join(threeToFiveList.toArray(), ","));
		log.loger.warn("带量(5%-8%)：" + fiveToEightList.size() + "----->股票：" + StringUtils.join(fiveToEightList.toArray(), ","));
		log.loger.warn("放量(8%-15%)：" + eightToFifteenList.size() + "----->股票：" + StringUtils.join(eightToFifteenList.toArray(), ","));
		log.loger.warn("巨量(15%-25%)：" + fifteenToTwentyFiveList.size() + "----->股票：" + StringUtils.join(fifteenToTwentyFiveList.toArray(), ","));
		log.loger.warn("成交怪异(大于25%)：" + largerTwentyFiveList.size() + "----->股票：" + StringUtils.join(largerTwentyFiveList.toArray(), ","));
	}
	
	/**
	 * calculate the average turnover rate of the stocks
	 *
	 */
	private <T extends BaseStock> Map<String, T> getAverageTurnoverRate(List<T> stockList, Class<T> clazz) throws InstantiationException, IllegalAccessException {

		Map<String, T> stockMap = new HashMap<String, T>();
		long num = 0;
		// calculate the sum of turnover rate and the count of same stockCode
		for (T stock : stockList) {
			num++;
			String stockCode = stock.getStockCode();
			Double turnoverRate = stock.getTurnoverRate();
			if (stockMap.containsKey(stockCode)) {
				T mapStock = stockMap.get(stockCode);
				Double mapTurnoverRate = mapStock.getTurnoverRate();
				Double turnover = DataUtils.add(mapTurnoverRate, turnoverRate).doubleValue();
				mapStock.setTurnoverRate(turnover);
				long count = mapStock.getNum();
				mapStock.setNum(++count);
				stockMap.put(stockCode, mapStock);
			} else {
				T newStock = clazz.newInstance();
				newStock.setStockCode(stockCode);
				newStock.setNum(Long.valueOf(DataUtils._INT_ONE));
				newStock.setTurnoverRate(turnoverRate);
				stockMap.put(stockCode, newStock);
			}
		}
		// calculate the average turnover rate
		for (T stock : stockMap.values()) {
			Double turnoverRate = stock.getTurnoverRate();
			long count = stock.getNum();
			Double averageRate = DataUtils.div(turnoverRate, Double.valueOf(count), DataUtils._INT_TWO).doubleValue();
			stock.setTurnoverRate(averageRate);
		}
		return stockMap;
	}

	public void statisticMonthTurnoverRate(String startDate) {
		allImportStockDao = new AllImportStockDao();
		try {
			Date nowDate = new Date();
			Date beginDate = DateUtils.monthToDate(startDate);
			Map<String, List<Double>> monthTurnoverMap = new LinkedHashMap<String, List<Double>>();
			while (beginDate.compareTo(nowDate) <= 0) {
				int year = DateUtils.getYearFromDate(beginDate);
				int month = DateUtils.getMonthFromDate(beginDate);
				Date firstDateOfMonth = DateUtils.getFirstDayOfMonth(year, month);
				Date lastDateOfMonth = DateUtils.getLastDayOfMonth(year, month);
				List<AllImportStock> allImportStockList = allImportStockDao.getAllImportStockByStockDate(new Date[]{firstDateOfMonth, lastDateOfMonth});
				Map<String, AllImportStock> averageStockMap = getAverageTurnoverRate(allImportStockList, AllImportStock.class);
				int allStockNum = averageStockMap.size()!=0?averageStockMap.size():-1;
				Map<String, List<String>> turnoverMap = new HashMap<String, List<String>>();
				for (AllImportStock allImportStock : averageStockMap.values()) {
					String stockCode = allImportStock.getStockCode();
					Double turnoverRate = allImportStock.getTurnoverRate();
					calculateAllSelectedStockTurnoverRate(stockCode, turnoverRate, turnoverMap);
				}
				List<Double> turnoverList = new ArrayList<Double>();
				//小于1%
				List<String> lessOneList = turnoverMap.get(ExtDailyStock.LESS_ONE);
				int lessOneNum = lessOneList!=null?lessOneList.size():0;
				Double lessOneRate = DataUtils.div(Double.valueOf(lessOneNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
				turnoverList.add(lessOneRate);
				//1%-2%
				List<String> oneToTwoList = turnoverMap.get(ExtDailyStock.ONE_TO_TWO);
				int oneToTwoNum = oneToTwoList!=null?oneToTwoList.size():0;
				Double oneToTwoRate = DataUtils.div(Double.valueOf(oneToTwoNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
				turnoverList.add(oneToTwoRate);
				//2%-3%
				List<String> twoToThreeList = turnoverMap.get(ExtDailyStock.TWO_TO_THREE);
				int twoToThreeNum = twoToThreeList!=null?twoToThreeList.size():0;
				Double twoToThreeRate = DataUtils.div(Double.valueOf(twoToThreeNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
				turnoverList.add(twoToThreeRate);
				//3%-5%
				List<String> threeToFiveList = turnoverMap.get(ExtDailyStock.THREE_TO_FIVE);
				int threeToFiveNum = threeToFiveList!=null?threeToFiveList.size():0;
				Double threeToFiveRate = DataUtils.div(Double.valueOf(threeToFiveNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
				turnoverList.add(threeToFiveRate);
				//5%-8%
				List<String> fiveToEightList = turnoverMap.get(ExtDailyStock.FIVE_TO_EIGHT);
				int fiveToEightNum = fiveToEightList!=null?fiveToEightList.size():0;
				Double fiveToEightRate = DataUtils.div(Double.valueOf(fiveToEightNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
				turnoverList.add(fiveToEightRate);
				//8%-15%
				List<String> eightToFifteenList = turnoverMap.get(ExtDailyStock.EIGHT_TO_FIFTEEN);
				int eightToFifteenNum = eightToFifteenList!=null?eightToFifteenList.size():0;
				Double eightToFifteenRate = DataUtils.div(Double.valueOf(eightToFifteenNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
				turnoverList.add(eightToFifteenRate);
				//15%-25%
				List<String> fifteenToTwentyFiveList = turnoverMap.get(ExtDailyStock.LARGER_TWENTY_FIVE);
				int fifteenToTwentyFiveNum = fifteenToTwentyFiveList!=null?fifteenToTwentyFiveList.size():0;
				Double fifteenToTwentyFiveRate = DataUtils.div(Double.valueOf(fifteenToTwentyFiveNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
				turnoverList.add(fifteenToTwentyFiveRate);
				//大于25%
				List<String> largerTwentyFiveList = turnoverMap.get(ExtDailyStock.LARGER_TWENTY_FIVE);
				int largerTwentyFiveNum = largerTwentyFiveList!=null?largerTwentyFiveList.size():0;
				Double largerTwentyFiveRate = DataUtils.div(Double.valueOf(largerTwentyFiveNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
				turnoverList.add(largerTwentyFiveRate);

				monthTurnoverMap.put(DateUtils.dateTimeToString(beginDate, DateUtils.YEAR_MONTH_FORMAT), turnoverList);
				beginDate = DateUtils.addOneMonth(beginDate);
			}
			printMonthTurnoverRate(monthTurnoverMap);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			closeDao(allImportStockDao);
		}
	}
	
	private void printMonthTurnoverRate(Map<String, List<Double>> monthTurnoverMap) {

		System.out.println("-------------------------------每月股票换手率统计分析--------------------------------");
		log.loger.warn("-------------------------------每月股票换手率统计分析--------------------------------");
		String lineTitle = "   时间                小于1%   1%-2%   2%-3%   3%-5%   5%-8%   8%-15%   15%-25%   大于25%";
		System.out.println(lineTitle);
		log.loger.warn(lineTitle);
		String lineText = "time        leOne   onTwo    three    tFive    eight    fifte     tFive      lFive";
		Iterator<Entry<String, List<Double>>> it = monthTurnoverMap.entrySet().iterator();
		while (it.hasNext()) {
		   Entry<String, List<Double>> entry = it.next();
		   String lineContent = lineText;
		   List<Double> valueList = entry.getValue();
		   lineContent = lineContent.replace("time", entry.getKey());
		   lineContent = lineContent.replace("leOne", valueList.get(0) + "%");
		   lineContent = lineContent.replace("onTwo", valueList.get(1) + "%");
		   lineContent = lineContent.replace("three", valueList.get(2) + "%");
		   lineContent = lineContent.replace("tFive", valueList.get(3) + "%");
		   lineContent = lineContent.replace("eight", valueList.get(4) + "%");
		   lineContent = lineContent.replace("fifte", valueList.get(5) + "%");
		   lineContent = lineContent.replace("tFive", valueList.get(6) + "%");
		   lineContent = lineContent.replace("lFive", valueList.get(7) + "%");
		   System.out.println(lineContent);
		   log.loger.warn(lineContent);
		}
	}
}
