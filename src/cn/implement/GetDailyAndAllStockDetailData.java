package cn.implement;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DateUtils;
import cn.com.MD5Utils;
import cn.com.PropertiesUtils;
import cn.com.StockUtils;
import cn.db.AllDetailStockDao;
import cn.db.AllInformationStockDao;
import cn.db.AllStockDao;
import cn.db.DetailStockDao;
import cn.db.InformationStockDao;
import cn.db.StatisticStockDao;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllInformationStock;
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
			log.loger.warn("--------------------ͳ�ƹ�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.dateToString(new Date()) + ")-----------------------");
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
				if (!CommonUtils.validateStockData(stockInfoArray[33], stockInfoArray[0], Double.valueOf(stockInfoArray[1]))) {
					System.out.println("------>" + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������Ч��");
					continue;
				}
				long tradedStockNumber = Long.valueOf(stockInfoArray[8]).longValue();
				float tradedAmount = Float.valueOf(stockInfoArray[9]).floatValue();
				if (tradedStockNumber != 0 && tradedAmount != 0) {
					String message = "";
					int saveUpdateFlg = informationStockDao.saveOrUpdateInformationStock(stockInfoArray[30], stockCode, stockInfo);
					if (saveUpdateFlg != 0) {
						if (saveUpdateFlg == 1) {
							++infoSaveNum;
							message = "��(information_stock_)����ɹ���";
						} else {
							++infoUpdateNum;
							message = "��(information_stock_)���³ɹ���";
						}
					} else {
						message = "��(information_stock_)�޲�����";
					}
					DetailStock detailStock = StockUtils.getDetailStockFromArray(stockInfoArray);
					AllStock allStock = allStockDao.getAllStockByStockCode(detailStock.getStockCode());
					// �����Ʊ������
					StockUtils.calculateTurnoverRate(detailStock, allStock);
					boolean saveFlg = detailStockDao.saveOrUpdateDetailStock(detailStock);
					if (saveFlg) {
						++detailSaveNum;
						message += "��(detail_stock_)����ɹ���";
					} else {
						message += "��(detail_stock_)�޲�����";
					}
					System.out.println("------>" + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")��" + message);
				} else {
					if (CommonUtils.isBlank(PropertiesUtils.getProperty(stockCode))) {
						System.out.println("------>" + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")���У�");
						log.loger.warn(" " + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")���У�");
					} else {
						System.out.println("------>" + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")ͣ�̣�");
						log.loger.warn(" " + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")ͣ�̣�");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(allStockDao, statisticStockDao, informationStockDao, detailStockDao);
			System.out.println("��ȡͳ�ƹ�Ʊ��Ϣ�������ӳɹ�����Ϊ: " + requestSuccess);
			log.loger.warn("��ȡͳ�ƹ�Ʊ��Ϣ�������ӳɹ�����Ϊ: " + requestSuccess);
			System.out.println("ͳ�ƹ�Ʊ��Ϣ��(information_stock_)��������" + infoSaveNum + "����¼��");
			log.loger.warn("ͳ�ƹ�Ʊ��Ϣ��(information_stock_)��������" + infoSaveNum + "����¼��");
			System.out.println("ͳ�ƹ�Ʊ��ϸ��Ϣ��(detail_stock_)��������" + detailSaveNum + "����¼��");
			log.loger.warn("ͳ�ƹ�Ʊ��ϸ��Ϣ��(detail_stock_)��������" + detailSaveNum + "����¼��");
			if (infoUpdateNum != 0) {
				System.out.println("ͳ�ƹ�Ʊ��ϸ��Ϣ��(information_stock_)�и�����" + infoUpdateNum + "����¼��");
				log.loger.warn("ͳ�ƹ�Ʊ��ϸ��Ϣ��(information_stock_)�и�����" + infoUpdateNum + "����¼��");
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
			Long maxNumInAllInformationStock = allInformationStockDao.getMaxNumFromAllInformationStock();
			Long maxNumInAllDetailStock = allDetailStockDao.getMaxNumFromAllDetailStock();
			System.out.println("--------------------���й�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			log.loger.warn("--------------------���й�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			for (AllStock stock : allStockList) {
				String stockCode = stock.getStockCode();
				System.out.print(++lineNum + "�� ");
				// ʹ���߳�ִ�г�ʱ����
				String stockInfo = CommonUtils.getRequestByTimeOut(stockCode);
				if (!CommonUtils.isBlank(stockInfo)) ++requestSuccess;
				else continue;
				String[] stockInfoArray = stockInfo.split(",");
				if (!CommonUtils.validateStockData(stockInfoArray[33], stockInfoArray[0], Double.valueOf(stockInfoArray[1]))) {
					System.out.println("------>" + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")������Ч��");
					continue;
				}
				long tradedStockNumber = Long.valueOf(stockInfoArray[8]).longValue();
				float tradedAmount = Float.valueOf(stockInfoArray[9]).floatValue();
				if (tradedStockNumber != 0 && tradedAmount != 0) {
					String message = "";
					AllInformationStock allInformationStock = getAllInformationStockFormStockInfo(++maxNumInAllInformationStock, stockInfoArray[30], stockCode, stockInfo);
					int saveUpdateFlg = allInformationStockDao.saveOrUpdateAllInformationStock(allInformationStock);
					// �ֿ�ѭ������ʱ���
					/*if (saveUpdateFlg != 0) {
						message = saveUpdateFlg==1?"����ɹ���":"���³ɹ���";
						// �����Ʊ������
						calculateTurnoverRate(allDetailStock);
						allDetailStockList.add(allDetailStock);
					} else {
						message = "�޲�����";
					}*/
					if (saveUpdateFlg != 0) {
						if (saveUpdateFlg == 1) {
							++infoSaveNum;
							message = "��(all_information_stock_)����ɹ���";
						} else {
							--maxNumInAllInformationStock;
							++infoUpdateNum;
							message = "��(all_information_stock_)���³ɹ���";
						}
					} else {
						--maxNumInAllInformationStock;
						message = "��(all_information_stock_)�޲�����";
					}
					AllDetailStock allDetailStock = StockUtils.getDetailStockFromArray(stockInfoArray);
					AllStock allStock = allStockDao.getAllStockByStockCode(allDetailStock.getStockCode());
					// �����Ʊ������
					StockUtils.calculateTurnoverRate(allDetailStock, allStock);
					allDetailStock.setNum(++maxNumInAllDetailStock);
					boolean saveFlg = allDetailStockDao.saveOrUpdateAllDetailStock(allDetailStock);
					if (saveFlg) {
						++detailSaveNum;
						message += "��(all_detail_stock_)����ɹ���";
					} else {
						--maxNumInAllDetailStock;
						message += "��(all_detail_stock_)�޲�����";
					}
					System.out.println("------>" + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")��" + message);
				} else {
					if (CommonUtils.isBlank(PropertiesUtils.getProperty(stockCode))) {
						System.out.println("------>" + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")���У�");
						log.loger.warn(" " + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")���У�");
					} else {
						System.out.println("------>" + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")ͣ�̣�");
						log.loger.warn(" " + stockInfoArray[30] + "�Ĺ�Ʊ" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")ͣ�̣�");
					}
				}
			}
			// �ֿ�ѭ������AllInformationStock��AllDetailStock
			/*for (int index=0; index<allDetailStockList.size(); index++) {
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
				System.out.println(DateUtils.dateTimeToString(new Date()) + "------>" + detailSaveNum + ": " + DateUtils.dateToString(allDetailStock.getStockDate()) + " ��Ʊ(" + allDetailStock.getStockCode() + ") " + message);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(allStockDao, allInformationStockDao, allDetailStockDao);
			System.out.println("��ȡ���й�Ʊ��Ϣ�������ӳɹ�����Ϊ: " + requestSuccess);
			log.loger.warn("��ȡ���й�Ʊ��Ϣ�������ӳɹ�����Ϊ: " + requestSuccess);
			System.out.println("���й�Ʊ��Ϣ��(all_information_stock_)��������" + infoSaveNum + "����¼��");
			log.loger.warn("���й�Ʊ��Ϣ��(all_information_stock_)��������" + infoSaveNum + "����¼��");
			System.out.println("���й�Ʊ��ϸ��Ϣ��(all_detail_stock_)��������" + detailSaveNum + "����¼��");
			log.loger.warn("���й�Ʊ��ϸ��Ϣ��(all_detail_stock_)��������" + detailSaveNum + "����¼��");
			if (infoUpdateNum != 0) {
				System.out.println("���й�Ʊ��ϸ��Ϣ��(detail_stock_)�и�����" + infoUpdateNum + "����¼��");
				log.loger.warn("���й�Ʊ��ϸ��Ϣ��(detail_stock_)�и�����" + infoUpdateNum + "����¼��");
			}
			long endTime = System.currentTimeMillis();
			System.out.println("��ȡ���й�Ʊ��Ϣ��ʱ: " + DateUtils.msecToTime(endTime - startTime));
		}
	}

	private AllInformationStock getAllInformationStockFormStockInfo(Long num, String sDate, String stockCode, String stockInfo) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		Date stockDate = DateUtils.stringToDate(sDate);
		AllInformationStock allInformationStock = new AllInformationStock(stockCode, stockDate);
		allInformationStock.setNum(num);
		allInformationStock.setStockInfo(stockInfo);
		String stockInfoDES = DESUtils.encryptToHex(stockInfo);
		String stockInfoMD5 = MD5Utils.getEncryptedPwd(stockInfo);
		allInformationStock.setStockInfoDES(stockInfoDES);
		allInformationStock.setStockInfoMD5(stockInfoMD5);
		allInformationStock.setInputTime(new Date());
		return allInformationStock;
	}
}