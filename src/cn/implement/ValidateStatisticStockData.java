package cn.implement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.JsonUtils;
import cn.com.PropertiesUtils;
import cn.com.StockUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.BaseStock;
import cn.db.bean.DailyStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticDetailStock;
import cn.db.bean.StatisticStock;
//import cn.db.bean.StatisticDetailStock;
//import cn.db.bean.StatisticStock;
import cn.log.Message;

public class ValidateStatisticStockData extends OperationData{
	
	/**
	 * 验证统计股票表(statistic_stock_)中的数据
	 * 
	 */
	public void validateStatisticStockData() {
		dailyStockDao = new DailyStockDao();
		originalStockDao = new OriginalStockDao();
		statisticStockDao = new StatisticStockDao();
		try {
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			if (statisticStockList.size() > 0) {
				// 验证stock_code_ DES加密
				boolean stockCodeFLg = validateStockCodeDESInStatisticStock(statisticStockList);
				// 验证stock_code_和first_date_(与daily_stock_表比较)
				boolean codeAndDateInDailyStockFlg = validateStockCodeAndFirstDateInDailyStock(statisticStockList);
				// 验证stock_code_和first_date_(与original_stock_表比较)
				boolean codeAndDateInOriginalStockFlg = validateStockCodeAndFirstDateInOriginalStock(statisticStockList);
				if (stockCodeFLg && codeAndDateInDailyStockFlg && codeAndDateInOriginalStockFlg) {
					System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>表(statistic_stock_)数据验证成功！" + CommonUtils.getLineBreak());
					log.loger.warn(DateUtils.dateTimeToString(new Date()) + "=====>表(statistic_stock_)数据验证成功！" + CommonUtils.getLineBreak());
				}
			} else {
				System.out.println("表(statistic_stock_)中没有股票数据！");
				log.loger.warn("表(statistic_stock_)中没有股票数据！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, originalStockDao, statisticStockDao);
		}
	}

	/**
	 * 验证stock_code_和first_date_(与original_stock_表比较)
	 *
	 */
	private boolean validateStockCodeAndFirstDateInOriginalStock(List<StatisticStock> statisticStockList) throws SQLException {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_stock_)中的stock_code_和first_date_(与original_stock_表比较)的正确性验证开始...");
		Message.clear();
		List<OriginalStock> originalStockList = originalStockDao.listOriginalData();
		Map<String, DailyStock> dailyStockMap = StockUtils.statisticStockCodeAndFirstDate(originalStockList); //get stockCode and firstDate
		Collection<DailyStock> dailyStockCollection = dailyStockMap.values();
		List<DailyStock> dailyStockList = new ArrayList<DailyStock>(dailyStockCollection);
		List<String[]> invalidList = listErrorStockCodeAndFirstDate(statisticStockList, dailyStockList, OriginalStock.TABLE_NAME);
		if (!Message.methodExecuteMessageIsEmpty())
			Message.printMethodExecuteMessage();
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------验证表(statistic_stock_)的字段(stock_code_和first_date_)无效(与original_stock_表比较)----------------");
			log.loger.warn("---------------验证表(statistic_stock_)的字段(stock_code_和first_date_)无效(与original_stock_表比较)----------------");
			for (int index=0; index<invalidList.size(); index++) {
				String[] data = invalidList.get(index);
				System.out.println("验证出的无效数据---" + (index+1) + ": 股票" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
													   + ") 表(statistic_stock_)错误的stock_code_=" + data[0] + " first_date_=" + data[1]
													   + " 表(daily_stock_)正确的stock_code_=" + data[2] + " first_date_=" + data[3]);

				log.loger.warn("验证出的无效数据---" + (index+1) + ": 股票" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
												   + ") 表(statistic_stock_)错误的stock_code_=" + data[0] + " first_date_=" + data[1]
												   + " 表(daily_stock_)正确的stock_code_=" + data[2] + " first_date_=" + data[3]);
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_stock_)中的stock_code_和first_date_(与original_stock_表比较)的正确性验证成功！");
		}
		return flg;
	}
	
	/**
	 * 验证stock_code_和first_date_(与daily_stock_表比较)
	 *
	 */
	private boolean validateStockCodeAndFirstDateInDailyStock(List<StatisticStock> statisticStockList) throws SQLException {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_stock_)中的字段(stock_code_和first_date_)的正确性验证开始...");
		List<DailyStock> dailyStockList = dailyStockDao.statisticDailyStock();
		List<String[]> invalidList = listErrorStockCodeAndFirstDate(statisticStockList, dailyStockList, DailyStock.TABLE_NAME);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------验证表(statistic_stock_)的字段(stock_code_和first_date_)无效(与daily_stock_表比较)----------------");
			log.loger.warn("---------------验证表(statistic_stock_)的字段(stock_code_和first_date_)无效(与daily_stock_表比较)----------------");
			for (int index=0; index<invalidList.size(); index++) {
				String[] data = invalidList.get(index);
				System.out.println("验证出的无效数据----->" + (index+1) + ": 股票" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
													   + ") 表(statistic_stock_)错误的stock_code_=" + data[0] + " first_date_=" + data[1]
													   + " 表(daily_stock_)正确的stock_code_=" + data[2] + " first_date_=" + data[3]);

				log.loger.warn("验证出的无效数据----->" + (index+1) + ": 股票" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
												   + ") 表(statistic_stock_)错误的stock_code_=" + data[0] + " first_date_=" + data[1]
												   + " 表(daily_stock_)正确的stock_code_=" + data[2] + " first_date_=" + data[3]);
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_stock_)中的字段(stock_code_和first_date_)的正确性验证成功！");
		}
		return flg;
	}

	private List<String[]> listErrorStockCodeAndFirstDate(List<StatisticStock> statisticStockList, List<DailyStock> dailyStockList, String tableFlg) throws SQLException {
		
		List<String[]> errorCodeAndDateList = new ArrayList<String[]>();
		try {
			for (StatisticStock statisticStock : statisticStockList) {
				String stockCode = statisticStock.getStockCode();
				Date firstDate = statisticStock.getFirstDate();
				// 比较stock_code_
				DailyStock dailyStock = CommonUtils.containStockCode(stockCode, dailyStockList);
				if (dailyStock != null) {
					// 比较first_date_
					if (firstDate.compareTo(dailyStock.getStockDate()) != 0) {
						String[] errorCodeAndDateArray = new String[4];
						errorCodeAndDateArray[0] = stockCode; //wrong stockCode
						errorCodeAndDateArray[1] = DateUtils.dateToString(firstDate); //wrong firstDate
						errorCodeAndDateArray[2] = dailyStock.getStockCode(); //correct stockCode
						errorCodeAndDateArray[3] = DateUtils.dateToString(dailyStock.getStockDate()); //correct stockDate
						errorCodeAndDateList.add(errorCodeAndDateArray);
					}
				} else {
					System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_stock_)中的股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")不存在表(" + tableFlg + ")中！");
					log.loger.warn("----->表(statistic_stock_)中的股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")不存在表(" + tableFlg + ")中！");
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return errorCodeAndDateList;
	}

	/**
	 * 验证statistic_detail_stock_表中总涨跌次数
	 *
	 */
	/*private StatisticDetailStock validateUpAndDownNumber(StatisticDetailStock statisticUpAndDownStock, StatisticDetailStock statisticDetailStock) {

		StatisticDetailStock errorStatisticDetailStock = null;
		try {
			String stockCode = statisticDetailStock.getStockCode();
			Integer upDownNumber = statisticDetailStock.getUpDownNumber();
			Integer upNumber = statisticDetailStock.getUpNumber();
			Integer downNumber = statisticDetailStock.getDownNumber();
			Integer statisticUpDownNumber = statisticUpAndDownStock.getUpDownNumber();
			Integer statisticUpNumber = statisticUpAndDownStock.getUpNumber();
			Integer statisticDownNumber = statisticUpAndDownStock.getDownNumber();
			if (statisticUpDownNumber.compareTo(upDownNumber) != 0 
					|| statisticUpNumber.compareTo(upNumber) != 0
					|| statisticDownNumber.compareTo(downNumber) != 0) {
				errorStatisticDetailStock = new StatisticDetailStock();
				errorStatisticDetailStock.setStockCode(stockCode);
				errorStatisticDetailStock.setUpDownNumber(statisticUpDownNumber);
				errorStatisticDetailStock.setUpNumber(statisticUpNumber);
				errorStatisticDetailStock.setDownNumber(statisticDownNumber);
				errorStatisticDetailStock.setErrorUpDownNumber(upDownNumber);
				errorStatisticDetailStock.setErrorUpNumber(upNumber);
				errorStatisticDetailStock.setErrorDownNumber(downNumber);
			}
		} catch (Exception ex) {
			System.out.println("验证统计股票信息表(statistic_stock_)中的股票" + statisticDetailStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(statisticDetailStock.getStockCode()) + ")涨跌次数异常！");
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return errorStatisticDetailStock;
	}*/
	
	/**
	 * 验证表(statistic_stock_)中的stock_code_ DES加密
	 *
	 */
	private boolean validateStockCodeDESInStatisticStock(List<StatisticStock> stockList) {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_stock_)中字段(stock_code_)的 DES加密验证开始...");
		List<StatisticStock> invalidList = CommonUtils.listErrorStockCodeDES(stockList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------DES验证统计股票信息表(statistic_stock_)无效数据----------------");
			log.loger.warn("---------------DES验证统计股票信息表(statistic_stock_)无效数据----------------");
			for (int index=0; index<invalidList.size(); index++) {
				StatisticStock data = invalidList.get(index);
				System.out.println("DES验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
														  + ") 未加密的stock_code_=" + data.getStockCode() + " 解密的stock_code=" + DESUtils.decryptHex(data.getStockCodeDES()));

				log.loger.warn("DES验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
													  + ") 未加密的stock_code_=" + data.getStockCode() + " 解密的stock_code=" + DESUtils.decryptHex(data.getStockCodeDES()));
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_stock_)中字段(stock_code_)的 DES加密验证成功！");
		}
		return flg;
	}

}
