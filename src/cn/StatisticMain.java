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
				System.out.println("请输入开始日期(格式:2000-01-01)：");
				String startDate = br.readLine();
				if (CommonUtils.isEnd(startDate)) break;
				System.out.println("请选择排序标识(0:涨跌幅, 1:最近最大跌幅, 2:最近最大涨幅, 3:总涨跌次数, 4:涨次数, 5:跌次数)：0");
				String sortFlg = br.readLine();
				if (CommonUtils.isBlank(sortFlg)) sortFlg = "0";
				if (CommonUtils.isEnd(sortFlg)) break;
				
				if (CommonUtils.isBlank(startDate)) {
					System.out.println("输入的开始日期不能为空！");
					continue;
				}
				if (!CommonUtils.validateSortFlg(sortFlg, DataUtils.CONSTANT_INT_ZERO, DataUtils.CONSTANT_INT_FIVE)) {
					System.out.println("选择的排序标识不正确:" + sortFlg + ", 应该为:0～5");
					continue;
				}
				String message = DateUtils.checkDate(startDate, null);
				if (message != null) {
					System.out.println(message + "！");
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
