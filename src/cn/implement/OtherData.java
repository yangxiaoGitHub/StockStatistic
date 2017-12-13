package cn.implement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.JsonUtils;
import cn.com.MD5Utils;
import cn.com.PropertiesUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllInformationStockDao;
import cn.db.AllStockDao;
import cn.db.DailyStockDao;
import cn.db.DetailStockDao;
import cn.db.HistoryStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllInformationStock;
import cn.db.bean.AllStock;
import cn.db.bean.DailyStock;
import cn.db.bean.DetailStock;
import cn.db.bean.HistoryStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticStock;
import cn.log.Log;

public class OtherData extends OperationData {
	Log log = Log.getLoger();

	/**
	 * ��ԭʼ��Ʊ(original_stock_)���ݽ���MD5����
	 * 
	 */
	public void handleOriginalMD5() {

		originalStockDao = new OriginalStockDao();
		int codesMD5 = 0;
		int ratesMD5 = 0;
		try {
			List<OriginalStock> originalList = originalStockDao.listOriginalData();
			for (OriginalStock stock : originalList) {
				Date stockDate = stock.getStockDate();
				String stockCodesMD5 = stock.getStockCodesMD5();
				String changeRatesMD5 = stock.getChangeRatesMD5();
				String stockCodesMD5_ = MD5Utils.getEncryptedPwd(stock.getStockCodes());
				String changeRatesMD5_ = MD5Utils.getEncryptedPwd(stock.getChangeRates());
				if (CommonUtils.isBlank(stockCodesMD5) && CommonUtils.isBlank(changeRatesMD5)) {
					originalStockDao.updateCodesRatesMD5(stockDate, stockCodesMD5_, changeRatesMD5_);
					codesMD5++;
					ratesMD5++;
				} else if (CommonUtils.isBlank(stock.getChangeRatesMD5())) {
					originalStockDao.updateChangeRatesMD5(stockDate, changeRatesMD5_);
					ratesMD5++;
				} else if (CommonUtils.isBlank(stock.getStockCodesMD5())) {
					originalStockDao.updateStockCodesMD5(stockDate, stockCodesMD5_);
					codesMD5++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(originalStockDao);
			System.out.println("ԭʼ��Ʊ���ݱ��и�����" + codesMD5 + "��stockCodeMD5��¼��" + ratesMD5 + "��changeRatesMD5��¼��");
			log.loger.info("ԭʼ��Ʊ���ݱ��и�����" + codesMD5 + "��stockCodeMD5��¼��" + ratesMD5 + "��changeRatesMD5��¼��");
		}
	}

	/**
	 * ͳ��ÿ�չ�Ʊ���ݵ�statistic_stock_����
	 * 
	 */
	public void statisticDailyStock() {
		
		dailyStockDao = new DailyStockDao();
		statisticStockDao = new StatisticStockDao();
		int number = 0;
		List<StatisticStock> saveList = new ArrayList<StatisticStock>();
		try {
			List<DailyStock> dailyStockList = dailyStockDao.statisticDailyStock();
			Long maxNum = statisticStockDao.getMaxNumFromStatisticStock();
			for (DailyStock stock : dailyStockList) {
				String stockCode = stock.getStockCode();
				boolean flg = statisticStockDao.isExistInStatisticStock(stockCode);
				if (!flg) {
					Date firstDate = stock.getStockDate();
					String stockCodeDES = stock.getEncryptStockCode();
					StatisticStock statistic = new StatisticStock(stockCode, firstDate, stockCodeDES);
					statistic.setNum(++maxNum);
					setUpDownNumberAndUpDownJson(statistic);
					statistic.setInputTime(new Date());
					boolean saveFlg = statisticStockDao.saveStatisticStock(statistic);
					if (saveFlg) {
						saveList.add(statistic);
						++number;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			String strStock = getStocks(saveList);
			System.out.println("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��������" + number + "����¼��" + strStock);
			log.loger.info("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��������" + number + "����¼��" + strStock);
		}
	}

	private String getStocks(List<StatisticStock> saveList) {
		
		if (saveList==null || saveList.size()==0) return "";
		String result = "";
		for (StatisticStock stock : saveList) {
			result += "," + stock.getStockCode() + "(" + PropertiesUtils.getProperty(stock.getStockCode()) + ")";
		}
		return result.substring(1, result.length());
	}

	/**
	 * ���ӻ�������й�Ʊ���ݵ�all_stock_����
	 * 
	 */
	public void handleAllStock() {
		
		allStockDao = new AllStockDao();
		int saveNum = 0;
		int updateNum = 0;
		try {
			Long maxNum = allStockDao.getMaxNumFromAllStock();
			Properties properties = PropertiesUtils.getStockNameProperties();
	        Iterator<Entry<Object, Object>> itr = properties.entrySet().iterator();
	        while (itr.hasNext()){
	        	Entry<Object, Object> entry = (Entry<Object, Object>)itr.next();
	        	String stockCode = entry.getKey().toString();
	        	String stockName = entry.getValue().toString();
	        	AllStock stock = new AllStock(stockCode, stockName);
	        	stock.setNum(++maxNum);
	        	stock.setInputTime(new Date());
				AllStock allStock = allStockDao.getAllStockByStockCode(stockCode);
				if (allStock == null) {
					boolean saveFlg = allStockDao.saveAllStock(stock);
					if (saveFlg) ++saveNum;
				} else {
					if (!stockName.equals(allStock.getStockName())) {
						boolean updateFlg = allStockDao.updateAllStockName(stock);
						if (updateFlg) ++updateNum;
					}
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao);
			System.out.println("���й�Ʊ��(all_stock_)��������" + saveNum + "����¼��");
			log.loger.info("���й�Ʊ��(all_stock_)��������" + saveNum + "����¼��");
			System.out.println("���й�Ʊ��(all_stock_)�и�����" + updateNum + "����¼��");
			log.loger.info("���й�Ʊ��(all_stock_)�и�����" + updateNum + "����¼��");
		}
	}

	/**
	 * �������й�Ʊ��Ϣ��(all_information_stock_)�е�num_�ֶ�
	 * 
	 */
	public void updateNumOfAllInformationStock() {
		
		long num = 1;
		allInformationStockDao = new AllInformationStockDao();
		try {
			Date startDate = DateUtils.String2Date(DateUtils.startDateInAllDetailStock);
			Date nowTime = new Date();
			while (startDate.compareTo(nowTime) < 0) {
				List<AllInformationStock> allInformationStockList = allInformationStockDao.getAllInformationStockByStockDate(startDate);
				//��˳��������й�Ʊ��Ϣ��(all_information_stock_)�е�num_�ֶ�
				Properties properties = PropertiesUtils.getStockNameProperties();
		        Iterator<Entry<Object, Object>> itr = properties.entrySet().iterator();
		        while (itr.hasNext()){
		        	Entry<Object, Object> entry = (Entry<Object, Object>)itr.next();
		        	String stockCode = entry.getKey().toString();
		        	AllInformationStock allInformationStock = getAllInformationStock(stockCode, allInformationStockList);
		        	if (allInformationStock != null) {
			        	boolean updateFlg = allInformationStockDao.updateNum(allInformationStock, num);
			        	if (updateFlg) num++; 
		        	}
		        }
				startDate = DateUtils.addOneDay(startDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allInformationStockDao);
			System.out.println("���й�Ʊ��ϸ��Ϣ��(all_detail_stock_)����������" + num + "����¼��num_�ֶΣ�");
			log.loger.info("���й�Ʊ��ϸ��Ϣ��(all_detail_stock_)����������" + num + "����¼��num_�ֶΣ�");
		}
	}

	private AllInformationStock getAllInformationStock(String stockCode, List<AllInformationStock> allInformationStockList) {
		
		AllInformationStock stock = null;
		for (AllInformationStock allInformationStock : allInformationStockList) {
			if (allInformationStock.getStockCode().equals(stockCode)) {
				stock = allInformationStock;
				break;
			}
		}
		return stock;
	}

	/**
	 * ͳ��ÿ�չ�Ʊ���ݵ��ǵ�������statistic_stock_����(һ�㲻ʹ��)
	 * 
	 */
	public void statisticUpAndDownToStatisticStock() {

		dailyStockDao = new DailyStockDao();
		statisticStockDao = new StatisticStockDao();
		int upDownNum = 0;
		int upNum = 0;
		int downNum = 0;
		int jsonNum = 0;
		try {
			// �����ֶ�up_down_number_
			Map<String, StatisticStock> dailyStockUpDownMap = dailyStockDao.statisticUpDownInDailyStock();
			Set<Entry<String, StatisticStock>> upDownEntries = dailyStockUpDownMap.entrySet();
			Iterator<Entry<String, StatisticStock>> upDownIterator = upDownEntries.iterator();
			while (upDownIterator.hasNext()) {
				Entry<String, StatisticStock> entry = upDownIterator.next();
				String stockCode = entry.getKey();
				StatisticStock statisticStock = entry.getValue();
				boolean updateFlg = statisticStockDao.updateUpDownNumber(stockCode, statisticStock);
				if (updateFlg) upDownNum++;
			}
			// �����ֶ�up_number_
			Map<String, StatisticStock> dailyStockUpMap = dailyStockDao.statisticUpInDailyStock();
			Set<Entry<String, StatisticStock>> upEntries = dailyStockUpMap.entrySet();
			Iterator<Entry<String, StatisticStock>> upIterator = upEntries.iterator();
			while (upIterator.hasNext()) {
				Entry<String, StatisticStock> entry = upIterator.next();
				String stockCode = entry.getKey();
				StatisticStock statisticStock = entry.getValue();
				boolean updateFlg = statisticStockDao.updateUpNumber(stockCode, statisticStock);
				if (updateFlg) upNum++;
			}
			// �����ֶ�down_number_
			Map<String, StatisticStock> dailyStockDownMap = dailyStockDao.statisticDownInDailyStock();
			Set<Entry<String, StatisticStock>> downEntries = dailyStockDownMap.entrySet();
			Iterator<Entry<String, StatisticStock>> downIterator = downEntries.iterator();
			while (downIterator.hasNext()) {
				Entry<String, StatisticStock> entry = downIterator.next();
				String stockCode = entry.getKey();
				StatisticStock statisticStock = entry.getValue();
				boolean downdateFlg = statisticStockDao.updateDownNumber(stockCode, statisticStock);
				if (downdateFlg) downNum++;
			}
			// �����ֶ�one_week_,half_month_,one_month_,two_month_,three_month_,half_year_,one_year_
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			for (StatisticStock statisticStock : statisticStockList) {
				String stockCode = statisticStock.getStockCode();
				StatisticStock statisticStock_ = new StatisticStock(stockCode);
				// ǰһ�ܵ��ǵ�����
				String preOneWeekUpAndDownJson = getPreOneWeekJson(stockCode);
				statisticStock_.setOneWeek(preOneWeekUpAndDownJson);
				// ǰ���µ��ǵ�����
				String preHalfMonthUpAndDownJson = getPreHalfMonthJson(stockCode);
				statisticStock_.setHalfMonth(preHalfMonthUpAndDownJson);
				// ǰһ�µ��ǵ�����
				String preOneMonthUpAndDownJson = getPreOneMonthJson(stockCode);
				statisticStock_.setOneMonth(preOneMonthUpAndDownJson);
				// ǰ���µ��ǵ�����
				String preTwoMonthUpAndDownJson = getPreTwoMonthJson(stockCode);
				statisticStock_.setTwoMonth(preTwoMonthUpAndDownJson);
				// ǰ���µ��ǵ�����
				String preThreeMonthUpAndDownJson = getPreThreeMonthJson(stockCode);
				statisticStock_.setThreeMonth(preThreeMonthUpAndDownJson);
				// ǰ������ǵ�����
				String preHalfYearUpAndDownJson = getPreHalfYearJson(stockCode);
				statisticStock_.setHalfYear(preHalfYearUpAndDownJson);
				// ǰһ����ǵ�����
				String preOneYearUpAndDownJson = getPreOneYearJson(stockCode);
				statisticStock_.setOneYear(preOneYearUpAndDownJson);
				boolean updateFlg = statisticStockDao.updateAllJsonRow(statisticStock_);
				if (updateFlg) jsonNum++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao,statisticStockDao);
			System.out.println("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�и�����" + upDownNum + "����¼���ֶ�(up_down_number_), " + upNum + "����¼���ֶ�(up_number_), " + downNum + "����¼���ֶ�(down_number_), " + jsonNum + "����¼��Json�ֶΣ�");
			log.loger.info("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�и�����" + upDownNum + "����¼���ֶ�(up_down_number_), " + upNum + "����¼���ֶ�(up_number_), " + downNum + "����¼���ֶ�(down_number_), " + jsonNum + "����¼��Json�ֶΣ�");
		}
	}

	/**
	 * �������й�Ʊ(all_stock_)����ͨ��
	 * 
	 */
	public void handleAllCirculationStock() {
		
		int updateNum = 0;
		allStockDao = new AllStockDao();
		try {
			Properties properties = PropertiesUtils.getNowPriceAndCirculValueProperties();
	        Iterator<Entry<Object, Object>> itrator = properties.entrySet().iterator();
	        while (itrator.hasNext()){
	        	Entry<Object, Object> entry = (Entry<Object, Object>)itrator.next();
	        	String stockAliasCode = entry.getKey().toString();
	        	String stockCirculation = entry.getValue().toString();
	        	String stockCode = CommonUtils.getStockCodeByAliasCode(stockAliasCode);
	        	String[] circulationArray = stockCirculation.split(",");
	        	if (circulationArray[0].equals("--") || circulationArray[1].equals("--")) continue;
	        	Double nowPrice = Double.valueOf(circulationArray[0]);
	        	Long circulationValue = Long.valueOf(circulationArray[1]);
	        	BigDecimal complexCirculationStock = new BigDecimal(circulationValue.doubleValue()/nowPrice.doubleValue()).setScale(0, BigDecimal.ROUND_HALF_UP);
	        	AllStock stock = new AllStock(stockCode, null);
	        	stock.setCirculationValue(circulationValue);
	        	stock.setCirculationStockComplex(complexCirculationStock.longValue());
	        	stock.setCirculationStockSimple(getCirculationStockSimple(complexCirculationStock));
	        	stock.setInputTime(new Date());
				AllStock allStock = allStockDao.getAllStockByStockCode(stockCode);
				if (allStock != null) {
					boolean updateFlg = allStockDao.updateAllStockCirculation(stock);
					if (updateFlg) ++updateNum;
					System.out.println(DateUtils.DateTimeToString(new Date()) + "---��Ʊ" + stock.getStockCode() + "(" + PropertiesUtils.getProperty(stock.getStockCode()) + ") ��ͨ��: " + stock.getCirculationStockSimple_NoJson());
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao);
			System.out.println(DateUtils.DateTimeToString(new Date()) + " ���й�Ʊ��(all_stock_)�и�����" + updateNum + "����ͨ�ɺ���ͨ��ֵ��¼��");
			log.loger.info(" ���й�Ʊ��(all_stock_)�и�����" + updateNum + "����ͨ�ɺ���ͨ��ֵ��¼��");
		}
	}
	
	/**
	 * �������صĹ�Ʊ���ݵ���Ʊ��ʷ���ݱ�(history_stock_)��
	 * 
	 */
	public void handleDownloadDailyData() {
		
		long saveNum = 0;
		historyStockDao = new HistoryStockDao();
		try {
			Long maxNum = historyStockDao.getMaxNumFromHistoryStock();
			Properties properties = PropertiesUtils.getStockNameProperties();
			Enumeration<? extends Object> keyEnumeration = properties.propertyNames();
			while(keyEnumeration.hasMoreElements()){
			    String stockCode = keyEnumeration.nextElement().toString();
				Date recentDateInHistoryStock = getMaxStockDateByStockCodeInHistoryStock(stockCode);
				if (fileExistInHistoryStock(recentDateInHistoryStock)) continue;
	        	String fileName = stockCode +  ".txt";
	        	final String filePath = "E:\\txd\\" + fileName;
	        	String encoding="GBK";
				try {
					File file = new File(filePath);
					// �ж��ļ��Ƿ����
					if (file.isFile() && file.exists()) {
						InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
						BufferedReader bufferedReader = new BufferedReader(read);
						String lineTxt = null;
						while ((lineTxt = bufferedReader.readLine()) != null) {
							String[] lineArray = getLineArrayFromLineTxt(lineTxt);
							if (lineArray.length > 0) {
								if (!CommonUtils.isValidDate(lineArray[0]) || !CommonUtils.isLegalDate(lineArray[0], recentDateInHistoryStock)) continue;
								boolean saveFlg = saveHistoryStock(stockCode, lineArray, maxNum);
								if (saveFlg) {
									saveNum++;
									System.out.println(saveNum + "---" + DateUtils.DateTimeToString(new Date()) + " ��ʷ��Ʊ��Ϣ��(history_stock_)������" + lineArray[0] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")");
								}
							}
						}
						read.close();
					} else {
						System.out.println(DateUtils.DateTimeToString(new Date()) + " ��ȡ�ļ�ʱ�Ҳ���ָ�����ļ���" + filePath);
						log.loger.info(" ��ȡ�ļ�ʱ�Ҳ���ָ�����ļ���" + filePath);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					log.loger.error(ex);
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(historyStockDao);
			System.out.println(DateUtils.DateTimeToString(new Date()) + " ��ʷ��Ʊ��Ϣ��(history_stock_)������" + saveNum + "����¼��");
			log.loger.info(" ��ʷ��Ʊ��Ϣ��(history_stock_)������" + saveNum + "����¼��");
		}
	}

	private boolean saveHistoryStock(String stockCode, String[] lineArray, Long lineNum) throws SQLException {
		
		boolean saveFlg = false;
		//Date stockDate = DateUtils.String2Date(lineArray[0]);
		//if (!historyStockDao.isExistInHistoryStock(stockCode, stockDate)) {
			HistoryStock historyStock = getHistoryStockByArray(stockCode, lineArray);
			//Long maxNum = historyStockDao.getMaxNumFromHistoryStock();
			historyStock.setNum(++lineNum);
			saveFlg = historyStockDao.saveHistoryStock(historyStock);
		//}
		return saveFlg;
	}

	private HistoryStock getHistoryStockByArray(String stockCode, String[] lineArray) {
		
		Date stockDate = DateUtils.String2Date(lineArray[0]);
		Double openPrice = Double.parseDouble(lineArray[1]);
		Double highPrice = Double.parseDouble(lineArray[2]);
		Double lowPrice = Double.parseDouble(lineArray[3]);
		Double closePrice = Double.parseDouble(lineArray[4]);
		Long tradedStockNum = Long.parseLong(lineArray[5]);
		Float tradedAmount = Float.parseFloat(lineArray[6]);
		HistoryStock historyStock = new HistoryStock(stockCode, stockDate);
		historyStock.setOpenPrice(openPrice);
		historyStock.setHighPrice(highPrice);
		historyStock.setLowPrice(lowPrice);
		historyStock.setClosePrice(closePrice);
		historyStock.setTradedStockNumber(tradedStockNum);
		historyStock.setTradedAmount(tradedAmount);
		historyStock.setInputTime(new Date());
		return historyStock;
	}
	
	private boolean fileExistInHistoryStock(Date recentDateInHistoryStock) throws SQLException {

		Date recentDate = DateUtils.String2Date("2017-12-08");
		if (CommonUtils.compareDate(recentDateInHistoryStock, recentDate)) return true;
		else return false;
	}

	private Date getMaxStockDateByStockCodeInHistoryStock(String stockCode) throws SQLException {
		
		Date[] minMaxDate = historyStockDao.getMinMaxDate(stockCode);
		return minMaxDate[1];
	}

	private String[] getLineArrayFromLineTxt(String lineTxt) {
		
		List<String> lineList = new ArrayList<String>();
		String[] lineArray = lineTxt.split("\t");
		for (String value : lineArray) {
			if (!CommonUtils.isBlank(value)) lineList.add(value.trim());
		}
		return (String[])lineList.toArray(new String[lineList.size()]);
	}

	/**
	 * �ÿ�չ�Ʊ��Ϣ��(daily_stock_)�е��ǵ���(change_rate_)�ͻ�����(turnover_rate_)
	 * 
	 */
	public void handleChangeRateAndTurnoverRateInDailyStock() {
		
		
	}

	/**
	 * 0.3321 �� <1          (ȡ4λ����)  -->3321��
	 * 1.3653 �� >=1   <10   (ȡС����2λ)-->1.37��
	 * 23.265 �� >=10  <100  (ȡС����1λ)-->23.3��
	 * 362.02 �� >=100 <1000 (ȡ��)      -->362��
	 * 2235.1 �� >1000                  -->2235��
	 */
	private String getCirculationStockSimple(BigDecimal complexCirculationStock) {

		String unit = AllStock.UNIT_HUNDRED_MILLION;
		Double reValue = 0.0;
		BigDecimal complexCirculationValue = complexCirculationStock.divide(DataUtils.CONSTANT_HUNDRED_MILLION);
		if (complexCirculationValue.compareTo(DataUtils.CONSTANT_ONE) < 0) {
			unit = AllStock.UNIT_TEN_THOUSAND;
			BigDecimal tempValue = complexCirculationStock.divide(DataUtils.CONSTANT_TEN_THOUSAND);
			reValue = getPreFourNumber(tempValue);
		} else if (complexCirculationValue.compareTo(DataUtils.CONSTANT_ONE) >= 0 && complexCirculationValue.compareTo(DataUtils.CONSTANT_TEN) < 0) {
			reValue = complexCirculationValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); // ȡ2λС��
		} else if (complexCirculationValue.compareTo(DataUtils.CONSTANT_TEN) >= 0 && complexCirculationValue.compareTo(DataUtils.CONSTANT_HUNDRED) < 0) {
			reValue = complexCirculationValue.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue(); // ȡ1λС��
		} else if (complexCirculationValue.compareTo(DataUtils.CONSTANT_HUNDRED) >= 0) {
			reValue = complexCirculationValue.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue(); // ȡ��
		}
		//{"value":2.05,"unit":"��"}
		Map<String, String> circulationSimpleMap = new HashMap<String, String>();
		circulationSimpleMap.put(AllStock.JSON_VALUE, DataUtils.subZeroAndDot(reValue)); 
		circulationSimpleMap.put(AllStock.JSON_UNIT, unit);
	    String curculationJson = JsonUtils.mapToJson(circulationSimpleMap);
		return curculationJson;
	}

	/**
	 * 3365.96  <10000   (ȡ��) -->3366
     * 55628.65 >=10000  (ȡǰ4λ����) -->5563
	 */
	private Double getPreFourNumber(BigDecimal value) {
		
		Double reValue = 0.0;
		if (value.compareTo(DataUtils.CONSTANT_TEN_THOUSAND) < 0) {
			reValue = value.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			Double doubleFive = Double.valueOf(value.toString().substring(0, 5));
			reValue = new BigDecimal(doubleFive/10).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return reValue;
	}
}
