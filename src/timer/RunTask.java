package timer;

import java.util.TimerTask;

import cn.implement.GetDailyAndAllStockDetailData;

public class RunTask extends TimerTask {

	@Override
	public void run() {
		try {
			GetDailyAndAllStockDetailData detail = new GetDailyAndAllStockDetailData();
			// ��ȡͳ�ƹ�Ʊ����ϸ��Ϣ
			detail.getStockDetailData();
			// ��ȡ���й�Ʊ����ϸ��Ϣ
			detail.getAllStockInformationData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
