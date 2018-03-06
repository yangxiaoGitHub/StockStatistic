package cn.implement;

import cn.db.AllDetailStockDao;
import cn.db.AllImportStockDao;
import cn.db.AllInformationStockDao;
import cn.db.AllStockDao;
import cn.db.DailyStockDao;
import cn.db.DetailStockDao;
import cn.db.HistoryStockDao;
import cn.db.InformationStockDao;
import cn.db.OperationDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.StatisticStockDao;

public abstract class BaseData {

	protected DailyStockDao dailyStockDao = null;
	protected StatisticStockDao statisticStockDao = null;
	protected StatisticDetailStockDao statisticDetailStockDao = null;
	protected OriginalStockDao originalStockDao = null;
	protected AllDetailStockDao allDetailStockDao = null;
	protected AllInformationStockDao allInformationStockDao = null;
	protected AllStockDao allStockDao = null;
	protected DetailStockDao detailStockDao = null;
	protected InformationStockDao informationStockDao = null;
	protected HistoryStockDao historyStockDao = null;
	protected AllImportStockDao allImportStockDao = null;

	protected void closeAllDao() {
		if (dailyStockDao != null) {
			dailyStockDao.close();
			dailyStockDao = null;
		}
		if (statisticStockDao != null) {
			statisticStockDao.close();
			statisticStockDao = null;
		}
		if (statisticDetailStockDao != null) {
			statisticDetailStockDao.close();
			statisticDetailStockDao = null;
		}
		if (originalStockDao != null) {
			originalStockDao.close();
			originalStockDao = null;
		}
		if (allDetailStockDao != null) {
			allDetailStockDao.close();
			allDetailStockDao = null;
		}
		if (allInformationStockDao != null) {
			allInformationStockDao.close();
			allInformationStockDao = null;
		}
		if (allStockDao != null) {
			allStockDao.close();
			allStockDao = null;
		}
		if (detailStockDao != null) {
			detailStockDao.close();
			detailStockDao = null;
		}
		if (informationStockDao != null) {
			informationStockDao.close();
			informationStockDao = null;
		}
		if (historyStockDao != null) {
			historyStockDao.close();
			historyStockDao = null;
		}
		if (allImportStockDao != null) {
			allImportStockDao.close();
			allImportStockDao = null;
		}
	}

	protected abstract void closeDao(OperationDao... daoList);
}
