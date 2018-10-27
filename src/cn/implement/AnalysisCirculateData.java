package cn.implement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.db.AllImportStockDao;
import cn.db.bean.AllImportStock;
import cn.db.bean.ext.ExtAllImportStock;
import cn.db.bean.ext.ExtDailyStock;

public class AnalysisCirculateData extends OperationData {

	/**
	 * 统计一段时间内所有导入股票(all_import_stock_)的流通市值
	 *
	 */
	public void analysisAllStockCirculate(String startDate) {
		
		allImportStockDao = new AllImportStockDao();
		try{
			Date nowDate = new Date();
			Date beginDate = DateUtils.monthToDate(startDate);
			Map<String, List<Double>> monthCirculateMap = new LinkedHashMap<String, List<Double>>();
			while (beginDate.compareTo(nowDate) <= 0) {
				int year = DateUtils.getYearFromDate(beginDate);
				int month = DateUtils.getMonthFromDate(beginDate);
				Date firstDateOfMonth = DateUtils.getFirstDayOfMonth(year, month);
				Date lastDateOfMonth = DateUtils.getLastDayOfMonth(year, month);
				List<AllImportStock> importStockList = allImportStockDao.getAllImportStockByStockDate(new Date[]{firstDateOfMonth, lastDateOfMonth});
				//除去流通市值为0的记录
				List<AllImportStock> allImportStockList = new ArrayList<AllImportStock>();
				for (AllImportStock importStock:importStockList) {
					Long circulateValue = importStock.getCirculateValue();
					if (circulateValue != 0) allImportStockList.add(importStock); 
				}
				Map<String, AllImportStock> averageStockMap = getAverageCircualte(allImportStockList);
				int allStockNum = averageStockMap.size()!=0?averageStockMap.size():-1;
				Map<String, List<String>> circulateMap = new HashMap<String, List<String>>();
				for (AllImportStock allImportStock : averageStockMap.values()) {
					String stockCode = allImportStock.getStockCode();
					Long averageCirculate = allImportStock.getCirculateValue();
					calculateCirculateValue(stockCode, averageCirculate, circulateMap);
				}
				List<Double> circualteList = new ArrayList<Double>();
				caculateCircualateList(allStockNum, circulateMap, circualteList);
				
				monthCirculateMap.put(DateUtils.dateTimeToString(beginDate, DateUtils.YEAR_MONTH_FORMAT), circualteList);
				beginDate = DateUtils.addOneMonth(beginDate);
			}
			printMonthCirculateRate(monthCirculateMap);
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(allImportStockDao);
		}
	}

	private void caculateCircualateList(int allStockNum, Map<String, List<String>> circulateMap, List<Double> circualteList) {
		
		//1亿-50亿
		List<String> oneHmToFiftyHmList = circulateMap.get(ExtAllImportStock.ONE_TO_FIFTY);
		int oneHmToFiftyHmNum = oneHmToFiftyHmList!=null?oneHmToFiftyHmList.size():0;
		Double oneHmToFiftyHmRate = DataUtils.div(Double.valueOf(oneHmToFiftyHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(oneHmToFiftyHmRate);
		//50亿-100亿
		List<String> fiftyHmToOneHundredHmList = circulateMap.get(ExtAllImportStock.FIFTY_TO_ONE_HUNDRED);
		int fiftyHmToOneHundredHmNum = fiftyHmToOneHundredHmList!=null?fiftyHmToOneHundredHmList.size():0;
		Double fiftyHmToOneHundredHmRate = DataUtils.div(Double.valueOf(fiftyHmToOneHundredHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(fiftyHmToOneHundredHmRate);
		//100亿-300亿
		List<String> oneHundredHmToThreeHundredHmList = circulateMap.get(ExtAllImportStock.ONE_HUNDRED_TO_THREE_HUNDRED);
		int oneHundredHmToThreeHundredHmNum = oneHundredHmToThreeHundredHmList!=null?oneHundredHmToThreeHundredHmList.size():0;
		Double oneHundredHmToThreeHundredHmRate = DataUtils.div(Double.valueOf(oneHundredHmToThreeHundredHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(oneHundredHmToThreeHundredHmRate);
		//300亿-600亿
		List<String> threeHundredHmToSixHundredHmList = circulateMap.get(ExtAllImportStock.THREE_HUNDRED_TO_SIX_HUNDRED);
		int threeHundredHmToSixHundredHmNum = threeHundredHmToSixHundredHmList!=null?threeHundredHmToSixHundredHmList.size():0;
		Double threeHundredHmToSixHundredHmRate = DataUtils.div(Double.valueOf(threeHundredHmToSixHundredHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(threeHundredHmToSixHundredHmRate);
		//600亿-1000亿
		List<String> sixHundredHmToOneThousandHmList = circulateMap.get(ExtAllImportStock.SIX_HUNDRED_TO_ONE_THOUSAND);
		int sixHundredHmToOneThousandHmNum = sixHundredHmToOneThousandHmList!=null?sixHundredHmToOneThousandHmList.size():0;
		Double sixHundredHmToOneThousandHmRate = DataUtils.div(Double.valueOf(sixHundredHmToOneThousandHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(sixHundredHmToOneThousandHmRate);
		//1000亿-4000亿
		List<String> oneThousandHmToFourThousandHmList = circulateMap.get(ExtAllImportStock.ONE_THOUSAND_TO_FOUR_THOUSAND);
		int oneThousandHmToFourThousandHmNum = oneThousandHmToFourThousandHmList!=null?oneThousandHmToFourThousandHmList.size():0;
		Double oneThousandHmToFourThousandHmRate = DataUtils.div(Double.valueOf(oneThousandHmToFourThousandHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(oneThousandHmToFourThousandHmRate);
		//4000亿-7000亿
		List<String> fourThousandHmToSevenThousandHmList = circulateMap.get(ExtAllImportStock.FOUR_THOUSAND_TO_SEVEN_THOUSAND );
		int fourThousandHmToSevenThousandHmNum = fourThousandHmToSevenThousandHmList!=null?fourThousandHmToSevenThousandHmList.size():0;
		Double fourThousandHmToSevenThousandHmRate = DataUtils.div(Double.valueOf(fourThousandHmToSevenThousandHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(fourThousandHmToSevenThousandHmRate);
		//7000亿-13000亿
		List<String> sevenThousandHmToThirteenThousandHmList = circulateMap.get(ExtAllImportStock.SEVEN_THOUSAND_TO_THIRTEEN_THOUSAND);
		int sevenThousandHmToThirteenThousandHmNum = sevenThousandHmToThirteenThousandHmList!=null?sevenThousandHmToThirteenThousandHmList.size():0;
		Double sevenThousandHmToThirteenThousandHmRate = DataUtils.div(Double.valueOf(sevenThousandHmToThirteenThousandHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(sevenThousandHmToThirteenThousandHmRate);
		//13000亿-18000亿
		List<String> thirteenThousandHmToEighteenThousandHmList = circulateMap.get(ExtAllImportStock.THIRTEEN_THOUSAND_TO_EIGHTEEN_THOUSAND);
		int thirteenThousandHmToEighteenThousandHmNum = thirteenThousandHmToEighteenThousandHmList!=null?thirteenThousandHmToEighteenThousandHmList.size():0;
		Double thirteenThousandHmToEighteenThousandHmRate = DataUtils.div(Double.valueOf(thirteenThousandHmToEighteenThousandHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(thirteenThousandHmToEighteenThousandHmRate);
		//18000亿以上
		List<String> greaterEighteenThousandHmList = circulateMap.get(ExtAllImportStock.GREATER_EIGHTEEN_THOUSAND);
		int greaterEighteenThousandHmNum = greaterEighteenThousandHmList!=null?greaterEighteenThousandHmList.size():0;
		Double greaterEighteenThousandHmRate = DataUtils.div(Double.valueOf(greaterEighteenThousandHmNum*100), Double.valueOf(allStockNum), DataUtils._INT_ONE).doubleValue();
		circualteList.add(greaterEighteenThousandHmRate);
	}
	
	private void printMonthCirculateRate(Map<String, List<Double>> monthCirculateMap) {

		System.out.println("-------------------------------每月股票流通市值统计分析--------------------------------");
		log.loger.warn("-------------------------------每月股票流通市值统计分析--------------------------------");
		String lineTitle = "   时间                 0-10     10-28      18-26     26-35    35-45    45-58    58-80    80-110    110-250    250以上";
		System.out.println(lineTitle);
		log.loger.warn(lineTitle);
		String lineText = "time        fifty     oneHu     threeHu     sixHu     oneThou     fourThou      sevenThou      thirteenTh      eighteenTh      largerEigh";
		Iterator<Entry<String, List<Double>>> it = monthCirculateMap.entrySet().iterator();
		while (it.hasNext()) {
		   Entry<String, List<Double>> entry = it.next();
		   String lineContent = lineText;
		   List<Double> valueList = entry.getValue();
		   lineContent = lineContent.replace("time", entry.getKey());
		   lineContent = lineContent.replace("fifty", valueList.get(0) + "%");
		   lineContent = lineContent.replace("oneHu", valueList.get(1) + "%");
		   lineContent = lineContent.replace("threeHu", valueList.get(2) + "%");
		   lineContent = lineContent.replace("sixHu", valueList.get(3) + "%");
		   lineContent = lineContent.replace("oneThou", valueList.get(4) + "%");
		   lineContent = lineContent.replace("fourThou", valueList.get(5) + "%");
		   lineContent = lineContent.replace("sevenThou", valueList.get(6) + "%");
		   lineContent = lineContent.replace("thirteenTh", valueList.get(7) + "%");
		   lineContent = lineContent.replace("eighteenTh", valueList.get(8) + "%");
		   lineContent = lineContent.replace("largerEigh", valueList.get(9) + "%");
		   System.out.println(lineContent);
		   log.loger.warn(lineContent);
		}
	}

	/**
	 * 计算所有导入股票一个月内的日平均流通市值
	 *
	 */
	private Map<String, AllImportStock> getAverageCircualte(List<AllImportStock> allImportStockList) {
		
		Map<String, AllImportStock> stockMap = new HashMap<String, AllImportStock>();
		long num = 0;
		// calculate the sum of circulate rate and the count of same stockCode
		for (AllImportStock stock : allImportStockList) {
			num++;
			String stockCode = stock.getStockCode();
			Long circulateValue = stock.getCirculateValue();
			if (stockMap.containsKey(stockCode)) {
				AllImportStock mapStock = stockMap.get(stockCode);
				Long mapCirculateValue = mapStock.getCirculateValue();
				long circulate = DataUtils.add(mapCirculateValue, circulateValue).longValue();
				mapStock.setCirculateValue(circulate);
				long count = mapStock.getNum();
				mapStock.setNum(++count);
				stockMap.put(stockCode, mapStock);
			} else {
				AllImportStock newStock = new AllImportStock();
				newStock.setStockCode(stockCode);
				newStock.setNum(Long.valueOf(DataUtils._INT_ONE));
				newStock.setCirculateValue(circulateValue);
				stockMap.put(stockCode, newStock);
			}
		}
		// calculate the average circulate value
		for (AllImportStock stock : stockMap.values()) {
			Long circulate = stock.getCirculateValue();
			long count = stock.getNum();
			Long averageCirculate = DataUtils.div(circulate, count, DataUtils._INT_ZERO).longValue();
			stock.setCirculateValue(averageCirculate);
		}
		return stockMap;
	}
	
	/**
	 * 计算一段时间内，流通市值在一定范围内的股票
	 *
	 */
	private void calculateCirculateValue(String stockCode, Long averageCirculate, Map<String, List<String>> circulateMap) {
		
		String circulateFlg = CommonUtils.getCirculateFlg(averageCirculate);
		switch(circulateFlg) {
		case ExtAllImportStock.ONE_TO_FIFTY:
			if (circulateMap.containsKey(ExtAllImportStock.ONE_TO_FIFTY)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.ONE_TO_FIFTY);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.ONE_TO_FIFTY, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.ONE_TO_FIFTY, stockCodeList);
			}
			break;
		case ExtAllImportStock.FIFTY_TO_ONE_HUNDRED:
			if (circulateMap.containsKey(ExtAllImportStock.FIFTY_TO_ONE_HUNDRED)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.FIFTY_TO_ONE_HUNDRED);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.FIFTY_TO_ONE_HUNDRED, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.FIFTY_TO_ONE_HUNDRED, stockCodeList);
			}
			break;
		case ExtAllImportStock.ONE_HUNDRED_TO_THREE_HUNDRED:
			if (circulateMap.containsKey(ExtAllImportStock.ONE_HUNDRED_TO_THREE_HUNDRED)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.ONE_HUNDRED_TO_THREE_HUNDRED);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.ONE_HUNDRED_TO_THREE_HUNDRED, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.ONE_HUNDRED_TO_THREE_HUNDRED, stockCodeList);
			}
			break;
		case ExtAllImportStock.THREE_HUNDRED_TO_SIX_HUNDRED:
			if (circulateMap.containsKey(ExtAllImportStock.THREE_HUNDRED_TO_SIX_HUNDRED)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.THREE_HUNDRED_TO_SIX_HUNDRED);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.THREE_HUNDRED_TO_SIX_HUNDRED, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.THREE_HUNDRED_TO_SIX_HUNDRED, stockCodeList);
			}
			break;
		case ExtAllImportStock.SIX_HUNDRED_TO_ONE_THOUSAND:
			if (circulateMap.containsKey(ExtAllImportStock.SIX_HUNDRED_TO_ONE_THOUSAND)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.SIX_HUNDRED_TO_ONE_THOUSAND);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.SIX_HUNDRED_TO_ONE_THOUSAND, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.SIX_HUNDRED_TO_ONE_THOUSAND, stockCodeList);
			}
			break;
		case ExtAllImportStock.ONE_THOUSAND_TO_FOUR_THOUSAND:
			if (circulateMap.containsKey(ExtAllImportStock.ONE_THOUSAND_TO_FOUR_THOUSAND)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.ONE_THOUSAND_TO_FOUR_THOUSAND);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.ONE_THOUSAND_TO_FOUR_THOUSAND, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.ONE_THOUSAND_TO_FOUR_THOUSAND, stockCodeList);
			}
			break;
		case ExtAllImportStock.FOUR_THOUSAND_TO_SEVEN_THOUSAND:
			if (circulateMap.containsKey(ExtAllImportStock.FOUR_THOUSAND_TO_SEVEN_THOUSAND)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.FOUR_THOUSAND_TO_SEVEN_THOUSAND);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.FOUR_THOUSAND_TO_SEVEN_THOUSAND, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.FOUR_THOUSAND_TO_SEVEN_THOUSAND, stockCodeList);
			}
			break;
		case ExtAllImportStock.SEVEN_THOUSAND_TO_THIRTEEN_THOUSAND:
			if (circulateMap.containsKey(ExtAllImportStock.SEVEN_THOUSAND_TO_THIRTEEN_THOUSAND)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.SEVEN_THOUSAND_TO_THIRTEEN_THOUSAND);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.SEVEN_THOUSAND_TO_THIRTEEN_THOUSAND, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.SEVEN_THOUSAND_TO_THIRTEEN_THOUSAND, stockCodeList);
			}
			break;
		case ExtAllImportStock.THIRTEEN_THOUSAND_TO_EIGHTEEN_THOUSAND:
			if (circulateMap.containsKey(ExtAllImportStock.THIRTEEN_THOUSAND_TO_EIGHTEEN_THOUSAND)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.THIRTEEN_THOUSAND_TO_EIGHTEEN_THOUSAND);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.THIRTEEN_THOUSAND_TO_EIGHTEEN_THOUSAND, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.THIRTEEN_THOUSAND_TO_EIGHTEEN_THOUSAND, stockCodeList);
			}
			break;
		case ExtAllImportStock.GREATER_EIGHTEEN_THOUSAND:
			if (circulateMap.containsKey(ExtAllImportStock.GREATER_EIGHTEEN_THOUSAND)) {
				List<String> stockCodeList = circulateMap.get(ExtAllImportStock.GREATER_EIGHTEEN_THOUSAND);
				List<String> codeList = new ArrayList<String>(stockCodeList);
				codeList.add(stockCode);
				circulateMap.put(ExtAllImportStock.GREATER_EIGHTEEN_THOUSAND, codeList);
			} else {
				List<String> stockCodeList = Arrays.asList(stockCode);
				circulateMap.put(ExtAllImportStock.GREATER_EIGHTEEN_THOUSAND, stockCodeList);
			}
			break;
		default:
			System.out.println("换手率标识值：" + circulateFlg + " 不正确！");
			break;
		}
	}
}
