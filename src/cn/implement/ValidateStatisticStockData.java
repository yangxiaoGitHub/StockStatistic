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
	 * ��֤ͳ�ƹ�Ʊ��(statistic_stock_)�е�����
	 * 
	 */
	public void validateStatisticStockData() {
		dailyStockDao = new DailyStockDao();
		originalStockDao = new OriginalStockDao();
		statisticStockDao = new StatisticStockDao();
		try {
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			if (statisticStockList.size() > 0) {
				// ��֤stock_code_ DES����
				boolean stockCodeFLg = validateStockCodeDESInStatisticStock(statisticStockList);
				// ��֤stock_code_��first_date_(��daily_stock_��Ƚ�)
				boolean codeAndDateInDailyStockFlg = validateStockCodeAndFirstDateInDailyStock(statisticStockList);
				// ��֤stock_code_��first_date_(��original_stock_��Ƚ�)
				boolean codeAndDateInOriginalStockFlg = validateStockCodeAndFirstDateInOriginalStock(statisticStockList);
				if (stockCodeFLg && codeAndDateInDailyStockFlg && codeAndDateInOriginalStockFlg) {
					System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>��(statistic_stock_)������֤�ɹ���" + CommonUtils.getLineBreak());
					log.loger.warn(DateUtils.dateTimeToString(new Date()) + "=====>��(statistic_stock_)������֤�ɹ���" + CommonUtils.getLineBreak());
				}
			} else {
				System.out.println("��(statistic_stock_)��û�й�Ʊ���ݣ�");
				log.loger.warn("��(statistic_stock_)��û�й�Ʊ���ݣ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, originalStockDao, statisticStockDao);
		}
	}

	/**
	 * ��֤stock_code_��first_date_(��original_stock_��Ƚ�)
	 *
	 */
	private boolean validateStockCodeAndFirstDateInOriginalStock(List<StatisticStock> statisticStockList) throws SQLException {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(statistic_stock_)�е�stock_code_��first_date_(��original_stock_��Ƚ�)����ȷ����֤��ʼ...");
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
			System.out.println("---------------��֤��(statistic_stock_)���ֶ�(stock_code_��first_date_)��Ч(��original_stock_��Ƚ�)----------------");
			log.loger.warn("---------------��֤��(statistic_stock_)���ֶ�(stock_code_��first_date_)��Ч(��original_stock_��Ƚ�)----------------");
			for (int index=0; index<invalidList.size(); index++) {
				String[] data = invalidList.get(index);
				System.out.println("��֤������Ч����---" + (index+1) + ": ��Ʊ" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
													   + ") ��(statistic_stock_)�����stock_code_=" + data[0] + " first_date_=" + data[1]
													   + " ��(daily_stock_)��ȷ��stock_code_=" + data[2] + " first_date_=" + data[3]);

				log.loger.warn("��֤������Ч����---" + (index+1) + ": ��Ʊ" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
												   + ") ��(statistic_stock_)�����stock_code_=" + data[0] + " first_date_=" + data[1]
												   + " ��(daily_stock_)��ȷ��stock_code_=" + data[2] + " first_date_=" + data[3]);
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(statistic_stock_)�е�stock_code_��first_date_(��original_stock_��Ƚ�)����ȷ����֤�ɹ���");
		}
		return flg;
	}
	
	/**
	 * ��֤stock_code_��first_date_(��daily_stock_��Ƚ�)
	 *
	 */
	private boolean validateStockCodeAndFirstDateInDailyStock(List<StatisticStock> statisticStockList) throws SQLException {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(statistic_stock_)�е��ֶ�(stock_code_��first_date_)����ȷ����֤��ʼ...");
		List<DailyStock> dailyStockList = dailyStockDao.statisticDailyStock();
		List<String[]> invalidList = listErrorStockCodeAndFirstDate(statisticStockList, dailyStockList, DailyStock.TABLE_NAME);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------��֤��(statistic_stock_)���ֶ�(stock_code_��first_date_)��Ч(��daily_stock_��Ƚ�)----------------");
			log.loger.warn("---------------��֤��(statistic_stock_)���ֶ�(stock_code_��first_date_)��Ч(��daily_stock_��Ƚ�)----------------");
			for (int index=0; index<invalidList.size(); index++) {
				String[] data = invalidList.get(index);
				System.out.println("��֤������Ч����----->" + (index+1) + ": ��Ʊ" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
													   + ") ��(statistic_stock_)�����stock_code_=" + data[0] + " first_date_=" + data[1]
													   + " ��(daily_stock_)��ȷ��stock_code_=" + data[2] + " first_date_=" + data[3]);

				log.loger.warn("��֤������Ч����----->" + (index+1) + ": ��Ʊ" + data[2] + "(" + PropertiesUtils.getProperty(data[2]) 
												   + ") ��(statistic_stock_)�����stock_code_=" + data[0] + " first_date_=" + data[1]
												   + " ��(daily_stock_)��ȷ��stock_code_=" + data[2] + " first_date_=" + data[3]);
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(statistic_stock_)�е��ֶ�(stock_code_��first_date_)����ȷ����֤�ɹ���");
		}
		return flg;
	}

	private List<String[]> listErrorStockCodeAndFirstDate(List<StatisticStock> statisticStockList, List<DailyStock> dailyStockList, String tableFlg) throws SQLException {
		
		List<String[]> errorCodeAndDateList = new ArrayList<String[]>();
		try {
			for (StatisticStock statisticStock : statisticStockList) {
				String stockCode = statisticStock.getStockCode();
				Date firstDate = statisticStock.getFirstDate();
				// �Ƚ�stock_code_
				DailyStock dailyStock = CommonUtils.containStockCode(stockCode, dailyStockList);
				if (dailyStock != null) {
					// �Ƚ�first_date_
					if (firstDate.compareTo(dailyStock.getStockDate()) != 0) {
						String[] errorCodeAndDateArray = new String[4];
						errorCodeAndDateArray[0] = stockCode; //wrong stockCode
						errorCodeAndDateArray[1] = DateUtils.dateToString(firstDate); //wrong firstDate
						errorCodeAndDateArray[2] = dailyStock.getStockCode(); //correct stockCode
						errorCodeAndDateArray[3] = DateUtils.dateToString(dailyStock.getStockDate()); //correct stockDate
						errorCodeAndDateList.add(errorCodeAndDateArray);
					}
				} else {
					System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(statistic_stock_)�еĹ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")�����ڱ�(" + tableFlg + ")�У�");
					log.loger.warn("----->��(statistic_stock_)�еĹ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")�����ڱ�(" + tableFlg + ")�У�");
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return errorCodeAndDateList;
	}

	/**
	 * ��֤statistic_detail_stock_�������ǵ�����
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
			System.out.println("��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�еĹ�Ʊ" + statisticDetailStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(statisticDetailStock.getStockCode()) + ")�ǵ������쳣��");
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return errorStatisticDetailStock;
	}*/
	
	/**
	 * ��֤��(statistic_stock_)�е�stock_code_ DES����
	 *
	 */
	private boolean validateStockCodeDESInStatisticStock(List<StatisticStock> stockList) {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(statistic_stock_)���ֶ�(stock_code_)�� DES������֤��ʼ...");
		List<StatisticStock> invalidList = CommonUtils.listErrorStockCodeDES(stockList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------DES��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��Ч����----------------");
			log.loger.warn("---------------DES��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��Ч����----------------");
			for (int index=0; index<invalidList.size(); index++) {
				StatisticStock data = invalidList.get(index);
				System.out.println("DES��֤������Ч����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
														  + ") δ���ܵ�stock_code_=" + data.getStockCode() + " ���ܵ�stock_code=" + DESUtils.decryptHex(data.getStockCodeDES()));

				log.loger.warn("DES��֤������Ч����---" + (index+1) +": ��Ʊ" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
													  + ") δ���ܵ�stock_code_=" + data.getStockCode() + " ���ܵ�stock_code=" + DESUtils.decryptHex(data.getStockCodeDES()));
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(statistic_stock_)���ֶ�(stock_code_)�� DES������֤�ɹ���");
		}
		return flg;
	}

}
