package cn.implement;

import java.util.Date;

import cn.db.DailyStockDao;
import cn.log.Log;

public class ComData extends OperationData {
	Log log = Log.getLoger();
	
	public Date getRecentDateFromDailyStock() {

		Date[] minMaxDate = new Date[2];
		dailyStockDao = new DailyStockDao();
		try {
			minMaxDate = dailyStockDao.getMinMaxDate();
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			closeDao(dailyStockDao);
		}
		return minMaxDate[1];
	}
}