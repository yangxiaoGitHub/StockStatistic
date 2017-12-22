package cn;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cn.com.CommonUtils;
import cn.implement.AnalysisDailyAndDetailStockData;

public class StatisticMain {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			AnalysisDailyAndDetailStockData analysisDetailStockData = new AnalysisDailyAndDetailStockData();

			while(true) {
				System.out.println("请输入开始日期(格式:2000-01-01)：");
				String startDate = br.readLine();
				if (CommonUtils.isEnd(startDate)) break;

				if (CommonUtils.isBlank(startDate)) {
					System.out.println("输入的开始日期不能为空！");
					continue;
				}
				String message = CommonUtils.checkDate(startDate, null);
				if (message != null) {
					System.out.println(message + "！");
					continue;
				}
				try {
					analysisDetailStockData.analysisStock(startDate);
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
