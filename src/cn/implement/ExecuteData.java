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

public class ExecuteData extends OperationData {

	public void searchStock(String stockCode, String startDate, String endDate, String changeFlg) {
		
		dailyStockDao = new DailyStockDao();
		try {
			String sDate = "";
			if (!startDate.trim().equals(DataUtils.CONSTANT_BLANK) && !endDate.trim().equals(DataUtils.CONSTANT_BLANK)) {
				sDate = startDate + "至" + endDate;
			} else {
				Date[] minMaxDate = dailyStockDao.getMinMaxDate();
				sDate = DateUtils.dateToString(minMaxDate[0]) + "至" + DateUtils.dateToString(minMaxDate[1]);
			}
			String stockName = PropertiesUtils.getProperty(stockCode);
			List<DailyStock> dailyList = dailyStockDao.searchStockByTime(stockCode, startDate, endDate, changeFlg);
			if (dailyList.size() > 0) {
				String change = changeFlg.equals("0")?"跌":(changeFlg.equals("1")?"涨":"涨跌");
				System.out.println("-------------------------------" + sDate + " " + stockName + "(" + stockCode + ")次数统计(" + change + ")--------------------------------");
				log.loger.info("-------------------------------" + sDate + " " + stockName + "(" + stockCode + ")次数统计(" + change + ")--------------------------------");
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")出现的次数：" + dailyList.size() + "次");
				log.loger.info(sDate + " " + stockName + "(" + stockCode + ")出现的次数：" + dailyList.size() + "次");
				String dateStr = "";
				for (int index=0; index<dailyList.size(); index++) {
					DailyStock data = dailyList.get(index);
					dateStr += DateUtils.dateToString(data.getStockDate()) + ",";
				}
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")出现的日期：");
				System.out.println(dateStr.substring(0, sDate.length()));
				log.loger.info(sDate + " " + stockName + "(" + stockCode + ")出现的日期：");
				log.loger.info(dateStr.substring(0, sDate.length()));
			} else {
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")出现的次数为：0次！");
				log.loger.info(sDate + " " + stockName + "(" + stockCode + ")出现的次数为：0次！");
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
			String sDate = DateUtils.dateToString(minMaxDate[0]) + "至" + DateUtils.dateToString(minMaxDate[1]);
			List<StatisticStock> statisticStockList = statisticStockDao.listStatisticStock();
			System.out.println("-------------------------------" + sDate + " 每日选择的股票出现的次数统计--------------------------------");
			log.loger.info("-------------------------------" + sDate + " 每日选择的股票出现的次数统计--------------------------------");
			System.out.println("股票               总涨跌次数            前一周涨跌次数     前半月涨跌次数     前一月涨跌次数       前二月涨跌次数        前三月涨跌次数        前半年涨跌次数        前一年涨跌次数");
			log.loger.info("股票               总涨跌次数            前一周涨跌次数     前半月涨跌次数     前一月涨跌次数       前二月涨跌次数        前三月涨跌次数        前半年涨跌次数        前一年涨跌次数");
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
