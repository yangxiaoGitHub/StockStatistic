package cn.implement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.com.StockUtils;
import cn.db.AllImportStockDao;
import cn.db.DailyStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllImportStock;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticDetailStock;
import cn.db.bean.StatisticStock;
import cn.db.bean.ext.ExtStatisticStock;

public class AnalysisDailyAndDetailStockData extends OperationData {

	/**
	 * 统计分析从输入日起股票的涨跌幅、最大涨跌幅、涨跌次数、最近日期的最大涨幅和最近日期的最大跌幅
	 *
	 */
	public void analysisStock(String startDate, String sortFlg) {
		dailyStockDao = new DailyStockDao();
		//detailStockDao = new DetailStockDao();
		statisticStockDao = new StatisticStockDao();
		allImportStockDao = new AllImportStockDao();
		//statisticDetailStockDao = new StatisticDetailStockDao();
		try {
			Date beginDate = DateUtils.stringToDate(startDate);
			List<ExtStatisticStock> statisticStockList = new ArrayList<ExtStatisticStock>();
			Date endDate = dailyStockDao.getRecentStockDate();
			List<DailyStock> dailyList = dailyStockDao.analysisStockByTime(beginDate, endDate, null);
			if (dailyList.size() > 0) {
				System.out.println("-------------------------------" + startDate + "至" + DateUtils.dateToString(endDate) + "股票涨跌幅统计--------------------------------");
				log.loger.warn("-------------------------------" + startDate + "至" + DateUtils.dateToString(endDate) + "股票涨跌幅统计--------------------------------");
				for (int index=0; index<dailyList.size(); index++) {
					DailyStock dailyStock = dailyList.get(index);
					String stockCode = dailyStock.getStockCode();
					//List<DetailStock> detailStockList = detailStockDao.getDetailStockByTime(stockCode, beginDate, endDate);
					List<AllImportStock> allImportStockList = allImportStockDao.getAllImportStockByCodeAndDate(stockCode, beginDate, endDate);
					if (CommonUtils.isBlank(allImportStockList)) continue;
					Double[] minMaxPrices = CommonUtils.getMinMaxPrices(allImportStockList);
					AllImportStock firstImportStock = DataUtils.getFirstAllImportStock(allImportStockList);
					AllImportStock lastImportStock = DataUtils.getLastAllImportStock(allImportStockList);
					StatisticStock statisticStock = statisticStockDao.getStatisticStockByStockCode(stockCode);
					ExtStatisticStock extStatisticStock = new ExtStatisticStock(statisticStock);
					calculateMinMaxChangeRate(extStatisticStock, firstImportStock, minMaxPrices);
					calculateLastDateChangeRate(extStatisticStock, firstImportStock, lastImportStock);
					calculateRecentMinMaxChangeRate(extStatisticStock, minMaxPrices, lastImportStock);
					Date[] startEndDate = {beginDate, endDate};
					Integer upDownNumber = dailyStock.getCount();
					Integer upNumber = dailyStockDao.getUpNumberByStockCode(stockCode, startEndDate);
					Integer downNumber = dailyStockDao.getDownNumberByStockCode(stockCode, startEndDate);
					extStatisticStock.setUpDownNumber(upDownNumber);
					extStatisticStock.setUpNumber(upNumber);
					extStatisticStock.setDownNumber(downNumber);
					statisticStockList.add(extStatisticStock);
				}
				printStatisticStockList(statisticStockList, Integer.valueOf(sortFlg).intValue());
			} else {
				System.out.println("此时间段(" + startDate + "至" + DateUtils.dateToString(endDate) + ")没有每日股票数据！");
				log.loger.warn("此时间段(" + startDate + "至" + DateUtils.dateToString(endDate) + ")没有每日股票数据！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, statisticStockDao, allImportStockDao);
		}
	}

	/**
	 * 统计分析某一时间段股票出现的次数
	 *
	 */
	public void analysisStock(String startDate, String endDate, String changeFlg) {
		dailyStockDao = new DailyStockDao();
		try {
			List<DailyStock> dailyList = dailyStockDao.analysisStockByTime(DateUtils.stringToDate(startDate), DateUtils.stringToDate(endDate), changeFlg);
			if (dailyList.size() > 0) {
				String change = changeFlg.equals("0")?StatisticDetailStock.CH_DOWN:(changeFlg.equals("1")?StatisticDetailStock.CH_UP:StatisticDetailStock.CH_UP_DOWN);
				System.out.println("-------------------------------" + startDate + "至" + endDate + "股票次数统计(" + change + ")--------------------------------");
				log.loger.warn("-------------------------------" + startDate + "至" + endDate + "股票次数统计(" + change + ")--------------------------------");
				List<DailyStock> analysisList = setDatesToDailyStock(dailyList, startDate, endDate, changeFlg);
				for (int index=0; index<analysisList.size(); index++) {
					DailyStock data = analysisList.get(index);
					String stockName = PropertiesUtils.getProperty(data.getStockCode());
					String spaces = CommonUtils.getSpaceByString(stockName);
					System.out.println(DataUtils.supplyNumber(index+1, analysisList.size()) + "---股票出现的次数: " + data.getCount() + " 股票代码: " + data.getStockCode() + "(" + stockName + ")" + spaces + " 出现的日期: (" + data.getStockDates() + ")");
					log.loger.warn(DataUtils.supplyNumber(index+1, analysisList.size()) + "---股票出现的次数: " + data.getCount() + " 股票代码: " + data.getStockCode() + "(" + stockName + ")" + spaces + " 出现的日期: (" + data.getStockDates() + ")");
				}
			} else {
				System.out.println("此时间段(" + startDate + "至" + endDate + ")没有每日股票数据！");
				log.loger.warn("此时间段(" + startDate + "至" + endDate + ")没有每日股票数据！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao);
		}
	}

	private List<DailyStock> setDatesToDailyStock(List<DailyStock> dailyList, String startDate, String endDate, String changeFlg) throws SQLException {

		List<DailyStock> stockList = new ArrayList<DailyStock>();
		for (DailyStock stock : dailyList) {
			List<Date> dateList = dailyStockDao.analysisStockDatesByStockCode(stock.getStockCode(), startDate, endDate, changeFlg);
			stock.setDates(dateList);
			stockList.add(stock);
		}
		return stockList;
	}
	
	/*
	序号        股票代码        股票名称        最近涨跌幅        最大跌幅        最大涨幅       涨跌次数
	632     000632          某某股份        -32.15%       -10.30%    15.42%          涨跌:13涨:9跌:4
	456     600457          某某股份        11.03%        -14.32%    12.69%          涨跌:11涨:2跌:9
	*/
	private void printStatisticStockList(List<ExtStatisticStock> statisticStockList, final int sortFlg) {

		Collections.sort(statisticStockList, new Comparator<ExtStatisticStock>() {
		    public int compare(ExtStatisticStock firstStock, ExtStatisticStock secondStock) {
		    	int flg = 0;
		    	switch (sortFlg){
		    	case DataUtils._INT_ZERO:
		    		flg = firstStock.getNowChangeRate().compareTo(secondStock.getNowChangeRate());
		    		break;
		    	case DataUtils._INT_ONE:
		    		flg = firstStock.getRecentMaxDeclineRate().compareTo(secondStock.getRecentMaxDeclineRate());
		    		break;
		    	case DataUtils._INT_TWO:
		    		flg = secondStock.getRecentMaxRiseRate().compareTo(firstStock.getRecentMaxRiseRate());
		    		break;
		    	case DataUtils._INT_THREE:
		    		flg = secondStock.getUpDownNumber().compareTo(firstStock.getUpDownNumber());
		    		break;
		    	case DataUtils._INT_FOUR:
		    		flg = secondStock.getUpNumber().compareTo(firstStock.getUpNumber());
		    		break;
		    	case DataUtils._INT_FIVE:
		    		flg = secondStock.getDownNumber().compareTo(firstStock.getDownNumber());
		    		break;
		    	default:
		    		flg = firstStock.getNowChangeRate().compareTo(secondStock.getNowChangeRate());
		    		break;
		    	}
		        return flg;
		    }
		});
		String lineTitle = "序号        股票代码        股票名称        涨跌幅            最大跌幅        最大涨幅       最近最大跌幅       最近最大涨幅       涨跌次数";
		System.out.println(lineTitle);
		log.loger.warn(lineTitle);
		//System.out.println("000     000807     云铝股份        -29.02%     -30.11%     0.0%       -29.02%        1.55%         涨跌:1涨:1跌:0");
		String lineText = "NUM     STOCKC     STOCKNAM        RECENTC     MAXDOWN     MAXUPR     RECENTD        RECENTU        涨跌:UPD涨:UP跌:DO";
		for (int index=0; index<statisticStockList.size(); index++) {
			ExtStatisticStock extStatisticStock = statisticStockList.get(index);
			String stockCode = extStatisticStock.getStockCode();
			String lineContent = lineText;
			// 序号
			lineContent = lineContent.replace("NUM", DataUtils.supplyNumber(index+1, statisticStockList.size()));
			// 股票代码
			lineContent = lineContent.replace("STOCKC", stockCode);
			// 股票名称
			String stockName = PropertiesUtils.getProperty(stockCode);
			if (stockName.length() <= DataUtils._INT_THREE)
				stockName = CommonUtils.addSpaceInMiddle(stockName);
			lineContent = lineContent.replace("STOCKNAM", stockName);
			// 涨跌幅
			lineContent = lineContent.replace("RECENTC", CommonUtils.appendSpace(extStatisticStock.getNowChangeRate() + "%", DataUtils._INT_SEVEN));
			// 最大跌幅
			lineContent = lineContent.replace("MAXDOWN", CommonUtils.appendSpace(extStatisticStock.getMaxDeclineRate() + "%", DataUtils._INT_SEVEN));
			// 最大涨幅
			lineContent = lineContent.replace("MAXUPR", CommonUtils.appendSpace(extStatisticStock.getMaxRiseRate() + "%", DataUtils._INT_SIX));
			// 最近最大跌幅
			lineContent = lineContent.replace("RECENTD", CommonUtils.appendSpace(extStatisticStock.getRecentMaxDeclineRate() + "%", DataUtils._INT_SEVEN));
			// 最近最大涨幅
			lineContent = lineContent.replace("RECENTU", CommonUtils.appendSpace(extStatisticStock.getRecentMaxRiseRate() + "%", DataUtils._INT_SIX));
			// 涨跌次数
			lineContent = lineContent.replace("UPD", extStatisticStock.getUpDownNumber().toString());
			// 涨次数
			lineContent = lineContent.replace("UP", extStatisticStock.getUpNumber().toString());
			// 跌次数
			lineContent = lineContent.replace("DO", extStatisticStock.getDownNumber().toString());
			System.out.println(lineContent);
			// change the content of .log file to hex
			log.loger.warn(DataUtils.stringToHex(lineContent));
		}
	}
	
	/**
	 * 计算股票最大涨跌幅和涨跌幅
	 *
	 */
	private void calculateMinMaxChangeRate(ExtStatisticStock statisticStock, AllImportStock firstImportStock, Double[] minMaxPrices) {
		//最大涨跌幅
		double firstPrice = firstImportStock.getCurrent().doubleValue();
		double minPrice = minMaxPrices[0].doubleValue(); 
		double maxPrice = minMaxPrices[1].doubleValue();
		double maxDeclineRate = StockUtils.getChangeRate(firstPrice, minPrice);
		double maxRiseRate = StockUtils.getChangeRate(firstPrice, maxPrice);
		statisticStock.setMaxDeclineRate(maxDeclineRate);
		statisticStock.setMaxRiseRate(maxRiseRate);
	}
	
	/**
	 * 计算股票到最近日期的涨跌幅
	 *
	 */
	private void calculateLastDateChangeRate(ExtStatisticStock statisticStock, AllImportStock firstImportStock, AllImportStock lastImportStock) {
		//涨跌幅
		double firstPrice = firstImportStock.getCurrent().doubleValue();
		double nowPrice = lastImportStock.getCurrent();
		double nowChangeRate = StockUtils.getChangeRate(firstPrice, nowPrice);
		statisticStock.setNowChangeRate(nowChangeRate);
	}
	
	/**
	 * 计算股票最近日期的最大涨跌幅
	 * 
	 */
	private void calculateRecentMinMaxChangeRate(ExtStatisticStock statisticStock, Double[] minMaxPrices, AllImportStock lastImportStock) {
		//最近日期的最大涨跌幅
		double minPrice = minMaxPrices[0].doubleValue();
		double maxPrice = minMaxPrices[1].doubleValue();
		double nowPrice = lastImportStock.getCurrent();
		double recentMaxDeclineRate = StockUtils.getChangeRate(maxPrice, nowPrice);
		double recentMaxRiseRate = StockUtils.getChangeRate(minPrice, nowPrice);
		statisticStock.setRecentMaxDeclineRate(recentMaxDeclineRate);
		statisticStock.setRecentMaxRiseRate(recentMaxRiseRate);
	}
}
