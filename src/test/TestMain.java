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
			System.out.println("--------------------���й�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.dateToString(new Date()) + ")-----------------------");
			log.loger.info("--------------------���й�Ʊ��ȡ��ϸ��Ϣ(" + DateUtils.dateToString(new Date()) + ")-----------------------");
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
					// System.out.print(++lineNum + "�� ");
					// ʹ���߳�ִ�г�ʱ����
					// String stockInfo = CommonUtils.getRequestByTimeOut(stockCode);
					String stockInfo = getStockInfoByStockCode(startStockDate, stockCode);
					if (stockInfo != null) {
						++requestSuccess;
						firstTime = new Date();
						System.out.println(requestSuccess + "--1-->���й�Ʊ��ϸ��Ϣ��(all_information_stock_)�л�ȡ��Ʊ��ϸ��Ϣ��ʱ��: "
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
							System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")������Ч��");
							continue;
						}
						int saveUpdateFlg = allInformationStockTestDao.saveOrUpdateAllInformationStock(stockInfoArray[30], stockCode, stockInfo);
						if (saveUpdateFlg == 1) {
							++infoSaveNum;
							secondTime = new Date();
							System.out.println(infoSaveNum + "--2-->���й�Ʊԭʼ������Ϣ���Ա�(all_information_stock_test_)���ӹ�Ʊ(" + stockCode + ")��ʱ��: "
									+ DateUtils.msecToTime(secondTime.getTime() - firstTime.getTime()));
						} else if (saveUpdateFlg == 2) {
							++infoUpdateNum;
						}
						// �����Ʊ������
						calculateTurnoverRate(allDetailStockTest);
						// saveList.add(AllDetailStockTest);
						boolean saveFlg = allDetailStockTestDao.saveOrUpdateAllDetailStockTest(allDetailStockTest);
						if (saveFlg) {
							++detailSaveNum;
							thirdTime = new Date();
							System.out.println(detailSaveNum + "--3-->���й�Ʊ��ϸ��Ϣ���Ա�(all_detail_stock_test_)���ӹ�Ʊ(" + stockCode + ")��ʱ��: "
									+ DateUtils.msecToTime(thirdTime.getTime() - secondTime.getTime()));
						}
						// String message = "�޲�����";
						// if (saveUpdateFlg != 0 && saveFlg)
						//    message = "����ɹ���";
						// if (saveUpdateFlg != 0)
						// message = "����ɹ���";
						// System.out.println("----->" + stockInfoArray[30] + " ��Ʊ(" + stockCode + ")" + message);
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
				startStockDate = DateUtils.addOneDay(startStockDate);
			}

			/* for (AllDetailStockTest AllDetailStockTest : saveList) { boolean
			 * saveFlg =
			 * allDetailStockDao.saveOrUpdateAllDetailStock(AllDetailStockTest);
			 * if (saveFlg) { ++detailSaveNum;
			 * System.out.println(DateUtils.DateTimeToString(new Date()) + "  "
			 * + detailSaveNum + "---���й�Ʊ��ϸ��Ϣ��(all_detail_stock_)������" +
			 * DateUtils.Date2String(AllDetailStockTest.getStockDate()) + "�Ĺ�Ʊ"
			 * + AllDetailStockTest.getStockCode() + "(" +
			 * AllDetailStockTest.getStockName() + ")"); } } */
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(allStockDao, allInformationStockDao, allInformationStockTestDao, allDetailStockTestDao);
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
		// �����ǵ���
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