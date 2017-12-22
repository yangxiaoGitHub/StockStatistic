package cn.implement;

import java.io.IOException;
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
import cn.com.JsonUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticStock;

public class ValidateStatisticStockData extends OperationData{

	/**
	 * ��֤ͳ�ƹ�Ʊ���ݣ��������Ч����
	 * 
	 */
	public void validateStatisticStockData() {
		statisticStockDao = new StatisticStockDao();
		dailyStockDao = new DailyStockDao();
		try {
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			if (statisticStockList.size() > 0) {
				// ��֤stock_code_ DES����
				boolean stockCodeFLg = validateStockCodeDES(statisticStockList);
				// ��֤stock_code_��first_date_
				boolean stockCodeFirstDateFlg = validateStockCodeAndFirstDate(statisticStockList);
				// ��֤statistic_stock_���й�Ʊ���ǵ�����
				boolean upDownNumberFlg = validateUpAndDownNumber(statisticStockList);
				// ��֤���ǵ�������Json�ǵ�����(��original_stock_��Ƚ�)
				boolean jsonUpDownNumberFlg = validateJsonUpAndDownNumber(statisticStockList);
				if (stockCodeFLg && stockCodeFirstDateFlg && upDownNumberFlg /*&& jsonUpDownNumberFlg*/) {
					System.out.println("ͳ�ƹ�Ʊ����(statistic_stock_)��֤�ɹ���");
					log.loger.info("ͳ�ƹ�Ʊ����(statistic_stock_)��֤�ɹ���");
				}
			} else {
				System.out.println("���ݿ���û��ͳ�ƹ�Ʊ���ݣ�");
				log.loger.info("���ݿ���û��ͳ�ƹ�Ʊ���ݣ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(statisticStockDao, dailyStockDao);
		}
	}
	
	/**
	 * ��֤statistic_stock_���е����ǵ�����
	 *
	 */
	private boolean validateUpAndDownNumber(List<StatisticStock> statisticStockList) throws SQLException {
		
		boolean flg = true;
		// ͳ��daily_stock_�����ǵ�����
		Map<String, StatisticStock> upDownAndNumberMap = combineUpAndDownNumberMap();
	    // �ϲ��ǵ�������֤
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

	/**
	 * ��֤statistic_stock_����Json�ǵ�����
	 *
	 */
	private boolean validateJsonUpAndDownNumber(List<StatisticStock> statisticStockList) throws SQLException, IOException {
		
		boolean flg = true;
		// ��original_stock_����ͳ�������ǵ�����(����Json�ǵ�����)
		Map<String, StatisticStock> originalUpDownNumberMap = statisticUpAndDownNumberInOriginalStock();
	    // �ϲ��ǵ�������֤
		List<StatisticStock> invalidUpAndDownList = listErrorJsonUpAndDownNumber(statisticStockList, upDownAndNumberMap);
		if (invalidUpAndDownList.size() > 0) {
			flg = false;
			System.out.println("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ǵ�������Ч----------------");
			log.loger.info("---------------��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ǵ�������Ч----------------");
			for (int index=0; index<invalidUpAndDownList.size(); index++) {
				StatisticStock data = invalidUpAndDownList.get(index);
				System.out.println("��֤�ǵ�������Ч---" + CommonUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": ��Ʊ" + data.getStockCode() 
								 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")�ڱ�(statistic_stock_)�еĴ�������Ϊ: " + getCorrectErrorMessages(data)[1] + "  ͳ�Ƶ���ȷ����Ϊ: " + getCorrectErrorMessages(data)[0]);
				log.loger.info("��֤�ǵ�������Ч---" + CommonUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": ��Ʊ" + data.getStockCode() 
				 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")�ڱ�(statistic_stock_)�еĴ�������Ϊ: " + getCorrectErrorMessages(data)[1] + "  ͳ�Ƶ���ȷ����Ϊ: " + getCorrectErrorMessages(data)[0]);
			}
		}
		return flg;
	}

	private String[] getCorrectErrorMessages(StatisticStock data) throws IOException {
		
		String correctMessage = "";
		String errorMessage = "";
		Integer errorUpDownFlg = data.getErrorUpDownFlg();
		switch (errorUpDownFlg) {
		case DataUtils.CONSTANT_INTEGER_EIGHT:
			String oneWeekJson = data.getOneWeek();
			String errorOneWeekJson = data.getErrorOneWeek();
			if (!CommonUtils.isBlank(oneWeekJson) && !CommonUtils.isBlank(errorOneWeekJson)) {
				String[] oneWeekMessages = getCorrectErrorMessagesByJson(oneWeekJson, errorOneWeekJson, StatisticStock.PRE_ONE_WEEK_UP_DOWN_NUM);
				String oneWeekTitle = "һ��";
				correctMessage = oneWeekTitle + oneWeekMessages[0];
				errorMessage = oneWeekTitle + oneWeekMessages[1];
			}

		case DataUtils.CONSTANT_INTEGER_SEVEN:
			String halfMonthJson = data.getHalfMonth();
			String errorHalfMonthJson = data.getErrorHalfMonth();
			if (!CommonUtils.isBlank(halfMonthJson) && !CommonUtils.isBlank(errorHalfMonthJson)) {
				String[] halfMonthMessages = getCorrectErrorMessagesByJson(halfMonthJson, errorHalfMonthJson, StatisticStock.PRE_HALF_MONTH_UP_DOWN_NUM);
				String halfMonthTitle = "����";
				correctMessage += " " + halfMonthTitle + halfMonthMessages[0];
				errorMessage += " " + halfMonthTitle + halfMonthMessages[1];
			}
			
		case DataUtils.CONSTANT_INTEGER_SIX:
			String oneMonthJson = data.getOneMonth();
			String errorOneMonthJson = data.getErrorOneMonth();
			if (!CommonUtils.isBlank(oneMonthJson) && !CommonUtils.isBlank(errorOneMonthJson)) {
				String[] oneMonthMessages = getCorrectErrorMessagesByJson(oneMonthJson, errorOneMonthJson, StatisticStock.PRE_ONE_MONTH_UP_DOWN_NUM);
				String oneMonthTitle = "һ��";
				correctMessage += " " + oneMonthTitle + oneMonthMessages[0];
				errorMessage += " " + oneMonthTitle + oneMonthMessages[1];
			}

		case DataUtils.CONSTANT_INTEGER_FIVE:
			String twoMonthJson = data.getTwoMonth();
			String errorTwoMonthJson = data.getErrorTwoMonth();
			if (!CommonUtils.isBlank(twoMonthJson) && !CommonUtils.isBlank(errorTwoMonthJson)) {
				String[] twoMonthMessages = getCorrectErrorMessagesByJson(twoMonthJson, errorTwoMonthJson, StatisticStock.PRE_TWO_MONTH_UP_DOWN_NUM);
				String twoMonthTitle = "����";
				correctMessage += " " + twoMonthTitle + twoMonthMessages[0];
				errorMessage += " " + twoMonthTitle + twoMonthMessages[1];
			}

		case DataUtils.CONSTANT_INTEGER_FOUR:
			String threeMonthJson = data.getThreeMonth();
			String errorThreeMonthJson = data.getErrorThreeMonth();
			if (!CommonUtils.isBlank(threeMonthJson) && !CommonUtils.isBlank(errorThreeMonthJson)) {
				String[] threeMonthMessages = getCorrectErrorMessagesByJson(threeMonthJson, errorThreeMonthJson, StatisticStock.PRE_THREE_MONTH_UP_DOWN_NUM);
				String threeMonthTitle = "����";
				correctMessage += " " + threeMonthTitle + threeMonthMessages[0];
				errorMessage += " " + threeMonthTitle + threeMonthMessages[1];
			}
						
		case DataUtils.CONSTANT_INTEGER_THREE:
			String halfYearJson = data.getHalfYear();
			String errorHalfYearJson = data.getErrorHalfMonth();
			if (!CommonUtils.isBlank(halfYearJson) && !CommonUtils.isBlank(errorHalfYearJson)) {
				String[] halfYearMessages = getCorrectErrorMessagesByJson(halfYearJson, errorHalfYearJson, StatisticStock.PRE_HALF_YEAR_UP_DOWN_NUM);
				String halfYearTitle = "����";
				correctMessage += " " + halfYearTitle + halfYearMessages[0];
				errorMessage += " " + halfYearTitle + halfYearMessages[1];
			}
			
		case DataUtils.CONSTANT_INTEGER_TWO:
			String oneYearJson = data.getOneYear();
			String errorOneYearJson = data.getErrorOneYear();
			if (!CommonUtils.isBlank(oneYearJson) && !CommonUtils.isBlank(errorOneYearJson)) {
				String[] oneYearMessages = getCorrectErrorMessagesByJson(oneYearJson, errorOneYearJson, StatisticStock.PRE_ONE_YEAR_UP_DOWN_NUM);
				String oneYearTitle = "һ��";
				correctMessage += " " + oneYearTitle + oneYearMessages[0];
				errorMessage += " " + oneYearTitle + oneYearMessages[1];
			}

		case DataUtils.CONSTANT_INTEGER_ONE:
			Integer upDownNumber = data.getUpDownNumber();
			Integer upNumber = data.getUpNumber();
			Integer downNumber = data.getDownNumber();
			Integer errorUpDownNumber = data.getErrorUpDownNumber();
			Integer errorUpNumber = data.getErrorUpNumber();
			Integer errorDownNumber = data.getDownNumber();
			correctMessage += " " + "���ǵ�:" + upDownNumber + "��:" + upNumber + "��:" + downNumber;
			errorMessage += " " + "���ǵ�:" + errorUpDownNumber + "��:" + errorUpNumber + "��:" + errorDownNumber;
			break;
		default:
			IOException ioException = new IOException("���ڱ�ʶ(errorUpDownFlg)����ȷ: " + errorUpDownFlg);
			throw ioException;
		}
		String[] correctErrorArray = {correctMessage, errorMessage};
		return correctErrorArray;
	}

	private String[] getCorrectErrorMessagesByJson(String correctUpAndDownJson, String errorUpAndDownJson, String periodFlg) throws IOException {
		
		String[] upAndDownNumberKeys = getUpAndDownNumberKeysByFlg(periodFlg);
		String upDownNumberKey = upAndDownNumberKeys[0];
		String upNumberKey = upAndDownNumberKeys[1];
		String downNumberKey = upAndDownNumberKeys[2];
		Map<String, Integer> correctUpAndDownMap = JsonUtils.getMapByJson(correctUpAndDownJson);
		Map<String, Integer> errorUpAndDownMap = JsonUtils.getMapByJson(errorUpAndDownJson);
		Integer correctUpDownNumber = correctUpAndDownMap.get(upDownNumberKey);
		Integer correctUpNumber = correctUpAndDownMap.get(upNumberKey);
		Integer correctDownNumber = correctUpAndDownMap.get(downNumberKey);
		Integer errorUpDownNumber = errorUpAndDownMap.get(upDownNumberKey);
		Integer errorUpNumber = errorUpAndDownMap.get(upNumberKey);
		Integer errorDownNumber = errorUpAndDownMap.get(downNumberKey);
		String correctMessage = "�ǵ�:" + correctUpDownNumber + "��:" + correctUpNumber + "��:" + correctDownNumber;
		String errorMessage = "�ǵ�:" + errorUpDownNumber + "��:" + errorUpNumber + "��:" + errorDownNumber;
		String[] messages = {correctMessage, errorMessage};
		return messages;
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
					errorCodeAndDateArray[2] = DataUtils.CONSTANT_BLANK;
					errorCodeAndDateArray[3] = DataUtils.CONSTANT_BLANK;
					errorCodeAndDateList.add(errorCodeAndDateArray);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return errorCodeAndDateList;
	}
	
	/**
	 * ͳ��daily_stock_���е��ǵ�����
	 *
	 */
	private Map<String, StatisticStock> combineUpAndDownNumberMap() throws SQLException {

		Map<String, StatisticStock> statisticStockMap = new HashMap<String, StatisticStock>();
		Map<String, StatisticStock> upDownNumberMap = dailyStockDao.statisticUpDownInDailyStock();
		Map<String, StatisticStock> upNumberMap = dailyStockDao.statisticUpInDailyStock();
		Map<String, StatisticStock> downNumberMap = dailyStockDao.statisticDownInDailyStock();
		statisticStockMap = combineUpAndDownNumber(upDownNumberMap, upNumberMap, downNumberMap);
		for (StatisticStock statisticStock : statisticStockMap.values())
			statisticUpDownNumberInDailyStock(statisticStock); // ͳ��Json�ǵ�����
		return statisticStockMap;
	}
	
	/**
	 * ͳ��original_stock_���е��ǵ�����
	 *
	 */
	private Map<String, StatisticStock> statisticUpAndDownNumberInOriginalStock() throws SQLException {
		
		List<OriginalStock> originalStockList = originalStockDao.listOriginalData();
		//ͳ�����е��ǵ�����
		Map<String, StatisticStock> upAndDownNumberMap = CommonUtils.statisticUpAndDownNumber(originalStockList);
		
		return null;
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

	/**
	 * ��֤�ǵ����������ش����ǵ�����List 
	 *
	 */
	private List<StatisticStock> listErrorUpAndDownNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticUpAndDownMap) {

		List<StatisticStock> errorUpAndDownNumberList = new ArrayList<StatisticStock>();
		for (StatisticStock statisticStock : statisticStockList) {
			String stockCode = statisticStock.getStockCode();
			StatisticStock statisticUpAndDownStock = statisticUpAndDownMap.get(stockCode);
			if (statisticUpAndDownStock == null) {
				System.out.println("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������ÿ�չ�Ʊ��Ϣ��(daily_stock_)�У�");
				log.loger.info("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������ÿ�չ�Ʊ��Ϣ��(daily_stock_)�У�");
				continue;
			}
			//��֤ͳ�ƹ�Ʊ��Ϣ���ǵ�����
			StatisticStock errorStatisticStock = validateUpAndDownNumber(statisticUpAndDownStock, statisticStock);
			if (errorStatisticStock != null) {
				errorUpAndDownNumberList.add(errorStatisticStock);
			}
		}
		return errorUpAndDownNumberList;
	}
	
	/**
	 * ��֤Json�ǵ����������ش���Json�ǵ�����List
	 *
	 */
	private List<StatisticStock> listErrorJsonUpAndDownNumber(List<StatisticStock> statisticStockList, Map<String, StatisticStock> statisticJsonUpAndDownMap) {

		List<StatisticStock> errorJsonUpAndDownNumberList = new ArrayList<StatisticStock>();
		for (StatisticStock statisticStock : statisticStockList) {
			String stockCode = statisticStock.getStockCode();
			StatisticStock statisticJsonUpAndDownStock = statisticJsonUpAndDownMap.get(stockCode);
			if (statisticJsonUpAndDownStock == null) {
				System.out.println("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������ÿ�չ�Ʊ��Ϣ��(daily_stock_)�У�");
				log.loger.info("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������ÿ�չ�Ʊ��Ϣ��(daily_stock_)�У�");
				continue;
			}
			//��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�е�Json�ǵ�����
			StatisticStock errorJsonStatisticStock = validateJsonUpAndDownNumber(statisticJsonUpAndDownStock, statisticStock);
			if (errorJsonStatisticStock.getErrorUpDownFlg().compareTo(DataUtils.CONSTANT_INTEGER_ZERO) != 0)
				errorJsonUpAndDownNumberList.add(errorJsonStatisticStock);
		}
		return errorJsonUpAndDownNumberList;
	}
	
	/**
	 * ��֤statistic_stock_����Json�ǵ�����
	 *
	 */
	private StatisticStock validateJsonUpAndDownNumber(StatisticStock statisticJsonUpAndDownStock, StatisticStock statisticStock) {

		String stockCode = statisticStock.getStockCode();
		StatisticStock errorJsonStatisticStock = new StatisticStock(stockCode);
		try {
			// �Ƚ����ǵ�����
			Integer upDownNumber = statisticStock.getUpDownNumber();
			Integer upNumber = statisticStock.getUpNumber();
			Integer downNumber = statisticStock.getDownNumber();
			Integer statisticUpDownNumber = statisticJsonUpAndDownStock.getUpDownNumber();
			Integer statisticUpNumber = statisticJsonUpAndDownStock.getUpNumber();
			Integer statisticDownNumber = statisticJsonUpAndDownStock.getDownNumber();
			if (upDownNumber.compareTo(statisticUpDownNumber) != 0 
					|| upNumber.compareTo(statisticUpNumber) != 0 
					|| downNumber.compareTo(statisticDownNumber) != 0) {
				errorJsonStatisticStock.setErrorUpDownFlg(DataUtils.CONSTANT_INTEGER_ONE);
				errorJsonStatisticStock.setUpDownNumber(statisticUpDownNumber);
				errorJsonStatisticStock.setUpNumber(statisticUpNumber);
				errorJsonStatisticStock.setDownNumber(statisticDownNumber);
				errorJsonStatisticStock.setErrorUpDownNumber(upDownNumber);
				errorJsonStatisticStock.setErrorUpNumber(upNumber);
				errorJsonStatisticStock.setErrorDownNumber(downNumber);
			}
			// �Ƚ�һ���ǵ�����
			String oneYearJson = statisticStock.getOneYear();
			String oneYearStatisticJson = statisticJsonUpAndDownStock.getOneYear();
			if (!validateJsonUpAndDownNumber(oneYearJson, oneYearStatisticJson, StatisticStock.PRE_ONE_YEAR_UP_DOWN_NUM)) {
				errorJsonStatisticStock.setErrorUpDownFlg(DataUtils.CONSTANT_INTEGER_TWO);
				errorJsonStatisticStock.setOneYear(oneYearStatisticJson);
				errorJsonStatisticStock.setErrorOneYear(oneYearJson);
			}
			// �Ƚϰ����ǵ�����
			String halfYearJson = statisticStock.getHalfYear();
			String halfYearStatisticJson = statisticJsonUpAndDownStock.getHalfYear();
			if (!validateJsonUpAndDownNumber(halfYearJson, halfYearStatisticJson, StatisticStock.PRE_HALF_YEAR_UP_DOWN_NUM)) {
				errorJsonStatisticStock.setErrorUpDownFlg(DataUtils.CONSTANT_INTEGER_THREE);
				errorJsonStatisticStock.setHalfYear(halfYearStatisticJson);
				errorJsonStatisticStock.setErrorHalfYear(halfYearJson);
			}
			// �Ƚ������ǵ�����
			String threeMonthJson = statisticStock.getThreeMonth();
			String threeMonthStatisticJson = statisticJsonUpAndDownStock.getThreeMonth();
			if (!validateJsonUpAndDownNumber(threeMonthJson, threeMonthStatisticJson, StatisticStock.PRE_THREE_MONTH_UP_DOWN_NUM)) {
				errorJsonStatisticStock.setErrorUpDownFlg(DataUtils.CONSTANT_INTEGER_FOUR);
				errorJsonStatisticStock.setThreeMonth(threeMonthStatisticJson);
				errorJsonStatisticStock.setErrorThreeMonth(threeMonthJson);
			}
			// �Ƚ϶����ǵ�����
			String twoMonthJson = statisticStock.getTwoMonth();
			String twoMonthStatisticJson = statisticJsonUpAndDownStock.getTwoMonth();
			if (!validateJsonUpAndDownNumber(twoMonthJson, twoMonthStatisticJson, StatisticStock.PRE_TWO_MONTH_UP_DOWN_NUM)) {
				errorJsonStatisticStock.setErrorUpDownFlg(DataUtils.CONSTANT_INTEGER_FIVE);
				errorJsonStatisticStock.setTwoMonth(twoMonthStatisticJson);
				errorJsonStatisticStock.setErrorTwoMonth(twoMonthJson);
			}
			// �Ƚ�һ���ǵ�����
			String oneMonthJson = statisticStock.getOneMonth();
			String oneMonthStatisticJson = statisticJsonUpAndDownStock.getOneMonth();
			if (!validateJsonUpAndDownNumber(oneMonthJson, oneMonthStatisticJson, StatisticStock.PRE_ONE_MONTH_UP_DOWN_NUM)) {
				errorJsonStatisticStock.setErrorUpDownFlg(DataUtils.CONSTANT_INTEGER_SIX);
				errorJsonStatisticStock.setOneMonth(oneMonthStatisticJson);
				errorJsonStatisticStock.setErrorOneMonth(oneMonthJson);
			}
			// �Ƚϰ����ǵ�����
			String halfMonthJson = statisticStock.getHalfMonth();
			String halfMonthStatisticJson = statisticJsonUpAndDownStock.getHalfMonth();
			if (!validateJsonUpAndDownNumber(halfMonthJson, halfMonthStatisticJson, StatisticStock.PRE_HALF_MONTH_UP_DOWN_NUM)) {
				errorJsonStatisticStock.setErrorUpDownFlg(DataUtils.CONSTANT_INTEGER_SEVEN);
				errorJsonStatisticStock.setHalfMonth(halfMonthStatisticJson);
				errorJsonStatisticStock.setErrorHalfMonth(halfMonthJson);
			}
			// �Ƚ�һ���ǵ�����
			String oneWeekJson = statisticStock.getOneWeek();
			String oneWeekStatisticJson = statisticJsonUpAndDownStock.getOneWeek();
			if (!validateJsonUpAndDownNumber(oneWeekJson, oneWeekStatisticJson, StatisticStock.PRE_ONE_WEEK_UP_DOWN_NUM)) {
				errorJsonStatisticStock.setErrorUpDownFlg(DataUtils.CONSTANT_INTEGER_EIGHT);
				errorJsonStatisticStock.setOneWeek(oneWeekStatisticJson);
				errorJsonStatisticStock.setErrorOneWeek(oneWeekJson);
			}
		} catch (Exception ex) {
			System.out.println("��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�еĹ�Ʊ" + statisticStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(statisticStock.getStockCode()) + ")�ǵ������쳣��");
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return errorJsonStatisticStock;
	}

	/**
	 * ��֤statistic_stock_�������ǵ�����
	 *
	 */
	private StatisticStock validateUpAndDownNumber(StatisticStock statisticUpAndDownStock, StatisticStock statisticStock) {

		StatisticStock errorStatisticStock = null;
		try {
			String stockCode = statisticStock.getStockCode();
			Integer upDownNumber = statisticStock.getUpDownNumber();
			Integer upNumber = statisticStock.getUpNumber();
			Integer downNumber = statisticStock.getDownNumber();
			Integer statisticUpDownNumber = statisticUpAndDownStock.getUpDownNumber();
			Integer statisticUpNumber = statisticUpAndDownStock.getUpNumber();
			Integer statisticDownNumber = statisticUpAndDownStock.getDownNumber();
			if (statisticUpDownNumber.compareTo(upDownNumber) != 0 
					|| statisticUpNumber.compareTo(upNumber) != 0
					|| statisticDownNumber.compareTo(downNumber) != 0) {
				errorStatisticStock = new StatisticStock();
				errorStatisticStock.setStockCode(stockCode);
				errorStatisticStock.setUpDownNumber(statisticUpDownNumber);
				errorStatisticStock.setUpNumber(statisticUpNumber);
				errorStatisticStock.setDownNumber(statisticDownNumber);
				errorStatisticStock.setErrorUpDownNumber(upDownNumber);
				errorStatisticStock.setErrorUpNumber(upNumber);
				errorStatisticStock.setErrorDownNumber(downNumber);
			}
		} catch (Exception ex) {
			System.out.println("��֤ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�еĹ�Ʊ" + statisticStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(statisticStock.getStockCode()) + ")�ǵ������쳣��");
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
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
