package cn.implement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.ObjectUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticDetailStock;
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
			log.loger.warn(" " + info + "了" + sDate + "的原始股票数据(original_stock_)！");
		}
	}
	
	/**
	 * 解析原始股票数据到每日股票数据表(daily_stock_)和表(statistic_stock_)中
	 * 
	 */
	public void analysisOriginalData() {

		originalStockDao = new OriginalStockDao();
		try {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(original_stock_)解析原始股票数据到表(daily_stock_)中开始...");
			List<OriginalStock> originalStockList = originalStockDao.listOriginalData();
			for (OriginalStock originalStock : originalStockList) {
				Date stDate = originalStock.getStockDate();
				String stockDate = DateUtils.dateTimeToString(stDate);
				String stockCodes = originalStock.getStockCodes();
				String changeRates = originalStock.getChangeRates();
				String turnoverRates = originalStock.getTurnoverRates();
				try {
					handleDailyStockData(stockDate, stockCodes, changeRates, turnoverRates);
				} catch (Exception ex) {
					ex.printStackTrace();
					continue;
				}
			}
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(daily_stock_)统计股票数据到表(statistic_stock_)表中结束...");
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			closeDao(originalStockDao);
		}
	}
	
	/**
	 * 解析原始股票数据到每日股票数据表(daily_stock_)、表(statistic_stock_)和表(statistic_detail_stock_)中
	 *
	 */
	public void handleDailyStockData(String sDate, String stockCodes, String changeRates, String turnoverRates) {

		int dailySum = 0;
		int statisticSum = 0;
		int statisticDetailSum = 0;
		try {
			dailyStockDao = new DailyStockDao();
			statisticStockDao = new StatisticStockDao();
			statisticDetailStockDao = new StatisticDetailStockDao();
			List<DailyStock> dailyStockList = new ArrayList<DailyStock>();
			String[] codeArray = stockCodes.split(",");
			String[] changeRateArray = changeRates.split(",");
			String[] turnoverRateArray = null;
			if (!CommonUtils.isBlank(turnoverRates)) {
				turnoverRateArray = turnoverRates.split(",");
			}
			System.out.println("-----------------------增加每日股票数据(daily_stock_)---------------------------");
			// 增加每日股票数据(daily_stock_)
			for (int index = 0; index < codeArray.length; index++) {
				// 对数据进行转换
				DailyStock dailyStock = CommonUtils.getDailyStockFromArray(index, codeArray, changeRateArray, turnoverRateArray, sDate);
				// 保存输入的股票数据
				boolean existFlg = dailyStockDao.isExistInDailyStock(dailyStock);
				if (!existFlg) {
					boolean saveFlg = dailyStockDao.saveDailyStock(dailyStock);
					if (saveFlg) {
						dailySum++;
						dailyStockList.add(dailyStock);
						System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(daily_stock_)增加了股票  " + dailySum + "："
								+ dailyStock.getStockCode() + "(" + PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
						log.loger.warn("----->表(daily_stock_)增加了股票  " + dailySum + "：" + dailyStock.getStockCode() + "("
								+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
					}
				}
			}
			System.out.println("-----------------------增加统计股票数据(statistic_stock_)---------------------------");
			// 增加统计股票数据(statistic_stock_)
			for (DailyStock dailyStock : dailyStockList) {
				boolean saveStatisticFlg = saveStatisticStock(dailyStock);
				if (saveStatisticFlg) {
					++statisticSum;
					System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>表(statistic_stock_)增加了第" + statisticSum + "只股票：" + dailyStock.getStockCode() + "("
							+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + "), 统计日: " + DateUtils.dateToString(dailyStock.getStockDate()));
					log.loger.warn("=====>表(statistic_stock_)增加了第" + statisticSum + "只股票：" + dailyStock.getStockCode() + "("
							+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + "), 统计日: " + DateUtils.dateToString(dailyStock.getStockDate()));
				}
			}
			System.out.println("-----------------------增加每日股票涨跌次数统计数据(statistic_detail_stock_)---------------------------");
			// 增加每日股票涨跌次数统计数据(statistic_detail_stock_)
			for (DailyStock dailyStock : dailyStockList) {
				boolean saveStatisticDetailFlg = saveStatisticDetailStock(dailyStock);
				if (saveStatisticDetailFlg) {
					++statisticDetailSum;
					System.out.println(DateUtils.dateTimeToString(new Date()) + ">>>>>>表(statistic_detail_stock_)增加了股票  " + statisticDetailSum + "："
							+ dailyStock.getStockCode() + "(" + PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
					log.loger.warn(">>>>>>表(statistic_detail_stock_)增加了股票  " + statisticDetailSum + "："
							+ dailyStock.getStockCode() + "(" + PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, statisticStockDao, statisticDetailStockDao);
			System.out.println("");
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + sDate + " 表(daily_stock_)共增加了" + dailySum + "条记录！");
			log.loger.warn("----->" + sDate + " 表(daily_stock_)共增加了" + dailySum + "条记录！");
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + sDate + " 表(statistic_stock_)共增加了" + statisticSum + "条记录！");
			log.loger.warn("----->" + sDate + " 表(statistic_stock_)共增加了" + statisticSum + "条记录！");
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + sDate + " 表(statistic_detail_stock_)共更新了" + statisticDetailSum + "条记录！");
			log.loger.warn("----->" + sDate + " 表(statistic_detail_stock_)共更新了" + statisticDetailSum + "条记录！");
		}
	}

	private boolean saveStatisticStock(DailyStock dailyStock) throws SQLException {

		String stockCode = dailyStock.getStockCode();
		boolean saveFlg = false;
		if (!statisticStockDao.isExistInStatisticStock(stockCode)) {
			Date stockDate = dailyStock.getStockDate();
			String stockCodeDes = DESUtils.encryptToHex(stockCode);
			StatisticStock statisticStock = new StatisticStock(stockCode, stockDate, stockCodeDes);
			Long maxNum = statisticStockDao.getMaxNum(StatisticStock.TABLE_NAME);
			statisticStock.setNum(++maxNum);
			statisticStock.setNote(DataUtils._BLANK);
			statisticStock.setInputTime(new Date());
			saveFlg = statisticStockDao.saveStatisticStock(statisticStock);
		}
		return saveFlg;
	}
	
	private boolean saveStatisticDetailStock(DailyStock dailyStock) throws SQLException {
		
		String stockCode = dailyStock.getStockCode();
		Date stockDate = dailyStock.getStockDate();
		boolean saveFlg = false;
		long maxNum = statisticDetailStockDao.getMaxNum(StatisticDetailStock.TABLE_NAME);
		if (!statisticDetailStockDao.isExistInStatisticDetailStock(stockCode, stockDate)) {
			StatisticDetailStock statisticDetailStock = new StatisticDetailStock(stockCode, stockDate);
			statisticDetailStock.setStockCodeDES(DESUtils.encryptToHex(stockCode));
			// 统计日期
			statisticDetailStock.setStockDate(stockDate);
			// 总涨跌次数
			Map<String, Integer> upDownMap = getUpAndDownNumber(stockCode);
			statisticDetailStock.setUpDownNumber(upDownMap.get(StatisticDetailStock.UP_DOWN_KEY));
			statisticDetailStock.setUpNumber(upDownMap.get(StatisticDetailStock.UP_KEY));
			statisticDetailStock.setDownNumber(upDownMap.get(StatisticDetailStock.DOWN_KEY));
			// 前一周的涨跌次数
			String preOneWeekUpAndDownJson = getPreOneWeekJson(stockCode, stockDate);
			statisticDetailStock.setOneWeek(preOneWeekUpAndDownJson);
			// 前半月的涨跌次数
			String preHalfMonthUpAndDownJson = getPreHalfMonthJson(stockCode, stockDate);
			statisticDetailStock.setHalfMonth(preHalfMonthUpAndDownJson);
			// 前一月的涨跌次数
			String preOneMonthUpAndDownJson = getPreOneMonthJson(stockCode, stockDate);
			statisticDetailStock.setOneMonth(preOneMonthUpAndDownJson);
			// 前二月的涨跌次数
			String preTwoMonthUpAndDownJson = getPreTwoMonthJson(stockCode, stockDate);
			statisticDetailStock.setTwoMonth(preTwoMonthUpAndDownJson);
			// 前三月的涨跌次数
			String preThreeMonthUpAndDownJson = getPreThreeMonthJson(stockCode, stockDate);
			statisticDetailStock.setThreeMonth(preThreeMonthUpAndDownJson);
			// 前半年的涨跌次数
			String preHalfYearUpAndDownJson = getPreHalfYearJson(stockCode, stockDate);
			statisticDetailStock.setHalfYear(preHalfYearUpAndDownJson);
			// 前一年的涨跌次数
			String preOneYearUpAndDownJson = getPreOneYearJson(stockCode, stockDate);
			statisticDetailStock.setOneYear(preOneYearUpAndDownJson);
			statisticDetailStock.setNote(DataUtils._BLANK);
			statisticDetailStock.setInputTime(new Date());
			boolean isExistFlg = statisticDetailStockDao.isExistInStatisticDetailStock(stockCode, stockDate);
			if (!isExistFlg) {
				statisticDetailStock.setNum(++maxNum);
				//ObjectUtils.printProperties(statisticDetailStock);
				saveFlg = statisticDetailStockDao.saveStatisticDetailStock(statisticDetailStock);
			}
		}
		return saveFlg;
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