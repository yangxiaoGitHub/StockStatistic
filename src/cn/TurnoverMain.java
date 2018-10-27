package cn;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.implement.AnalysisTurnoverRateData;

public class TurnoverMain {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			AnalysisTurnoverRateData analysis = new AnalysisTurnoverRateData();

			while(true) {
				System.out.println("请输入统计标识(0:按月份统计, 1:按时间段统计)：0");
				String statisticFlg = br.readLine();
				if (CommonUtils.isBlank(statisticFlg)) statisticFlg = "0";
				if (CommonUtils.isEnd(statisticFlg)) break;
				if (!CommonUtils.validateSortFlg(statisticFlg, DataUtils._INT_ZERO, DataUtils._INT_ONE)) {
					System.out.println("选择的统计标识不正确:" + statisticFlg + ", 应该为:0～1！");
					continue;
				}
				if (statisticFlg.equals("0")) {
					System.out.println("请输入开始月份(格式:2000-01)：");
					String startDate = br.readLine();
					if (CommonUtils.isEnd(startDate)) break;
					
					if (CommonUtils.isBlank(startDate)) {
						System.out.println("输入的开始日期不能为空！");
						continue;
					}
					
					String message = DateUtils.checkDate(startDate, null);
					if (message != null) {
						System.out.println(message + "！");
						continue;
					}
					try {
						analysis.statisticMonthTurnoverRate(startDate);
					} catch (Exception ex) {
						ex.printStackTrace();
						break;
					}
				} else {
					System.out.println("请输入开始统计日期(格式:2000-01-01)：");
					String startDate = br.readLine();
					if (CommonUtils.isEnd(startDate)) break;
					if (CommonUtils.isBlank(startDate)) {
						System.out.println("输入开始统计日期不能为空！");
						continue;
					}
					System.out.println("请输入结束统计日期(格式:2000-01-01)：");
					String endDate = br.readLine();
					if (CommonUtils.isEnd(endDate)) break;
					if (CommonUtils.isBlank(endDate)) {
						System.out.println("输入结束统计日期不能为空！");
						continue;
					}

					String message = DateUtils.checkDate(startDate, endDate);
					if (message != null) {
						System.out.println(message + "！");
						continue;
					}

					System.out.println("请输入统计标识(0:每日选择的股票, 1:所有选择的股票, 2:所有导入的股票)：0");
					String changeFlg = br.readLine();
					if (CommonUtils.isBlank(changeFlg)) changeFlg = "0";
					if (CommonUtils.isEnd(changeFlg)) break;
					if (!CommonUtils.validateSortFlg(changeFlg, DataUtils._INT_ZERO, DataUtils._INT_TWO)) {
						System.out.println("选择的排序标识不正确:" + changeFlg + ", 应该为:0～2！");
						continue;
					}

					try {
						analysis.analysisTurnoverRate(new String[]{startDate, endDate}, changeFlg);
					} catch (Exception ex) {
						ex.printStackTrace();
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
