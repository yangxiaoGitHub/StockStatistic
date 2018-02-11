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
import cn.db.AllImportStockDao;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllImportStock;
import cn.db.bean.OriginalStock;

public class ValidateOriginalStockData extends OperationData {
	
	/**
	 * ��֤ԭʼ��Ʊ���ݣ��������Ч����
	 * 
	 */
	public void validateOriginalStockData() {

		originalStockDao = new OriginalStockDao();
		try {
			List<OriginalStock> originalList = originalStockDao.listOriginalData();
			if (originalList.size() > 0) {
				// MD5��֤ԭʼ��Ʊ����
				boolean originalDataMD5Flg = checkOriginalDataMD5(originalList);
				// DES��֤ԭʼ��Ʊ����
				boolean originalDataDESFlg = checkOriginalDataDES(originalList);
				// ��֤ԭʼ��Ʊ����
				boolean originalNumberFlg = checkOriginalStockNumber(originalList);
				if (originalDataMD5Flg && originalDataDESFlg && originalNumberFlg) {
					System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>ԭʼ��Ʊ����(original_stock_)��֤�ɹ���" + CommonUtils.getLineBreak());
					log.loger.warn(DateUtils.dateTimeToString(new Date()) + "=====>ԭʼ��Ʊ����(original_stock_)��֤�ɹ���" + CommonUtils.getLineBreak());
				}
			} else {
				System.out.println("���ݿ���û��ԭʼ��Ʊ���ݣ�");
				log.loger.warn("���ݿ���û��ԭʼ��Ʊ���ݣ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(originalStockDao);
		}
	}
	
	/**
	 * ��֤����Ĺ�Ʊ��Ϣ
	 * 
	 */
	public String checkInputStockData(String sDate, String codes, String names, String changeRates, String turnoverRates) throws Exception {
		
		if (!DateUtils.isValidDate(sDate)) {
			return "��������ڸ�ʽ����ȷ��";
		}
		Date recentStockDate = isRecentStockDate(sDate);
		if (recentStockDate != null) {
			return "�������ӻ��޸���ǰÿ�չ�Ʊ���ݣ����ݿ�������ÿ�չ�Ʊ���ݵ�����Ϊ��" + recentStockDate;
		}
		String[] codeArray = codes.split(",");
		String[] nameArray = names.split(",");
		String[] changeRateArray = changeRates.split(",");
		String[] turnoverRateArray = turnoverRates.split(",");
		String errorStockCodes = CommonUtils.isExistStockCode(codeArray);
		if (!CommonUtils.isBlank(errorStockCodes)) {
			return "����Ĺ�Ʊ����(" + errorStockCodes + ")�����ڣ�";
		}
		// ��֤�ظ�����
		List<String> duplicateStockList = CommonUtils.getDuplicateStocks(codeArray);
		if (!CommonUtils.isBlank(duplicateStockList)) {
			return "����Ĺ�Ʊ" + CommonUtils.getDuplicateMessage(duplicateStockList) + "�ظ���";
		}
		if (!CommonUtils.isBlank(turnoverRates)) {
			if (codeArray.length!=changeRateArray.length 
				|| codeArray.length!=turnoverRateArray.length 
				|| changeRateArray.length!=turnoverRateArray.length
				|| codeArray.length != nameArray.length) {
				return "����Ĺ�Ʊ���롢��Ʊ���ơ��ǵ����ͻ����ʵ�������Ӧ���ϣ�";
			}
		} else {
			if (codeArray.length != changeRateArray.length) {
				return "����Ĺ�Ʊ������ǵ�����������Ӧ���ϣ�";
			}
		}
		
		allDetailStockDao = new AllDetailStockDao();
		allImportStockDao = new AllImportStockDao();
		try {
			// ��֤�����Ʊ���ƺ�ʵ�ʹ�Ʊ����
			List<String[]> errorStockNameList = CommonUtils.listErrorStockName(codeArray, nameArray);
			if (errorStockNameList.size() > 0) {
				return "����Ĺ�ƱΪ��" + CommonUtils.getErrorStockName(errorStockNameList) + "  ʵ�ʹ�ƱΪ��" + CommonUtils.getActualStockName(errorStockNameList);
			}
	
			// ��֤�����Ʊ�ǵ�����ʵ�ʹ�Ʊ�ǵ���
			List<String[]> errorStockChangeRateList = listErrorStockChangeRate(sDate, codeArray, changeRateArray);
			if (errorStockChangeRateList.size() > 0) {
				return "�����Ʊ�ǵ���Ϊ��" + CommonUtils.getErrorStockMessage(errorStockChangeRateList) + "  ʵ�ʹ�Ʊ�ǵ���Ϊ��" + CommonUtils.getActualStockMessage(errorStockChangeRateList);
			}
			
			// ��֤�����Ʊ�Ļ�����
			List<String[]> errorTurnoverRateList = listErrorStockTurnoverRate(sDate, codeArray, turnoverRateArray);
			if (errorTurnoverRateList.size() > 0) {
				return "�����Ʊ������Ϊ��" + CommonUtils.getErrorStockMessage(errorTurnoverRateList) + "  ʵ�ʹ�Ʊ������Ϊ��" + CommonUtils.getActualStockMessage(errorTurnoverRateList);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			closeDao(allDetailStockDao, allImportStockDao);
		}
		return null;
	}
	
	/**
	 * ��֤�����Ʊ���ǵ���
	 *
	 */
	private List<String[]> listErrorStockChangeRate(String sDate, String[] codeArray, String[] changeRateArray) throws Exception {
		
		boolean allDetailStockFlg = true;
		boolean allImportStockFlg = true;
		List<String[]> errorStockList = new ArrayList<String[]>();
		for (int index=0; index<codeArray.length; index++) {
			String inputChangeRate = changeRateArray[index];
			Date stockDate = DateUtils.stringToDate(sDate);
			String stockCode = codeArray[index];
			AllDetailStock allDetailStock = allDetailStockDao.getAllDetailStockByKey(stockDate, stockCode);
			if (allDetailStock == null) {
				if (allDetailStockFlg) {
					allDetailStockFlg = false;
					Exception exception = new IOException(sDate + " ��Ʊ" + codeArray[index] + "(" + PropertiesUtils.getProperty(codeArray[index]) + ")...�����ڱ�(all_detail_stock_)�У�");
					exception.printStackTrace();
					log.loger.error(exception);
				}
			} else if (Double.valueOf(inputChangeRate).compareTo(allDetailStock.getChangeRate()) == 0){
			   continue;
			}

			AllImportStock allImportStock = allImportStockDao.getAllImportStockByKey(stockDate, stockCode);
			if (allImportStock == null) {
				if (allImportStockFlg) {
					allImportStockFlg = false;
					Exception exception = new IOException(sDate + " ��Ʊ" + codeArray[index] + "(" + PropertiesUtils.getProperty(codeArray[index]) + ")...�����ڱ�(all_import_stock_)�У�");
					throw exception;
				}
			} else if (Double.valueOf(inputChangeRate).compareTo(allImportStock.getChangeRate()) != 0) {
				String[] errorStock = new String[3];
				errorStock[0] = codeArray[index];
				errorStock[1] = inputChangeRate;
				errorStock[2] = DataUtils.formatNumber(allImportStock.getChangeRate());
				errorStockList.add(errorStock);
			}
		}
		return errorStockList;
	}

	/**
	 * ��֤�����Ʊ�Ļ�����
	 *
	 */
	private List<String[]> listErrorStockTurnoverRate(String sDate, String[] codeArray, String[] turnoverRateArray) throws Exception {

		boolean allDetailStockFlg = true;
		boolean allImportStockFlg = true;
		List<String[]> errorStockList = new ArrayList<String[]>();
		for (int index=0; index<codeArray.length; index++) {
			String inputTurnoverRate = turnoverRateArray[index];
			Date stockDate = DateUtils.stringToDate(sDate);
			String stockCode = codeArray[index];
			AllDetailStock allDetailStock = allDetailStockDao.getAllDetailStockByKey(stockDate, stockCode);
			if (allDetailStock==null) {
				if (allDetailStockFlg) {
					allDetailStockFlg = false;
					Exception exception = new IOException(sDate + " ��Ʊ" + codeArray[index] + "(" + PropertiesUtils.getProperty(codeArray[index]) + ")...�����ڱ�(all_detail_stock_)�У�");
					exception.printStackTrace();
					log.loger.error(exception);
				}
			} else if (DataUtils.isEqualsTurnoverRate(Double.valueOf(inputTurnoverRate), allDetailStock.getTurnoverRate())) {
				continue;
			}

			AllImportStock allImportStock = allImportStockDao.getAllImportStockByKey(stockDate, stockCode);
			if (allImportStock == null) {
				if (allImportStockFlg) {
					allImportStockFlg = false;
					Exception exception = new IOException(sDate + " ��Ʊ" + codeArray[index] + "(" + PropertiesUtils.getProperty(codeArray[index]) + ")...�����ڱ�(all_import_stock_)�У�");
					throw exception;
				}
			} else if (!DataUtils.isEqualsTurnoverRate(Double.valueOf(inputTurnoverRate), allImportStock.getTurnoverRate())) {
				String[] errorStock = new String[3];
				errorStock[0] = codeArray[index];
				errorStock[1] = inputTurnoverRate;
				errorStock[2] = DataUtils.formatNumber(allImportStock.getTurnoverRate());
				errorStockList.add(errorStock);
			}
		}
		return errorStockList;
	}
	
	/**
	 * �ж�����������ǲ���ÿ�չ�Ʊ���ݱ�(daily_stock_)�к�ԭʼ���ݱ�(original_stock_)��������ڣ�����Ƿ���null��������Ƿ����������
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
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(original_stock_)���ֶ�(stock_code_)MD5������֤��ʼ...");
		List<OriginalStock> invalidList = validateOriginalDataMD5(originalList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------MD5��֤ԭʼ��Ʊ������֤��Ч����----------------");
			log.loger.warn("---------------MD5��֤ԭʼ��Ʊ������֤��Ч����----------------");
			for (int index=0; index<invalidList.size(); index++) {
				OriginalStock data = invalidList.get(index);
				System.out.println("MD5��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate())
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " change_rates_=" + data.getChangeRates());

				log.loger.warn("MD5��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " change_rates_=" + data.getChangeRates());
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(original_stock_)���ֶ�(stock_code_)MD5������֤�ɹ���");
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
	 * �ж�����������ǲ���ÿ�չ�Ʊ���ݱ���������ڣ�����Ƿ���null��������Ƿ����������
	 *
	 */
	private Date isRecentDateInDailyStock(String sDate) {

		Date recentStockDate = new Date();
		dailyStockDao = new DailyStockDao();
		try {
			recentStockDate = dailyStockDao.getRecentStockDate();
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao);
		}
		Date inputDate = DateUtils.stringToDate(sDate);
		return inputDate.compareTo(recentStockDate)>=0?null:recentStockDate; 
	}
	
	/**
	 * �ж�����������ǲ���ԭʼ��Ʊ���ݱ���������ڣ�����Ƿ���null��������Ƿ����������
	 *
	 */
	public Date isRecentDateInOriginalStock(String sDate) {

		Date recentStockDate = new Date();
		originalStockDao = new OriginalStockDao();
		try {
			recentStockDate = originalStockDao.getRecentStockDate();
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(originalStockDao);
		}
		Date inputDate = DateUtils.stringToDate(sDate);
		return inputDate.compareTo(recentStockDate)>=0?null:recentStockDate;
	}

	private boolean checkOriginalDataDES(List<OriginalStock> originalList) {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(original_stock_)���ֶ�(stock_code_)DES������֤��ʼ...");
		List<OriginalStock> invalidList = validateOriginalDataDES(originalList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------DES��֤ԭʼ��Ʊ������֤��Ч����----------------");
			log.loger.warn("---------------DES��֤ԭʼ��Ʊ������֤��Ч����----------------");
			for (int index=0; index<invalidList.size(); index++) {
				OriginalStock data = invalidList.get(index);
				System.out.println("DES��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " ���ܵ�stock_codes=" + data.getDecryptStockCodes()
									+ " change_rates_=" + data.getChangeRates()
									+ " ���ܵ�change_rates=" + data.getDecryptChangeRates());

				log.loger.warn("DES��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " ���ܵ�stock_codes=" + data.getDecryptStockCodes()
									+ " change_rates_=" + data.getChangeRates()
									+ " ���ܵ�change_rates=" + data.getDecryptChangeRates());
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(original_stock_)���ֶ�(stock_code_)DES������֤�ɹ���");
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
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(original_stock_)�й�Ʊ��������ȷ����֤��ʼ...");
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
			System.out.println("---------------ԭʼ��Ʊ������֤��Ч����----------------");
			log.loger.warn("---------------ԭʼ��Ʊ������֤��Ч����----------------");
			for (int index=0; index<invalidateOriginalList.size(); index++) {
				OriginalStock data = invalidateOriginalList.get(index);
				System.out.println("��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " change_rates_=" + data.getChangeRates());

				log.loger.warn("��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_number_=" + data.getStockNumber()
									+ " stock_codes_=" + data.getStockCodes()
									+ " change_rates_=" + data.getChangeRates());
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(original_stock_)�й�Ʊ��������ȷ����֤�ɹ���");
		}
		return flg;
	}
}