package cn.implement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.bean.DailyStock;

public class AnalysisDailyAndDetailStockData extends OperationData {

	/**
	 * 统计分析从输入日起股票的最大涨、跌幅和涨、跌次数
	 *
	 */
	public void analysisStock(String startDate) {
		dailyStockDao = new DailyStockDao();
		try {
			List<DailyStock> dailyList = dailyStockDao.analysisStockByTime(DateUtils.stringToDate(startDate), new Date(), null);
			/*if (dailyList.size() > 0) {
				String change = changeFlg.equals("0")?"跌":(changeFlg.equals("1")?"涨":"涨跌");
				System.out.println("-------------------------------" + startDate + "至" + endDate + "股票次数统计(" + change + ")--------------------------------");
				log.loger.info("-------------------------------" + startDate + "至" + endDate + "股票次数统计(" + change + ")--------------------------------");
				List<DailyStock> analysisList = setDatesToDailyStock(dailyList, startDate, endDate, changeFlg);
				for (int index=0; index<analysisList.size(); index++) {
					DailyStock data = analysisList.get(index);
					String stockName = PropertiesUtils.getProperty(data.getStockCode());
					String spaces = CommonUtils.getSpaceByString(stockName);
					System.out.println(CommonUtils.supplyNumber(index+1, analysisList.size()) + "---股票出现的次数: " + data.getCount() + " 股票代码: " + data.getStockCode() + "(" + stockName + ")" + spaces + " 出现的日期: (" + data.getStockDates() + ")");
					log.loger.info(CommonUtils.supplyNumber(index+1, analysisList.size()) + "---股票出现的次数: " + data.getCount() + " 股票代码: " + data.getStockCode() + "(" + stockName + ")" + spaces + " 出现的日期: (" + data.getStockDates() + ")");
				}
			} else {
				System.out.println("此时间段(" + startDate + "至" + endDate + ")没有每日股票数据！");
				log.loger.info("此时间段(" + startDate + "至" + endDate + ")没有每日股票数据！");
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao);
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
				String change = changeFlg.equals("0")?"跌":(changeFlg.equals("1")?"涨":"涨跌");
				System.out.println("-------------------------------" + startDate + "至" + endDate + "股票次数统计(" + change + ")--------------------------------");
				log.loger.info("-------------------------------" + startDate + "至" + endDate + "股票次数统计(" + change + ")--------------------------------");
				List<DailyStock> analysisList = setDatesToDailyStock(dailyList, startDate, endDate, changeFlg);
				for (int index=0; index<analysisList.size(); index++) {
					DailyStock data = analysisList.get(index);
					String stockName = PropertiesUtils.getProperty(data.getStockCode());
					String spaces = CommonUtils.getSpaceByString(stockName);
					System.out.println(CommonUtils.supplyNumber(index+1, analysisList.size()) + "---股票出现的次数: " + data.getCount() + " 股票代码: " + data.getStockCode() + "(" + stockName + ")" + spaces + " 出现的日期: (" + data.getStockDates() + ")");
					log.loger.info(CommonUtils.supplyNumber(index+1, analysisList.size()) + "---股票出现的次数: " + data.getCount() + " 股票代码: " + data.getStockCode() + "(" + stockName + ")" + spaces + " 出现的日期: (" + data.getStockDates() + ")");
				}
			} else {
				System.out.println("此时间段(" + startDate + "至" + endDate + ")没有每日股票数据！");
				log.loger.info("此时间段(" + startDate + "至" + endDate + ")没有每日股票数据！");
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
}
