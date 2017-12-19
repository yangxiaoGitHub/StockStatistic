package cn.implement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticStock;

public class ValidateStatisticStockData extends OperationData{

	/**
	 * 验证统计股票数据，并输出无效数据
	 * 
	 */
	public void validateStatisticStockData() {
		statisticStockDao = new StatisticStockDao();
		try {
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			if (statisticStockList.size() > 0) {
				// 验证stock_code_ DES加密
				boolean stockCodeFLg = validateStockCodeDES(statisticStockList);
				// 验证stock_code_和first_date_
				boolean stockCodeFirstDateFlg = validateStockCodeAndFirstDate(statisticStockList);
				// 验证股票出现的涨跌、涨、跌次数
				boolean upDownNumberFlg = validateUpAndDownNumber(statisticStockList);
				if (stockCodeFLg && stockCodeFirstDateFlg && upDownNumberFlg) {
					System.out.println("统计股票数据(statistic_stock_)验证成功！");
					log.loger.info("统计股票数据(statistic_stock_)验证成功！");
				}
			} else {
				System.out.println("数据库中没有统计股票数据！");
				log.loger.info("数据库中没有统计股票数据！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(statisticStockDao);
		}
	}

	private boolean validateUpAndDownNumber(List<StatisticStock> statisticStockList) throws SQLException {
		
		boolean flg = true;
	    Map<String, StatisticStock> upDownAndNumberMap = combineUpAndDownNumberMap();
	    // 合并涨跌次数验证
		/*List<StatisticStock> invalidUpDownList = listErrorUpDownNumber(statisticStockList, upDownAndNumberMap);
		List<StatisticStock> invalidUpList = listErrorUpNumber(statisticStockList, upDownAndNumberMap);
		List<StatisticStock> invalidDownList = listErrorDownNumber(statisticStockList, upDownAndNumberMap);
		List<StatisticStock> invalidSumList = listErrorSum(statisticStockList);*/
		
		List<StatisticStock> invalidUpAndDownList = listErrorUpAndDownNumber(statisticStockList, upDownAndNumberMap);
		if (invalidUpAndDownList.size() > 0) {
			flg = false;
			System.out.println("---------------验证统计股票信息表(statistic_stock_)的涨跌次数无效----------------");
			log.loger.info("---------------验证统计股票信息表(statistic_stock_)的涨跌次数无效----------------");
			for (int index=0; index<invalidUpAndDownList.size(); index++) {
				StatisticStock data = invalidUpAndDownList.get(index);
				System.out.println("验证涨跌次数无效---" + CommonUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode() 
								 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")在统计股票信息表(statistic_stock_)中的数据为: " + StatisticStock.UP_DOWN_NUMBER + ":" 
								 + data.getErrorUpDownNumber() + "," + StatisticStock.UP_NUMBER + ":" + data.getErrorUpNumber() + "," + StatisticStock.DOWN_NUMBER + ":" + data.getErrorDownNumber() 
								 + "  在每日股票信息表中的统计数据为: " + StatisticStock.UP_DOWN_NUMBER + ":" + data.getUpDownNumber() + "," + StatisticStock.UP_NUMBER + ":" + data.getUpNumber() 
								 + "," + StatisticStock.DOWN_NUMBER + ":" + data.getDownNumber());
				log.loger.info("验证涨跌次数无效---" + CommonUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode() 
								 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")在统计股票信息表(statistic_stock_)中的数据为: " + StatisticStock.UP_DOWN_NUMBER + ":" 
								 + data.getErrorUpDownNumber() + "," + StatisticStock.UP_NUMBER + ":" + data.getErrorUpNumber() + "," + StatisticStock.DOWN_NUMBER + ":" + data.getErrorDownNumber() 
								 + "  在每日股票信息表中的统计数据为: " + StatisticStock.UP_DOWN_NUMBER + ":" + data.getUpDownNumber() + "," + StatisticStock.UP_NUMBER + ":" + data.getUpNumber() 
								 + "," + StatisticStock.DOWN_NUMBER + ":" + data.getDownNumber());
			}
		}
		return flg;
	}

	private boolean validateStockCodeAndFirstDate(List<StatisticStock> statisticStockList) throws SQLException {
		
		boolean flg = true;
		List<String[]> invalidList = listErrorStockCodeAndFirstDate(statisticStockList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------验证统计股票信息表(statistic_stock_)的字段(stock_code_和first_date_)无效----------------");
			log.loger.info("---------------验证统计股票信息表(statistic_stock_)的字段(stock_code_和first_date_)无效----------------");
			for (int index=0; index<invalidList.size(); index++) {
				String[] data = invalidList.get(index);
				System.out.println("验证出的无效数据---" + (index+1) + ": 股票" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
													   + ") 表(statistic_stock_)错误的stock_code_=" + data[0] + " first_date_=" + data[1]
													   + " 表(daily_stock_)正确的stock_code_=" + data[2] + " first_date_=" + data[3]);

				log.loger.info("验证出的无效数据---" + (index+1) + ": 股票" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
												   + ") 表(statistic_stock_)错误的stock_code_=" + data[0] + " first_date_=" + data[1]
												   + " 表(daily_stock_)正确的stock_code_=" + data[2] + " first_date_=" + data[3]);
			}
		}
		return flg;
	}

	private List<String[]> listErrorStockCodeAndFirstDate(List<StatisticStock> statisticStockList) throws SQLException {
		
		List<String[]> errorCodeAndDateList = new ArrayList<String[]>();
		dailyStockDao = new DailyStockDao();
		try {
			List<DailyStock> dailyStockList = dailyStockDao.statisticDailyStock();
			for (StatisticStock statisticStock : statisticStockList) {
				String stockCode = statisticStock.getStockCode();
				Date firstDate = statisticStock.getFirstDate();
				// 比较stock_code_
				DailyStock dailyStock_stockCode = containStockCode(stockCode, dailyStockList);
				if (dailyStock_stockCode != null) {
					// 比较first_date_
					if (firstDate.compareTo(dailyStock_stockCode.getStockDate()) != 0) {
						String[] errorCodeAndDateArray = new String[4];
						errorCodeAndDateArray[0] = stockCode;
						errorCodeAndDateArray[1] = DateUtils.dateToString(firstDate);
						errorCodeAndDateArray[2] = dailyStock_stockCode.getStockCode();
						errorCodeAndDateArray[3] = DateUtils.dateToString(dailyStock_stockCode.getStockDate());
						errorCodeAndDateList.add(errorCodeAndDateArray);
					}
				} else {
					String[] errorCodeAndDateArray = new String[4];
					errorCodeAndDateArray[0] = stockCode;
					errorCodeAndDateArray[1] = DateUtils.dateToString(firstDate);
					errorCodeAndDateArray[2] = "";
					errorCodeAndDateArray[3] = "";
					errorCodeAndDateList.add(errorCodeAndDateArray);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			log.loger.error(ex);
		} finally {
			closeDao(dailyStockDao);
		}
		return errorCodeAndDateList;
	}
	
	private Map<String, StatisticStock> combineUpAndDownNumberMap() throws SQLException {

		dailyStockDao = new DailyStockDao();
		Map<String, StatisticStock> statisticStockMap = new HashMap<String, StatisticStock>();
		try {
			Map<String, StatisticStock> upDownNumberMap = dailyStockDao.statisticUpDownInDailyStock();
			Map<String, StatisticStock> upNumberMap = dailyStockDao.statisticUpInDailyStock();
			Map<String, StatisticStock> downNumberMap = dailyStockDao.statisticDownInDailyStock();
			statisticStockMap = combineUpAndDownNumber(upDownNumberMap, upNumberMap, downNumberMap);
		} catch(Exception ex) {
			ex.printStackTrace();
			log.loger.error(ex);
		}
		return statisticStockMap;
	}

	private Map<String, StatisticStock> combineUpAndDownNumber(Map<String, StatisticStock> upDownNumberMap, 
															   Map<String, StatisticStock> upNumberMap,
															   Map<String, StatisticStock> downNumberMap) {

		Map<String, StatisticStock> statisticStockMap = new HashMap<String, StatisticStock>();
		//初始化涨和跌次数
		for (StatisticStock statisticStock : upDownNumberMap.values()) {
			statisticStock.setUpNumber(DataUtils.CONSTANT_INTEGER_ZERO);
			statisticStock.setDownNumber(DataUtils.CONSTANT_INTEGER_ZERO);
			statisticStockMap.put(statisticStock.getStockCode(), statisticStock);
		}
		// 合并upNumberMap
		for (StatisticStock statisticStock : upNumberMap.values()) {
			String stockCode = statisticStock.getStockCode();
			Integer upNumber = statisticStock.getUpNumber();
			if (statisticStockMap.containsKey(stockCode)) {
				StatisticStock stock = statisticStockMap.get(stockCode);
				stock.setUpNumber(upNumber);
			} else {
				statisticStockMap.put(stockCode, statisticStock);
			}
		}
		// 合并downNumberMap
		for (StatisticStock statisticStock : downNumberMap.values()) {
			String stockCode = statisticStock.getStockCode();
			Integer downNumber = statisticStock.getDownNumber();
			if (statisticStockMap.containsKey(stockCode)) {
				StatisticStock stock = statisticStockMap.get(stockCode);
				stock.setDownNumber(downNumber);
			} else {
				statisticStockMap.put(stockCode, statisticStock);
			}
		}
		return statisticStockMap;
	}

	/*private List<StatisticStock> listErrorUpDownNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticStockMap) throws SQLException {
		
		List<StatisticStock> errorUpDownNumberList = new ArrayList<StatisticStock>();
		for (StatisticStock statisticStock : statisticStockList) {
			String stockCode = statisticStock.getStockCode();
			Integer upDownNumber = statisticStock.getUpDownNumber();
			StatisticStock errorStatisticStock = compareUpAndDownNumber(stockCode, upDownNumber, statisticStockMap, 2);
			if (errorStatisticStock != null) {
				errorStatisticStock.setUpDownFlg(StatisticStock.UP_DOWN_FLG);
				errorUpDownNumberList.add(errorStatisticStock);
			}
		}
		return errorUpDownNumberList;
	}*/
	
	/*private List<StatisticStock> listErrorUpNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticStockMap) throws SQLException {
		
		List<StatisticStock> errorUpNumberList = new ArrayList<StatisticStock>();
		for (StatisticStock statisticStock : statisticStockList) {
			String stockCode = statisticStock.getStockCode();
			Integer upNumber = statisticStock.getUpNumber();
			StatisticStock errorStatisticStock = compareUpAndDownNumber(stockCode, upNumber, statisticStockMap, 3);
			if (errorStatisticStock != null) {
				errorStatisticStock.setUpDownFlg(StatisticStock.UP_FLG);
				errorUpNumberList.add(errorStatisticStock);
			}
		}
		return errorUpNumberList;
	}*/
	
	/*private List<StatisticStock> listErrorDownNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticStockMap) throws SQLException {
		
		List<StatisticStock> errorDownNumberList = new ArrayList<StatisticStock>();
		for (StatisticStock statisticStock : statisticStockList) {
			String stockCode = statisticStock.getStockCode();
			Integer downNumber = statisticStock.getDownNumber();
			StatisticStock errorStatisticStock = compareUpAndDownNumber(stockCode, downNumber, statisticStockMap, 1);
			if (errorStatisticStock != null) {
				errorStatisticStock.setUpDownFlg(StatisticStock.DOWN_FLG);
				errorDownNumberList.add(errorStatisticStock);
			}
		}
		return errorDownNumberList;
	}*/
	
	/**
	 * 验证涨跌次数，返回错误涨跌次数List 
	 *
	 */
	private List<StatisticStock> listErrorUpAndDownNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticUpAndDownMap) {

		List<StatisticStock> errorUpAndDownNumberList = new ArrayList<StatisticStock>();
		for (StatisticStock statisticStock : statisticStockList) {
			String stockCode = statisticStock.getStockCode();
			StatisticStock statisticUpAndDownStock = statisticUpAndDownMap.get(stockCode);
			//验证统计股票信息的涨跌次数
			StatisticStock errorStatisticStock = validateUpAndDownNumber(statisticUpAndDownStock, statisticStock);
			if (errorStatisticStock != null) {
				errorUpAndDownNumberList.add(errorStatisticStock);
			}
		}
		return errorUpAndDownNumberList;
	}
	
	private StatisticStock validateUpAndDownNumber(StatisticStock statisticUpAndDownStock, StatisticStock statisticStock) {

		StatisticStock errorStatisticStock = null;
		try {
			String stockCode = statisticStock.getStockCode();
			Integer upDownNumber = statisticStock.getUpDownNumber();
			Integer upNumber = statisticStock.getUpNumber();
			Integer downNumber = statisticStock.getDownNumber();
			if (statisticUpAndDownStock == null) {
				System.out.println("统计股票信息表(statistic_stock_)中" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")不存在每日股票信息表(daily_stock_)中！");
				log.loger.info("统计股票信息表(statistic_stock_)中" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")不存在每日股票信息表(daily_stock_)中！");
			} else {
				Integer statisticUpDownNumber = statisticUpAndDownStock.getUpDownNumber();
				Integer statisticUpNumber = statisticUpAndDownStock.getUpNumber();
				Integer statisticDownNumber = statisticUpAndDownStock.getDownNumber();
				if (statisticUpDownNumber.compareTo(upDownNumber)!=0 
						|| statisticUpNumber.compareTo(upNumber)!=0 
						|| statisticDownNumber.compareTo(downNumber)!=0) {
					errorStatisticStock = new StatisticStock();
					errorStatisticStock.setStockCode(stockCode);
					errorStatisticStock.setUpDownNumber(statisticUpDownNumber);
					errorStatisticStock.setUpNumber(statisticUpNumber);
					errorStatisticStock.setDownNumber(statisticDownNumber);
					errorStatisticStock.setErrorUpDownNumber(upDownNumber);
					errorStatisticStock.setErrorUpNumber(upNumber);
					errorStatisticStock.setErrorDownNumber(downNumber);
				}
			}
		} catch(Exception ex) {
			System.out.println("验证统计股票信息表(statistic_stock_)中的股票" + statisticStock.getStockCode() + "(" + PropertiesUtils.getProperty(statisticStock.getStockCode()) + ")涨跌次数异常！");
			ex.printStackTrace();
			log.loger.error(ex);
		}
		return errorStatisticStock;
	}
	
	private DailyStock containStockCode(String stockCode, List<DailyStock> dailyStockList) {

		DailyStock reDailyStock = null;
		for (DailyStock dailyStock : dailyStockList) {
			String stockCode_ = dailyStock.getStockCode();
			if (stockCode.equals(stockCode_)) {
				reDailyStock = dailyStock;
				break;
			}
		}
		return reDailyStock;
	}

	private boolean validateStockCodeDES(List<StatisticStock> statisticStockList) {
		
		boolean flg = true;
		List<StatisticStock> invalidList = listErrorStockCodeDES(statisticStockList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------DES验证统计股票信息表(statistic_stock_)无效数据----------------");
			log.loger.info("---------------DES验证统计股票信息表(statistic_stock_)无效数据----------------");
			for (int index=0; index<invalidList.size(); index++) {
				StatisticStock data = invalidList.get(index);
				System.out.println("DES验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
														  + ") 未加密的stock_code_=" + data.getStockCode() + " 解密的stock_code=" + data.getDecryptStockCode());

				log.loger.info("DES验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
													  + ") 未加密的stock_code_=" + data.getStockCode() + " 解密的stock_code=" + data.getDecryptStockCode());
			}
		}
		return flg;
	}

	/*private List<StatisticStock> listErrorSum(List<StatisticStock> statisticStockList) {
		
		List<StatisticStock> errorSumList = new ArrayList<StatisticStock>();
		for (StatisticStock statisticStock : statisticStockList) {
			Integer upDownNum = statisticStock.getUpDownNumber();
			Integer upNum = statisticStock.getUpNumber();
			Integer downNum = statisticStock.getDownNumber();
			if (upDownNum != upNum + downNum) {
				statisticStock.setUpDownFlg(StatisticStock.ERROR_SUM_FLG);
				errorSumList.add(statisticStock);
			}
		}
		return errorSumList;
	}*/

	private List<StatisticStock> listErrorStockCodeDES(List<StatisticStock> statisticStockList) {

		List<StatisticStock> invalidList = new ArrayList<StatisticStock>();
		for (StatisticStock data : invalidList) {
			String stockCode = DESUtils.decryptHex(data.getStockCodeDES());
			if (!stockCode.equals(data.getStockCode())) {
				data.setDecryptStockCode(stockCode);
				invalidList.add(data);
			}
		}
		return invalidList;
	}
}
