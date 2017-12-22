package cn.implement;

import java.sql.Timestamp;
import java.util.Date;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
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
			log.loger.info(" " + info + "��" + sDate + "��ԭʼ��Ʊ����(original_stock_)��");
		}
	}

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

			for (int index = 0; index < codeArray.length; index++) {
				// �����ݽ���ת��
				DailyStock stockData = CommonUtils.getDailyStockFromArray(index, codeArray, changeRateArray, turnoverRateArray, sDate);
				// ��������Ĺ�Ʊ����
				boolean existFlg = dailyStockDao.isExistInDailyStock(stockData);
				if (!existFlg) {
					boolean saveFlg = dailyStockDao.saveDailyStock(stockData);
					if (saveFlg) {
						dailySum++;
						boolean updateFlg = updateStatisticStock(stockData);
						if (updateFlg) {
							statisticSum++;
							System.out.println(DateUtils.dateTimeToString(new Date())
									+ " ÿ�չ�Ʊ��Ϣ��(daily_stock_)��ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ӻ�����˹�Ʊ---" + dailySum + "��" + stockData.getStockCode()
									+ "(" + PropertiesUtils.getProperty(stockData.getStockCode()) + ")");
							log.loger.info(" ÿ�չ�Ʊ��Ϣ��(daily_stock_)��ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)���ӻ�����˹�Ʊ---" + dailySum + "��"
									+ stockData.getStockCode() + "(" + PropertiesUtils.getProperty(stockData.getStockCode()) + ")");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + sDate + " ÿ�չ�Ʊ��Ϣ��(daily_stock_)��������" + dailySum + "����¼��");
			log.loger.info(" " + sDate + " ÿ�չ�Ʊ��Ϣ��(daily_stock_)��������" + dailySum + "����¼��");
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + sDate + " ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��������" + statisticSum + "����¼��");
			log.loger.info(" " + sDate + " ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��������" + statisticSum + "����¼��");
		}
	}

	private boolean updateStatisticStock(DailyStock dailyStock) throws Exception {

		boolean updateFlg = false;
		String stockCode = dailyStock.getStockCode();
		String changeFlg = dailyStock.getChangeFlg();
		StatisticStock statisticStock = statisticStockDao.getStatisticStockByStockCode(stockCode);
		if (null != statisticStock) {
			// �����Ʊ���ǵ�����
			calculateUpDownNumber(statisticStock, changeFlg);
			updateFlg = statisticStockDao.updateStatisticStock(statisticStock);
		}
		return updateFlg;
	}
}