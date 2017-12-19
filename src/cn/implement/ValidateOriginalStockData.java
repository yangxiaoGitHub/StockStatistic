package cn.implement;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.MD5Utils;
import cn.com.PropertiesUtils;
import cn.db.AllDetailStockDao;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.OriginalStock;

public class ValidateOriginalStockData extends OperationData {
	
	/**
	 * 验证原始股票数据，并输出无效数据
	 * 
	 */
	public void validateOriginalStockData() {

		originalStockDao = new OriginalStockDao();
		try {
			List<OriginalStock> originalList = originalStockDao.listOriginalData();
			if (originalList.size() > 0) {
				// MD5验证原始股票数据
				boolean originalDataMD5Flg = checkOriginalDataMD5(originalList);
				// DES验证原始股票数据
				boolean originalDataDESFlg = checkOriginalDataDES(originalList);
				// 验证原始股票数量
				boolean originalNumberFlg = checkOriginalStockNumber(originalList);
				if (originalDataMD5Flg && originalDataDESFlg && originalNumberFlg) {
					System.out.println("原始股票数据(original_stock_)验证成功！");
					log.loger.info("原始股票数据(original_stock_)验证成功！");
				}
			} else {
				System.out.println("数据库中没有原始股票数据！");
				log.loger.info("数据库中没有原始股票数据！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(originalStockDao);
		}
	}
	
	/**
	 * 验证输入的股票信息
	 * 
	 */
	public String checkInputStockData(String sDate, String codes, String names, String changeRates, String turnoverRates) throws Exception {
		
		if (!CommonUtils.isValidDate(sDate)) {
			return "输入的日期格式不正确！";
		}
		Date recentStockDate = isRecentStockDate(sDate);
		if (recentStockDate != null) {
			return "不能增加或修改以前每日股票数据，数据库中最新每日股票数据的日期为：" + recentStockDate;
		}
		String[] codeArray = codes.split(",");
		String[] nameArray = names.split(",");
		String[] changeRateArray = changeRates.split(",");
		String[] turnoverRateArray = turnoverRates.split(",");
		String errorStockCodes = CommonUtils.isExistStockCode(codeArray);
		if (!CommonUtils.isBlank(errorStockCodes)) {
			return "输入的股票代码(" + errorStockCodes + ")不存在！";
		}
		// 验证重复数据
		List<String> duplicateStockList = CommonUtils.getDuplicateStocks(codeArray);
		if (!CommonUtils.isBlank(duplicateStockList)) {
			return "输入的股票" + CommonUtils.getDuplicateMessage(duplicateStockList) + "重复！";
		}
		if (!CommonUtils.isBlank(turnoverRates)) {
			if (codeArray.length!=changeRateArray.length 
				|| codeArray.length!=turnoverRateArray.length 
				|| changeRateArray.length!=turnoverRateArray.length
				|| codeArray.length != nameArray.length) {
				return "输入的股票代码、股票名称、涨跌幅和换手率的数量对应不上！";
			}
		} else {
			if (codeArray.length != changeRateArray.length) {
				return "输入的股票代码和涨跌幅的数量对应不上！";
			}
		}
		
		allDetailStockDao = new AllDetailStockDao(); // AllDetailStockDao.getInstance();
		try {
			// 验证输入股票名称和实际股票名称
			List<String[]> errorStockNameList = CommonUtils.listErrorStockName(codeArray, nameArray);
			if (errorStockNameList.size() > 0) {
				return "输入的股票为：" + CommonUtils.getErrorStockName(errorStockNameList) + "  实际股票为：" + CommonUtils.getActualStockName(errorStockNameList);
			}
	
			// 验证输入股票涨跌幅和实际股票涨跌幅
			List<String[]> errorStockChangeRateList = listErrorStockChangeRate(sDate, codeArray, changeRateArray);
			if (errorStockChangeRateList.size() > 0) {
				return "输入股票涨跌幅为：" + CommonUtils.getErrorStockMessage(errorStockChangeRateList) + "  实际股票涨跌幅为：" + CommonUtils.getActualStockMessage(errorStockChangeRateList);
			}
			
			// 验证输入股票的换手率
			List<String[]> errorTurnoverRateList = listErrorStockTurnoverRate(sDate, codeArray, turnoverRateArray);
			if (errorTurnoverRateList.size() > 0) {
				return "输入股票换手率为：" + CommonUtils.getErrorStockMessage(errorTurnoverRateList) + "  实际股票换手率为：" + CommonUtils.getActualStockMessage(errorTurnoverRateList);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			log.loger.error(ex);
		} finally {
			closeDao(allDetailStockDao);
		}
		return null;
	}
	
	/**
	 * 验证输入股票的涨跌幅
	 *
	 */
	private List<String[]> listErrorStockChangeRate(String sDate, String[] codeArray, String[] changeRateArray) throws Exception {
		
		List<String[]> errorStockList = new ArrayList<String[]>();
		for (int index=0; index<codeArray.length; index++) {
			String inputChangeRate = changeRateArray[index];
			AllDetailStock allDetailStock = allDetailStockDao.getAllDetailStockByKey(DateUtils.stringToDate(sDate), codeArray[index]);
			if (allDetailStock != null) {
				if (Double.valueOf(inputChangeRate).compareTo(allDetailStock.getChangeRate()) != 0) {
					String[] errorStock = new String[3];
					errorStock[0] = codeArray[index];
					errorStock[1] = inputChangeRate;
					errorStock[2] = DataUtils.formatNumber(allDetailStock.getChangeRate());
					errorStockList.add(errorStock);
				}
			} else {
				Exception exception = new IOException(sDate + " 股票" + codeArray[index] + "(" + PropertiesUtils.getProperty(codeArray[index]) + ")不存在所有股票详细信息表(all_detail_stock_)中！");
				exception.printStackTrace();
				log.loger.error(exception);
			}
		}
		return errorStockList;
	}
	
	/**
	 * 验证输入股票的换手率
	 *
	 */
	private List<String[]> listErrorStockTurnoverRate(String sDate, String[] codeArray, String[] turnoverRateArray) throws Exception {

		List<String[]> errorStockList = new ArrayList<String[]>();
		for (int index=0; index<codeArray.length; index++) {
			String inputTurnoverRate = turnoverRateArray[index];
			AllDetailStock allDetailStock = allDetailStockDao.getAllDetailStockByKey(DateUtils.stringToDate(sDate), codeArray[index]);
			if (allDetailStock != null) {
				Double realTurnoverRate = allDetailStock.getTurnoverRate();
				if (!CommonUtils.isZeroOrNull(realTurnoverRate) && !CommonUtils.isEqualsTurnoverRate(Double.valueOf(inputTurnoverRate), realTurnoverRate)) {
					String[] errorStock = new String[3];
					errorStock[0] = codeArray[index];
					errorStock[1] = inputTurnoverRate;
					errorStock[2] = DataUtils.formatNumber(realTurnoverRate);
					errorStockList.add(errorStock);
				}
			} else {
				Exception exception = new IOException(sDate + " 股票" + codeArray[index] + "(" + PropertiesUtils.getProperty(codeArray[index]) + ")不存在所有股票详细信息表(all_detail_stock_)中！");
				log.loger.error(exception);
				throw exception;
			}
		}
		return errorStockList;
	}
	
	/**
	 * 判断输入的日期是不是每日股票数据表(daily_stock_)中和原始数据表(original_stock_)中最近日期，如果是返回null，如果不是返回最近日期
	 *
	 */
	private Date isRecentStockDate(String sDate) {
		
		Date recentDate = null;
		Date recentDateInDailyStock = isRecentDateInDailyStock(sDate);
		Date recentDateInOriginalStock = isRecentDateInOriginalStock(sDate);
		if (recentDateInDailyStock != null && recentDateInOriginalStock != null) {
			int compareValue = recentDateInDailyStock.compareTo(recentDateInOriginalStock);
			if (compareValue >= 0) recentDate = recentDateInDailyStock;
			else recentDate = recentDateInOriginalStock;
		} else if (recentDateInDailyStock != null && recentDateInOriginalStock == null) {
			recentDate = recentDateInDailyStock;
		} else if (recentDateInDailyStock == null && recentDateInOriginalStock != null) {
			recentDate = recentDateInOriginalStock;
		}
		return recentDate;
	}
	
	private boolean checkOriginalDataMD5(List<OriginalStock> originalList) 
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		boolean flg = true;
		List<OriginalStock> invalidList = validateOriginalDataMD5(originalList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------MD5验证原始股票数据验证无效数据----------------");
			log.loger.info("---------------MD5验证原始股票数据验证无效数据----------------");
			for (int index=0; index<invalidList.size(); index++) {
				OriginalStock data = invalidList.get(index);
				System.out.println("MD5验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate())
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " change_rates_=" + data.getChangeRates());

				log.loger.info("MD5验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " change_rates_=" + data.getChangeRates());
			}
		} 
		return flg;
	}
	
	private List<OriginalStock> validateOriginalDataMD5(List<OriginalStock> originalList) 
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		List<OriginalStock> invalidList = new ArrayList<OriginalStock>();
		for (OriginalStock data : originalList) {
			String stockCodes = data.getStockCodes();
			String changeRates = data.getChangeRates();
			String stockCodesMD5 = data.getStockCodesMD5();
			String changeRatesMD5 = data.getChangeRatesMD5();
			if (!MD5Utils.validateData(stockCodes, stockCodesMD5) || !MD5Utils.validateData(changeRates, changeRatesMD5)) {
				invalidList.add(data);
			}
		}
		return invalidList;
	}

	/**
	 * 判断输入的日期是不是每日股票数据表中最近日期，如果是返回null，如果不是返回最近日期
	 *
	 */
	private Date isRecentDateInDailyStock(String sDate) {

		Date recentStockDate = new Date();
		dailyStockDao = new DailyStockDao();
		try {
			recentStockDate = dailyStockDao.getRecentStockDate();
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao);
		}
		Date inputDate = DateUtils.stringToDate(sDate);
		return inputDate.compareTo(recentStockDate)>=0?null:recentStockDate; 
	}
	
	/**
	 * 判断输入的日期是不是原始股票数据表中最近日期，如果是返回null，如果不是返回最近日期
	 *
	 */
	public Date isRecentDateInOriginalStock(String sDate) {

		Date recentStockDate = new Date();
		originalStockDao = new OriginalStockDao();
		try {
			recentStockDate = originalStockDao.getRecentStockDate();
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(originalStockDao);
		}
		Date inputDate = DateUtils.stringToDate(sDate);
		return inputDate.compareTo(recentStockDate)>=0?null:recentStockDate;
	}

	private boolean checkOriginalDataDES(List<OriginalStock> originalList) {
		
		boolean flg = true;
		List<OriginalStock> invalidList = validateOriginalDataDES(originalList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------DES验证原始股票数据验证无效数据----------------");
			log.loger.info("---------------DES验证原始股票数据验证无效数据----------------");
			for (int index=0; index<invalidList.size(); index++) {
				OriginalStock data = invalidList.get(index);
				System.out.println("DES验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " 解密的stock_codes=" + data.getDecryptStockCodes()
									+ " change_rates_=" + data.getChangeRates()
									+ " 解密的change_rates=" + data.getDecryptChangeRates());

				log.loger.info("DES验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " 解密的stock_codes=" + data.getDecryptStockCodes()
									+ " change_rates_=" + data.getChangeRates()
									+ " 解密的change_rates=" + data.getDecryptChangeRates());
			}
		} 
		return flg;
	}
	
	private List<OriginalStock> validateOriginalDataDES(List<OriginalStock> originalList) {
		
		List<OriginalStock> invalidList = new ArrayList<OriginalStock>();
		for (OriginalStock data : originalList) {
			String decryptCodes = DESUtils.decryptHex(data.getStockCodesEncrypt());
			String decryptRates = DESUtils.decryptHex(data.getChangeRatesEncrypt());
			if (!decryptCodes.equals(data.getStockCodes()) || !decryptRates.equals(data.getChangeRates())) {
				data.setDecryptStockCodes(decryptCodes);
				data.setDecryptChangeRates(decryptRates);
				invalidList.add(data);
			}
		}
		return invalidList;
	}
	
	private boolean checkOriginalStockNumber(List<OriginalStock> originalList) {
		
		boolean flg = true;
		List<OriginalStock> invalidateOriginalList = new ArrayList<OriginalStock>();
		for (OriginalStock stock : originalList) {
			String stockCodes = stock.getStockCodes();
			String changeRates = stock.getChangeRates();
			String[] codeArray = stockCodes.split(",");
			String[] rateArray = changeRates.split(",");
			if (stock.getStockNumber()!=codeArray.length || stock.getStockNumber()!=rateArray.length) {
				invalidateOriginalList.add(stock);
			}
		}
		if (invalidateOriginalList.size() > 0) {
			flg = false;
			System.out.println("---------------原始股票数量验证无效数据----------------");
			log.loger.info("---------------原始股票数量验证无效数据----------------");
			for (int index=0; index<invalidateOriginalList.size(); index++) {
				OriginalStock data = invalidateOriginalList.get(index);
				System.out.println("验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " change_rates_=" + data.getChangeRates());

				log.loger.info("验证出的无效数据---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " change_rates_=" + data.getChangeRates());
			}
		} 
		return flg;
	}
}