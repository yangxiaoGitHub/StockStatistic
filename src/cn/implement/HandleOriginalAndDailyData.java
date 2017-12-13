package cn.implement;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticStock;
import cn.log.Log;

 public class HandleOriginalAndDailyData extends OperationData {
	Log log = Log.getLoger();

	public void handleOriginal(String sDate, String stockCodes, String changeRates, String turnoverRates) {

		originalStockDao = new OriginalStockDao();
		String info = "无操作";
		try {
			// 保存数据
			int original_count = originalStockDao.searchOriginalData(sDate);
			if (original_count == 0) {
				originalStockDao.addOriginalData(sDate, stockCodes, changeRates, turnoverRates);
				info = "添加";
			} else {
				originalStockDao.updateOriginalData(sDate, stockCodes, changeRates, turnoverRates);
				info = "更新";
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(originalStockDao);
			System.out.println(DateUtils.DateTimeToString(new Date()) + " " +  info + "了" + sDate + "的原始股票数据(original_stock_)！");
			log.loger.info(" " +  info + "了" + sDate + "的原始股票数据(original_stock_)！");
		}
	}
	
	public void handleDaily(String sDate, String stockCodes, String changeRates, String turnoverRates) {

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

			for (int index=0; index<codeArray.length; index++) {
				// 对数据进行转换
				DailyStock stockData = getStockData(index, codeArray, changeRateArray, turnoverRateArray, sDate);
				// 保存数据
				boolean existFlg = dailyStockDao.isExistInDailyStock(stockData);
				if (!existFlg) {
					boolean saveFlg = dailyStockDao.saveDailyStock(stockData);
					if (saveFlg) {
						dailySum++;
						System.out.println(DateUtils.DateTimeToString(new Date()) + " 每日股票数据表(daily_stock_)增加股票---" + (index+1) + "：" + stockData.getStockCode() + "(" + PropertiesUtils.getProperty(stockData.getStockCode()) + ")");
						log.loger.info(" 每日股票数据表(daily_stock_)增加股票---" + (index+1) + "：" + stockData.getStockCode() + "(" + PropertiesUtils.getProperty(stockData.getStockCode()) + ")");
						boolean saveUpdateFlg = updateStatisticStock(stockData);
						if (saveUpdateFlg) statisticSum++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  " + sDate + " 每日股票信息表(daily_stock_)共添加了" + dailySum + "条记录！");
			log.loger.info(" " + sDate + " 每日股票信息表(daily_stock_)共添加了" + dailySum + "条记录！");
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  " + sDate + " 统计股票信息表(statistic_stock_)共更新了" + statisticSum + "条记录！");
			log.loger.info(" " + sDate + " 统计股票信息表(statistic_stock_)共更新了" + statisticSum + "条记录！");
		}
	}

	private boolean updateStatisticStock(DailyStock dailyStock) throws SQLException {

		String stockCode = dailyStock.getStockCode();
		StatisticStock statisticStock = new StatisticStock(stockCode);
		setUpDownNumberAndUpDownJson(statisticStock);
		return statisticStockDao.updateStatisticStock(statisticStock);
	}

	private DailyStock getStockData(int index, String[] codes, String[] changeRates, String[] turnoverRates, String sDate) {
		DailyStock stockData = new DailyStock();
		Date stockDate = DateUtils.String2Date(sDate);
		Timestamp inputTime = new Timestamp(System.currentTimeMillis());
		stockData.setStockCode(codes[index]);
		stockData.setStockDate(stockDate);
		Double changeRate = Double.valueOf(changeRates[index]);
		stockData.setChangeRate(changeRate);
		if (turnoverRates != null) {
			Double turnoverRate = Double.valueOf(turnoverRates[index]);
			stockData.setTurnoverRate(turnoverRate);
		} else {
			stockData.setTurnoverRate(null);
		}
		String changeFlg = changeRate>0?"1":"0";
		stockData.setChangeFlg(changeFlg);
		stockData.setInputTime(inputTime);
		return stockData;
	}
}