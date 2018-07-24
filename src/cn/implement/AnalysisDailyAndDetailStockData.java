package cn.implement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.com.StockUtils;
import cn.db.AllImportStockDao;
import cn.db.DailyStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllImportStock;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticDetailStock;
import cn.db.bean.StatisticStock;
import cn.db.bean.ext.ExtStatisticStock;

public class AnalysisDailyAndDetailStockData extends OperationData {

	/**
	 * ͳ�Ʒ��������������Ʊ���ǵ���������ǵ������ǵ�������������ڵ�����Ƿ���������ڵ�������
	 *
	 */
	public void analysisStock(String startDate, String sortFlg) {
		dailyStockDao = new DailyStockDao();
		//detailStockDao = new DetailStockDao();
		statisticStockDao = new StatisticStockDao();
		allImportStockDao = new AllImportStockDao();
		//statisticDetailStockDao = new StatisticDetailStockDao();
		try {
			Date beginDate = DateUtils.stringToDate(startDate);
			List<ExtStatisticStock> statisticStockList = new ArrayList<ExtStatisticStock>();
			Date endDate = dailyStockDao.getRecentStockDate();
			List<DailyStock> dailyList = dailyStockDao.analysisStockByTime(beginDate, endDate, null);
			if (dailyList.size() > 0) {
				System.out.println("-------------------------------" + startDate + "��" + DateUtils.dateToString(endDate) + "��Ʊ�ǵ���ͳ��--------------------------------");
				log.loger.warn("-------------------------------" + startDate + "��" + DateUtils.dateToString(endDate) + "��Ʊ�ǵ���ͳ��--------------------------------");
				for (int index=0; index<dailyList.size(); index++) {
					DailyStock dailyStock = dailyList.get(index);
					String stockCode = dailyStock.getStockCode();
					//List<DetailStock> detailStockList = detailStockDao.getDetailStockByTime(stockCode, beginDate, endDate);
					List<AllImportStock> allImportStockList = allImportStockDao.getAllImportStockByCodeAndDate(stockCode, beginDate, endDate);
					if (CommonUtils.isBlank(allImportStockList)) continue;
					Double[] minMaxPrices = CommonUtils.getMinMaxPrices(allImportStockList);
					AllImportStock firstImportStock = DataUtils.getFirstAllImportStock(allImportStockList);
					AllImportStock lastImportStock = DataUtils.getLastAllImportStock(allImportStockList);
					StatisticStock statisticStock = statisticStockDao.getStatisticStockByStockCode(stockCode);
					ExtStatisticStock extStatisticStock = new ExtStatisticStock(statisticStock);
					calculateMinMaxChangeRate(extStatisticStock, firstImportStock, minMaxPrices);
					calculateLastDateChangeRate(extStatisticStock, firstImportStock, lastImportStock);
					calculateRecentMinMaxChangeRate(extStatisticStock, minMaxPrices, lastImportStock);
					Date[] startEndDate = {beginDate, endDate};
					Integer upDownNumber = dailyStock.getCount();
					Integer upNumber = dailyStockDao.getUpNumberByStockCode(stockCode, startEndDate);
					Integer downNumber = dailyStockDao.getDownNumberByStockCode(stockCode, startEndDate);
					extStatisticStock.setUpDownNumber(upDownNumber);
					extStatisticStock.setUpNumber(upNumber);
					extStatisticStock.setDownNumber(downNumber);
					statisticStockList.add(extStatisticStock);
				}
				printStatisticStockList(statisticStockList, Integer.valueOf(sortFlg).intValue());
			} else {
				System.out.println("��ʱ���(" + startDate + "��" + DateUtils.dateToString(endDate) + ")û��ÿ�չ�Ʊ���ݣ�");
				log.loger.warn("��ʱ���(" + startDate + "��" + DateUtils.dateToString(endDate) + ")û��ÿ�չ�Ʊ���ݣ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, statisticStockDao, allImportStockDao);
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
				String change = changeFlg.equals("0")?StatisticDetailStock.CH_DOWN:(changeFlg.equals("1")?StatisticDetailStock.CH_UP:StatisticDetailStock.CH_UP_DOWN);
				System.out.println("-------------------------------" + startDate + "��" + endDate + "��Ʊ����ͳ��(" + change + ")--------------------------------");
				log.loger.warn("-------------------------------" + startDate + "��" + endDate + "��Ʊ����ͳ��(" + change + ")--------------------------------");
				List<DailyStock> analysisList = setDatesToDailyStock(dailyList, startDate, endDate, changeFlg);
				for (int index=0; index<analysisList.size(); index++) {
					DailyStock data = analysisList.get(index);
					String stockName = PropertiesUtils.getProperty(data.getStockCode());
					String spaces = CommonUtils.getSpaceByString(stockName);
					System.out.println(DataUtils.supplyNumber(index+1, analysisList.size()) + "---��Ʊ���ֵĴ���: " + data.getCount() + " ��Ʊ����: " + data.getStockCode() + "(" + stockName + ")" + spaces + " ���ֵ�����: (" + data.getStockDates() + ")");
					log.loger.warn(DataUtils.supplyNumber(index+1, analysisList.size()) + "---��Ʊ���ֵĴ���: " + data.getCount() + " ��Ʊ����: " + data.getStockCode() + "(" + stockName + ")" + spaces + " ���ֵ�����: (" + data.getStockDates() + ")");
				}
			} else {
				System.out.println("��ʱ���(" + startDate + "��" + endDate + ")û��ÿ�չ�Ʊ���ݣ�");
				log.loger.warn("��ʱ���(" + startDate + "��" + endDate + ")û��ÿ�չ�Ʊ���ݣ�");
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
	
	/*
	���        ��Ʊ����        ��Ʊ����        ����ǵ���        ������        ����Ƿ�       �ǵ�����
	632     000632          ĳĳ�ɷ�        -32.15%       -10.30%    15.42%          �ǵ�:13��:9��:4
	456     600457          ĳĳ�ɷ�        11.03%        -14.32%    12.69%          �ǵ�:11��:2��:9
	*/
	private void printStatisticStockList(List<ExtStatisticStock> statisticStockList, final int sortFlg) {

		Collections.sort(statisticStockList, new Comparator<ExtStatisticStock>() {
		    public int compare(ExtStatisticStock firstStock, ExtStatisticStock secondStock) {
		    	int flg = 0;
		    	switch (sortFlg){
		    	case DataUtils._INT_ZERO:
		    		flg = firstStock.getNowChangeRate().compareTo(secondStock.getNowChangeRate());
		    		break;
		    	case DataUtils._INT_ONE:
		    		flg = firstStock.getRecentMaxDeclineRate().compareTo(secondStock.getRecentMaxDeclineRate());
		    		break;
		    	case DataUtils._INT_TWO:
		    		flg = secondStock.getRecentMaxRiseRate().compareTo(firstStock.getRecentMaxRiseRate());
		    		break;
		    	case DataUtils._INT_THREE:
		    		flg = secondStock.getUpDownNumber().compareTo(firstStock.getUpDownNumber());
		    		break;
		    	case DataUtils._INT_FOUR:
		    		flg = secondStock.getUpNumber().compareTo(firstStock.getUpNumber());
		    		break;
		    	case DataUtils._INT_FIVE:
		    		flg = secondStock.getDownNumber().compareTo(firstStock.getDownNumber());
		    		break;
		    	default:
		    		flg = firstStock.getNowChangeRate().compareTo(secondStock.getNowChangeRate());
		    		break;
		    	}
		        return flg;
		    }
		});
		String lineTitle = "���        ��Ʊ����        ��Ʊ����        �ǵ���            ������        ����Ƿ�       ���������       �������Ƿ�       �ǵ�����";
		System.out.println(lineTitle);
		log.loger.warn(lineTitle);
		//System.out.println("000     000807     �����ɷ�        -29.02%     -30.11%     0.0%       -29.02%        1.55%         �ǵ�:1��:1��:0");
		String lineText = "NUM     STOCKC     STOCKNAM        RECENTC     MAXDOWN     MAXUPR     RECENTD        RECENTU        �ǵ�:UPD��:UP��:DO";
		for (int index=0; index<statisticStockList.size(); index++) {
			ExtStatisticStock extStatisticStock = statisticStockList.get(index);
			String stockCode = extStatisticStock.getStockCode();
			String lineContent = lineText;
			// ���
			lineContent = lineContent.replace("NUM", DataUtils.supplyNumber(index+1, statisticStockList.size()));
			// ��Ʊ����
			lineContent = lineContent.replace("STOCKC", stockCode);
			// ��Ʊ����
			String stockName = PropertiesUtils.getProperty(stockCode);
			if (stockName.length() <= DataUtils._INT_THREE)
				stockName = CommonUtils.addSpaceInMiddle(stockName);
			lineContent = lineContent.replace("STOCKNAM", stockName);
			// �ǵ���
			lineContent = lineContent.replace("RECENTC", CommonUtils.appendSpace(extStatisticStock.getNowChangeRate() + "%", DataUtils._INT_SEVEN));
			// ������
			lineContent = lineContent.replace("MAXDOWN", CommonUtils.appendSpace(extStatisticStock.getMaxDeclineRate() + "%", DataUtils._INT_SEVEN));
			// ����Ƿ�
			lineContent = lineContent.replace("MAXUPR", CommonUtils.appendSpace(extStatisticStock.getMaxRiseRate() + "%", DataUtils._INT_SIX));
			// ���������
			lineContent = lineContent.replace("RECENTD", CommonUtils.appendSpace(extStatisticStock.getRecentMaxDeclineRate() + "%", DataUtils._INT_SEVEN));
			// �������Ƿ�
			lineContent = lineContent.replace("RECENTU", CommonUtils.appendSpace(extStatisticStock.getRecentMaxRiseRate() + "%", DataUtils._INT_SIX));
			// �ǵ�����
			lineContent = lineContent.replace("UPD", extStatisticStock.getUpDownNumber().toString());
			// �Ǵ���
			lineContent = lineContent.replace("UP", extStatisticStock.getUpNumber().toString());
			// ������
			lineContent = lineContent.replace("DO", extStatisticStock.getDownNumber().toString());
			System.out.println(lineContent);
			// change the content of .log file to hex
			log.loger.warn(DataUtils.stringToHex(lineContent));
		}
	}
	
	/**
	 * �����Ʊ����ǵ������ǵ���
	 *
	 */
	private void calculateMinMaxChangeRate(ExtStatisticStock statisticStock, AllImportStock firstImportStock, Double[] minMaxPrices) {
		//����ǵ���
		double firstPrice = firstImportStock.getCurrent().doubleValue();
		double minPrice = minMaxPrices[0].doubleValue(); 
		double maxPrice = minMaxPrices[1].doubleValue();
		double maxDeclineRate = StockUtils.getChangeRate(firstPrice, minPrice);
		double maxRiseRate = StockUtils.getChangeRate(firstPrice, maxPrice);
		statisticStock.setMaxDeclineRate(maxDeclineRate);
		statisticStock.setMaxRiseRate(maxRiseRate);
	}
	
	/**
	 * �����Ʊ��������ڵ��ǵ���
	 *
	 */
	private void calculateLastDateChangeRate(ExtStatisticStock statisticStock, AllImportStock firstImportStock, AllImportStock lastImportStock) {
		//�ǵ���
		double firstPrice = firstImportStock.getCurrent().doubleValue();
		double nowPrice = lastImportStock.getCurrent();
		double nowChangeRate = StockUtils.getChangeRate(firstPrice, nowPrice);
		statisticStock.setNowChangeRate(nowChangeRate);
	}
	
	/**
	 * �����Ʊ������ڵ�����ǵ���
	 * 
	 */
	private void calculateRecentMinMaxChangeRate(ExtStatisticStock statisticStock, Double[] minMaxPrices, AllImportStock lastImportStock) {
		//������ڵ�����ǵ���
		double minPrice = minMaxPrices[0].doubleValue();
		double maxPrice = minMaxPrices[1].doubleValue();
		double nowPrice = lastImportStock.getCurrent();
		double recentMaxDeclineRate = StockUtils.getChangeRate(maxPrice, nowPrice);
		double recentMaxRiseRate = StockUtils.getChangeRate(minPrice, nowPrice);
		statisticStock.setRecentMaxDeclineRate(recentMaxDeclineRate);
		statisticStock.setRecentMaxRiseRate(recentMaxRiseRate);
	}
}
