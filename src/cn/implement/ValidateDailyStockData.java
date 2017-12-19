package cn.implement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.OriginalStock;

public class ValidateDailyStockData extends OperationData {

	/**
	 * 验证每日股票数据，并输出无效数据
	 * 
	 */
	public void validateDailyStockData() {
		dailyStockDao = new DailyStockDao();
		originalStockDao = new OriginalStockDao();
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
				if (existFlg && codeChangRateFlg && changFlg && numberFlg) {
					System.out.println("每日股票数据(daily_stock_)验证成功！");
					log.loger.info("每日股票数据(daily_stock_)验证成功！");
				}
			} else {
				System.out.println("数据库中没有每日股票数据！");
				log.loger.info("数据库中没有每日股票数据！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao, originalStockDao);
		}
	}
	
	private boolean validateDailyStockExist(List<DailyStock> dailyList) throws Exception {

		boolean flg = true;
		List<OriginalStock> originalList = originalStockDao.listOriginalData();
		Map<Date, List<String>> originalMap = getOriginalMapFromList(originalList);
		List<DailyStock> invalidList = checkDailyStockExist(originalMap, dailyList);

		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------每日股票数据股票代码不存在原始股票数据中----------------");
			log.loger.info("---------------每日股票数据股票代码不存在原始股票数据中----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("不存在的每日股票数据---" + (index+1) +": 股票" + data.getStockCode() + "(" 
														  + PropertiesUtils.getProperty(data.getStockCode()) 
														  + ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()));
				log.loger.info("不存在的每日股票数据---" + (index+1) +": 股票" + data.getStockCode() + "(" 
													  + PropertiesUtils.getProperty(data.getStockCode()) 
													  + ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()));
			}
		} 
		return flg;
	}
	
	private boolean validateStockCodeAndChangeRate(List<DailyStock> dailyList) {
		
		boolean flg = true;
		List<DailyStock> invalidList = checkStockCodeAndChangeRate(dailyList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------每日股票数据股票代码和涨跌率验证无效数据----------------");
			log.loger.info("---------------每日股票数据股票代码和涨跌率验证无效数据----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("验证出的无效数据--" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_code_=" + data.getStockCode()
									+ " 解密的change_rate_=" + data.getChangeRate()
									+ " 解密的change_rate=" + data.getDecryptChangeRate());
				log.loger.info("验证出的无效数据--" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_code_=" + data.getStockCode()
									+ " 解密的change_rate_=" + data.getChangeRate()
									+ " 解密的change_rate=" + data.getDecryptChangeRate());
			}
		} 
		return flg;
	}
	
	private boolean validateChangFlg(List<DailyStock> dailyList) {
		
		boolean flg = true;
		List<DailyStock> invalidList = checkChangFlg(dailyList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------每日股票数据涨跌标识验证无效数据----------------");
			log.loger.info("---------------每日股票数据涨跌标识验证无效数据----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
									+ ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()) 
									+ " 未解密的stock_code_=" + data.getStockCode()
									+ " 解密的stock_code_=" + data.getDecryptStockCode()
									+ " 未解密的change_rate_=" + data.getChangeRate()
									+ " 解密的change_rate_=" + data.getDecryptChangeRate());
				log.loger.info("验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
									+ ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()) 
									+ " 未解密的stock_code_=" + data.getStockCode()
									+ " 解密的stock_code_=" + data.getDecryptStockCode()
									+ " 未解密的change_rate_=" + data.getChangeRate()
									+ " 解密的change_rate_=" + data.getDecryptChangeRate());
			}
		} 
		return flg;
	}
	
	private boolean validateDailyStockNumber() throws Exception {
		
		boolean flg = true;
		List<DailyStock> invalidDataList = new ArrayList<DailyStock>();
		List<DailyStock> dailyStockData = dailyStockDao.statisticDailyData();
		List<OriginalStock> originalStockData = originalStockDao.listOriginalData();
		for (DailyStock daily : dailyStockData) {
			for (OriginalStock original : originalStockData) {
				if (CommonUtils.compareDate(daily.getStockDate(), original.getStockDate())) {
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
				if (CommonUtils.compareDate(original.getStockDate(), daily.getStockDate())) {
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
			log.loger.info("---------------原始股票数量和每日股票数量验证无效数据----------------");
			for (int index=0; index<invalidDataList.size(); index++) {
				DailyStock data = invalidDataList.get(index);
				System.out.println("验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " 原始股票数量 stock_number_=" + data.getOriginalCount()
									+ " 每日股票数量 daily_count=" + data.getDailyCount());
				log.loger.info("验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " 原始股票数量 stock_number_=" + data.getOriginalCount()
									+ " 每日股票数量 daily_count=" + data.getDailyCount());
			}
		}
		return flg;
	}
	
	private List<DailyStock> checkStockCodeAndChangeRate(List<DailyStock> dailyList) {

		List<DailyStock> invalidList = new ArrayList<DailyStock>();
		for (DailyStock data : dailyList) {
			String decryptCode = DESUtils.decryptHex(data.getEncryptStockCode());
			String decryptRate = DESUtils.decryptHex(data.getEncryptChangeRate());
			if (!decryptCode.equals(data.getStockCode()) 
				|| Double.valueOf(decryptRate).doubleValue()!=data.getChangeRate().doubleValue()) {
				data.setDecryptStockCode(decryptCode);
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
			if (CommonUtils.compareDate(stockDate, data.getStockDate())) {
				return true;
			}
		}
		return false;
	}
}
