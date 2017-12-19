package cn.implement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
import cn.db.HistoryStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllInformationStock;
import cn.db.bean.AllStock;
import cn.db.bean.DailyStock;
import cn.db.bean.HistoryStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticStock;

public class OtherData extends OperationData {

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
			for (DailyStock dailyStock : dailyStockList) {
				String stockCode = dailyStock.getStockCode();
				boolean existFlg = statisticStockDao.isExistInStatisticStock(stockCode);
				if (!existFlg) {
					Integer upDownNumber = dailyStock.getCount();
					if (upDownNumber > 1) {
						System.out.println(DateUtils.dateTimeToString(new Date()) + " ÿ�չ�Ʊ��Ϣ��(daily_stock_)�й�Ʊͳ�ƴ���Ϊ:" + dailyStock.getCount()
								+ "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")û�����ӵ�ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�У�");
						continue;
					}
					if (!existFlg) {
						Date firstStockDate = dailyStock.getStockDate();
						String stockCodeDES = DESUtils.encryptToHex(stockCode);
						StatisticStock statisticStock = new StatisticStock(stockCode, firstStockDate, stockCodeDES);
						statisticStock.setNum(++maxNum);
						// ��ʼ���ǵ�����
						initializeStatisticStock(statisticStock);
						DailyStock newDailyStock = dailyStockDao.getDailyStockByKey(dailyStock.getRecentStockDate(), stockCode);
						// �����Ʊ���ǵ�����
						calculateUpDownNumber(statisticStock, newDailyStock.getChangeFlg());
						statisticStock.setNote(DataUtils.CONSTANT_BLANK);
						statisticStock.setInputTime(new Date());
						boolean saveFlg = statisticStockDao.saveStatisticStock(statisticStock);
						if (saveFlg) {
							saveList.add(statisticStock);
							++number;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(ex);
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			String strStock = getStocks(saveList);
			System.out.println("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��������" + number + "����¼��" + strStock);
			log.loger.info("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��������" + number + "����¼��" + strStock);
		}
	}

	private void initializeStatisticStock(StatisticStock statisticStock) {
		//��ʼ�����ǵ�����
		statisticStock.setUpDownNumber(DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setUpNumber(DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setDownNumber(DataUtils.CONSTANT_INTEGER_ZERO);
		//��ʼ��һ���ǵ�����
		Map<String, Integer> jsonMap = new HashMap<String, Integer>();
		jsonMap.put(StatisticStock.PRE_ONE_WEEK_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_WEEK_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_WEEK_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setOneWeek(JsonUtils.getJsonByMap(jsonMap));
		//��ʼ�������ǵ�����
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_HALF_MONTH_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_HALF_MONTH_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_HALF_MONTH_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setHalfMonth(JsonUtils.getJsonByMap(jsonMap));
		//��ʼ��һ���ǵ�����
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_ONE_MONTH_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_MONTH_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_MONTH_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setOneMonth(JsonUtils.getJsonByMap(jsonMap));
		//��ʼ�������ǵ�����
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_TWO_MONTH_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_TWO_MONTH_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_TWO_MONTH_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setTwoMonth(JsonUtils.getJsonByMap(jsonMap));
		//��ʼ�������ǵ�����
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_THREE_MONTH_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_THREE_MONTH_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_THREE_MONTH_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setThreeMonth(JsonUtils.getJsonByMap(jsonMap));
		//��ʼ�������ǵ�����
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_HALF_YEAR_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_HALF_YEAR_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_HALF_YEAR_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setHalfYear(JsonUtils.getJsonByMap(jsonMap));
		//��ʼ��һ���ǵ�����
		jsonMap.clear();
		jsonMap.put(StatisticStock.PRE_ONE_YEAR_UP_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_YEAR_UP_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		jsonMap.put(StatisticStock.PRE_ONE_YEAR_DOWN_NUM, DataUtils.CONSTANT_INTEGER_ZERO);
		statisticStock.setOneYear(JsonUtils.getJsonByMap(jsonMap));
	}

	private String getStocks(List<StatisticStock> saveList) {

		if (saveList == null || saveList.size() == 0)
			return DataUtils.CONSTANT_BLANK;
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
		
		int saveNum = 0;
		int updateNum = 0;
		try {
			allStockDao = new AllStockDao(); //AllStockDao.getInstance();
			Long maxNum = allStockDao.getMaxNumFromAllStock();
			Properties properties = PropertiesUtils.getStockNameProperties();
			Iterator<Entry<Object, Object>> itr = properties.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<Object, Object> entry = itr.next();
				String stockCode = entry.getKey().toString();
				String stockName = entry.getValue().toString();
				AllStock stock = new AllStock(stockCode, stockName);
				stock.setNum(++maxNum);
				stock.setInputTime(new Date());
				// �����Ʊ����ͨ��
				this.setCirculationInAllStock(stock);
				AllStock allStock = allStockDao.getAllStockByStockCode(stockCode);
				if (allStock == null) {
					boolean saveFlg = allStockDao.saveAllStock(stock);
					if (saveFlg) {
						++saveNum;
						System.out.println(DateUtils.dateTimeToString(new Date()) + " ���й�Ʊ��(all_stock_)�������˹�Ʊ" + stock.getStockCode() + "("
								+ stockName + ")");
					}
				} else {
					if (!stockName.equals(allStock.getStockName())) {
						boolean updateFlg = allStockDao.updateAllStockName(stock);
						if (updateFlg) {
							++updateNum;
							System.out.println(DateUtils.dateTimeToString(new Date()) + " ���й�Ʊ��(all_stock_)�и����˹�Ʊ" + stock.getStockCode() + "("
									+ stockName + ")");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + " ���й�Ʊ��(all_stock_)��������" + saveNum + "����¼��");
			log.loger.info("���й�Ʊ��(all_stock_)��������" + saveNum + "����¼��");
			System.out.println(DateUtils.dateTimeToString(new Date()) + " ���й�Ʊ��(all_stock_)�и�����" + updateNum + "����¼��");
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
			Date startDate = DateUtils.stringToDate("2017-11-06");
			Date nowTime = new Date();
			while (startDate.compareTo(nowTime) < 0) {
				List<AllInformationStock> allInformationStockList = allInformationStockDao.getAllInformationStockByStockDate(startDate);
				// ��˳��������й�Ʊ��Ϣ��(all_information_stock_)�е�num_�ֶ�
				Properties properties = PropertiesUtils.getStockNameProperties();
				Iterator<Entry<Object, Object>> itr = properties.entrySet().iterator();
				while (itr.hasNext()) {
					Entry<Object, Object> entry = itr.next();
					String stockCode = entry.getKey().toString();
					AllInformationStock allInformationStock = getAllInformationStock(stockCode, allInformationStockList);
					if (allInformationStock != null) {
						boolean updateFlg = allInformationStockDao.updateNum(allInformationStock, num);
						if (updateFlg)
							num++;
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
				if (updateFlg) {
					upDownNum++;
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + upDownNum + "--->ͳ�ƹ�Ʊ��Ϣ������˹�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")��up_down_number_�ֶΣ�");
				}
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
				if (updateFlg) {
					upNum++;
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + upNum + "--->ͳ�ƹ�Ʊ��Ϣ������˹�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")��up_number_�ֶΣ�");
				}
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
				if (downdateFlg) {
					downNum++;
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + downNum + "--->ͳ�ƹ�Ʊ��Ϣ������˹�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")��down_number_�ֶΣ�");
				}
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
				statisticStock_.setInputTime(new Date());
				boolean updateFlg = statisticStockDao.updateAllJsonRow(statisticStock_);
				if (updateFlg) {
					jsonNum++;
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + jsonNum + "--->ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�����˹�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")��json�ֶΣ�");
					log.loger.info(" " + jsonNum + "--->ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�����˹�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")��json�ֶΣ�");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			System.out.println("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�и�����" + upDownNum + "����¼���ֶ�(up_down_number_), " + upNum + "����¼���ֶ�(up_number_), " + downNum
					+ "����¼���ֶ�(down_number_), " + jsonNum + "����¼��Json�ֶΣ�");
			log.loger.info("ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)�и�����" + upDownNum + "����¼���ֶ�(up_down_number_), " + upNum + "����¼���ֶ�(up_number_), " + downNum
					+ "����¼���ֶ�(down_number_), " + jsonNum + "����¼��Json�ֶΣ�");
		}
	}

	/**
	 * �������й�Ʊ(all_stock_)����ͨ��
	 * 
	 */
	public void handleAllCirculationStock(String handleFlg) {

		int updateNum = 0;
		try {
			allStockDao = new AllStockDao(); // AllStockDao.getInstance();
			Properties properties = PropertiesUtils.getNowPriceAndCirculValueProperties();
			Iterator<Entry<Object, Object>> itrator = properties.entrySet().iterator();
			while (itrator.hasNext()) {
				Entry<Object, Object> entry = itrator.next();
				String stockAliasCode = entry.getKey().toString();
				String stockCode = CommonUtils.getStockCodeByAliasCode(stockAliasCode);
				AllStock newAllStock = new AllStock(stockCode, null);
				newAllStock.setInputTime(new Date());
				// �����Ʊ����ͨ��
				this.setCirculationInAllStock(newAllStock);

				/*
				 * String[] circulationArray = stockCirculation.split(","); if
				 * (circulationArray[0].equals("--") ||
				 * circulationArray[1].equals("--")) continue; Double nowPrice =
				 * Double.valueOf(circulationArray[0]); Long circulationValue =
				 * Long.valueOf(circulationArray[1]); BigDecimal
				 * complexCirculationStock = new
				 * BigDecimal(circulationValue.doubleValue()/nowPrice.
				 * doubleValue()).setScale(0, BigDecimal.ROUND_HALF_UP);
				 * newAllStock.setCirculationValue(circulationValue);
				 * newAllStock.setCirculationStockComplex(
				 * complexCirculationStock.longValue());
				 * newAllStock.setCirculationStockSimple(
				 * getCirculationStockSimple(complexCirculationStock));
				 */
				AllStock oldAllStock = allStockDao.getAllStockByStockCode(stockCode);
				if (oldAllStock != null) {
					boolean updateFlg = false;
					String messages = "";
					int equalFlg = isEqualAllStock(oldAllStock, newAllStock); // 0:������,
																				// 1:����ͨ�����,��������,
																				// 2:�����
					if (equalFlg == 0) { // ��ʾ����
						if (handleFlg.equals("1")) { // ����
							updateFlg = allStockDao.updateAllStockCirculation(newAllStock);
							messages = "��ֵͨ����ͨ�ɶ������ʱ��";
						} else {
							System.out.println("---------------------------��Ʊ" + newAllStock.getStockCode() + "("
									+ PropertiesUtils.getProperty(newAllStock.getStockCode()) + ") ��ͨ�ɺ���ֵͨ�������: ");
							log.loger.info("---------------------------��Ʊ" + newAllStock.getStockCode() + "("
									+ PropertiesUtils.getProperty(newAllStock.getStockCode()) + ") ��ͨ�ɺ���ֵͨ�������: ");
							System.out.println("---------����ֵͨ(circulation_value_):" + oldAllStock.getCirculationValue()
									+ " ����ͨ��(circulation_stock_complex_):" + oldAllStock.getCirculationStockComplex()
									+ " ����ͨ��(��)(circulation_stock_simple_):" + oldAllStock.getCirculationStockSimple_NoJson());
							log.loger.info("---------����ֵͨ(circulation_value_):" + oldAllStock.getCirculationValue()
									+ " ����ͨ��(circulation_stock_complex_):" + oldAllStock.getCirculationStockComplex()
									+ " ����ͨ��(��)(circulation_stock_simple_):" + oldAllStock.getCirculationStockSimple_NoJson());
							System.out.println("---------����ֵͨ(circulation_value_):" + newAllStock.getCirculationValue()
									+ " ����ͨ��(circulation_stock_complex_):" + newAllStock.getCirculationStockComplex()
									+ " ����ͨ��(��)(circulation_stock_simple_):" + newAllStock.getCirculationStockSimple_NoJson());
							log.loger.info("---------����ֵͨ(circulation_value_):" + newAllStock.getCirculationValue()
									+ " ����ͨ��(circulation_stock_complex_):" + newAllStock.getCirculationStockComplex()
									+ " ����ͨ��(��)(circulation_stock_simple_):" + newAllStock.getCirculationStockSimple_NoJson());
						}
					} else if (equalFlg == 1) { // ����
						updateFlg = allStockDao.updateAllStockCirculation(newAllStock);
						messages = "��ͨ��(��)���ʱ��";
					}
					if (updateFlg) {
						++updateNum;
						System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + updateNum + "---" + messages + "���й�Ʊ��(all_stock_)�����˹�Ʊ"
								+ newAllStock.getStockCode() + "(" + PropertiesUtils.getProperty(newAllStock.getStockCode()) + ")����ͨ�ɺ���ֵͨ�ֶ�");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + " ���й�Ʊ��(all_stock_)�и�����" + updateNum + "����ͨ�ɺ���ͨ��ֵ��¼��");
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
			while (keyEnumeration.hasMoreElements()) {
				String stockCode = keyEnumeration.nextElement().toString();
				Date recentDateInHistoryStock = getMaxStockDateByStockCodeInHistoryStock(stockCode);
				if (fileExistInHistoryStock(recentDateInHistoryStock))
					continue;
				String fileName = stockCode + ".txt";
				final String filePath = "E:\\txd\\" + fileName;
				String encoding = "GBK";
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
								if (!CommonUtils.isValidDate(lineArray[0]) || !CommonUtils.isLegalDate(lineArray[0], recentDateInHistoryStock))
									continue;
								boolean saveFlg = saveHistoryStock(stockCode, lineArray, maxNum);
								if (saveFlg) {
									saveNum++;
									System.out.println(saveNum + "---" + DateUtils.dateTimeToString(new Date()) + " ��ʷ��Ʊ��Ϣ��(history_stock_)������"
											+ lineArray[0] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")");
								}
							}
						}
						read.close();
					} else {
						System.out.println(DateUtils.dateTimeToString(new Date()) + " ��ȡ�ļ�ʱ�Ҳ���ָ�����ļ���" + filePath);
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
			System.out.println(DateUtils.dateTimeToString(new Date()) + " ��ʷ��Ʊ��Ϣ��(history_stock_)������" + saveNum + "����¼��");
			log.loger.info(" ��ʷ��Ʊ��Ϣ��(history_stock_)������" + saveNum + "����¼��");
		}
	}

	/**
	 * �ÿ�չ�Ʊ��Ϣ��(daily_stock_)�е��ǵ���(change_rate_)�ͻ�����(turnover_rate_)
	 * 
	 */
	public void handleChangeRateAndTurnoverRateInDailyStock() {

		int updateNum = 0;
		try {
			dailyStockDao = new DailyStockDao();
			allDetailStockDao = new AllDetailStockDao(); // AllDetailStockDao.getInstance();
			List<DailyStock> dailyStockList = dailyStockDao.listDailyStockByChangeRateOrTurnoverRate();
			for (DailyStock dailyStock : dailyStockList) {
				String stockCode = dailyStock.getStockCode();
				Date stockDate = dailyStock.getStockDate();
				int fieldFlg = 0; // 0:changeRate��turnoverRate, 1:changeRate,
									// 2:turnoverRate
				AllDetailStock allDetailStock = allDetailStockDao.getAllDetailStockByKey(stockDate, stockCode);
				if (null == allDetailStock)
					continue;
				if (CommonUtils.isZeroOrNull(dailyStock.getChangeRate()) || CommonUtils.isMinMaxValue(dailyStock.getChangeRate())) {
					Double changeRateInAllDetailStock = allDetailStock.getChangeRate();
					dailyStock.setChangeRate(changeRateInAllDetailStock);
					String changeRateDES = DESUtils.encryptToHex(changeRateInAllDetailStock.toString());
					dailyStock.setEncryptChangeRate(changeRateDES);
					fieldFlg = 1;
				}
				if (CommonUtils.isZeroOrNull(dailyStock.getTurnoverRate())) {
					Double turnoverRateInAllDetailStock = allDetailStock.getTurnoverRate();
					dailyStock.setTurnoverRate(turnoverRateInAllDetailStock);
					String turnoverRateDES = DESUtils.encryptToHex(turnoverRateInAllDetailStock.toString());
					dailyStock.setEncryptTurnoverRate(turnoverRateDES);
					fieldFlg = 2;
				}
				boolean updateFlg = dailyStockDao.updateChangeRateAndTurnoverRate(dailyStock, fieldFlg);
				if (updateFlg) {
					++updateNum;
					String sField = (fieldFlg == 0 ? "change_rate_�ֶκ�turnover_rate_�ֶ�" : (fieldFlg == 1 ? "change_rate_�ֶ�" : "turnover_rate_�ֶ�"));
					System.out.println(DateUtils.dateTimeToString(new Date()) + " " + updateNum + "---ÿ�չ�Ʊ��Ϣ��(daily_stock_)����������Ϊ��"
							+ DateUtils.dateToString(stockDate) + "����Ʊ����Ϊ��" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")��"
							+ sField);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao, allDetailStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + " ÿ�չ�Ʊ��Ϣ��(daily_stock_)������" + updateNum + "���ǵ����ͻ����ʼ�¼��");
			log.loger.info(" ÿ�չ�Ʊ��Ϣ��(daily_stock_)������" + updateNum + "���ǵ����ͻ����ʼ�¼��");
		}
	}

	private boolean saveHistoryStock(String stockCode, String[] lineArray, Long lineNum) throws SQLException {

		boolean saveFlg = false;
		// Date stockDate = DateUtils.String2Date(lineArray[0]);
		// if (!historyStockDao.isExistInHistoryStock(stockCode, stockDate)) {
		HistoryStock historyStock = getHistoryStockByArray(stockCode, lineArray);
		// Long maxNum = historyStockDao.getMaxNumFromHistoryStock();
		historyStock.setNum(++lineNum);
		saveFlg = historyStockDao.saveHistoryStock(historyStock);
		// }
		return saveFlg;
	}

	private HistoryStock getHistoryStockByArray(String stockCode, String[] lineArray) {

		Date stockDate = DateUtils.stringToDate(lineArray[0]);
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

	private int isEqualAllStock(AllStock oldAllStock, AllStock newAllStock) {

		int flg = 1; // 0:������, 1:����ͨ�����,��������, 2:�����
		Long oldCirculationValue = oldAllStock.getCirculationValue();
		Long oldCirculationStockComplex = oldAllStock.getCirculationStockComplex();
		String oldCirculationStockSimple = oldAllStock.getCirculationStockSimple_NoJson();
		Long newCirculationValue = newAllStock.getCirculationValue();
		Long newCirculationStockComplex = newAllStock.getCirculationStockComplex();
		String newCirculationStockSimple = newAllStock.getCirculationStockSimple_NoJson();

		if (oldCirculationValue.compareTo(newCirculationValue) == 0 && oldCirculationStockComplex.compareTo(newCirculationStockComplex) == 0
				&& newCirculationStockSimple.equals(oldCirculationStockSimple)) {
			flg = 2;
		} else if (oldCirculationValue.compareTo(newCirculationValue) == 0 && oldCirculationStockComplex.compareTo(newCirculationStockComplex) == 0
				&& newCirculationStockSimple.equals(oldCirculationStockSimple)) {
			flg = 1;
		} else if (oldCirculationValue.compareTo(newCirculationValue) == 0 && oldCirculationStockComplex.compareTo(newCirculationStockComplex) == 0
				&& !newCirculationStockSimple.equals(oldCirculationStockSimple)) {
			flg = 0;
		}
		return flg;
	}

	private boolean fileExistInHistoryStock(Date recentDateInHistoryStock) throws SQLException {

		Date recentDate = DateUtils.stringToDate("2017-12-08");
		if (CommonUtils.compareDate(recentDateInHistoryStock, recentDate))
			return true;
		else
			return false;
	}

	private Date getMaxStockDateByStockCodeInHistoryStock(String stockCode) throws SQLException {

		Date[] minMaxDate = historyStockDao.getMinMaxDate(stockCode);
		return minMaxDate[1];
	}

	private String[] getLineArrayFromLineTxt(String lineTxt) {

		List<String> lineList = new ArrayList<String>();
		String[] lineArray = lineTxt.split("\t");
		for (String value : lineArray) {
			if (!CommonUtils.isBlank(value))
				lineList.add(value.trim());
		}
		return lineList.toArray(new String[lineList.size()]);
	}
}
