package cn.implement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.bean.DailyStock;

public class AnalysisDailyAndDetailStockData extends OperationData {

	/**
	 * ͳ�Ʒ��������������Ʊ������ǡ��������ǡ�������
	 *
	 */
	public void analysisStock(String startDate) {
		dailyStockDao = new DailyStockDao();
		try {
			List<DailyStock> dailyList = dailyStockDao.analysisStockByTime(DateUtils.stringToDate(startDate), new Date(), null);
			/*if (dailyList.size() > 0) {
				String change = changeFlg.equals("0")?"��":(changeFlg.equals("1")?"��":"�ǵ�");
				System.out.println("-------------------------------" + startDate + "��" + endDate + "��Ʊ����ͳ��(" + change + ")--------------------------------");
				log.loger.info("-------------------------------" + startDate + "��" + endDate + "��Ʊ����ͳ��(" + change + ")--------------------------------");
				List<DailyStock> analysisList = setDatesToDailyStock(dailyList, startDate, endDate, changeFlg);
				for (int index=0; index<analysisList.size(); index++) {
					DailyStock data = analysisList.get(index);
					String stockName = PropertiesUtils.getProperty(data.getStockCode());
					String spaces = CommonUtils.getSpaceByString(stockName);
					System.out.println(CommonUtils.supplyNumber(index+1, analysisList.size()) + "---��Ʊ���ֵĴ���: " + data.getCount() + " ��Ʊ����: " + data.getStockCode() + "(" + stockName + ")" + spaces + " ���ֵ�����: (" + data.getStockDates() + ")");
					log.loger.info(CommonUtils.supplyNumber(index+1, analysisList.size()) + "---��Ʊ���ֵĴ���: " + data.getCount() + " ��Ʊ����: " + data.getStockCode() + "(" + stockName + ")" + spaces + " ���ֵ�����: (" + data.getStockDates() + ")");
				}
			} else {
				System.out.println("��ʱ���(" + startDate + "��" + endDate + ")û��ÿ�չ�Ʊ���ݣ�");
				log.loger.info("��ʱ���(" + startDate + "��" + endDate + ")û��ÿ�չ�Ʊ���ݣ�");
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao);
		}
	}
	
	/**
	 * ͳ�Ʒ���ĳһʱ��ι�Ʊ���ֵĴ���
	 *
	 */
	public void analysisStock(String startDate, String endDate, String changeFlg) {
		dailyStockDao = new DailyStockDao();
		try {
			List<DailyStock> dailyList = dailyStockDao.analysisStockByTime(DateUtils.stringToDate(startDate), DateUtils.stringToDate(endDate), changeFlg);
			if (dailyList.size() > 0) {
				String change = changeFlg.equals("0")?"��":(changeFlg.equals("1")?"��":"�ǵ�");
				System.out.println("-------------------------------" + startDate + "��" + endDate + "��Ʊ����ͳ��(" + change + ")--------------------------------");
				log.loger.info("-------------------------------" + startDate + "��" + endDate + "��Ʊ����ͳ��(" + change + ")--------------------------------");
				List<DailyStock> analysisList = setDatesToDailyStock(dailyList, startDate, endDate, changeFlg);
				for (int index=0; index<analysisList.size(); index++) {
					DailyStock data = analysisList.get(index);
					String stockName = PropertiesUtils.getProperty(data.getStockCode());
					String spaces = CommonUtils.getSpaceByString(stockName);
					System.out.println(CommonUtils.supplyNumber(index+1, analysisList.size()) + "---��Ʊ���ֵĴ���: " + data.getCount() + " ��Ʊ����: " + data.getStockCode() + "(" + stockName + ")" + spaces + " ���ֵ�����: (" + data.getStockDates() + ")");
					log.loger.info(CommonUtils.supplyNumber(index+1, analysisList.size()) + "---��Ʊ���ֵĴ���: " + data.getCount() + " ��Ʊ����: " + data.getStockCode() + "(" + stockName + ")" + spaces + " ���ֵ�����: (" + data.getStockDates() + ")");
				}
			} else {
				System.out.println("��ʱ���(" + startDate + "��" + endDate + ")û��ÿ�չ�Ʊ���ݣ�");
				log.loger.info("��ʱ���(" + startDate + "��" + endDate + ")û��ÿ�չ�Ʊ���ݣ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao);
		}
	}

	private List<DailyStock> setDatesToDailyStock(List<DailyStock> dailyList, String startDate, String endDate, String changeFlg) throws SQLException {

		List<DailyStock> stockList = new ArrayList<DailyStock>();
		for (DailyStock stock : dailyList) {
			List<Date> dateList = dailyStockDao.analysisStockDatesByStockCode(stock.getStockCode(), startDate, endDate, changeFlg);
			stock.setDates(dateList);
			stockList.add(stock);
		}
		return stockList;
	}
}
