package cn.implement;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticDetailStock;
import cn.db.bean.StatisticStock;

public class ExecuteData extends OperationData {

	public void searchStock(String stockCode, String startDate, String endDate, String changeFlg) {
		
		dailyStockDao = new DailyStockDao();
		try {
			String sDate = "";
			if (!startDate.trim().equals(DataUtils._BLANK) && !endDate.trim().equals(DataUtils._BLANK)) {
				sDate = startDate + "��" + endDate;
			} else {
				Date[] minMaxDate = dailyStockDao.getMinMaxDate();
				sDate = DateUtils.dateToString(minMaxDate[0]) + "��" + DateUtils.dateToString(minMaxDate[1]);
			}
			String stockName = PropertiesUtils.getProperty(stockCode);
			List<DailyStock> dailyList = dailyStockDao.searchStockByTime(stockCode, startDate, endDate, changeFlg);
			if (dailyList.size() > 0) {
				String change = changeFlg.equals("0")?StatisticDetailStock.CH_DOWN:(changeFlg.equals("1")?StatisticDetailStock.CH_UP:StatisticDetailStock.CH_UP_DOWN);
				System.out.println("-------------------------------" + sDate + " " + stockName + "(" + stockCode + ")����ͳ��(" + change + ")--------------------------------");
				log.loger.warn("-------------------------------" + sDate + " " + stockName + "(" + stockCode + ")����ͳ��(" + change + ")--------------------------------");
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")���ֵĴ�����" + dailyList.size() + "��");
				log.loger.warn(sDate + " " + stockName + "(" + stockCode + ")���ֵĴ�����" + dailyList.size() + "��");
				String dateStr = "";
				for (int index=0; index<dailyList.size(); index++) {
					DailyStock data = dailyList.get(index);
					dateStr += DateUtils.dateToString(data.getStockDate()) + ",";
				}
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")���ֵ����ڣ�");
				System.out.println(dateStr.substring(0, sDate.length()));
				log.loger.warn(sDate + " " + stockName + "(" + stockCode + ")���ֵ����ڣ�");
				log.loger.warn(dateStr.substring(0, sDate.length()));
			} else {
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")���ֵĴ���Ϊ��0�Σ�");
				log.loger.warn(sDate + " " + stockName + "(" + stockCode + ")���ֵĴ���Ϊ��0�Σ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao);
		}
	}

	public void statisticDetailStock() {
		
		dailyStockDao = new DailyStockDao();
		statisticStockDao = new StatisticStockDao();
		statisticDetailStockDao = new StatisticDetailStockDao();
		try {
			Date nowDate = new Date();
			Date[] minMaxDate = dailyStockDao.getMinMaxDate();
			String sDate = DateUtils.dateToString(nowDate) + "��" + DateUtils.dateToString(minMaxDate[1]);
			List<StatisticDetailStock> statisticDetailStockList = new ArrayList<StatisticDetailStock>();
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			for (StatisticStock statisticStock : statisticStockList) {
				StatisticDetailStock statisticDetailStock = new StatisticDetailStock(statisticStock.getStockCode(), nowDate);
				super.setUpAndDownNumberJson(statisticDetailStock);
				statisticDetailStockList.add(statisticDetailStock);
			}
			System.out.println("-------------------------------" + sDate + " ÿ��ѡ��Ĺ�Ʊ���ֵĴ���ͳ��--------------------------------");
			log.loger.warn("-------------------------------" + sDate + " ÿ��ѡ��Ĺ�Ʊ���ֵĴ���ͳ��--------------------------------");
			String lineTitle = "��Ʊ               ���ǵ�����            ǰһ���ǵ�����     ǰ�����ǵ�����     ǰһ���ǵ�����       ǰ�����ǵ�����        ǰ�����ǵ�����        ǰ�����ǵ�����        ǰһ���ǵ�����";
			System.out.println(lineTitle);
			log.loger.warn(lineTitle);
			for (StatisticDetailStock statisticDetailStock : statisticDetailStockList) {
				String stockCode = statisticDetailStock.getStockCode();
				String stockName = PropertiesUtils.getProperty(stockCode);
				String stockCodeName = stockCode + "(" + stockName + ")";
				String allUpDownNum = statisticDetailStock.getAllUpDownNumber();
				String preOneWeekNum = statisticDetailStock.getOneWeek_NoJson();
				String preHalfMonthNum = statisticDetailStock.getHalfMonth_NoJson();
				String preOneMonthNum = statisticDetailStock.getOneMonth_NoJson();
				String preTwoMonthNum = statisticDetailStock.getTwoMonth_NoJson();
				String preThreeMonthNum = statisticDetailStock.getThreeMonth_NoJson();
				String preHalfYearNum = statisticDetailStock.getHalfYear_NoJson();
				String preOneYearNum = statisticDetailStock.getOneYear_NoJson();
				String lineContent = stockCodeName + DataUtils._SPACES + allUpDownNum + DataUtils._SPACES + preOneWeekNum + DataUtils._SPACES + preHalfMonthNum + DataUtils._SPACES + preOneMonthNum + DataUtils._SPACES + preTwoMonthNum + DataUtils._SPACES + preThreeMonthNum + DataUtils._SPACES + preHalfYearNum + DataUtils._SPACES + preOneYearNum;
				System.out.println(lineContent);
				log.loger.warn(lineContent);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			closeDao(dailyStockDao, statisticStockDao, statisticDetailStockDao);
		}
	}
	
	public void statisticStockAvgNum(String startDate) {

		originalStockDao = new OriginalStockDao();
		try {
			Date nowDate = new Date();
			Date beginDate = DateUtils.monthToDate(startDate);
			Map<String, Integer> avgNumberMap = new TreeMap<String, Integer>();
			while (beginDate.compareTo(nowDate) <= 0) {
				int year = DateUtils.getYearFromDate(beginDate);
				int month = DateUtils.getMonthFromDate(beginDate);
				Date firstDateOfMonth = DateUtils.getFirstDayOfMonth(year, month);
				Date lastDateOfMonth = DateUtils.getLastDayOfMonth(year, month);
				double avgNum = originalStockDao.getAvgNumberBySockDate(firstDateOfMonth, lastDateOfMonth);
				avgNumberMap.put(DateUtils.dateTimeToString(beginDate, DateUtils.YEAR_MONTH_FORMAT), DataUtils.getRoundInt(avgNum));
				beginDate = DateUtils.addOneMonth(beginDate);
			}
			
			String endDate = DateUtils.dateToMonth(nowDate);
			String sDate = startDate + "��" + endDate;
			System.out.println("-------------------------------" + sDate + " ÿ��ѡ���Ʊ��ƽ������--------------------------------");
			log.loger.warn("-------------------------------" + sDate + " ÿ��ѡ���Ʊ��ƽ������--------------------------------");
			String lineTitle = "ʱ��                 ƽ��ѡ������";
			System.out.println(lineTitle);
			log.loger.warn(lineTitle);
			String lineText = "TIME             AVG_NUM";

			Iterator<Entry<String, Integer>> it = avgNumberMap.entrySet().iterator();
			while (it.hasNext()) {
			   Entry<String, Integer> entry = it.next();
			   String lineContent = lineText;
			   lineContent = lineContent.replace("TIME", entry.getKey());
			   lineContent = lineContent.replace("AVG_NUM", entry.getValue().toString());
			   System.out.println(lineContent);
			   log.loger.warn(lineContent);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			closeDao(originalStockDao);
		}
	}
}
