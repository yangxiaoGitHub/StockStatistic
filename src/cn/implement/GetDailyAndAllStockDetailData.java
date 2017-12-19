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
			System.out.println("--------------------ͳ�ƹ�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			log.loger.info("--------------------ͳ�ƹ�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			for (StatisticStock stock : statisticList) {
				String stockCode = stock.getStockCode();
				if (searchStockDate != null) {
					boolean existFlg = informationStockDao.isExistInInformationStock(stockCode, searchStockDate);
					if (existFlg)
						continue;
				}
				System.out.print(++lineNum + "�� ");
				// ʹ���߳�ִ�г�ʱ����
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
						System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")������Ч��");
						continue;
					}
					int saveUpdateFlg = informationStockDao.saveOrUpdateInformationStock(stockInfoArray[30], stockCode, stockInfo);
					if (saveUpdateFlg == 1)
						++infoSaveNum;
					else if (saveUpdateFlg == 2)
						++infoUpdateNum;
					// �����Ʊ������
					calculateTurnoverRate(detailStock);
					boolean saveFlg = detailStockDao.saveOrUpdateDetailStock(detailStock);
					if (saveFlg)
						++detailSaveNum;
					String message = "�޲�����";
					if (saveUpdateFlg != 0 && saveFlg)
						message = "����ɹ���";
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
			System.out.println("��ȡͳ�ƹ�Ʊ��Ϣ��ʱ: " + DateUtils.msecToTime(endTime - startTime));
		}
	}

	/**
	 * ��ȡ���й�Ʊ����ϸ��Ϣ
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
			System.out.println("--------------------���й�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			log.loger.info("--------------------���й�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			for (AllStock stock : allStockList) {
				String stockCode = stock.getStockCode();
				System.out.print(++lineNum + "�� ");
				// ʹ���߳�ִ�г�ʱ����
				String stockInfo = CommonUtils.getRequestByTimeOut(stockCode);
				if (!CommonUtils.isBlank(stockInfo)) ++requestSuccess;
				else continue;
				String[] stockInfoArray = stockInfo.split(",");
				long tradedStockNumber = Long.valueOf(stockInfoArray[8]).longValue();
				float tradedAmount = Float.valueOf(stockInfoArray[9]).floatValue();
				if (tradedStockNumber != 0 && tradedAmount != 0) {
					AllDetailStock allDetailStock = getDetailStockFromArray(stockInfoArray);
					if (!validateStockData(allDetailStock)) {
						System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")������Ч��");
						continue;
					}
					String message = "";
					int saveUpdateFlg = allInformationStockDao.saveOrUpdateAllInformationStock(stockInfoArray[30], stockCode, stockInfo);
					// �ֿ�ѭ������ʱ���
					if (saveUpdateFlg != 0) {
						message = saveUpdateFlg==1?"����ɹ���":"���³ɹ���";
						// �����Ʊ������
						calculateTurnoverRate(allDetailStock);
						allDetailStockList.add(allDetailStock);
					} else {
						message = "�޲�����";
					}
					
					/*if (saveUpdateFlg == 1) {
						++infoSaveNum;
						message = "���й�Ʊԭʼ��Ϣ��(all_information_stock_)����";
					} else if (saveUpdateFlg == 2) {
						++infoUpdateNum;
						message = "���й�Ʊԭʼ��Ϣ��(all_information_stock_)����";
					}
					// �����Ʊ������
					calculateTurnoverRate(allDetailStock);
					boolean saveFlg = allDetailStockDao.saveOrUpdateAllDetailStock(allDetailStock);
					if (saveFlg) {
						++detailSaveNum;
						if (!message.equals(DataUtils.CONSTANT_BLANK)) {
							message += ", ";
						}
						message += "���й�Ʊ��ϸ��Ϣ��(all_detail_stock_)����";
					}
					
					if (!message.equals(DataUtils.CONSTANT_BLANK)) {
						message += "�ɹ���";
					} else {
						message = "�޲�����";
					}*/
					System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")�����й�Ʊԭʼ��Ϣ��(all_information_stock_)��" + message);
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
			// �ֿ�ѭ������AllInformationStock��AllDetailStock
			for (int index=0; index<allDetailStockList.size(); index++) {
				String message = "";
				AllDetailStock allDetailStock = allDetailStockList.get(index);
				boolean saveFlg = allDetailStockDao.saveOrUpdateAllDetailStock(allDetailStock);
				if (saveFlg) {
					++detailSaveNum;
					if (!message.equals(DataUtils.CONSTANT_BLANK)) {
						message += ", ";
					}
					message += "���й�Ʊ��ϸ��Ϣ��(all_detail_stock_)����";
				}
				if (!message.equals(DataUtils.CONSTANT_BLANK)) {
					message += "�ɹ���";
				} else {
					message = "�޲�����";
				}
				System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + detailSaveNum + ": " + DateUtils.dateToString(allDetailStock.getStockDate()) + " ��Ʊ(" + allDetailStock.getStockCode() + ") " + message);
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
			System.out.println("��ȡ���й�Ʊ��Ϣ��ʱ: " + DateUtils.msecToTime(endTime - startTime));
		}
	}
}