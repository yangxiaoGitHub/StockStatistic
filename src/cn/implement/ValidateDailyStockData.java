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
	 * ��֤ÿ�չ�Ʊ���ݣ��������Ч����
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
				// ��֤ÿ�չ�Ʊ�����Ƿ���ԭʼ��Ʊ������
				boolean existFlg = validateDailyStockExist(dailyList);
				// ��֤�����Ƿ���ȷ
				boolean codeChangRateFlg = validateStockCodeAndChangeRate(dailyList);
				// ��֤�ǵ���ʶ
				boolean changFlg = validateChangFlg(dailyList);
				// ��֤ÿ�չ�Ʊ���ݵ�����
				boolean numberFlg = validateDailyStockNumber();
				// ��֤ÿ�չ�Ʊ���ݵ��ǵ����ͻ�����
				boolean changeTurnoverFlg = validateChangeRateAndTurnoverRate(dailyList);
				if (existFlg && codeChangRateFlg && changFlg && numberFlg && changeTurnoverFlg) {
					System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>ÿ�չ�Ʊ����(daily_stock_)��֤�ɹ���" + CommonUtils.getLineBreak());
					log.loger.warn(DateUtils.dateTimeToString(new Date()) + "=====>ÿ�չ�Ʊ����(daily_stock_)��֤�ɹ���" + CommonUtils.getLineBreak());
				}
			} else {
				System.out.println("���ݿ���û��ÿ�չ�Ʊ���ݣ�");
				log.loger.warn("���ݿ���û��ÿ�չ�Ʊ���ݣ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, originalStockDao, allDetailStockDao, allImportStockDao, historyStockDao);
		}
	}
	
	private boolean validateChangeRateAndTurnoverRate(List<DailyStock> dailyList) throws SQLException, IOException {
		
		System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>��(daily_stock_)��ÿ�չ�Ʊ���ǵ�������������֤��ʼ...");
		String stockCodes = getStockCodesFromList(dailyList);
		Date[] minMaxDate = getMinMaxDateFromList(dailyList);
		// ���(all_detail_stock_)�Ƚ�
		List<AllDetailStock> detailStockList = allDetailStockDao.getAllDetailStockByCodesAndTime(stockCodes, minMaxDate);
		Map<String, AllDetailStock> allDetailStockMap = (Map<String, AllDetailStock>) CommonUtils.convertListToMap(detailStockList, false);
		List<String[]> allDetailSotckInvalidList = getErrorListFromAllDetailStock(dailyList, allDetailStockMap);
		// ���(all_import_stock_)�Ƚ�
		List<AllImportStock> allImportStockList = allImportStockDao.getAllImportStockByCodesAndDate(stockCodes, minMaxDate);
		Map<String, AllImportStock> allImportStockMap = (Map<String, AllImportStock>) CommonUtils.convertListToMap(allImportStockList, false);
		List<String[]> allImportStockInvalidList = getErrorListFromAllImportStock(dailyList, allImportStockMap);
		// ���(history_stock_)�Ƚ�
		List<HistoryStock> historyStockList = historyStockDao.getHistoryStockByCodesAndTime(stockCodes, minMaxDate);
		Map<String, HistoryStock> historyStockMap = (Map<String, HistoryStock>) CommonUtils.convertListToMap(historyStockList, false);
		List<String[]> historyStockInvalidList = getErrorListFromHistoryStock(dailyList, historyStockMap);
		boolean printFLg = printInvalidList(allDetailSotckInvalidList, allImportStockInvalidList, historyStockInvalidList);
		if (!printFLg)
			System.out.println(DateUtils.dateTimeToString(new Date()) + ">>>>>>>��(daily_stock_)��ÿ�չ�Ʊ���ǵ�������������֤�ɹ���");
		System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>��(daily_stock_)��ÿ�չ�Ʊ���ǵ�������������֤����...");
		return false;
	}

	private boolean printInvalidList(List<String[]> allDetailSotckInvalidList, 
									 List<String[]> allImportStockInvalidList,
									 List<String[]> historyStockInvalidList) {

		boolean flg = false;
		if (allDetailSotckInvalidList.size() > 0) {
			System.out.println("---------------��(daily_stock_)�ǵ����ͻ�������֤��Ч����(���(all_detail_stock_)�Ƚ�)----------------");
			log.loger.warn("---------------��(daily_stock_)�ǵ����ͻ�������֤��Ч����(���(all_detail_stock_)�Ƚ�)----------------");
			for (int index = 0; index < allDetailSotckInvalidList.size(); index++) {
				String[] data = allDetailSotckInvalidList.get(index);
				String[] messages = getErrorMessages(data);
				System.out.println((index + 1) + ": " + data[0] + "�Ĺ�Ʊ" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "�ڱ�(daily_stock_)�е�" + messages[0] + "-----" + "�ڱ�(all_detail_stock_)�е�" + messages[1]);
				log.loger.warn((index + 1) + ": " + data[0] + "�Ĺ�Ʊ" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "�ڱ�(daily_stock_)�е�" + messages[0] + "-----" + "�ڱ�(all_detail_stock_)�е�" + messages[1]);
				flg = true;
			}
		}
		if (allImportStockInvalidList.size() > 0) {
			System.out.println("---------------��(daily_stock_)�ǵ����ͻ�������֤��Ч����(���(all_import_stock_)�Ƚ�)----------------");
			log.loger.warn("---------------��(daily_stock_)�ǵ����ͻ�������֤��Ч����(���(all_import_stock_)�Ƚ�)----------------");
			for (int index = 0; index < allImportStockInvalidList.size(); index++) {
				String[] data = allImportStockInvalidList.get(index); //0:stockDate,1:stockCode,2:changeRate(daily_stock_),3:changeRate(all_import_stock_),
																	  //4:turnoverRate(daily_stock_),5:turnoverRate(all_import_stock_)
				String[] messages = getErrorMessages(data);
				System.out.println((index + 1) + ": " + data[0] + "�Ĺ�Ʊ" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "�ڱ�(daily_stock_)�е�" + messages[0] + "-----" + "�ڱ�(all_import_stock_)�е�" + messages[1]);
				log.loger.warn((index + 1) + ": " + data[0] + "�Ĺ�Ʊ" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "�ڱ�(daily_stock_)�е�" + messages[0] + "-----" + "�ڱ�(all_import_stock_)�е�" + messages[1]);
				flg = true;
			}
		}
		if (historyStockInvalidList.size() > 0) {
			System.out.println("---------------��(daily_stock_)�ǵ����ͻ�������֤��Ч����(���(history_stock_)�Ƚ�)----------------");
			log.loger.warn("---------------��(daily_stock_)�ǵ����ͻ�������֤��Ч����(���(history_stock_)�Ƚ�)----------------");
			for (int index = 0; index < historyStockInvalidList.size(); index++) {
				String[] data = historyStockInvalidList.get(index);
				String[] messages = new String[2];
				if (Double.parseDouble(data[2]) != 0) {
					messages[0] = "�ǵ���Ϊ��" + data[2] + "%";
				}
				if (Double.parseDouble(data[3]) != 0) {
					messages[1] = "�ǵ���Ϊ��" + data[3] + "%";
				}
				System.out.println((index + 1) + ": " + data[0] + "�Ĺ�Ʊ" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "�ڱ�(daily_stock_)�е�" + messages[0] + "-----" + "�ڱ�(history_stock_)�е�" + messages[1]);
				log.loger.warn((index + 1) + ": " + data[0] + "�Ĺ�Ʊ" + data[1] + "(" + PropertiesUtils.getProperty(data[1]) + ")"
						+ "�ڱ�(daily_stock_)�е�" + messages[0] + "-----" + "�ڱ�(history_stock_)�е�" + messages[1]);
				flg = true;
			}
		}
		return flg;
	}

	private boolean validateDailyStockExist(List<DailyStock> dailyList) throws Exception {

		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(daily_stock_)�еĹ�Ʊ�Ƿ����ԭʼ��Ʊ��Ϣ��(original_stock_)����֤��ʼ...");
		List<OriginalStock> originalList = originalStockDao.listOriginalData();
		Map<Date, List<String>> originalMap = getOriginalMapFromList(originalList);
		List<DailyStock> invalidList = checkDailyStockExist(originalMap, dailyList);

		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------ÿ�չ�Ʊ���ݹ�Ʊ���벻����ԭʼ��Ʊ������----------------");
			log.loger.warn("---------------ÿ�չ�Ʊ���ݹ�Ʊ���벻����ԭʼ��Ʊ������----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("�����ڵ�ÿ�չ�Ʊ����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" 
														  + PropertiesUtils.getProperty(data.getStockCode()) 
														  + ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()));
				log.loger.warn("�����ڵ�ÿ�չ�Ʊ����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" 
													  + PropertiesUtils.getProperty(data.getStockCode()) 
													  + ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()));
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(daily_stock_)�еĹ�Ʊ�Ƿ����ԭʼ��Ʊ��Ϣ��(original_stock_)����֤�ɹ���");
		}
		return flg;
	}
	
	private boolean validateStockCodeAndChangeRate(List<DailyStock> dailyList) {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(daily_stock_)���ֶι�Ʊ����(stock_code_)���ǵ���(change_rate_)����Ч����֤��ʼ...");
		List<DailyStock> invalidList = checkStockCodeAndChangeRate(dailyList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------ÿ�չ�Ʊ���ݹ�Ʊ������ǵ�����֤��Ч����----------------");
			log.loger.warn("---------------ÿ�չ�Ʊ���ݹ�Ʊ������ǵ�����֤��Ч����----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("��֤������Ч����--" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_code_=" + data.getStockCode()
									+ " ���ܵ�change_rate_=" + data.getChangeRate()
									+ " ���ܵ�change_rate=" + data.getDecryptChangeRate());
				log.loger.warn("��֤������Ч����--" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " stock_code_=" + data.getStockCode()
									+ " ���ܵ�change_rate_=" + data.getChangeRate()
									+ " ���ܵ�change_rate=" + data.getDecryptChangeRate());
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(daily_stock_)���ֶι�Ʊ����(stock_code_)���ǵ���(change_rate_)����Ч����֤�ɹ���");
		}
		return flg;
	}
	
	private boolean validateChangFlg(List<DailyStock> dailyList) {
		
		boolean flg = true;
		List<DailyStock> invalidList = checkChangFlg(dailyList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------ÿ�չ�Ʊ�����ǵ���ʶ��֤��Ч����----------------");
			log.loger.warn("---------------ÿ�չ�Ʊ�����ǵ���ʶ��֤��Ч����----------------");
			for (int index=0; index<invalidList.size(); index++) {
				DailyStock data = invalidList.get(index);
				System.out.println("��֤������Ч����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
									+ ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()) 
									+ " δ���ܵ�stock_code_=" + data.getStockCode()
									+ " ���ܵ�stock_code_=" + data.getStockCodeDES()
									+ " δ���ܵ�change_rate_=" + data.getChangeRate()
									+ " ���ܵ�change_rate_=" + data.getDecryptChangeRate());
				log.loger.warn("��֤������Ч����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
									+ ") stock_date_=" +  DateUtils.dateToString(data.getStockDate()) 
									+ " δ���ܵ�stock_code_=" + data.getStockCode()
									+ " ���ܵ�stock_code_=" + data.getStockCodeDES()
									+ " δ���ܵ�change_rate_=" + data.getChangeRate()
									+ " ���ܵ�change_rate_=" + data.getDecryptChangeRate());
			}
		} 
		return flg;
	}
	
	private boolean validateDailyStockNumber() throws Exception {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(daily_stock_)��ÿ�չ�Ʊ��������֤��ʼ...");
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
			System.out.println("---------------ԭʼ��Ʊ������ÿ�չ�Ʊ������֤��Ч����----------------");
			log.loger.warn("---------------ԭʼ��Ʊ������ÿ�չ�Ʊ������֤��Ч����----------------");
			for (int index=0; index<invalidDataList.size(); index++) {
				DailyStock data = invalidDataList.get(index);
				System.out.println("��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " ԭʼ��Ʊ���� stock_number_=" + data.getOriginalCount()
									+ " ÿ�չ�Ʊ���� daily_count=" + data.getDailyCount());
				log.loger.warn("��֤������Ч����---" + (index+1) +": stock_date_=" 
									+  DateUtils.dateToString(data.getStockDate()) 
									+ " ԭʼ��Ʊ���� stock_number_=" + data.getOriginalCount()
									+ " ÿ�չ�Ʊ���� daily_count=" + data.getDailyCount());
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(daily_stock_)��ÿ�չ�Ʊ��������֤�ɹ���");
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
		System.out.println("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_detail_stock_)�Ƚ�)��֤��ʼ...");
		log.loger.warn("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_detail_stock_)�Ƚ�)��֤��ʼ...");
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
//				System.out.println("-------->��(daily_stock_)�е����ڣ�" + DateUtils.dateToString(stockDate) + "�Ĺ�Ʊ" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")�ڱ�(all_detail_stock_)�в����ڣ�");
//				log.loger.warn("-------->��(daily_stock_)�е����ڣ�" + DateUtils.dateToString(stockDate) + "�Ĺ�Ʊ" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")�ڱ�(all_detail_stock_)�в����ڣ�");
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
		System.out.println(" @@@@@@@@@@@@>���¹�Ʊ�����ڱ�(all_detail_stock_)�У�" + StringUtils.join(notExistList.toArray(), ","));
		log.loger.warn(" @@@@@@@@@@@@>���¹�Ʊ�����ڱ�(all_detail_stock_)�У�" + StringUtils.join(notExistList.toArray(), ","));
		System.out.println(" >>>>>>>>>>>>>��(daily_stock_)�ǵ����ͻ��������(all_detail_stock_)�Ƚϴ���Ϊ��" + count + "��");
		log.loger.warn(" >>>>>>>>>>>>>��(daily_stock_)�ǵ����ͻ��������(all_detail_stock_)�Ƚϴ���Ϊ��" + count + "��");
		if (invalidList.size() == 0) {
			System.out.println("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_detail_stock_)�Ƚ�)��֤�ɹ���");
			log.loger.warn("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_detail_stock_)�Ƚ�)��֤�ɹ���");
		} else {
			System.out.println("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_detail_stock_)�Ƚ�)��֤ʧ�ܣ�");
			log.loger.warn("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_detail_stock_)�Ƚ�)��֤ʧ�ܣ�");
		}
		return invalidList;
	}

	private List<String[]> getErrorListFromAllImportStock(List<DailyStock> dailyList, Map<String, AllImportStock> allImportStockMap) {

		long count = 0;
		List<String[]> invalidList = new ArrayList<String[]>();
		List<String> notExistList = new ArrayList<String>();
		System.out.println("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_import_stock_)�Ƚ�)��֤��ʼ...");
		log.loger.warn("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_import_stock_)�Ƚ�)��֤��ʼ...");
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
//				System.out.println("-------->��(daily_stock_)�е����ڣ�" + DateUtils.dateToString(stockDate) + "�Ĺ�Ʊ" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")�ڱ�(all_import_stock_)�в����ڣ�");
//				log.loger.warn("-------->��(daily_stock_)�е����ڣ�" + DateUtils.dateToString(stockDate) + "�Ĺ�Ʊ" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")�ڱ�(all_import_stock_)�в����ڣ�");
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

		System.out.println(" @@@@@@@@@@@@>���¹�Ʊ�����ڱ�(all_import_stock_)�У�" + StringUtils.join(notExistList.toArray(), ","));
		log.loger.warn(" @@@@@@@@@@@@>���¹�Ʊ�����ڱ�(all_import_stock_)�У�" + StringUtils.join(notExistList.toArray(), ","));
		System.out.println(" >>>>>>>>>>>>>��(daily_stock_)�ǵ����ͻ��������(all_import_stock_)�Ƚϴ���Ϊ��" + count + "��");
		log.loger.warn(" >>>>>>>>>>>>>��(daily_stock_)�ǵ����ͻ��������(all_import_stock_)�Ƚϴ���Ϊ��" + count + "��");
		if (invalidList.size() == 0) {
		    System.out.println("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_import_stock_)�Ƚ�)��֤�ɹ���");
		    log.loger.warn("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_import_stock_)�Ƚ�)��֤�ɹ���");
		} else {
		    System.out.println("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_import_stock_)�Ƚ�)��֤ʧ�ܣ�");
		    log.loger.warn("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(all_import_stock_)�Ƚ�)��֤ʧ�ܣ�");
		}

		return invalidList;
	}

	private List<String[]> getErrorListFromHistoryStock(List<DailyStock> dailyList, Map<String, HistoryStock> historyStockMap) throws SQLException, IOException {
		
		long count = 0;
		List<String[]> invalidList = new ArrayList<String[]>();
		List<String> notExistList = new ArrayList<String>();
		System.out.println("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(history_stock_)�Ƚ�)��֤��ʼ...");
		log.loger.warn("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(history_stock_)�Ƚ�)��֤��ʼ...");
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
//				System.out.println("-------->��(daily_stock_)�е����ڣ�" + DateUtils.dateToString(stockDate) + "�Ĺ�Ʊ" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")�ڱ�(history_stock_)�в����ڣ�");
//				log.loger.warn("-------->��(daily_stock_)�е����ڣ�" + DateUtils.dateToString(stockDate) + "�Ĺ�Ʊ" 
//								 + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")�ڱ�(history_stock_)�в����ڣ�");
				continue;
			}
			HistoryStock preHistoryStock = getHistoryStockByKey(stockCode, stockDate);
			if (preHistoryStock == null) {
				Exception exception = new IOException(DateUtils.dateToString(stockDate) + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")�ڱ�(history_stock_)��û��ǰһ�տ������ݣ�");
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
		System.out.println(" @@@@@@@@@@@@>���¹�Ʊ�����ڱ�(history_stock_)�У�" + StringUtils.join(notExistList.toArray(), ","));
		log.loger.warn(" @@@@@@@@@@@@>���¹�Ʊ�����ڱ�(history_stock_)�У�" + StringUtils.join(notExistList.toArray(), ","));
		System.out.println(" >>>>>>>>>>>>>��(daily_stock_)�ǵ����ͻ��������(history_stock_)�Ƚϴ���Ϊ��" + count + "��");
		log.loger.warn(" >>>>>>>>>>>>>��(daily_stock_)�ǵ����ͻ��������(history_stock_)�Ƚϴ���Ϊ��" + count + "��");
		if (invalidList.size() == 0) {
		    System.out.println("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(history_stock_)�Ƚ�)��֤�ɹ���");
		    log.loger.warn("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(history_stock_)�Ƚ�)��֤�ɹ���");
		} else {
		    System.out.println("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(history_stock_)�Ƚ�)��֤ʧ�ܣ�");
		    log.loger.warn("++++++++>��(daily_stock_)�ǵ����ͻ�����(���(history_stock_)�Ƚ�)��֤ʧ�ܣ�");
		}
		return invalidList;
	}
	
	private String[] getErrorMessages(String[] data) {

		String[] messages = {DataUtils._BLANK, DataUtils._BLANK};
		if ((DataUtils._MIN_CHANGE_RATE.compareTo(Double.parseDouble(data[2]))) != 0) {
			messages[0] = "�ǵ���Ϊ��" + data[2] + "%��";
		}
		if (DataUtils._MIN_CHANGE_RATE.compareTo(Double.parseDouble(data[3])) != 0) {
			messages[0] += "������Ϊ��" + data[3] + "%";
		}
		if (DataUtils._MIN_CHANGE_RATE.compareTo(Double.parseDouble(data[4])) != 0) { // || Double.parseDouble(data[5]) != 0) {
			messages[1] = "�ǵ���Ϊ��" + data[4] + "%��";
		}
		if (DataUtils._MIN_CHANGE_RATE.compareTo(Double.parseDouble(data[5])) != 0) {
			messages[1] += "������Ϊ��" + data[5] + "%";
		}
		return messages;
	}
}
