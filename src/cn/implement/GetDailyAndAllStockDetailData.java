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
			System.out.println("--------------------ͳ�ƹ�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.Date2String(new Date()) + ")-----------------------");
			log.loger.info("--------------------ͳ�ƹ�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.Date2String(new Date()) + ")-----------------------");
			for (StatisticStock stock : statisticList) {
				String stockCode = stock.getStockCode();
				if (searchStockDate != null) {
					boolean existFlg = informationStockDao.isExistInInformationStock(stockCode, searchStockDate);
					if (existFlg) continue;
				}
				System.out.print(++lineNum + "�� ");
				// ʹ���߳�ִ�г�ʱ����
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
					//�����Ʊ������
					calculateTurnoverRate(detailStock);
					boolean saveFlg = detailStockDao.saveOrUpdateDetailStock(detailStock);
					if (saveFlg) ++detailSaveNum;
					String message = "�޲�����";
					if (saveUpdateFlg!=0 && saveFlg) message = "����ɹ���";
					System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")" + message);
				} else {
					if (CommonUtils.isBlank(PropertiesUtils.getProperty(stockCode))) {
						System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")���У�");
						log.loger.info(" " + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")���У�");
					} else {
						System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")ͣ�̣�");
						log.loger.info(" " + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")ͣ�̣�");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao, statisticStockDao, informationStockDao, detailStockDao);
			System.out.println("��ȡͳ�ƹ�Ʊ��Ϣ�������ӳɹ�����Ϊ: " + requestSuccess);
			log.loger.info("��ȡͳ�ƹ�Ʊ��Ϣ�������ӳɹ�����Ϊ: " + requestSuccess);
			System.out.println("ͳ�ƹ�Ʊ��Ϣ��(information_stock_)��������" + infoSaveNum + "����¼��");
			log.loger.info("ͳ�ƹ�Ʊ��Ϣ��(information_stock_)��������" + infoSaveNum + "����¼��");
			System.out.println("ͳ�ƹ�Ʊ��ϸ��Ϣ��(detail_stock_)��������" + detailSaveNum + "����¼��");
			log.loger.info("ͳ�ƹ�Ʊ��ϸ��Ϣ��(detail_stock_)��������" + detailSaveNum + "����¼��");
			if (infoUpdateNum != 0) {
				System.out.println("ͳ�ƹ�Ʊ��ϸ��Ϣ��(information_stock_)�и�����" + infoUpdateNum + "����¼��");
				log.loger.info("ͳ�ƹ�Ʊ��ϸ��Ϣ��(information_stock_)�и�����" + infoUpdateNum + "����¼��");
			}
			long endTime = System.currentTimeMillis();
			System.out.println("��ȡͳ�ƹ�Ʊ��Ϣ��ʱ: " + DateUtils.msecToTime(endTime-startTime));
		}
	}

	/**
	 * ��ȡ���й�Ʊ����ϸ��Ϣ
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
			System.out.println("--------------------���й�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.Date2String(new Date()) + ")-----------------------");
			log.loger.info("--------------------���й�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.Date2String(new Date()) + ")-----------------------");
			for (int index=0; index<allStockList.size(); index++) {
				AllStock stock = allStockList.get(index);
				String stockCode = stock.getStockCode();
				if (searchStockDate != null) {
					boolean existInAllInformationStockFlg = allInformationStockDao.isExistInAllInformationStock(stockCode, searchStockDate);
					boolean existInAllDetailStockFlg = allDetailStockDao.isExistInAllDetailStock(stockCode, searchStockDate);
					if (existInAllInformationStockFlg && existInAllDetailStockFlg) continue;
				}
				System.out.print(++lineNum + "�� ");
				// ʹ���߳�ִ�г�ʱ����
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
					//�����Ʊ������
					calculateTurnoverRate(allDetailStock);
					boolean saveFlg = allDetailStockDao.saveOrUpdateAllDetailStock(allDetailStock);
					if (saveFlg) ++detailSaveNum;
					String message = "�޲�����";
					if (saveUpdateFlg!=0 && saveFlg) message = "����ɹ���";
					System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")" + message);
				} else {
					if (CommonUtils.isBlank(PropertiesUtils.getProperty(stockCode))) {
						System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")���У�");
						log.loger.info(" " + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")���У�");
					} else {
						System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")ͣ�̣�");
						log.loger.info(" " + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")ͣ�̣�");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao, allInformationStockDao, allDetailStockDao);
			System.out.println("��ȡ���й�Ʊ��Ϣ�������ӳɹ�����Ϊ: " + requestSuccess);
			log.loger.info("��ȡ���й�Ʊ��Ϣ�������ӳɹ�����Ϊ: " + requestSuccess);
			System.out.println("���й�Ʊ��Ϣ��(all_information_stock_)��������" + infoSaveNum + "����¼��");
			log.loger.info("���й�Ʊ��Ϣ��(all_information_stock_)��������" + infoSaveNum + "����¼��");
			System.out.println("���й�Ʊ��ϸ��Ϣ��(all_detail_stock_)��������" + detailSaveNum + "����¼��");
			log.loger.info("���й�Ʊ��ϸ��Ϣ��(all_detail_stock_)��������" + detailSaveNum + "����¼��");
			if (infoUpdateNum != 0) {
				System.out.println("���й�Ʊ��ϸ��Ϣ��(detail_stock_)�и�����" + infoUpdateNum + "����¼��");
				log.loger.info("���й�Ʊ��ϸ��Ϣ��(detail_stock_)�и�����" + infoUpdateNum + "����¼��");
			}
			long endTime = System.currentTimeMillis();
			System.out.println("��ȡ���й�Ʊ��Ϣ��ʱ: " + DateUtils.msecToTime(endTime-startTime));
		}
	}

/*	private static Double getTwoDecimal(double changeRate) {
		BigDecimal bigValue = new BigDecimal(changeRate);  
		double dValue = bigValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return dValue;
	}*/
}