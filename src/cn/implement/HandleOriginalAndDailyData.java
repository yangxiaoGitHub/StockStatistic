package cn.implement;

import java.sql.SQLException;
import java.util.Date;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticDetailStock;

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

	public void handleDailyStockData(String sDate, String stockCodes, String changeRates, String turnoverRates) {

		int dailySum = 0;
		int statisticSum = 0;
		try {
			dailyStockDao = new DailyStockDao();
			statisticDetailStockDao = new StatisticDetailStockDao();
			String[] codeArray = stockCodes.split(",");
			String[] changeRateArray = changeRates.split(",");
			String[] turnoverRateArray = null;
			if (!CommonUtils.isBlank(turnoverRates)) {
				turnoverRateArray = turnoverRates.split(",");
			}
			//����ÿ�չ�Ʊ����(daily_stock_)
			for (int index = 0; index < codeArray.length; index++) {
				// �����ݽ���ת��
				DailyStock dailyStock = CommonUtils.getDailyStockFromArray(index, codeArray, changeRateArray, turnoverRateArray, sDate);
				// ��������Ĺ�Ʊ����
				boolean existFlg = dailyStockDao.isExistInDailyStock(dailyStock);
				if (!existFlg) {
					boolean saveFlg = dailyStockDao.saveDailyStock(dailyStock);
					if (saveFlg) {
						dailySum++;
						System.out.print(DateUtils.dateTimeToString(new Date()) + "----->��(daily_stock_)�����˹�Ʊ  " + dailySum + "��"
								+ dailyStock.getStockCode() + "(" + PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
						log.loger.warn("----->��(daily_stock_)�����˹�Ʊ  " + dailySum + "��" + dailyStock.getStockCode() + "("
								+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")");
						boolean updateFlg = updateStatisticDetailStock(dailyStock);
						if (updateFlg) {
							++statisticSum;
							System.out.println("----->��(statistic_detail_stock_)�����˵�" + statisticSum + "ֻ��Ʊ��" + dailyStock.getStockCode() + "("
									+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + "), ͳ����: " + DateUtils.dateToString(dailyStock.getStockDate()));
							log.loger.warn("----->��(statistic_detail_stock_)�����˵�" + statisticSum + "ֻ��Ʊ��" + dailyStock.getStockCode() + "("
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