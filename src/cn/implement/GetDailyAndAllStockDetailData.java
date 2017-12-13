package cn.implement;

import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
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
import cn.log.Log;

public class GetDailyAndAllStockDetailData extends OperationData {
	Log log = Log.getLoger();
	
	public void getStockDetailData() {
		
		allStockDao = new AllStockDao();
		statisticStockDao = new StatisticStockDao();
		informationStockDao = new InformationStockDao();
		detailStockDao = new DetailStockDao();
		int detailSaveNum = 0;
		int infoSaveNum = 0;
		int infoUpdateNum = 0;
		int requestSuccess = 0;
		long startTime = System.currentTimeMillis();
		try {
			int lineNum = 0;
			Date searchStockDate = null;
			List<StatisticStock> statisticList = statisticStockDao.listStatisticStock();
			System.out.println("--------------------统计股票获取详细信息(" + DateUtils.Date2String(new Date()) + ")-----------------------");
			log.loger.info("--------------------统计股票获取详细信息(" + DateUtils.Date2String(new Date()) + ")-----------------------");
			for (StatisticStock stock : statisticList) {
				String stockCode = stock.getStockCode();
				if (searchStockDate != null) {
					boolean existFlg = informationStockDao.isExistInInformationStock(stockCode, searchStockDate);
					if (existFlg) continue;
				}
				System.out.print(++lineNum + "： ");
				// 使用线程执行超时处理
				String stockInfo = CommonUtils.getRequestByTimeOut(stockCode);
				if (stockInfo != null) ++requestSuccess; else continue;
				String[] stockInfoArray = stockInfo.split(",");
				if (searchStockDate == null) searchStockDate = DateUtils.String2Date(stockInfoArray[30]);
				long tradedStockNumber = Long.valueOf(stockInfoArray[8]).longValue();
				float tradedAmount = Float.valueOf(stockInfoArray[9]).floatValue();
				if (tradedStockNumber!=0 && tradedAmount!=0) {
					int saveUpdateFlg = informationStockDao.saveOrUpdateInformationStock(stockInfoArray[30], stockCode, stockInfo);
					if (saveUpdateFlg==1) ++infoSaveNum; else if (saveUpdateFlg==2) ++infoUpdateNum;
					DetailStock detailStock = getDetailStockFromArray(stockInfoArray);
					//计算股票换手率
					calculateTurnoverRate(detailStock);
					boolean saveFlg = detailStockDao.saveOrUpdateDetailStock(detailStock);
					if (saveFlg) ++detailSaveNum;
					String message = "无操作！";
					if (saveUpdateFlg!=0 && saveFlg) message = "保存成功！";
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
			System.out.println("获取统计股票信息耗时: " + DateUtils.msecToTime(endTime-startTime));
		}
	}

	/**
	 * 获取所有股票的详细信息
	 * 
	 */
	public void getAllStockInformationData() {
		allStockDao = new AllStockDao();
		allInformationStockDao = new AllInformationStockDao();
		allDetailStockDao = new AllDetailStockDao();
		int infoSaveNum = 0;
		int infoUpdateNum = 0;
		int detailSaveNum = 0;
		int requestSuccess = 0;
		long startTime = System.currentTimeMillis();
		try {
			int lineNum = 0;
			Date searchStockDate = null;
			List<AllStock> allStockList = allStockDao.listAllStock();
			System.out.println("--------------------所有股票获取详细信息(" + DateUtils.Date2String(new Date()) + ")-----------------------");
			log.loger.info("--------------------所有股票获取详细信息(" + DateUtils.Date2String(new Date()) + ")-----------------------");
			for (int index=0; index<allStockList.size(); index++) {
				AllStock stock = allStockList.get(index);
				String stockCode = stock.getStockCode();
				if (searchStockDate != null) {
					boolean existInAllInformationStockFlg = allInformationStockDao.isExistInAllInformationStock(stockCode, searchStockDate);
					boolean existInAllDetailStockFlg = allDetailStockDao.isExistInAllDetailStock(stockCode, searchStockDate);
					if (existInAllInformationStockFlg && existInAllDetailStockFlg) continue;
				}
				System.out.print(++lineNum + "： ");
				// 使用线程执行超时处理
				String stockInfo = CommonUtils.getRequestByTimeOut(stockCode);
				if (stockInfo != null) ++requestSuccess; else continue;
				String[] stockInfoArray = stockInfo.split(",");
				if (searchStockDate == null) searchStockDate = DateUtils.String2Date(stockInfoArray[30]);
				long tradedStockNumber = Long.valueOf(stockInfoArray[8]).longValue();
				float tradedAmount = Float.valueOf(stockInfoArray[9]).floatValue();
				if (tradedStockNumber!=0 && tradedAmount!=0) {
					int saveUpdateFlg = allInformationStockDao.saveOrUpdateAllInformationStock(stockInfoArray[30], stockCode, stockInfo);
					if (saveUpdateFlg==1) ++infoSaveNum; else if (saveUpdateFlg==2) ++infoUpdateNum;
					AllDetailStock allDetailStock = getDetailStockFromArray(stockInfoArray);
					//计算股票换手率
					calculateTurnoverRate(allDetailStock);
					boolean saveFlg = allDetailStockDao.saveOrUpdateAllDetailStock(allDetailStock);
					if (saveFlg) ++detailSaveNum;
					String message = "无操作！";
					if (saveUpdateFlg!=0 && saveFlg) message = "保存成功！";
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
			System.out.println("获取所有股票信息耗时: " + DateUtils.msecToTime(endTime-startTime));
		}
	}

/*	private static Double getTwoDecimal(double changeRate) {
		BigDecimal bigValue = new BigDecimal(changeRate);  
		double dValue = bigValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return dValue;
	}*/
}