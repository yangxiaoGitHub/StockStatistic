package cn;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.implement.AnalysisDailyAndDetailStockData;

public class StatisticMain {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			AnalysisDailyAndDetailStockData analysisDetailStockData = new AnalysisDailyAndDetailStockData();

			while(true) {
				System.out.println("�����뿪ʼ����(��ʽ:2000-01-01)��");
				String startDate = br.readLine();
				if (CommonUtils.isEnd(startDate)) break;
				System.out.println("��ѡ�������ʶ(0:�ǵ���, 1:���������, 2:�������Ƿ�, 3:���ǵ�����, 4:�Ǵ���, 5:������)��0");
				String sortFlg = br.readLine();
				if (CommonUtils.isBlank(sortFlg)) sortFlg = "0";
				if (CommonUtils.isEnd(sortFlg)) break;
				
				if (CommonUtils.isBlank(startDate)) {
					System.out.println("����Ŀ�ʼ���ڲ���Ϊ�գ�");
					continue;
				}
				if (!CommonUtils.validateSortFlg(sortFlg, DataUtils.CONSTANT_INT_ZERO, DataUtils.CONSTANT_INT_FIVE)) {
					System.out.println("ѡ��������ʶ����ȷ:" + sortFlg + ", Ӧ��Ϊ:0��5");
					continue;
				}
				String message = DateUtils.checkDate(startDate, null);
				if (message != null) {
					System.out.println(message + "��");
					continue;
				}
				try {
					analysisDetailStockData.analysisStock(startDate, sortFlg);
				} catch (Exception ex) {
					ex.printStackTrace();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
