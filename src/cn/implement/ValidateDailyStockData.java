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
	 * ��֤ÿ�չ�Ʊ���ݣ��������Ч����
	 * 
	 */
	public void validateDailyStockData() {
		dailyStockDao = new DailyStockDao();
		originalStockDao = new OriginalStockDao();
		try {
			List<DailyStock> dailyList = dailyStockDao.listDailyData();
			if (dailyList.size() > 0) {
				// ��֤ÿ�չ�Ʊ�����Ƿ���ԭʼ��Ʊ������
				boolean existFlg = validateDailyStockExist(dailyList);
				// ��֤�����Ƿ���ȷ
				boolean codeChangRateFlg = validateStockCodeAndChangeRate(dailyList);
				// ��֤�ǵ���ʶ
				boolean changFlg = validateChangFlg(dailyList);
				// ��֤ÿ�չ�Ʊ���ݵ�����
				boolean numberFlg = validateDailyStockNumber();
				if (existFlg && codeChangRateFlg && changFlg && numberFlg) {
					System.out.println("ÿ�չ�Ʊ����(daily_stock_)��֤�ɹ���");
					log.loger.info("ÿ�չ�Ʊ����(daily_stock_)��֤�ɹ���");
				}
			} else {
				System.out.println("���ݿ���û��ÿ�չ�Ʊ���ݣ�");
				log.loger.info("���ݿ���û��ÿ�չ�Ʊ���ݣ�");
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
			System.out.println("---------------ÿ�չ�Ʊ���ݹ�Ʊ���벻����ԭʼ��Ʊ������----------------");
			log.loger.info("---------------ÿ�չ�Ʊ���ݹ�Ʊ���벻����ԭʼ��Ʊ������----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("�����ڵ�ÿ�չ�Ʊ����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" 
														  + PropertiesUtils.getProperty(data.getStockCode()) 
														  + ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()));
				log.loger.info("�����ڵ�ÿ�չ�Ʊ����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" 
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
			System.out.println("---------------ÿ�չ�Ʊ���ݹ�Ʊ������ǵ�����֤��Ч����----------------");
			log.loger.info("---------------ÿ�չ�Ʊ���ݹ�Ʊ������ǵ�����֤��Ч����----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("��֤������Ч����--" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_code_=" + data.getStockCode()
									+ " ���ܵ�change_rate_=" + data.getChangeRate()
									+ " ���ܵ�change_rate=" + data.getDecryptChangeRate());
				log.loger.info("��֤������Ч����--" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_code_=" + data.getStockCode()
									+ " ���ܵ�change_rate_=" + data.getChangeRate()
									+ " ���ܵ�change_rate=" + data.getDecryptChangeRate());
			}
		} 
		return flg;
	}
	
	private boolean validateChangFlg(List<DailyStock> dailyList) {
		
		boolean flg = true;
		List<DailyStock> invalidList = checkChangFlg(dailyList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------ÿ�չ�Ʊ�����ǵ���ʶ��֤��Ч����----------------");
			log.loger.info("---------------ÿ�չ�Ʊ�����ǵ���ʶ��֤��Ч����----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("��֤������Ч����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
									+ ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()) 
									+ " δ���ܵ�stock_code_=" + data.getStockCode()
									+ " ���ܵ�stock_code_=" + data.getDecryptStockCode()
									+ " δ���ܵ�change_rate_=" + data.getChangeRate()
									+ " ���ܵ�change_rate_=" + data.getDecryptChangeRate());
				log.loger.info("��֤������Ч����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
									+ ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()) 
									+ " δ���ܵ�stock_code_=" + data.getStockCode()
									+ " ���ܵ�stock_code_=" + data.getDecryptStockCode()
									+ " δ���ܵ�change_rate_=" + data.getChangeRate()
									+ " ���ܵ�change_rate_=" + data.getDecryptChangeRate());
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
			System.out.println("---------------ԭʼ��Ʊ������ÿ�չ�Ʊ������֤��Ч����----------------");
			log.loger.info("---------------ԭʼ��Ʊ������ÿ�չ�Ʊ������֤��Ч����----------------");
			for (int index=0; index<invalidDataList.size(); index++) {
				DailyStock data = invalidDataList.get(index);
				System.out.println("��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " ԭʼ��Ʊ���� stock_number_=" + data.getOriginalCount()
									+ " ÿ�չ�Ʊ���� daily_count=" + data.getDailyCount());
				log.loger.info("��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " ԭʼ��Ʊ���� stock_number_=" + data.getOriginalCount()
									+ " ÿ�չ�Ʊ���� daily_count=" + data.getDailyCount());
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
