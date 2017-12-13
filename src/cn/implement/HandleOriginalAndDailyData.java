package cn.implement;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticStock;
import cn.log.Log;

 public class HandleOriginalAndDailyData extends OperationData {
	Log log = Log.getLoger();

	public void handleOriginal(String sDate, String stockCodes, String changeRates, String turnoverRates) {

		originalStockDao = new OriginalStockDao();
		String info = "�޲���";
		try {
			// ��������
			int original_count = originalStockDao.searchOriginalData(sDate);
			if (original_count == 0) {
				originalStockDao.addOriginalData(sDate, stockCodes, changeRates, turnoverRates);
				info = "���";
			} else {
				originalStockDao.updateOriginalData(sDate, stockCodes, changeRates, turnoverRates);
				info = "����";
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(originalStockDao);
			System.out.println(DateUtils.DateTimeToString(new Date()) + " " +  info + "��" + sDate + "��ԭʼ��Ʊ����(original_stock_)��");
			log.loger.info(" " +  info + "��" + sDate + "��ԭʼ��Ʊ����(original_stock_)��");
		}
	}
	
	public void handleDaily(String sDate, String stockCodes, String changeRates, String turnoverRates) {

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

			for (int index=0; index<codeArray.length; index++) {
				// �����ݽ���ת��
				DailyStock stockData = getStockData(index, codeArray, changeRateArray, turnoverRateArray, sDate);
				// ��������
				boolean existFlg = dailyStockDao.isExistInDailyStock(stockData);
				if (!existFlg) {
					boolean saveFlg = dailyStockDao.saveDailyStock(stockData);
					if (saveFlg) {
						dailySum++;
						System.out.println(DateUtils.DateTimeToString(new Date()) + " ÿ�չ�Ʊ���ݱ�(daily_stock_)���ӹ�Ʊ---" + (index+1) + "��" + stockData.getStockCode() + "(" + PropertiesUtils.getProperty(stockData.getStockCode()) + ")");
						log.loger.info(" ÿ�չ�Ʊ���ݱ�(daily_stock_)���ӹ�Ʊ---" + (index+1) + "��" + stockData.getStockCode() + "(" + PropertiesUtils.getProperty(stockData.getStockCode()) + ")");
						boolean saveUpdateFlg = updateStatisticStock(stockData);
						if (saveUpdateFlg) statisticSum++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  " + sDate + " ÿ�չ�Ʊ��Ϣ��(daily_stock_)�������" + dailySum + "����¼��");
			log.loger.info(" " + sDate + " ÿ�չ�Ʊ��Ϣ��(daily_stock_)�������" + dailySum + "����¼��");
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  " + sDate + " ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��������" + statisticSum + "����¼��");
			log.loger.info(" " + sDate + " ͳ�ƹ�Ʊ��Ϣ��(statistic_stock_)��������" + statisticSum + "����¼��");
		}
	}

	private boolean updateStatisticStock(DailyStock dailyStock) throws SQLException {

		String stockCode = dailyStock.getStockCode();
		StatisticStock statisticStock = new StatisticStock(stockCode);
		setUpDownNumberAndUpDownJson(statisticStock);
		return statisticStockDao.updateStatisticStock(statisticStock);
	}

	private DailyStock getStockData(int index, String[] codes, String[] changeRates, String[] turnoverRates, String sDate) {
		DailyStock stockData = new DailyStock();
		Date stockDate = DateUtils.String2Date(sDate);
		Timestamp inputTime = new Timestamp(System.currentTimeMillis());
		stockData.setStockCode(codes[index]);
		stockData.setStockDate(stockDate);
		Double changeRate = Double.valueOf(changeRates[index]);
		stockData.setChangeRate(changeRate);
		if (turnoverRates != null) {
			Double turnoverRate = Double.valueOf(turnoverRates[index]);
			stockData.setTurnoverRate(turnoverRate);
		} else {
			stockData.setTurnoverRate(null);
		}
		String changeFlg = changeRate>0?"1":"0";
		stockData.setChangeFlg(changeFlg);
		stockData.setInputTime(inputTime);
		return stockData;
	}
}