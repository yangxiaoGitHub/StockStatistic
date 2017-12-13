package cn;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cn.com.CommonUtils;
import cn.implement.AnalysisDailyAndDetailStockData;

public class AnalysisMain {
	
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			AnalysisDailyAndDetailStockData analysis = new AnalysisDailyAndDetailStockData();

			while(true) {
				System.out.println("�����뿪ʼ����(��ʽ:2000-01-01)��");
				String startDate = br.readLine();
				if (CommonUtils.isEnd(startDate)) break;
				System.out.println("�������������(��ʽ:2000-01-01)��");
				String endDate = br.readLine();
				if (CommonUtils.isEnd(endDate)) break;
				System.out.println("�������ǵ���ʶ(0:��, 1:��, 2:ȫ��)��2");
				String changeFlg = br.readLine();
				if (CommonUtils.isEnd(changeFlg)) break;

				if (CommonUtils.isBlank(startDate) ||
					CommonUtils.isBlank(endDate)) {
					System.out.println("��������ڲ���Ϊ�գ�");
					continue;
				}
				String message = CommonUtils.checkDate(startDate, endDate);
				if (message != null) {
					System.out.println(message + "��");
					continue;
				}
				try {
					analysis.analysisStock(startDate, endDate, changeFlg);
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
