package cn.implement;

import java.sql.SQLException;
import java.util.Date;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticDetailStock;

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
			log.loger.warn(" " + info + "了" + sDate + "的原始股票数据(original_stock_)！");
		}
	}

	public void handleDailyStockData(String sDate, String stockCodes, String changeRates, String turnoverRates) {

		int dailySum = 0;
		int statisticSum = 0;
		try {
			dailyStockDao = new DailyStockDao();
			statisticDetailStockDao = new StatisticDetailStockDao();
			String[] codeArray = stockCodes.split(",");
			String[] changeRateArray = changeRates.split(",");
			String[] turnoverRateArray = null;
			if (!CommonUtils.isBlank(turnoverRates)) {
				turnoverRateArray = turnoverRates.split(",");
			}
			//增加每日股票数据(daily_stock_)
			for (int index = 0; index < codeArray.length; index++) {
				// 对数据进行转换
				DailyStock dailyStock = CommonUtils.getDailyStockFromArray(index, codeArray, changeRateArray, turnoverRateArray, sDate);
				// 保存输入的股票数据
				boolean existFlg = dailyStockDao.isExistInDailyStock(dailyStock);
				if (!existFlg) {
					boolean saveFlg = dailyStockDao.saveDailyStock(dailyStock);
					if (saveFlg) {
						dailySum++;
						System.out.print(DateUtils.dateTimeToString(new Date()) + "----->表(daily_stock_)增加了股票  " + dailySum + "："
								+ dailyStock.getStockCode() + "(" + PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
						log.loger.warn("----->表(daily_stock_)增加了股票  " + dailySum + "：" + dailyStock.getStockCode() + "("
								+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
						boolean updateFlg = updateStatisticDetailStock(dailyStock);
						if (updateFlg) {
							++statisticSum;
							System.out.println("----->表(statistic_detail_stock_)更新了第" + statisticSum + "只股票：" + dailyStock.getStockCode() + "("
									+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + "), 统计日: " + DateUtils.dateToString(dailyStock.getStockDate()));
							log.loger.warn("----->表(statistic_detail_stock_)更新了第" + statisticSum + "只股票：" + dailyStock.getStockCode() + "("
									+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + "), 统计日: " + DateUtils.dateToString(dailyStock.getStockDate()));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + sDate + " 表(daily_stock_)共增加了" + dailySum + "条记录！");
			log.loger.warn("----->" + sDate + " 表(daily_stock_)共增加了" + dailySum + "条记录！");
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + sDate + " 表(statistic_detail_stock_)共更新了" + statisticSum + "条记录！");
			log.loger.warn("----->" + sDate + " 表(statistic_detail_stock_)共更新了" + statisticSum + "条记录！");
		}
	}

	private boolean updateStatisticDetailStock(DailyStock dailyStock) throws SQLException {

		boolean updateFlg = false;
		String stockCode = dailyStock.getStockCode();
		Date stockDate = dailyStock.getStockDate();
		String changeFlg = dailyStock.getChangeFlg();
		StatisticDetailStock statisticDetailStock = statisticDetailStockDao.getStatisticDetailStockByKey(stockCode, stockDate);
		if (null != statisticDetailStock) {
			// 计算股票的涨跌次数
			calculateUpDownNumber(statisticDetailStock, changeFlg);
			updateFlg = statisticDetailStockDao.updateStatisticDetailStock(statisticDetailStock);
		}
		return updateFlg;
	}
}