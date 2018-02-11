package cn.implement;

import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.DailyStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticDetailStock;
import cn.db.bean.StatisticStock;

public class ExecuteData extends OperationData {

	public void searchStock(String stockCode, String startDate, String endDate, String changeFlg) {
		
		dailyStockDao = new DailyStockDao();
		try {
			String sDate = "";
			if (!startDate.trim().equals(DataUtils._BLANK) && !endDate.trim().equals(DataUtils._BLANK)) {
				sDate = startDate + "至" + endDate;
			} else {
				Date[] minMaxDate = dailyStockDao.getMinMaxDate();
				sDate = DateUtils.dateToString(minMaxDate[0]) + "至" + DateUtils.dateToString(minMaxDate[1]);
			}
			String stockName = PropertiesUtils.getProperty(stockCode);
			List<DailyStock> dailyList = dailyStockDao.searchStockByTime(stockCode, startDate, endDate, changeFlg);
			if (dailyList.size() > 0) {
				String change = changeFlg.equals("0")?StatisticDetailStock.CH_DOWN:(changeFlg.equals("1")?StatisticDetailStock.CH_UP:StatisticDetailStock.CH_UP_DOWN);
				System.out.println("-------------------------------" + sDate + " " + stockName + "(" + stockCode + ")次数统计(" + change + ")--------------------------------");
				log.loger.warn("-------------------------------" + sDate + " " + stockName + "(" + stockCode + ")次数统计(" + change + ")--------------------------------");
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")出现的次数：" + dailyList.size() + "次");
				log.loger.warn(sDate + " " + stockName + "(" + stockCode + ")出现的次数：" + dailyList.size() + "次");
				String dateStr = "";
				for (int index=0; index<dailyList.size(); index++) {
					DailyStock data = dailyList.get(index);
					dateStr += DateUtils.dateToString(data.getStockDate()) + ",";
				}
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")出现的日期：");
				System.out.println(dateStr.substring(0, sDate.length()));
				log.loger.warn(sDate + " " + stockName + "(" + stockCode + ")出现的日期：");
				log.loger.warn(dateStr.substring(0, sDate.length()));
			} else {
				System.out.println(sDate + " " + stockName + "(" + stockCode + ")出现的次数为：0次！");
				log.loger.warn(sDate + " " + stockName + "(" + stockCode + ")出现的次数为：0次！");
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
		statisticDetailStockDao = new StatisticDetailStockDao();
		try {
			Date[] minMaxDate = dailyStockDao.getMinMaxDate();
			String sDate = DateUtils.dateToString(minMaxDate[0]) + "至" + DateUtils.dateToString(minMaxDate[1]);
			List<StatisticDetailStock> statisticDetailStockList = statisticDetailStockDao.listRecentStatisticDetailStock();
			System.out.println("-------------------------------" + sDate + " 每日选择的股票出现的次数统计--------------------------------");
			log.loger.warn("-------------------------------" + sDate + " 每日选择的股票出现的次数统计--------------------------------");
			System.out.println("股票               总涨跌次数            前一周涨跌次数     前半月涨跌次数     前一月涨跌次数       前二月涨跌次数        前三月涨跌次数        前半年涨跌次数        前一年涨跌次数");
			log.loger.warn("股票               总涨跌次数            前一周涨跌次数     前半月涨跌次数     前一月涨跌次数       前二月涨跌次数        前三月涨跌次数        前半年涨跌次数        前一年涨跌次数");
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
				// change the content of .log file to hex
				log.loger.warn(DataUtils.stringToHex(lineContent));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			closeDao(dailyStockDao, statisticStockDao);
		}
	}
}
