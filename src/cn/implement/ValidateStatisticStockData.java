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
	 * ��֤ͳ�ƹ�Ʊ���ݣ��������Ч����
	 * 
	 */
	public void validateStatisticStockData() {
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
	    // �ϲ��ǵ�������֤
		/*List<StatisticStock> invalidUpDownList = listErrorUpDownNumber(statisticStockList, upDownAndNumberMap);
		List<StatisticStock> invalidUpList = listErrorUpNumber(statisticStockList, upDownAndNumberMap);
		List<StatisticStock> invalidDownList = listErrorDownNumber(statisticStockList, upDownAndNumberMap);
		List<StatisticStock> invalidSumList = listErrorSum(statisticStockList);*/
		
		List<StatisticStock> invalidUpAndDownList = listErrorUpAndDownNumber(statisticStockList, upDownAndNumberMap);
		if (invalidUpAndDownList.size() > 0) {
			flg = false;
			System.out.println("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ǵ�������Ч----------------");
			log.loger.info("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ǵ�������Ч----------------");
			for (int index=0; index<invalidUpAndDownList.size(); index++) {
				StatisticStock data = invalidUpAndDownList.get(index);
				System.out.println("��֤�ǵ�������Ч---" + CommonUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": ��Ʊ" + data.getStockCode() 
								 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")��ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�е�����Ϊ: " + StatisticStock.UP_DOWN_NUMBER + ":" 
								 + data.getErrorUpDownNumber() + "," + StatisticStock.UP_NUMBER + ":" + data.getErrorUpNumber() + "," + StatisticStock.DOWN_NUMBER + ":" + data.getErrorDownNumber() 
								 + "  ��ÿ�չ�Ʊ��Ϣ���е�ͳ������Ϊ: " + StatisticStock.UP_DOWN_NUMBER + ":" + data.getUpDownNumber() + "," + StatisticStock.UP_NUMBER + ":" + data.getUpNumber() 
								 + "," + StatisticStock.DOWN_NUMBER + ":" + data.getDownNumber());
				log.loger.info("��֤�ǵ�������Ч---" + CommonUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": ��Ʊ" + data.getStockCode() 
								 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")��ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�е�����Ϊ: " + StatisticStock.UP_DOWN_NUMBER + ":" 
								 + data.getErrorUpDownNumber() + "," + StatisticStock.UP_NUMBER + ":" + data.getErrorUpNumber() + "," + StatisticStock.DOWN_NUMBER + ":" + data.getErrorDownNumber() 
								 + "  ��ÿ�չ�Ʊ��Ϣ���е�ͳ������Ϊ: " + StatisticStock.UP_DOWN_NUMBER + ":" + data.getUpDownNumber() + "," + StatisticStock.UP_NUMBER + ":" + data.getUpNumber() 
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
			System.out.println("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ֶ�(stock_code_��first_date_)��Ч----------------");
			log.loger.info("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ֶ�(stock_code_��first_date_)��Ч----------------");
			for (int index=0; index<invalidList.size(); index++) {
				String[] data = invalidList.get(index);
				System.out.println("��֤������Ч����---" + (index+1) + ": ��Ʊ" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
													   + ") ��(statistic_stock_)�����stock_code_=" + data[0] + " first_date_=" + data[1]
													   + " ��(daily_stock_)��ȷ��stock_code_=" + data[2] + " first_date_=" + data[3]);

				log.loger.info("��֤������Ч����---" + (index+1) + ": ��Ʊ" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
												   + ") ��(statistic_stock_)�����stock_code_=" + data[0] + " first_date_=" + data[1]
												   + " ��(daily_stock_)��ȷ��stock_code_=" + data[2] + " first_date_=" + data[3]);
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
		//��ʼ���Ǻ͵�����
		for (StatisticStock statisticStock : upDownNumberMap.values()) {
			statisticStock.setUpNumber(DataUtils.CONSTANT_INTEGER_ZERO);
			statisticStock.setDownNumber(DataUtils.CONSTANT_INTEGER_ZERO);
			statisticStockMap.put(statisticStock.getStockCode(), statisticStock);
		}
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
	 * ��֤�ǵ����������ش����ǵ�����List 
	 *
	 */
	private List<StatisticStock> listErrorUpAndDownNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticUpAndDownMap) {

		List<StatisticStock> errorUpAndDownNumberList = new ArrayList<StatisticStock>();
		for (StatisticStock statisticStock : statisticStockList) {
			String stockCode = statisticStock.getStockCode();
			StatisticStock statisticUpAndDownStock = statisticUpAndDownMap.get(stockCode);
			//��֤ͳ�ƹ�Ʊ��Ϣ���ǵ�����
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
				System.out.println("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������ÿ�չ�Ʊ��Ϣ��(daily_stock_)�У�");
				log.loger.info("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������ÿ�չ�Ʊ��Ϣ��(daily_stock_)�У�");
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
			System.out.println("��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�еĹ�Ʊ" + statisticStock.getStockCode() + "(" + PropertiesUtils.getProperty(statisticStock.getStockCode()) + ")�ǵ������쳣��");
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
