package cn.implement;

import java.util.Date;
import java.util.List;

import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticStock;
import cn.log.Log;

public class ExecuteData extends OperationData {
	Log log = Log.getLoger();

	public void searchStock(String stockCode, String startDate, String endDate, String changeFlg) {
		
		dailyStockDao = new DailyStockDao();
		try {
			String sDate = "";
			if (!startDate.trim().equals("") && !endDate.trim().equals("")) {
				sDate = startDate + "��" + endDate;
			} else {
				Date[] minMaxDate = dailyStockDao.getMinMaxDate();
				sDate = DateUtils.Date2String(minMaxDate[0]) + "��" + DateUtils.Date2String(minMaxDate[1]);
			}
			String stockName = PropertiesUtils.getProperty(stockCode);
			List<DailyStock> dailyList = dailyStockDao.searchStockByTime(stockCode, startDate, endDate, changeFlg);
			if (dailyList.size() > 0) {
				String change = changeFlg.equals("0")?"��":(changeFlg.equals("1")?"��":"�ǵ�");
				System.out.println("-------------------------------" + sDate + " " + stockName + "(" + stockCode + ")����ͳ��(" + change + ")--------------------------------");
				log.loger.info("-------------------------------" + sDate + " " + stockName + "(" + stockCode + ")����ͳ��(" + change + ")--------------------------------");
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")���ֵĴ�����" + dailyList.size() + "��");
				log.loger.info(sDate + " " + stockName + "(" + stockCode + ")���ֵĴ�����" + dailyList.size() + "��");
				String dateStr = "";
				for (int index=0; index<dailyList.size(); index++) {
					DailyStock data = dailyList.get(index);
					dateStr += DateUtils.Date2String(data.getStockDate()) + ",";
				}
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")���ֵ����ڣ�");
				System.out.println(dateStr.substring(0, sDate.length()));
				log.loger.info(sDate + " " + stockName + "(" + stockCode + ")���ֵ����ڣ�");
				log.loger.info(dateStr.substring(0, sDate.length()));
			} else {
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")���ֵĴ���Ϊ��0�Σ�");
				log.loger.info(sDate + " " + stockName + "(" + stockCode + ")���ֵĴ���Ϊ��0�Σ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao);
		}
	}

	public void statisticDetailStock() {
		
		dailyStockDao = new DailyStockDao();
		statisticStockDao = new StatisticStockDao();
		try {
			Date[] minMaxDate = dailyStockDao.getMinMaxDate();
			String sDate = DateUtils.Date2String(minMaxDate[0]) + "��" + DateUtils.Date2String(minMaxDate[1]);
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			System.out.println("-------------------------------" + sDate + " ÿ��ѡ��Ĺ�Ʊ���ֵĴ���ͳ��--------------------------------");
			log.loger.info("-------------------------------" + sDate + " ÿ��ѡ��Ĺ�Ʊ���ֵĴ���ͳ��--------------------------------");
			System.out.println("��Ʊ               ���ǵ�����            ǰһ���ǵ�����     ǰ�����ǵ�����     ǰһ���ǵ�����       ǰ�����ǵ�����        ǰ�����ǵ�����        ǰ�����ǵ�����        ǰһ���ǵ�����");
			log.loger.info("��Ʊ               ���ǵ�����            ǰһ���ǵ�����     ǰ�����ǵ�����     ǰһ���ǵ�����       ǰ�����ǵ�����        ǰ�����ǵ�����        ǰ�����ǵ�����        ǰһ���ǵ�����");
			for (StatisticStock statisticStock : statisticStockList) {
				String stockCode = statisticStock.getStockCode();
				String stockName = PropertiesUtils.getProperty(stockCode);
				String stockCodeName = stockCode + "(" + stockName + ")";
				String allUpDownNum = statisticStock.getAllUpDownNumber();
				String preOneWeekNum = statisticStock.getOneWeek_NoJson();
				String preHalfMonthNum = statisticStock.getHalfMonth_NoJson();
				String preOneMonthNum = statisticStock.getOneMonth_NoJson();
				String preTwoMonthNum = statisticStock.getTwoMonth_NoJson();
				String preThreeMonthNum = statisticStock.getThreeMonth_NoJson();
				String preHalfYearNum = statisticStock.getHalfYear_NoJson();
				String preOneYearNum = statisticStock.getOneYear_NoJson();
				System.out.println(stockCodeName + DataUtils.CONSTANT_SPACES + allUpDownNum + DataUtils.CONSTANT_SPACES + preOneWeekNum + DataUtils.CONSTANT_SPACES + preHalfMonthNum + DataUtils.CONSTANT_SPACES + preOneMonthNum + DataUtils.CONSTANT_SPACES + preTwoMonthNum + DataUtils.CONSTANT_SPACES + preThreeMonthNum + DataUtils.CONSTANT_SPACES + preHalfYearNum + DataUtils.CONSTANT_SPACES + preOneYearNum);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(ex);
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
		}
	}
}
