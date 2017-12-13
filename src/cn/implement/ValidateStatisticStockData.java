package cn.implement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticStock;
import cn.log.Log;

public class ValidateStatisticStockData extends OperationData{
	Log log = Log.getLoger();

	/**
	 * ��֤ͳ�ƹ�Ʊ���ݣ��������Ч����
	 * 
	 */
	public void validateStatistic() {
		statisticStockDao = new StatisticStockDao();
		try {
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			if (statisticStockList.size() > 0) {
				// ��֤stock_code_ DES����
				boolean stockCodeFLg = validateStockCodeDES(statisticStockList);
				// ��֤stock_code_��first_date_
				boolean stockCodeFirstDateFlg = validateStockCodeAndFirstDate(statisticStockList);
				// ��֤��Ʊ���ֵ��ǵ����ǡ�������
				boolean upDownNumberFlg = validateUpAndDownNumber(statisticStockList);
				if (stockCodeFLg && stockCodeFirstDateFlg && upDownNumberFlg) {
					System.out.println("ͳ�ƹ�Ʊ����(statistic_stock_)��֤�ɹ���");
					log.loger.info("ͳ�ƹ�Ʊ����(statistic_stock_)��֤�ɹ���");
				}
			} else {
				System.out.println("���ݿ���û��ͳ�ƹ�Ʊ���ݣ�");
				log.loger.info("���ݿ���û��ͳ�ƹ�Ʊ���ݣ�");
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
		List<StatisticStock> invalidUpDownList = listErrorUpDownNumber(statisticStockList, upDownAndNumberMap);
		List<StatisticStock> invalidUpList = listErrorUpNumber(statisticStockList, upDownAndNumberMap);
		List<StatisticStock> invalidDownList = listErrorDownNumber(statisticStockList, upDownAndNumberMap);
		List<StatisticStock> invalidSumList = listErrorSum(statisticStockList);
		List<StatisticStock> invalidList = new ArrayList<StatisticStock>(invalidUpDownList);
		invalidList.addAll(invalidUpList);
		invalidList.addAll(invalidDownList);
		invalidList.addAll(invalidSumList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ǵ�������Ч----------------");
			log.loger.info("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ǵ�������Ч----------------");
			for (int index=0; index<invalidList.size(); index++) {
				StatisticStock data = invalidList.get(index);
				String errorMessage = "";
				switch(data.getUpDownFlg()) {
					case 1:
						errorMessage = " ����ĵ�����Ϊ��down_number_=" + data.getErrorDownNumber() + " ��ȷ�ĵ�����Ϊ��down_number_=" + data.getDownNumber();
						break;
					case 2:
						errorMessage = " ������ǵ�����Ϊ��up_down_number_=" + data.getErrorUpDownNumber() + " ��ȷ���ǵ�����Ϊ��up_down_number_=" + data.getUpDownNumber();
						break;
					case 3:
						errorMessage = " ������Ǵ���Ϊ��up_number_=" + data.getErrorUpNumber() + " ��ȷ���ǵ�����Ϊ��up_number_=" + data.getUpNumber();
						break;
					case 4:
						errorMessage = " �ǵ������������Ǵ����͵�����֮�ͣ��ǵ�����Ϊ��" + data.getUpDownNumber() + " �Ǵ���Ϊ��" + data.getUpNumber() + " ������Ϊ��" + data.getDownNumber();
						break;
					default:
						errorMessage = " �ǵ�������Ч��";
						break;
				}
				System.out.println("��֤�ǵ�������Ч---" + CommonUtils.supplyNumber(index+1, invalidList.size()) + ": ��Ʊ" + data.getStockCode() 
													   + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")" + errorMessage);
				log.loger.info("��֤�ǵ�������Ч---" + CommonUtils.supplyNumber(index+1, invalidList.size()) +": ��Ʊ" + data.getStockCode() 
												   + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")" + errorMessage);
			}
		}
		return flg;
	}

	private boolean validateStockCodeAndFirstDate(List<StatisticStock> statisticStockList) throws SQLException {
		
		boolean flg = true;
		List<String[]> invalidList = listErrorStockCodeAndFirstDate(statisticStockList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ֶ�(stock_code_��first_date_)��Ч----------------");
			log.loger.info("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ֶ�(stock_code_��first_date_)��Ч----------------");
			for (int index=0; index<invalidList.size(); index++) {
				String[] data = invalidList.get(index);
				System.out.println("��֤������Ч����---" + (index+1) + ": ��Ʊ" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
													   + ") �����stock_code_=" + data[0] + " first_date_=" + data[1]
													   + " ��ȷ��stock_code_=" + data[2] + " first_date_=" + data[3]);

				log.loger.info("��֤������Ч����---" + (index+1) + ": ��Ʊ" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
												   + ") �����stock_code_=" + data[0] + " first_date_=" + data[1]
												   + " ��ȷ��stock_code_=" + data[2] + " first_date_=" + data[3]);
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
				// �Ƚ�stock_code_
				DailyStock dailyStock_stockCode = containStockCode(stockCode, dailyStockList);
				if (dailyStock_stockCode != null) {
					// �Ƚ�first_date_
					if (firstDate.compareTo(dailyStock_stockCode.getStockDate()) != 0) {
						String[] errorCodeAndDateArray = new String[4];
						errorCodeAndDateArray[0] = stockCode;
						errorCodeAndDateArray[1] = DateUtils.Date2String(firstDate);
						errorCodeAndDateArray[2] = dailyStock_stockCode.getStockCode();
						errorCodeAndDateArray[3] = DateUtils.Date2String(dailyStock_stockCode.getStockDate());
						errorCodeAndDateList.add(errorCodeAndDateArray);
					}
				} else {
					String[] errorCodeAndDateArray = new String[4];
					errorCodeAndDateArray[0] = stockCode;
					errorCodeAndDateArray[1] = DateUtils.Date2String(firstDate);
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
		statisticStockMap.putAll(upDownNumberMap);
		// �ϲ�upNumberMap
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
		// �ϲ�downNumberMap
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

	private List<StatisticStock> listErrorUpDownNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticStockMap) throws SQLException {
		
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
	}
	
	private List<StatisticStock> listErrorUpNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticStockMap) throws SQLException {
		
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
	}
	
	private List<StatisticStock> listErrorDownNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticStockMap) throws SQLException {
		
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
	}
	
	private StatisticStock compareUpAndDownNumber(String stockCode, Integer upDownNumber, Map<String, StatisticStock> statisticStockMap, Integer upDownFlg) {
		//upDownFlg 1:�� 2:�ǵ� 3:��
		StatisticStock statisticStock = null;
		try {
			StatisticStock statisticStock_ = statisticStockMap.get(stockCode);
			if (statisticStock_ == null) {
				System.out.println("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������ÿ�չ�Ʊ��Ϣ��(daily_stock_)�У�");
				log.loger.info("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������ÿ�չ�Ʊ��Ϣ��(daily_stock_)�У�");
			} else {
					switch(upDownFlg) {
					case 1:
						if (upDownNumber.compareTo(statisticStock_.getDownNumber()) != 0) {
							statisticStock = new StatisticStock();
							statisticStock.setStockCode(stockCode);
							statisticStock.setDownNumber(statisticStock_.getDownNumber());
							statisticStock.setErrorDownNumber(upDownNumber);
						}
						break;
					case 2:
						if (upDownNumber.compareTo(statisticStock_.getUpDownNumber()) != 0) {
							statisticStock = new StatisticStock();
							statisticStock.setStockCode(stockCode);
							statisticStock.setUpDownNumber(statisticStock_.getUpDownNumber());
							statisticStock.setErrorUpDownNumber(upDownNumber);
						}
						break;
					case 3:
						if (upDownNumber.compareTo(statisticStock_.getUpNumber()) != 0) {
							statisticStock = new StatisticStock();
							statisticStock.setStockCode(stockCode);
							statisticStock.setUpNumber(statisticStock_.getUpNumber());
							statisticStock.setErrorUpNumber(upDownNumber);
						}
						break;
					default:
						System.out.println("�ǵ���ʶ��Ч(upDownFlg=" + upDownFlg + ")");
						log.loger.error("�ǵ���ʶ��Ч(upDownFlg=" + upDownFlg + ")");
						break;
					}
			}
		} catch(Exception ex) {
			String upDownNumber_statistic = "";
			String stockCode_daily = "";
			String upDownNumber_daily = "";
			
			if (statisticStockMap.containsKey(stockCode)) {
				stockCode_daily = stockCode;
			}
			switch(upDownFlg) {
				case 1:
					if (statisticStockMap.containsKey(stockCode)) upDownNumber_daily = statisticStockMap.get(stockCode).getDownNumber()==null?"0":statisticStockMap.get(stockCode).getDownNumber().toString();
					upDownNumber_statistic = " down_number_=" + upDownNumber;
					upDownNumber_daily = " down_number_=" + upDownNumber_daily;
					break;
				case 2:
					if (statisticStockMap.containsKey(stockCode)) upDownNumber_daily = statisticStockMap.get(stockCode).getUpDownNumber()==null?"0":statisticStockMap.get(stockCode).getUpDownNumber().toString();
					upDownNumber_statistic = " upDown_number_=" + upDownNumber;
					upDownNumber_daily = " upDown_number_=" + upDownNumber_daily;
					break;
				case 3:
					if (statisticStockMap.containsKey(stockCode)) upDownNumber_daily = statisticStockMap.get(stockCode).getUpNumber()==null?"0":statisticStockMap.get(stockCode).getUpNumber().toString();
					upDownNumber_statistic = " up_number_=" + upDownNumber;
					upDownNumber_daily = " up_number_=" + upDownNumber_daily;
					break;
				default:
					System.out.println("�ǵ���ʶ��Ч(upDownFlg=" + upDownFlg + ")");
					log.loger.error("�ǵ���ʶ��Ч(upDownFlg=" + upDownFlg + ")");
					break;
			}
			System.out.println("ͳ�ƹ�Ʊ��Ϣ����stock_code_=" + stockCode + upDownNumber_statistic + " ÿ�չ�Ʊ���ݱ���stock_code_=" + stockCode_daily + upDownNumber_daily);
			ex.printStackTrace();
			log.loger.error(ex);
		}
		return statisticStock;
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
			System.out.println("---------------DES��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��Ч����----------------");
			log.loger.info("---------------DES��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��Ч����----------------");
			for (int index=0; index<invalidList.size(); index++) {
				StatisticStock data = invalidList.get(index);
				System.out.println("DES��֤������Ч����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
														  + ") δ���ܵ�stock_code_=" + data.getStockCode() + " ���ܵ�stock_code=" + data.getDecryptStockCode());

				log.loger.info("DES��֤������Ч����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
													  + ") δ���ܵ�stock_code_=" + data.getStockCode() + " ���ܵ�stock_code=" + data.getDecryptStockCode());
			}
		}
		return flg;
	}

	private List<StatisticStock> listErrorSum(List<StatisticStock> statisticStockList) {
		
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
	}

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
