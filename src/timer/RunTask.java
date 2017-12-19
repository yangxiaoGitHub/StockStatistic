package timer;

import java.util.TimerTask;

import cn.implement.GetDailyAndAllStockDetailData;

public class RunTask extends TimerTask {

	@Override
	public void run() {
		try {
			GetDailyAndAllStockDetailData detail = new GetDailyAndAllStockDetailData();
			// 获取统计股票的详细信息
			detail.getStockDetailData();
			// 获取所有股票的详细信息
			detail.getAllStockInformationData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
