package cn.implement;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticDetailStock;
import cn.db.bean.StatisticStock;

public class HandleOriginalAndDailyData extends OperationData {

	public void handleOriginalStockData(String sDate, String stockCodes, String changeRates, String turnoverRates) {

		originalStockDao = new OriginalStockDao();
		String info = "�޲���";
		try {
			// ��������
			final int original_count = originalStockDao.searchOriginalData(sDate);
			if (original_count == 0) {
				originalStockDao.addOriginalData(sDate, stockCodes, changeRates, turnoverRates);
				info = "����";
			} else {
				originalStockDao.updateOriginalData(sDate, stockCodes, changeRates, turnoverRates);
				info = "����";
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(originalStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + " " + info + "��" + sDate + "��ԭʼ��Ʊ����(original_stock_)��");
			log.loger.warn(" " + info + "��" + sDate + "��ԭʼ��Ʊ����(original_stock_)��");
		}
	}
	
	/**
	 * ����ԭʼ��Ʊ���ݵ�ÿ�չ�Ʊ���ݱ�(daily_stock_)�ͱ�(statistic_stock_)��
	 * 
	 */
	public void analysisOriginalData() {

		originalStockDao = new OriginalStockDao();
		try {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(original_stock_)����ԭʼ��Ʊ���ݵ���(daily_stock_)�п�ʼ...");
			List<OriginalStock> originalStockList = originalStockDao.listOriginalData();
			for (OriginalStock originalStock : originalStockList) {
				Date stDate = originalStock.getStockDate();
				String stockDate = DateUtils.dateTimeToString(stDate);
				String stockCodes = originalStock.getStockCodes();
				String changeRates = originalStock.getChangeRates();
				String turnoverRates = originalStock.getTurnoverRates();
				try {
					handleDailyStockData(stockDate, stockCodes, changeRates, turnoverRates);
				} catch (Exception ex) {
					ex.printStackTrace();
					continue;
				}
			}
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(daily_stock_)ͳ�ƹ�Ʊ���ݵ���(statistic_stock_)���н���...");
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			closeDao(originalStockDao);
		}
	}
	
	/**
	 * ����ԭʼ��Ʊ���ݵ�ÿ�չ�Ʊ���ݱ�(daily_stock_)�ͱ�(statistic_stock_)��
	 *
	 */
	public void handleDailyStockData(String sDate, String stockCodes, String changeRates, String turnoverRates) {

		int dailySum = 0;
		int statisticSum = 0;
		try {
			dailyStockDao = new DailyStockDao();
			statisticStockDao = new StatisticStockDao();
			String[] codeArray = stockCodes.split(",");
			String[] changeRateArray = changeRates.split(",");
			String[] turnoverRateArray = null;
			if (!CommonUtils.isBlank(turnoverRates)) {
				turnoverRateArray = turnoverRates.split(",");
			}
			// ����ÿ�չ�Ʊ����(daily_stock_)
			for (int index = 0; index < codeArray.length; index++) {
				// �����ݽ���ת��
				DailyStock dailyStock = CommonUtils.getDailyStockFromArray(index, codeArray, changeRateArray, turnoverRateArray, sDate);
				// ��������Ĺ�Ʊ����
				boolean existFlg = dailyStockDao.isExistInDailyStock(dailyStock);
				if (!existFlg) {
					boolean saveFlg = dailyStockDao.saveDailyStock(dailyStock);
					if (saveFlg) {
						dailySum++;
						System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(daily_stock_)�����˹�Ʊ  " + dailySum + "��"
								+ dailyStock.getStockCode() + "(" + PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
						log.loger.warn("----->��(daily_stock_)�����˹�Ʊ  " + dailySum + "��" + dailyStock.getStockCode() + "("
								+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
						//boolean updateFlg = updateStatisticDetailStock(dailyStock);
						boolean saveStatisticFlg = saveStatisticStock(dailyStock);
						if (saveStatisticFlg) {
							++statisticSum;
							System.out.println(DateUtils.dateTimeToString(new Date()) + "----->��(statistic_stock_)�����˵�" + statisticSum + "ֻ��Ʊ��" + dailyStock.getStockCode() + "("
									+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + "), ͳ����: " + DateUtils.dateToString(dailyStock.getStockDate()));
							log.loger.warn("----->��(statistic_stock_)�����˵�" + statisticSum + "ֻ��Ʊ��" + dailyStock.getStockCode() + "("
									+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + "), ͳ����: " + DateUtils.dateToString(dailyStock.getStockDate()));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + sDate + " ��(daily_stock_)��������" + dailySum + "����¼��");
			log.loger.warn("----->" + sDate + " ��(daily_stock_)��������" + dailySum + "����¼��");
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + sDate + " ��(statistic_detail_stock_)��������" + statisticSum + "����¼��");
			log.loger.warn("----->" + sDate + " ��(statistic_detail_stock_)��������" + statisticSum + "����¼��");
		}
	}

	private boolean saveStatisticStock(DailyStock dailyStock) throws SQLException {

		String stockCode = dailyStock.getStockCode();
		boolean saveFlg = false;
		if (!statisticStockDao.isExistInStatisticStock(stockCode)) {
			Date stockDate = dailyStock.getStockDate();
			String stockCodeDes = DESUtils.encryptToHex(stockCode);
			StatisticStock statisticStock = new StatisticStock(stockCode, stockDate, stockCodeDes);
			Long maxNum = statisticStockDao.getMaxNum(StatisticStock.TABLE_NAME);
			statisticStock.setNum(++maxNum);
			statisticStock.setNote(DataUtils._BLANK);
			statisticStock.setInputTime(new Date());
			saveFlg = statisticStockDao.saveStatisticStock(statisticStock);
		}
		return saveFlg;
	}

	private boolean updateStatisticDetailStock(DailyStock dailyStock) throws SQLException {

		boolean updateFlg = false;
		String stockCode = dailyStock.getStockCode();
		Date stockDate = dailyStock.getStockDate();
		String changeFlg = dailyStock.getChangeFlg();
		StatisticDetailStock statisticDetailStock = statisticDetailStockDao.getStatisticDetailStockByKey(stockCode, stockDate);
		if (null != statisticDetailStock) {
			// �����Ʊ���ǵ�����
			calculateUpDownNumber(statisticDetailStock, changeFlg);
			updateFlg = statisticDetailStockDao.updateStatisticDetailStock(statisticDetailStock);
		}
		return updateFlg;
	}
}