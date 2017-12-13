package cn.implement;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllInformationStockDao;
import cn.db.AllStockDao;
import cn.db.DetailStockDao;
import cn.db.HistoryStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllInformationStock;
import cn.db.bean.AllStock;
import cn.db.bean.BaseStock;
import cn.db.bean.DetailStock;
import cn.db.bean.HistoryStock;

public class HandleDetailStockData extends OperationData {

	/**
	 * 增加所有股票详细信息表(all_detail_stock_)中的记录(2017-01-01)
	 * 
	 */
	public void addAllDetailStockFromHistoryStock() {

		allStockDao = new AllStockDao();
		allDetailStockDao = new AllDetailStockDao();
		historyStockDao = new HistoryStockDao();
		int detailSaveNum = 0;
		try {
			Date startTime = new Date();
			Long lineNum = allDetailStockDao.getMaxNumFromAllDetailStock();
			System.out.println("批量增加所有股票详细信息表(all_detail_stock_)开始时间：" + DateUtils.DateTimeToString(startTime));
			Date startDate = DateUtils.String2Date("2017-09-27");
			Date endDate = DateUtils.String2Date("2017-11-06");
			while (startDate.compareTo(endDate) < 0) {
				List<HistoryStock> historyStockList = historyStockDao.getHistoryStockByStockDate(startDate);
				Map<String, ? extends BaseStock> historyStockMap = getHistoryStockMapByPreStockDate(startDate);
				for (HistoryStock historyStock : historyStockList) {
					String stockCode = historyStock.getStockCode();
					String stockName = PropertiesUtils.getProperty(stockCode);
					if (CommonUtils.isBlank(stockName)) continue;
					Date stockDate = historyStock.getStockDate();
					boolean existFlg = allDetailStockDao.isExistInAllDetailStock(stockCode, stockDate);
					if (existFlg) continue;
					//if (existFlg && CommonUtils.isBlank(stockName)) System.out.println(lineNum + "---stock_code_：" + stockCode + " stock_date_：" + stockDate);
					AllDetailStock allDetailStock = new AllDetailStock(stockCode, stockDate);
					allDetailStock.setNum(++lineNum);
					String stockCodeDES = DESUtils.encryptToHex(stockCode);
					allDetailStock.setStockCodeDES(stockCodeDES);
					allDetailStock.setStockName(stockName);
					allDetailStock.setTodayOpen(historyStock.getOpenPrice());
					if (historyStockMap!=null && historyStockMap.get(stockCode)!=null) {
						HistoryStock preStock = (HistoryStock) historyStockMap.get(historyStock.getStockCode());
						allDetailStock.setYesterdayClose(preStock.getClosePrice());
					}
					allDetailStock.setCurrent(historyStock.getClosePrice());
					allDetailStock.setTodayHigh(historyStock.getHighPrice());
					allDetailStock.setTodayLow(historyStock.getLowPrice());
					allDetailStock.setTradedStockNumber(historyStock.getTradedStockNumber());
					allDetailStock.setTradedAmount(historyStock.getTradedAmount());
					allDetailStock.setInputTime(new Date());
					//计算涨跌幅
					this.calculateStockChangeRate(allDetailStock);
					//计算换手率
					this.calculateTurnoverRate(allDetailStock);
					//System.out.println("---stock_code_:" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ") stock_date_:" + DateUtils.Date2String(allDetailStock.getStockDate()) + " 涨跌幅:" + allDetailStock.getChangeRate() + "% 换手率:" + allDetailStock.getTurnoverRate() + "%");
					boolean saveFlg = allDetailStockDao.saveAllDetailStock(allDetailStock);
					if (saveFlg) {
						++detailSaveNum;
						System.out.println(detailSaveNum + "---" + DateUtils.DateTimeToString(new Date()) + " 所有股票详细信息表(all_detail_stock_)增加了" + DateUtils.Date2String(allDetailStock.getStockDate()) + "的股票" + allDetailStock.getStockCode() + "(" + PropertiesUtils.getProperty(allDetailStock.getStockCode()) + ")！");
					}
				}
				startDate = DateUtils.addOneDay(startDate);
			}
			Date endTime = new Date();
			System.out.println("批量增加所有股票详细信息表(all_detail_stock_)结束时间：" + DateUtils.DateTimeToString(endTime));
			System.out.println("批量增加所有股票详细信息耗时: " + DateUtils.msecToTime(endTime.getTime()-startTime.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allDetailStockDao, historyStockDao);
			System.out.println(DateUtils.DateTimeToString(new Date()) + " 所有股票详细信息表(all_detail_stock_)批量增加了" + detailSaveNum + "条记录！");
			log.loger.info(" 所有股票详细信息表(all_detail_stock_)批量增加了" + detailSaveNum + "条记录！");
		}
	}

	/**
	 * 解析所有股票信息表(all_information_stock_)中的股票信息到所有股票详细信息表(all_detail_stock_)中(一般不用)
	 * 
	 */
	public void handleAllStockDetail() {
		
		allStockDao = new AllStockDao();
		allInformationStockDao = new AllInformationStockDao();
		allDetailStockDao = new AllDetailStockDao();
		int detailSaveNum = 0;
		try {
			Date startTime = new Date();
			System.out.println("批量增加所有股票详细信息表(all_detail_stock_)开始时间：" + DateUtils.DateTimeToString(startTime));
			Date startDate = DateUtils.String2Date("2017-12-07");
			Date nowTime = new Date();
			while (startDate.compareTo(nowTime) <= 0) {
				List<AllInformationStock> allInformationStockList = allInformationStockDao.getAllInformationStockByStockDate(startDate);
				for (AllInformationStock stock : allInformationStockList) {
					String stockInfo = stock.getStockInfo();
					String[] stockInfoArray = stockInfo.split(",");
					AllDetailStock allDetailStock = getDetailStockFromArray(stockInfoArray);
					//计算股票换手率
					calculateTurnoverRate(allDetailStock);
					boolean saveFlg = allDetailStockDao.saveOrUpdateAllDetailStock(allDetailStock);
					if (saveFlg) {
						++detailSaveNum;
						System.out.println(detailSaveNum + "---" + DateUtils.DateTimeToString(new Date()) + " 股票详细信息表(all_detail_stock_)增加了" + DateUtils.Date2String(stock.getStockDate()) + "的股票" + stock.getStockCode() + "(" + PropertiesUtils.getProperty(stock.getStockCode()) + ")！");
					}
				}
				startDate = DateUtils.addOneDay(startDate);
			}
			Date endTime = new Date();
			System.out.println("批量增加所有股票详细信息表(all_detail_stock_)结束时间：" + DateUtils.DateTimeToString(endTime));
			System.out.println("批量增加所有股票详细信息耗时: " + DateUtils.msecToTime(endTime.getTime()-startTime.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao, allInformationStockDao, allDetailStockDao);
			System.out.println("所有股票详细信息表(all_detail_stock_)批量增加了" + detailSaveNum + "条记录！");
			log.loger.info("所有股票详细信息表(all_detail_stock_)批量增加了" + detailSaveNum + "条记录！");
		}
	}
	
	/**
	 * 计算每日所有股票(all_detail_stock_)的换手率
	 * 
	 */
	public void handleAllDetailStockTurnoverRate() {

		int updateNum = 0;
		long lineNum = 0;
		allStockDao = new AllStockDao();
		allDetailStockDao = new AllDetailStockDao();
		try {
			List<AllStock> allStockList = allStockDao.listAllStock();
			Date startDate = DateUtils.String2Date(DateUtils.startDateInAllDetailStock);
			Date nowTime = new Date();
			while (startDate.compareTo(nowTime) <= 0) {
				List<AllDetailStock> allDetailStockList = allDetailStockDao.getAllDetailStockByStockDate(startDate);
				for (AllDetailStock allDetailStock : allDetailStockList) {
					Double oldTurnoverRate = allDetailStock.getTurnoverRate();
					if (oldTurnoverRate!=null && oldTurnoverRate!=0) continue;
					String stockCode = allDetailStock.getStockCode();
					AllStock allStock = getAllStockByStockCode(stockCode, allStockList);
					Long circulationStock = allStock.getCirculationStockComplex();
					if (circulationStock==null || circulationStock==0) continue; 
					Long tradedStockNum = allDetailStock.getTradedStockNumber();
					Double newTurnoverRate = (tradedStockNum.doubleValue()/circulationStock.doubleValue())*100;
					newTurnoverRate = new BigDecimal(newTurnoverRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					String turnoverRateDES = DESUtils.encryptToHex(newTurnoverRate.toString());
					boolean updateFlg = allDetailStockDao.updateTurnoverRate(allDetailStock, newTurnoverRate, turnoverRateDES);
					if (updateFlg) updateNum++;
					System.out.println(++lineNum + "---" + allDetailStock.getStockDate() + " 股票" + allDetailStock.getStockCode() + "(" + PropertiesUtils.getProperty(allDetailStock.getStockCode()) + ")的换手率为：" + newTurnoverRate + "%");
				}
				startDate = DateUtils.addOneDay(startDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao, allDetailStockDao);
			System.out.println(DateUtils.DateTimeToString(new Date()) + " 所有股票详细信息表(all_detail_stock_)中更新了" + updateNum + "条记录的换手率！");
			log.loger.info(" 所有股票详细信息表(all_detail_stock_)中更新了" + updateNum + "条记录的换手率！");
		}
	}
	
	/**
	 * 计算每日选择股票(detail_stock_)的换手率
	 * 
	 */
	public void handDetailStockTurnoverRate() {
		
		int updateNum = 0;
		long lineNum = 0;
		allStockDao = new AllStockDao();
		detailStockDao = new DetailStockDao();
		try {
			List<AllStock> allStockList = allStockDao.listAllStock();
			Date startDate = DateUtils.String2Date(DateUtils.startDateInDetailStock);
			Date nowTime = new Date();
			while (startDate.compareTo(nowTime) <= 0) {
				List<DetailStock> detailStockList = detailStockDao.getDetailStockByStockDate(startDate);
				for (DetailStock detailStock : detailStockList) {
					Double oldTurnoverRate = detailStock.getTurnoverRate();
					if (oldTurnoverRate!=null && oldTurnoverRate!=0) continue;
					String stockCode = detailStock.getStockCode();
					AllStock allStock = getAllStockByStockCode(stockCode, allStockList);
					Long circulationStock = allStock.getCirculationStockComplex();
					if (circulationStock==null || circulationStock==0) continue; 
					Long tradedStockNum = detailStock.getTradedStockNumber();
					Double newTurnoverRate = (tradedStockNum.doubleValue()/circulationStock.doubleValue())*100;
					newTurnoverRate = new BigDecimal(newTurnoverRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					String turnoverRateDES = DESUtils.encryptToHex(newTurnoverRate.toString());
					boolean updateFlg = detailStockDao.updateTurnoverRate(detailStock, newTurnoverRate, turnoverRateDES);
					if (updateFlg) updateNum++;
					System.out.println(++lineNum + "---" + detailStock.getStockDate() + " 股票" + detailStock.getStockCode() + "(" + PropertiesUtils.getProperty(detailStock.getStockCode()) + ")的换手率为：" + newTurnoverRate + "%");
				}
				startDate = DateUtils.addOneDay(startDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao, detailStockDao);
			System.out.println(DateUtils.DateTimeToString(new Date()) + " 每日选择股票详细信息表(detail_stock_)中更新了" + updateNum + "条记录的换手率！");
			log.loger.info(" 每日选择股票详细信息表(detail_stock_)中更新了" + updateNum + "条记录的换手率！");
		}
	}

	private Map<String, ? extends BaseStock> getHistoryStockMapByPreStockDate(Date stockDate) throws SQLException {
		
		Date preDate = stockDate;
		List<HistoryStock> historyStockList = null;
		for (int index=0; index<DataUtils.CONSTANT_LONG_DAY; index++) {
			preDate = DateUtils.minusOneDay(preDate);
			historyStockList = historyStockDao.getHistoryStockByStockDate(preDate);
			if (historyStockList.size() > 0) break;
		}
		if (historyStockList.size() > 0) return CommonUtils.convertListToMap(historyStockList);
		else return null;
	}

	private AllStock getAllStockByStockCode(String stockCode, List<AllStock> allStockList) {
		
		AllStock allStock = null;
		for (AllStock stock : allStockList) {
			String code = stock.getStockCode();
			if (code.equals(stockCode)) {
				allStock = stock;
				break;
			}
		}
		return allStock;
	}
}
