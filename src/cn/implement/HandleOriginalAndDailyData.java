package cn.implement;

import java.sql.Timestamp;
import java.util.Date;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticStock;

public class HandleOriginalAndDailyData extends OperationData {

	public void handleOriginalStockData(String sDate, String stockCodes, String changeRates, String turnoverRates) {

		originalStockDao = new OriginalStockDao();
		String info = "无操作";
		try {
			// 保存数据
			final int original_count = originalStockDao.searchOriginalData(sDate);
			if (original_count == 0) {
				originalStockDao.addOriginalData(sDate, stockCodes, changeRates, turnoverRates);
				info = "增加";
			} else {
				originalStockDao.updateOriginalData(sDate, stockCodes, changeRates, turnoverRates);
				info = "增加";
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(originalStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + " " + info + "了" + sDate + "的原始股票数据(original_stock_)！");
			log.loger.info(" " + info + "了" + sDate + "的原始股票数据(original_stock_)！");
		}
	}

	public void handleDailyStockData(String sDate, String stockCodes, String changeRates, String turnoverRates) {

		int dailySum = 0;
		int statisticSum = 0;
		try {
			dailyStockDao = new DailyStockDao();
			statisticStockDao = new StatisticStockDao();
			String[] codeArray = stockCodes.split(",");
			String[] changeRateArray = changeRates.split(",");
			String[] turnoverRateArray = null;
			if (!CommonUtils.isBlank(turnoverRates)) {
				turnoverRateArray = turnoverRates.split(",");
			}

			for (int index = 0; index < codeArray.length; index++) {
				// 对数据进行转换
				DailyStock stockData = CommonUtils.getDailyStockFromArray(index, codeArray, changeRateArray, turnoverRateArray, sDate);
				// 保存输入的股票数据
				boolean existFlg = dailyStockDao.isExistInDailyStock(stockData);
				if (!existFlg) {
					boolean saveFlg = dailyStockDao.saveDailyStock(stockData);
					if (saveFlg) {
						dailySum++;
						boolean updateFlg = updateStatisticStock(stockData);
						if (updateFlg) {
							statisticSum++;
							System.out.println(DateUtils.dateTimeToString(new Date())
									+ " 每日股票信息表(daily_stock_)和统计股票信息表(statistic_stock_)增加或更新了股票---" + dailySum + "：" + stockData.getStockCode()
									+ "(" + PropertiesUtils.getProperty(stockData.getStockCode()) + ")");
							log.loger.info(" 每日股票信息表(daily_stock_)和统计股票信息表(statistic_stock_)增加或更新了股票---" + dailySum + "："
									+ stockData.getStockCode() + "(" + PropertiesUtils.getProperty(stockData.getStockCode()) + ")");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + sDate + " 每日股票信息表(daily_stock_)共增加了" + dailySum + "条记录！");
			log.loger.info(" " + sDate + " 每日股票信息表(daily_stock_)共增加了" + dailySum + "条记录！");
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + sDate + " 统计股票信息表(statistic_stock_)共更新了" + statisticSum + "条记录！");
			log.loger.info(" " + sDate + " 统计股票信息表(statistic_stock_)共更新了" + statisticSum + "条记录！");
		}
	}

	private boolean updateStatisticStock(DailyStock dailyStock) throws Exception {

		boolean updateFlg = false;
		String stockCode = dailyStock.getStockCode();
		String changeFlg = dailyStock.getChangeFlg();
		StatisticStock statisticStock = statisticStockDao.getStatisticStockByStockCode(stockCode);
		if (null != statisticStock) {
			// 计算股票的涨跌次数
			calculateUpDownNumber(statisticStock, changeFlg);
			updateFlg = statisticStockDao.updateStatisticStock(statisticStock);
		}
		return updateFlg;
	}
}