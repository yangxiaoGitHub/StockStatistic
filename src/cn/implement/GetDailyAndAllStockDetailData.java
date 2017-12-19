package cn.implement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllInformationStockDao;
import cn.db.AllStockDao;
import cn.db.DetailStockDao;
import cn.db.InformationStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllStock;
import cn.db.bean.DetailStock;
import cn.db.bean.StatisticStock;

public class GetDailyAndAllStockDetailData extends OperationData {

	public void getStockDetailData() {

		int detailSaveNum = 0;
		int infoSaveNum = 0;
		int infoUpdateNum = 0;
		int requestSuccess = 0;
		long startTime = System.currentTimeMillis();
		try {
			int lineNum = 0;
			Date searchStockDate = null;
			allStockDao = new AllStockDao(); //AllStockDao.getInstance();
			statisticStockDao = new StatisticStockDao();
			informationStockDao = new InformationStockDao();
			detailStockDao = new DetailStockDao();
			List<StatisticStock> statisticList = statisticStockDao.listStatisticStock();
			System.out.println("--------------------统计股票获取详细信息(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			log.loger.info("--------------------统计股票获取详细信息(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			for (StatisticStock stock : statisticList) {
				String stockCode = stock.getStockCode();
				if (searchStockDate != null) {
					boolean existFlg = informationStockDao.isExistInInformationStock(stockCode, searchStockDate);
					if (existFlg)
						continue;
				}
				System.out.print(++lineNum + "： ");
				// 使用线程执行超时处理
				String stockInfo = CommonUtils.getRequestByTimeOut(stockCode);
				if (stockInfo != null)
					++requestSuccess;
				else
					continue;
				String[] stockInfoArray = stockInfo.split(",");
				if (searchStockDate == null)
					searchStockDate = DateUtils.stringToDate(stockInfoArray[30]);
				long tradedStockNumber = Long.valueOf(stockInfoArray[8]).longValue();
				float tradedAmount = Float.valueOf(stockInfoArray[9]).floatValue();
				if (tradedStockNumber != 0 && tradedAmount != 0) {
					DetailStock detailStock = getDetailStockFromArray(stockInfoArray);
					if (!validateStockData(detailStock)) {
						System.out.println("----->" + stockInfoArray[30] + " 股票(" + stockCode + ")数据无效！");
						continue;
					}
					int saveUpdateFlg = informationStockDao.saveOrUpdateInformationStock(stockInfoArray[30], stockCode, stockInfo);
					if (saveUpdateFlg == 1)
						++infoSaveNum;
					else if (saveUpdateFlg == 2)
						++infoUpdateNum;
					// 计算股票换手率
					calculateTurnoverRate(detailStock);
					boolean saveFlg = detailStockDao.saveOrUpdateDetailStock(detailStock);
					if (saveFlg)
						++detailSaveNum;
					String message = "无操作！";
					if (saveUpdateFlg != 0 && saveFlg)
						message = "保存成功！";
					System.out.println("----->" + stockInfoArray[30] + " 股票(" + stockCode + ")" + message);
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
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao, statisticStockDao, informationStockDao, detailStockDao);
			System.out.println("获取统计股票信息请求连接成功次数为: " + requestSuccess);
			log.loger.info("获取统计股票信息请求连接成功次数为: " + requestSuccess);
			System.out.println("统计股票信息表(information_stock_)中增加了" + infoSaveNum + "条记录！");
			log.loger.info("统计股票信息表(information_stock_)中增加了" + infoSaveNum + "条记录！");
			System.out.println("统计股票详细信息表(detail_stock_)中增加了" + detailSaveNum + "条记录！");
			log.loger.info("统计股票详细信息表(detail_stock_)中增加了" + detailSaveNum + "条记录！");
			if (infoUpdateNum != 0) {
				System.out.println("统计股票详细信息表(information_stock_)中更新了" + infoUpdateNum + "条记录！");
				log.loger.info("统计股票详细信息表(information_stock_)中更新了" + infoUpdateNum + "条记录！");
			}
			long endTime = System.currentTimeMillis();
			System.out.println("获取统计股票信息耗时: " + DateUtils.msecToTime(endTime - startTime));
		}
	}

	/**
	 * 获取所有股票的详细信息
	 * 
	 */
	public void getAllStockInformationData() {

		int infoSaveNum = 0;
		int infoUpdateNum = 0;
		int detailSaveNum = 0;
		int requestSuccess = 0;
		long startTime = System.currentTimeMillis();
		try {
			int lineNum = 0;
			allStockDao = new AllStockDao(); //AllStockDao.getInstance();
			allInformationStockDao = new AllInformationStockDao();
			allDetailStockDao = new AllDetailStockDao(); //AllDetailStockDao.getInstance();
			List<AllStock> allStockList = allStockDao.listAllStock();
			List<AllDetailStock> allDetailStockList = new ArrayList<AllDetailStock>();
			System.out.println("--------------------所有股票获取详细信息(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			log.loger.info("--------------------所有股票获取详细信息(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			for (AllStock stock : allStockList) {
				String stockCode = stock.getStockCode();
				System.out.print(++lineNum + "： ");
				// 使用线程执行超时处理
				String stockInfo = CommonUtils.getRequestByTimeOut(stockCode);
				if (!CommonUtils.isBlank(stockInfo)) ++requestSuccess;
				else continue;
				String[] stockInfoArray = stockInfo.split(",");
				long tradedStockNumber = Long.valueOf(stockInfoArray[8]).longValue();
				float tradedAmount = Float.valueOf(stockInfoArray[9]).floatValue();
				if (tradedStockNumber != 0 && tradedAmount != 0) {
					AllDetailStock allDetailStock = getDetailStockFromArray(stockInfoArray);
					if (!validateStockData(allDetailStock)) {
						System.out.println("----->" + stockInfoArray[30] + " 股票(" + stockCode + ")数据无效！");
						continue;
					}
					String message = "";
					int saveUpdateFlg = allInformationStockDao.saveOrUpdateAllInformationStock(stockInfoArray[30], stockCode, stockInfo);
					// 分开循环保存时添加
					if (saveUpdateFlg != 0) {
						message = saveUpdateFlg==1?"保存成功！":"更新成功！";
						// 计算股票换手率
						calculateTurnoverRate(allDetailStock);
						allDetailStockList.add(allDetailStock);
					} else {
						message = "无操作！";
					}
					
					/*if (saveUpdateFlg == 1) {
						++infoSaveNum;
						message = "所有股票原始信息表(all_information_stock_)保存";
					} else if (saveUpdateFlg == 2) {
						++infoUpdateNum;
						message = "所有股票原始信息表(all_information_stock_)更新";
					}
					// 计算股票换手率
					calculateTurnoverRate(allDetailStock);
					boolean saveFlg = allDetailStockDao.saveOrUpdateAllDetailStock(allDetailStock);
					if (saveFlg) {
						++detailSaveNum;
						if (!message.equals(DataUtils.CONSTANT_BLANK)) {
							message += ", ";
						}
						message += "所有股票详细信息表(all_detail_stock_)保存";
					}
					
					if (!message.equals(DataUtils.CONSTANT_BLANK)) {
						message += "成功！";
					} else {
						message = "无操作！";
					}*/
					System.out.println("----->" + stockInfoArray[30] + " 股票(" + stockCode + ")在所有股票原始信息表(all_information_stock_)中" + message);
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
			// 分开循环保存AllInformationStock和AllDetailStock
			for (int index=0; index<allDetailStockList.size(); index++) {
				String message = "";
				AllDetailStock allDetailStock = allDetailStockList.get(index);
				boolean saveFlg = allDetailStockDao.saveOrUpdateAllDetailStock(allDetailStock);
				if (saveFlg) {
					++detailSaveNum;
					if (!message.equals(DataUtils.CONSTANT_BLANK)) {
						message += ", ";
					}
					message += "所有股票详细信息表(all_detail_stock_)保存";
				}
				if (!message.equals(DataUtils.CONSTANT_BLANK)) {
					message += "成功！";
				} else {
					message = "无操作！";
				}
				System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + detailSaveNum + ": " + DateUtils.dateToString(allDetailStock.getStockDate()) + " 股票(" + allDetailStock.getStockCode() + ") " + message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao, allInformationStockDao, allDetailStockDao);
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
}