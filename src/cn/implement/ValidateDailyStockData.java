package cn.implement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.com.StockUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllImportStockDao;
import cn.db.DailyStockDao;
import cn.db.HistoryStockDao;
import cn.db.OriginalStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllImportStock;
import cn.db.bean.DailyStock;
import cn.db.bean.HistoryStock;
import cn.db.bean.OriginalStock;

public class ValidateDailyStockData extends OperationData {

	/**
	 * 验证每日股票数据，并输出无效数据
	 * 
	 */
	public void validateDailyStockData() {
		dailyStockDao = new DailyStockDao();
		originalStockDao = new OriginalStockDao();
		allDetailStockDao = new AllDetailStockDao();
		allImportStockDao = new AllImportStockDao();
		historyStockDao = new HistoryStockDao();
		try {
			List<DailyStock> dailyList = dailyStockDao.listDailyData();
			if (dailyList.size() > 0) {
				// 验证每日股票数据是否在原始股票数据中
				boolean existFlg = validateDailyStockExist(dailyList);
				// 验证数据是否正确
				boolean codeChangRateFlg = validateStockCodeAndChangeRate(dailyList);
				// 验证涨跌标识
				boolean changFlg = validateChangFlg(dailyList);
				// 验证每日股票数据的数量
				boolean numberFlg = validateDailyStockNumber();
				// 验证每日股票数据的涨跌幅和换手率
				boolean changeTurnoverFlg = validateChangeRateAndTurnoverRate(dailyList);
				if (existFlg && codeChangRateFlg && changFlg && numberFlg && changeTurnoverFlg) {
					System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>每日股票数据(daily_stock_)验证成功！" + CommonUtils.getLineBreak());
					log.loger.warn(DateUtils.dateTimeToString(new Date()) + "=====>每日股票数据(daily_stock_)验证成功！" + CommonUtils.getLineBreak());
				}
			} else {
				System.out.println("数据库中没有每日股票数据！");
				log.loger.warn("数据库中没有每日股票数据！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, originalStockDao, allDetailStockDao, allImportStockDao, historyStockDao);
		}
	}
	
	private boolean validateChangeRateAndTurnoverRate(List<DailyStock> dailyList) throws SQLException, IOException {
		
		System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>表(daily_stock_)中每日股票的涨跌幅、换手率验证开始...");
		String stockCodes = getStockCodesFromList(dailyList);
		Date[] minMaxDate = getMinMaxDateFromList(dailyList);
		// 与表(all_detail_stock_)比较
		List<AllDetailStock> detailStockList = allDetailStockDao.getAllDetailStockByCodesAndTime(stockCodes, minMaxDate);
		Map<String, AllDetailStock> allDetailStockMap = (Map<String, AllDetailStock>) CommonUtils.convertListToMap(detailStockList, false);
		List<String[]> allDetailSotckInvalidList = getErrorListFromAllDetailStock(dailyList, allDetailStockMap);
		// 与表(all_import_stock_)比较
		List<AllImportStock> allImportStockList = allImportStockDao.getAllImportStockByCodesAndDate(stockCodes, minMaxDate);
		Map<String, AllImportStock> allImportStockMap = (Map<String, AllImportStock>) CommonUtils.convertListToMap(allImportStockList, false);
		List<String[]> allImportStockInvalidList = getErrorListFromAllImportStock(dailyList, allImportStockMap);
		// 与表(history_stock_)比较
		List<HistoryStock> historyStockList = historyStockDao.getHistoryStockByCodesAndTime(stockCodes, minMaxDate);
		Map<String, HistoryStock> historyStockMap = (Map<String, HistoryStock>) CommonUtils.convertListToMap(historyStockList, false);
		List<String[]> historyStockInvalidList = getErrorListFromHistoryStock(dailyList, historyStockMap);
		boolean printFLg = printInvalidList(allDetailSotckInvalidList, allImportStockInvalidList, historyStockInvalidList);
		if (!printFLg)
			System.out.println(DateUtils.dateTimeToString(new Date()) + ">>>>>>>表(daily_stock_)中每日股票的涨跌幅、换手率验证成功！");
		System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>表(daily_stock_)中每日股票的涨跌幅、换手率验证结束...");
		return false;
	}

	private boolean printInvalidList(List<String[]> allDetailSotckInvalidList, 
									 List<String[]> allImportStockInvalidList,
									 List<String[]> historyStockInvalidList) {

		boolean flg = false;
		if (allDetailSotckInvalidList.size() > 0) {
			System.out.println("---------------表(daily_stock_)涨跌幅和换手率验证无效数据(与表(all_detail_stock_)比较)----------------");
			log.loger.warn("---------------表(daily_stock_)涨跌幅和换手率验证无效数据(与表(all_detail_stock_)比较)----------------");
			for (int index = 0; index < allDetailSotckInvalidList.size(); index++) {
				String[] data = allDetailSotckInvalidList.get(index);
				String[] messages = getErrorMessages(data);
				System.out.println((index + 1) + ": " + data[0] + "的股票" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "在表(daily_stock_)中的" + messages[0] + "-----" + "在表(all_detail_stock_)中的" + messages[1]);
				log.loger.warn((index + 1) + ": " + data[0] + "的股票" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "在表(daily_stock_)中的" + messages[0] + "-----" + "在表(all_detail_stock_)中的" + messages[1]);
				flg = true;
			}
		}
		if (allImportStockInvalidList.size() > 0) {
			System.out.println("---------------表(daily_stock_)涨跌幅和换手率验证无效数据(与表(all_import_stock_)比较)----------------");
			log.loger.warn("---------------表(daily_stock_)涨跌幅和换手率验证无效数据(与表(all_import_stock_)比较)----------------");
			for (int index = 0; index < allImportStockInvalidList.size(); index++) {
				String[] data = allImportStockInvalidList.get(index); //0:stockDate,1:stockCode,2:changeRate(daily_stock_),3:changeRate(all_import_stock_),
																	  //4:turnoverRate(daily_stock_),5:turnoverRate(all_import_stock_)
				String[] messages = getErrorMessages(data);
				System.out.println((index + 1) + ": " + data[0] + "的股票" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "在表(daily_stock_)中的" + messages[0] + "-----" + "在表(all_import_stock_)中的" + messages[1]);
				log.loger.warn((index + 1) + ": " + data[0] + "的股票" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "在表(daily_stock_)中的" + messages[0] + "-----" + "在表(all_import_stock_)中的" + messages[1]);
				flg = true;
			}
		}
		if (historyStockInvalidList.size() > 0) {
			System.out.println("---------------表(daily_stock_)涨跌幅和换手率验证无效数据(与表(history_stock_)比较)----------------");
			log.loger.warn("---------------表(daily_stock_)涨跌幅和换手率验证无效数据(与表(history_stock_)比较)----------------");
			for (int index = 0; index < historyStockInvalidList.size(); index++) {
				String[] data = historyStockInvalidList.get(index);
				String[] messages = new String[2];
				if (Double.parseDouble(data[2]) != 0) {
					messages[0] = "涨跌幅为：" + data[2] + "%";
				}
				if (Double.parseDouble(data[3]) != 0) {
					messages[1] = "涨跌幅为：" + data[3] + "%";
				}
				System.out.println((index + 1) + ": " + data[0] + "的股票" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "在表(daily_stock_)中的" + messages[0] + "-----" + "在表(history_stock_)中的" + messages[1]);
				log.loger.warn((index + 1) + ": " + data[0] + "的股票" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "在表(daily_stock_)中的" + messages[0] + "-----" + "在表(history_stock_)中的" + messages[1]);
				flg = true;
			}
		}
		return flg;
	}

	private boolean validateDailyStockExist(List<DailyStock> dailyList) throws Exception {

		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(daily_stock_)中的股票是否存在原始股票信息表(original_stock_)中验证开始...");
		List<OriginalStock> originalList = originalStockDao.listOriginalData();
		Map<Date, List<String>> originalMap = getOriginalMapFromList(originalList);
		List<DailyStock> invalidList = checkDailyStockExist(originalMap, dailyList);

		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------每日股票数据股票代码不存在原始股票数据中----------------");
			log.loger.warn("---------------每日股票数据股票代码不存在原始股票数据中----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("不存在的每日股票数据---" + (index+1) +": 股票" + data.getStockCode() + "(" 
														  + PropertiesUtils.getProperty(data.getStockCode()) 
														  + ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()));
				log.loger.warn("不存在的每日股票数据---" + (index+1) +": 股票" + data.getStockCode() + "(" 
													  + PropertiesUtils.getProperty(data.getStockCode()) 
													  + ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()));
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(daily_stock_)中的股票是否存在原始股票信息表(original_stock_)中验证成功！");
		}
		return flg;
	}
	
	private boolean validateStockCodeAndChangeRate(List<DailyStock> dailyList) {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(daily_stock_)中字段股票代码(stock_code_)和涨跌幅(change_rate_)的有效性验证开始...");
		List<DailyStock> invalidList = checkStockCodeAndChangeRate(dailyList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------每日股票数据股票代码和涨跌率验证无效数据----------------");
			log.loger.warn("---------------每日股票数据股票代码和涨跌率验证无效数据----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("验证出的无效数据--" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_code_=" + data.getStockCode()
									+ " 解密的change_rate_=" + data.getChangeRate()
									+ " 解密的change_rate=" + data.getDecryptChangeRate());
				log.loger.warn("验证出的无效数据--" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_code_=" + data.getStockCode()
									+ " 解密的change_rate_=" + data.getChangeRate()
									+ " 解密的change_rate=" + data.getDecryptChangeRate());
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(daily_stock_)中字段股票代码(stock_code_)和涨跌幅(change_rate_)的有效性验证成功！");
		}
		return flg;
	}
	
	private boolean validateChangFlg(List<DailyStock> dailyList) {
		
		boolean flg = true;
		List<DailyStock> invalidList = checkChangFlg(dailyList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------每日股票数据涨跌标识验证无效数据----------------");
			log.loger.warn("---------------每日股票数据涨跌标识验证无效数据----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
									+ ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()) 
									+ " 未解密的stock_code_=" + data.getStockCode()
									+ " 解密的stock_code_=" + data.getStockCodeDES()
									+ " 未解密的change_rate_=" + data.getChangeRate()
									+ " 解密的change_rate_=" + data.getDecryptChangeRate());
				log.loger.warn("验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
									+ ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()) 
									+ " 未解密的stock_code_=" + data.getStockCode()
									+ " 解密的stock_code_=" + data.getStockCodeDES()
									+ " 未解密的change_rate_=" + data.getChangeRate()
									+ " 解密的change_rate_=" + data.getDecryptChangeRate());
			}
		} 
		return flg;
	}
	
	private boolean validateDailyStockNumber() throws Exception {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(daily_stock_)中每日股票的数量验证开始...");
		List<DailyStock> invalidDataList = new ArrayList<DailyStock>();
		List<DailyStock> dailyStockData = dailyStockDao.statisticDailyData();
		List<OriginalStock> originalStockData = originalStockDao.listOriginalData();
		for (DailyStock daily : dailyStockData) {
			for (OriginalStock original : originalStockData) {
				if (DateUtils.isEqualsTime(daily.getStockDate(), original.getStockDate())) {
					if (daily.getCount() != original.getStockNumber()) {
						DailyStock dailyValidate = new DailyStock();
						dailyValidate.setStockDate(daily.getStockDate());
						dailyValidate.setDailyCount(daily.getCount());
						dailyValidate.setOriginalCount(original.getStockNumber());
						invalidDataList.add(dailyValidate);
						break;
					}
				}
			}
		}
		
		for (OriginalStock original : originalStockData) {
			for (DailyStock daily : dailyStockData) {
				if (DateUtils.isEqualsTime(original.getStockDate(), daily.getStockDate())) {
					if (original.getStockNumber() != daily.getCount()) {
						if (!containInvalidStockDate(original.getStockDate(), invalidDataList)) {
							DailyStock originalValidate = new DailyStock();
							originalValidate.setStockDate(original.getStockDate());
							originalValidate.setOriginalCount(original.getStockNumber());
							originalValidate.setDailyCount(daily.getCount());
							invalidDataList.add(originalValidate);
							break;
						}
					}
				}
			}
		}
		
		if (invalidDataList.size() > 0) {
			flg = false;
			System.out.println("---------------原始股票数量和每日股票数量验证无效数据----------------");
			log.loger.warn("---------------原始股票数量和每日股票数量验证无效数据----------------");
			for (int index=0; index<invalidDataList.size(); index++) {
				DailyStock data = invalidDataList.get(index);
				System.out.println("验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " 原始股票数量 stock_number_=" + data.getOriginalCount()
									+ " 每日股票数量 daily_count=" + data.getDailyCount());
				log.loger.warn("验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " 原始股票数量 stock_number_=" + data.getOriginalCount()
									+ " 每日股票数量 daily_count=" + data.getDailyCount());
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(daily_stock_)中每日股票的数量验证成功！");
		}
		return flg;
	}
	
	private List<DailyStock> checkStockCodeAndChangeRate(List<DailyStock> dailyList) {

		List<DailyStock> invalidList = new ArrayList<DailyStock>();
		for (DailyStock data : dailyList) {
			String decryptCode = DESUtils.decryptHex(data.getStockCodeDES());
			String decryptRate = DESUtils.decryptHex(data.getEncryptChangeRate());
			if (!decryptCode.equals(data.getStockCode()) 
				|| Double.valueOf(decryptRate).doubleValue()!=data.getChangeRate().doubleValue()) {
				data.setStockCodeDES(decryptCode);
				data.setDecryptChangeRate(decryptRate);
				invalidList.add(data);
			}
		}
		return invalidList;
	}
	
	private List<DailyStock> checkDailyStockExist(Map<Date, List<String>> originalMap, List<DailyStock> dailyList) {
		
		List<DailyStock> dailyStockList = new ArrayList<DailyStock>();
		for (DailyStock stock : dailyList) {
			boolean existFlg = includeInOriginalStock(stock, originalMap);
			if (!existFlg) dailyStockList.add(stock);
		}
		return dailyStockList;
	}
	
	private boolean includeInOriginalStock(DailyStock stock, Map<Date, List<String>> originalMap) {
		
		String stockCode = stock.getStockCode();
		Date stockDate = stock.getStockDate();
		List<String> originalCodeList = originalMap.get(stockDate);
		return originalCodeList.contains(stockCode);
	}
	
	private Map<Date, List<String>> getOriginalMapFromList(List<OriginalStock> originalList) {

		Map<Date, List<String>> originalMap = new HashMap<Date, List<String>>();
		for (OriginalStock stock : originalList) {
			Date stockDate = stock.getStockDate();
			String[] stockCodeArray = stock.getStockCodes().split(",");
			List<String> stockCodeList = Arrays.asList(stockCodeArray);
			originalMap.put(stockDate, stockCodeList);
			
		}
		return originalMap;
	}
	
	private List<DailyStock> checkChangFlg(List<DailyStock> dailyList) {
		
		List<DailyStock> invalidList = new ArrayList<DailyStock>();
		for (DailyStock data : dailyList) {
			Double changeRate = data.getChangeRate();
			String changeFlg = changeRate>0?"1":"0";
			if (!changeFlg.equals(data.getChangeFlg())) {
				invalidList.add(data);
			}
		}
		return invalidList;
	}
	
	private boolean containInvalidStockDate(Date stockDate, List<DailyStock> invalidDataList) {
		
		for (DailyStock data : invalidDataList) {
			if (DateUtils.isEqualsTime(stockDate, data.getStockDate())) {
				return true;
			}
		}
		return false;
	}
	
	private String getStockCodesFromList(List<DailyStock> dailyList) {
		
		Set<String> stockCodeSet = new HashSet<String>();
		for (DailyStock stock : dailyList) {
			String stockCode = stock.getStockCode();
			stockCodeSet.add(stockCode);
		}
		return StringUtils.join(stockCodeSet.toArray(), ",");
	}

	private Date[] getMinMaxDateFromList(List<DailyStock> dailyList) {
		
		Date[] minMaxDate = {dailyList.get(0).getStockDate(), dailyList.get(0).getStockDate()};
		for (DailyStock dailyStock : dailyList) {
			Date stockDate = dailyStock.getStockDate();
			if (minMaxDate[0].compareTo(stockDate) > 0) minMaxDate[0] = stockDate;
			if (minMaxDate[1].compareTo(stockDate) < 0) minMaxDate[1] = stockDate;
		}
		return minMaxDate;
	}

	private List<String[]> getErrorListFromAllDetailStock(List<DailyStock> dailyList, Map<String, AllDetailStock> allDetailStockMap) {

		long count = 0;
		List<String[]> invalidList = new ArrayList<String[]>();
		List<String> notExistList = new ArrayList<String>();
		System.out.println("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_detail_stock_)比较)验证开始...");
		log.loger.warn("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_detail_stock_)比较)验证开始...");
		for (DailyStock dailyStock : dailyList) {
			String stockCode = dailyStock.getStockCode();
			Date stockDate = dailyStock.getStockDate();
			Double changeRate = dailyStock.getChangeRate();
			Double turnoverRate = dailyStock.getTurnoverRate();
			if (changeRate == DataUtils._MAX_CHANGE_RATE || changeRate == DataUtils._MIN_CHANGE_RATE || turnoverRate == DataUtils._INT_ZERO)
				continue;
			String mapKey = stockCode + DateUtils.dateToString(stockDate);
			AllDetailStock allDetailStock = allDetailStockMap.get(mapKey);
			if (allDetailStock == null) {
				notExistList.add(stockCode);
//				System.out.println("-------->表(daily_stock_)中的日期：" + DateUtils.dateToString(stockDate) + "的股票" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")在表(all_detail_stock_)中不存在！");
//				log.loger.warn("-------->表(daily_stock_)中的日期：" + DateUtils.dateToString(stockDate) + "的股票" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")在表(all_detail_stock_)中不存在！");
				continue;
			}
			Double _changeRate = allDetailStock.getChangeRate();
			Double _turnoverRate = allDetailStock.getTurnoverRate();
			count++;
			if (_changeRate.compareTo(changeRate)==0 && _turnoverRate.compareTo(turnoverRate)==0)
				continue;
			String value = DataUtils._MIN_CHANGE_RATE.toString();
			String[] errors = {value, value, value, value, value, value};
			errors[0] = DateUtils.dateToString(stockDate);
			errors[1] = stockCode;
			if (_changeRate.compareTo(changeRate)!=0) {
				errors[2] = changeRate.toString();
				errors[4] = _changeRate.toString();
			}
			if (_turnoverRate.compareTo(turnoverRate)!=0) {
				errors[3] = turnoverRate.toString();
				errors[5] = _turnoverRate.toString();
			}
			invalidList.add(errors);
		}
		System.out.println(" @@@@@@@@@@@@>以下股票不存在表(all_detail_stock_)中：" + StringUtils.join(notExistList.toArray(), ","));
		log.loger.warn(" @@@@@@@@@@@@>以下股票不存在表(all_detail_stock_)中：" + StringUtils.join(notExistList.toArray(), ","));
		System.out.println(" >>>>>>>>>>>>>表(daily_stock_)涨跌幅和换手率与表(all_detail_stock_)比较次数为：" + count + "！");
		log.loger.warn(" >>>>>>>>>>>>>表(daily_stock_)涨跌幅和换手率与表(all_detail_stock_)比较次数为：" + count + "！");
		if (invalidList.size() == 0) {
			System.out.println("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_detail_stock_)比较)验证成功！");
			log.loger.warn("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_detail_stock_)比较)验证成功！");
		} else {
			System.out.println("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_detail_stock_)比较)验证失败！");
			log.loger.warn("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_detail_stock_)比较)验证失败！");
		}
		return invalidList;
	}

	private List<String[]> getErrorListFromAllImportStock(List<DailyStock> dailyList, Map<String, AllImportStock> allImportStockMap) {

		long count = 0;
		List<String[]> invalidList = new ArrayList<String[]>();
		List<String> notExistList = new ArrayList<String>();
		System.out.println("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_import_stock_)比较)验证开始...");
		log.loger.warn("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_import_stock_)比较)验证开始...");
		for (DailyStock dailyStock : dailyList) {
			String stockCode = dailyStock.getStockCode();
			Date stockDate = dailyStock.getStockDate();
			Double changeRate = dailyStock.getChangeRate();
			Double turnoverRate = dailyStock.getTurnoverRate();
			if (changeRate == DataUtils._MAX_CHANGE_RATE || changeRate == DataUtils._MIN_CHANGE_RATE || turnoverRate == DataUtils._INT_ZERO)
				continue;
			String mapKey = stockCode + DateUtils.dateToString(stockDate);
			AllImportStock allImportStock = allImportStockMap.get(mapKey);
			if (allImportStock == null) {
				notExistList.add(stockCode);
//				System.out.println("-------->表(daily_stock_)中的日期：" + DateUtils.dateToString(stockDate) + "的股票" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")在表(all_import_stock_)中不存在！");
//				log.loger.warn("-------->表(daily_stock_)中的日期：" + DateUtils.dateToString(stockDate) + "的股票" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")在表(all_import_stock_)中不存在！");
				continue;
			}
			Double _changeRate = allImportStock.getChangeRate();
			Double _turnoverRate = allImportStock.getTurnoverRate();
			count++;
			if (_changeRate.compareTo(changeRate)==0 && _turnoverRate.compareTo(turnoverRate)==0)
				continue;
			String value = DataUtils._MIN_CHANGE_RATE.toString();
			String[] errors = {value, value, value, value, value, value};
			errors[0] = DateUtils.dateToString(stockDate);
			errors[1] = stockCode;
			if (_changeRate.compareTo(changeRate) != 0) {
				errors[2] = changeRate.toString();
				errors[4] = _changeRate.toString();
			}
			if (_turnoverRate.compareTo(turnoverRate) != 0) {
				errors[3] = turnoverRate.toString();
				errors[5] = _turnoverRate.toString();
			}
			invalidList.add(errors);
		}

		System.out.println(" @@@@@@@@@@@@>以下股票不存在表(all_import_stock_)中：" + StringUtils.join(notExistList.toArray(), ","));
		log.loger.warn(" @@@@@@@@@@@@>以下股票不存在表(all_import_stock_)中：" + StringUtils.join(notExistList.toArray(), ","));
		System.out.println(" >>>>>>>>>>>>>表(daily_stock_)涨跌幅和换手率与表(all_import_stock_)比较次数为：" + count + "！");
		log.loger.warn(" >>>>>>>>>>>>>表(daily_stock_)涨跌幅和换手率与表(all_import_stock_)比较次数为：" + count + "！");
		if (invalidList.size() == 0) {
		    System.out.println("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_import_stock_)比较)验证成功！");
		    log.loger.warn("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_import_stock_)比较)验证成功！");
		} else {
		    System.out.println("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_import_stock_)比较)验证失败！");
		    log.loger.warn("++++++++>表(daily_stock_)涨跌幅和换手率(与表(all_import_stock_)比较)验证失败！");
		}

		return invalidList;
	}

	private List<String[]> getErrorListFromHistoryStock(List<DailyStock> dailyList, Map<String, HistoryStock> historyStockMap) throws SQLException, IOException {
		
		long count = 0;
		List<String[]> invalidList = new ArrayList<String[]>();
		List<String> notExistList = new ArrayList<String>();
		System.out.println("++++++++>表(daily_stock_)涨跌幅和换手率(与表(history_stock_)比较)验证开始...");
		log.loger.warn("++++++++>表(daily_stock_)涨跌幅和换手率(与表(history_stock_)比较)验证开始...");
		for (DailyStock dailyStock : dailyList) {
			String stockCode = dailyStock.getStockCode();
			Date stockDate = dailyStock.getStockDate();
			String mapKey = stockCode + DateUtils.dateToString(stockDate);
			Double changeRate = dailyStock.getChangeRate();
			Double turnoverRate = dailyStock.getTurnoverRate();
			if (changeRate == DataUtils._MAX_CHANGE_RATE || changeRate == DataUtils._MIN_CHANGE_RATE || turnoverRate == DataUtils._INT_ZERO)
				continue;
			HistoryStock historyStock = historyStockMap.get(mapKey);
			if (historyStock == null) {
				notExistList.add(stockCode);
//				System.out.println("-------->表(daily_stock_)中的日期：" + DateUtils.dateToString(stockDate) + "的股票" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")在表(history_stock_)中不存在！");
//				log.loger.warn("-------->表(daily_stock_)中的日期：" + DateUtils.dateToString(stockDate) + "的股票" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")在表(history_stock_)中不存在！");
				continue;
			}
			HistoryStock preHistoryStock = getHistoryStockByKey(stockCode, stockDate);
			if (preHistoryStock == null) {
				Exception exception = new IOException(DateUtils.dateToString(stockDate) + "的股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")在表(history_stock_)中没有前一日开盘数据！");
				exception.printStackTrace();
				continue;
			}
			Double yesterdayClose = preHistoryStock.getClosePrice();
			Double current = historyStock.getClosePrice();
			Double historyChangeRate = StockUtils.getChangeRate(yesterdayClose, current);
			String value = DataUtils._MIN_CHANGE_RATE.toString();
			String[] errors = {value, value, value, value};
			errors[0] = DateUtils.dateToString(stockDate);
			errors[1] = stockCode;
			count++;
			if (changeRate.compareTo(historyChangeRate) != 0) {
				errors[2] = changeRate.toString();
				errors[3] = historyChangeRate.toString();
				invalidList.add(errors);
			}
		}
		System.out.println(" @@@@@@@@@@@@>以下股票不存在表(history_stock_)中：" + StringUtils.join(notExistList.toArray(), ","));
		log.loger.warn(" @@@@@@@@@@@@>以下股票不存在表(history_stock_)中：" + StringUtils.join(notExistList.toArray(), ","));
		System.out.println(" >>>>>>>>>>>>>表(daily_stock_)涨跌幅和换手率与表(history_stock_)比较次数为：" + count + "！");
		log.loger.warn(" >>>>>>>>>>>>>表(daily_stock_)涨跌幅和换手率与表(history_stock_)比较次数为：" + count + "！");
		if (invalidList.size() == 0) {
		    System.out.println("++++++++>表(daily_stock_)涨跌幅和换手率(与表(history_stock_)比较)验证成功！");
		    log.loger.warn("++++++++>表(daily_stock_)涨跌幅和换手率(与表(history_stock_)比较)验证成功！");
		} else {
		    System.out.println("++++++++>表(daily_stock_)涨跌幅和换手率(与表(history_stock_)比较)验证失败！");
		    log.loger.warn("++++++++>表(daily_stock_)涨跌幅和换手率(与表(history_stock_)比较)验证失败！");
		}
		return invalidList;
	}
	
	private String[] getErrorMessages(String[] data) {

		String[] messages = {DataUtils._BLANK, DataUtils._BLANK};
		if ((DataUtils._MIN_CHANGE_RATE.compareTo(Double.parseDouble(data[2]))) != 0) {
			messages[0] = "涨跌幅为：" + data[2] + "%，";
		}
		if (DataUtils._MIN_CHANGE_RATE.compareTo(Double.parseDouble(data[3])) != 0) {
			messages[0] += "换手率为：" + data[3] + "%";
		}
		if (DataUtils._MIN_CHANGE_RATE.compareTo(Double.parseDouble(data[4])) != 0) { // || Double.parseDouble(data[5]) != 0) {
			messages[1] = "涨跌幅为：" + data[4] + "%，";
		}
		if (DataUtils._MIN_CHANGE_RATE.compareTo(Double.parseDouble(data[5])) != 0) {
			messages[1] += "换手率为：" + data[5] + "%";
		}
		return messages;
	}
}
