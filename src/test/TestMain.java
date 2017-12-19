package test;

import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.AllDetailStockTestDao;
import cn.db.AllInformationStockDao;
import cn.db.AllInformationStockTestDao;
import cn.db.AllStockDao;
import cn.db.bean.AllDetailStockTest;
import cn.db.bean.AllInformationStock;
import cn.db.bean.AllStock;
import cn.implement.OperationData;
import cn.log.Log;

public class TestMain extends OperationData {
	Log log = Log.getLoger();

	public static void main(String[] args) {

		TestMain testMain = new TestMain();
		try {
			testMain.getAllStockInformationData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAllStockInformationData() {

		int infoSaveNum = 0;
		int infoUpdateNum = 0;
		int detailSaveNum = 0;
		int requestSuccess = 0;
		long startTime = System.currentTimeMillis();
		try {
			int lineNum = 0;
			allStockDao = new AllStockDao(); // AllStockDao.getInstance();
			allInformationStockDao = new AllInformationStockDao();
			allInformationStockTestDao = new AllInformationStockTestDao();
			allDetailStockTestDao = new AllDetailStockTestDao();
			// Date searchStockDate = null;
			Date startStockDate = DateUtils.stringToDate("2017-12-01");
			Date endStockDate = DateUtils.stringToDate("2017-12-15");
			List<AllStock> allStockList = allStockDao.listAllStock();
			Date firstTime = new Date();
			Date secondTime = new Date();
			Date thirdTime = new Date();
			System.out.println("--------------------所有股票获取详细信息(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			log.loger.info("--------------------所有股票获取详细信息(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			while (startStockDate.compareTo(endStockDate) <= 0) {
				// for (int index = 0; index < allStockList.size(); index++) {
				//	   AllStock stock = allStockList.get(index);
				for (AllStock stock : allStockList) {
					String stockCode = stock.getStockCode();
					/*if (searchStockDate != null) {
						boolean existInAllInformationStockFlg = allInformationStockTestDao.isExistInAllInformationStock(stockCode, searchStockDate);
						boolean existInAllDetailStockFlg = allDetailStockTestDao.isExistInAllDetailStockTest(stockCode, searchStockDate);
						if (existInAllInformationStockFlg && existInAllDetailStockFlg)
							continue;
					}*/
					// System.out.print(++lineNum + "： ");
					// 使用线程执行超时处理
					// String stockInfo = CommonUtils.getRequestByTimeOut(stockCode);
					String stockInfo = getStockInfoByStockCode(startStockDate, stockCode);
					if (stockInfo != null) {
						++requestSuccess;
						firstTime = new Date();
						System.out.println(requestSuccess + "--1-->所有股票详细信息表(all_information_stock_)中获取股票详细信息的时间: "
								+ DateUtils.msecToTime(firstTime.getTime() - thirdTime.getTime()));
					} else {
						continue;
					}
					String[] stockInfoArray = stockInfo.split(",");
					/*if (searchStockDate == null)
						searchStockDate = DateUtils.StringToDate(stockInfoArray[30]);*/
					long tradedStockNumber = Long.valueOf(stockInfoArray[8]).longValue();
					float tradedAmount = Float.valueOf(stockInfoArray[9]).floatValue();
					if (tradedStockNumber != 0 && tradedAmount != 0) {
						AllDetailStockTest allDetailStockTest = getDetailStockTestFromArray(stockInfoArray);
						if (!validateStockData(allDetailStockTest)) {
							System.out.println("----->" + stockInfoArray[30] + " 股票(" + stockCode + ")数据无效！");
							continue;
						}
						int saveUpdateFlg = allInformationStockTestDao.saveOrUpdateAllInformationStock(stockInfoArray[30], stockCode, stockInfo);
						if (saveUpdateFlg == 1) {
							++infoSaveNum;
							secondTime = new Date();
							System.out.println(infoSaveNum + "--2-->所有股票原始数据信息测试表(all_information_stock_test_)增加股票(" + stockCode + ")的时间: "
									+ DateUtils.msecToTime(secondTime.getTime() - firstTime.getTime()));
						} else if (saveUpdateFlg == 2) {
							++infoUpdateNum;
						}
						// 计算股票换手率
						calculateTurnoverRate(allDetailStockTest);
						// saveList.add(AllDetailStockTest);
						boolean saveFlg = allDetailStockTestDao.saveOrUpdateAllDetailStockTest(allDetailStockTest);
						if (saveFlg) {
							++detailSaveNum;
							thirdTime = new Date();
							System.out.println(detailSaveNum + "--3-->所有股票详细信息测试表(all_detail_stock_test_)增加股票(" + stockCode + ")的时间: "
									+ DateUtils.msecToTime(thirdTime.getTime() - secondTime.getTime()));
						}
						// String message = "无操作！";
						// if (saveUpdateFlg != 0 && saveFlg)
						//    message = "保存成功！";
						// if (saveUpdateFlg != 0)
						// message = "保存成功！";
						// System.out.println("----->" + stockInfoArray[30] + " 股票(" + stockCode + ")" + message);
					} else {
						if (CommonUtils.isBlank(PropertiesUtils.getProperty(stockCode))) {
							System.out.println("----->" + stockInfoArray[30] + " 股票(" + stockCode + ")退市！");
							log.loger.info(" " + stockInfoArray[30] + " 股票(" + stockCode + ")退市！");
						} else {
							System.out.println("----->" + stockInfoArray[30] + " 股票(" + stockCode + ")停盘！");
							log.loger.info(" " + stockInfoArray[30] + " 股票(" + stockCode + ")停盘！");
						}
					}
				}
				startStockDate = DateUtils.addOneDay(startStockDate);
			}

			/* for (AllDetailStockTest AllDetailStockTest : saveList) { boolean
			 * saveFlg =
			 * allDetailStockDao.saveOrUpdateAllDetailStock(AllDetailStockTest);
			 * if (saveFlg) { ++detailSaveNum;
			 * System.out.println(DateUtils.DateTimeToString(new Date()) + "  "
			 * + detailSaveNum + "---所有股票详细信息表(all_detail_stock_)增加了" +
			 * DateUtils.Date2String(AllDetailStockTest.getStockDate()) + "的股票"
			 * + AllDetailStockTest.getStockCode() + "(" +
			 * AllDetailStockTest.getStockName() + ")"); } } */
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao, allInformationStockDao, allInformationStockTestDao, allDetailStockTestDao);
			System.out.println("获取所有股票信息请求连接成功次数为: " + requestSuccess);
			log.loger.info("获取所有股票信息请求连接成功次数为: " + requestSuccess);
			System.out.println("所有股票信息表(all_information_stock_)中增加了" + infoSaveNum + "条记录！");
			log.loger.info("所有股票信息表(all_information_stock_)中增加了" + infoSaveNum + "条记录！");
			System.out.println("所有股票详细信息表(all_detail_stock_)中增加了" + detailSaveNum + "条记录！");
			log.loger.info("所有股票详细信息表(all_detail_stock_)中增加了" + detailSaveNum + "条记录！");
			if (infoUpdateNum != 0) {
				System.out.println("所有股票详细信息表(detail_stock_)中更新了" + infoUpdateNum + "条记录！");
				log.loger.info("所有股票详细信息表(detail_stock_)中更新了" + infoUpdateNum + "条记录！");
			}
			long endTime = System.currentTimeMillis();
			System.out.println("获取所有股票信息耗时: " + DateUtils.msecToTime(endTime - startTime));
		}
	}

	private AllDetailStockTest getDetailStockTestFromArray(String[] stockInfoArray) {

		AllDetailStockTest detailStock = new AllDetailStockTest();
		detailStock.setStockName(stockInfoArray[0]);
		detailStock.setTodayOpen(Double.valueOf(stockInfoArray[1]));
		detailStock.setYesterdayClose(Double.valueOf(stockInfoArray[2]));
		detailStock.setCurrent(Double.valueOf(stockInfoArray[3]));
		detailStock.setTodayHigh(Double.valueOf(stockInfoArray[4]));
		detailStock.setTodayLow(Double.valueOf(stockInfoArray[5]));
		detailStock.setTradedStockNumber(Long.valueOf(stockInfoArray[8]));
		detailStock.setTradedAmount(Float.valueOf(stockInfoArray[9]));
		detailStock.setStockDate(DateUtils.stringToDate(stockInfoArray[30]));
		detailStock.setTradedTime(DateUtils.stringToDateTime(stockInfoArray[30] + " " + stockInfoArray[31]));
		detailStock.setStockCode(stockInfoArray[33]);
		String stockCodeDES = DESUtils.encryptToHex(stockInfoArray[33]);
		detailStock.setStockCodeDES(stockCodeDES);
		// 计算涨跌幅
		calculateStockChangeRate(detailStock);
		return detailStock;
	}

	private String getStockInfoByStockCode(Date stockDate, String stockCode) throws Exception {

		String stockInfo = null;
		AllInformationStock allInfomationStock = allInformationStockDao.getAllInformationStockByKey(stockDate, stockCode);
		if (allInfomationStock != null) {
			stockInfo = allInfomationStock.getStockInfo();
		}
		return stockInfo;
	}
}