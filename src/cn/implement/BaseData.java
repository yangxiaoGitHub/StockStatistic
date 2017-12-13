package cn.implement;

import cn.db.AllDetailStockDao;
import cn.db.AllInformationStockDao;
import cn.db.AllStockDao;
import cn.db.DailyStockDao;
import cn.db.DetailStockDao;
import cn.db.HistoryStockDao;
import cn.db.InformationStockDao;
import cn.db.OperationDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticStockDao;

public abstract class BaseData {

	protected DailyStockDao dailyStockDao = null;
	protected StatisticStockDao statisticStockDao = null;
	protected OriginalStockDao originalStockDao = null;
	protected AllDetailStockDao allDetailStockDao = null;
	protected AllInformationStockDao allInformationStockDao = null;
	protected AllStockDao allStockDao = null;
	protected DetailStockDao detailStockDao = null;
	protected InformationStockDao informationStockDao = null;
	protected HistoryStockDao historyStockDao = null;
	
	protected void closeAllDao() {
		if (dailyStockDao != null) dailyStockDao = null;
		if (statisticStockDao != null) statisticStockDao = null;
		if (originalStockDao != null) originalStockDao = null;
		if (allDetailStockDao != null) allDetailStockDao = null;
		if (allInformationStockDao != null) allInformationStockDao = null;
		if (allStockDao != null) allStockDao = null;
		if (detailStockDao != null) detailStockDao = null;
		if (informationStockDao != null) informationStockDao = null;
		if (historyStockDao != null) historyStockDao = null;
	}

	protected abstract void closeDao(OperationDao... daoList);
}
