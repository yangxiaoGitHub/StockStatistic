package cn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.com.CommonUtils;
import cn.implement.ExecuteData;

public class ExecuteMain {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("请输入涨跌标识(1：统计单只股票在一段时间内出现的涨跌次数, 2：统计所有股票在一段时间内出现的涨跌次数)：2");
			String chooseFLg = br.readLine();
			chooseFLg = "1".equals(chooseFLg)?"1":"2";
			switch(chooseFLg) {
				case "1":
					searchSingleStock(br);
					break;
				case "2":
					searchAllStock(br);
					break;
				default:
					System.out.println("输入的涨跌标识错误！");
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	private static void searchAllStock(BufferedReader br) {

		ExecuteData execute = new ExecuteData();
		try {
			execute.statisticDetailStock();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void searchSingleStock(BufferedReader br) throws IOException {

		ExecuteData execute = new ExecuteData();
		while(true) {
			System.out.println("请输入股票代码：");
			String stockCode = br.readLine();
			if (CommonUtils.isEnd(stockCode)) break;
			System.out.println("请输入开始日期(格式:2000-01-01)：");
			String startDate = br.readLine();
			if (CommonUtils.isEnd(startDate)) break;
			System.out.println("请输入结束日期(格式:2000-01-01)：");
			String endDate = br.readLine();
			if (CommonUtils.isEnd(endDate)) break;
			System.out.println("请输入涨跌标识(0:跌, 1:涨, 2:全部)：2");
			String changeFlg = br.readLine();
			if (CommonUtils.isEnd(changeFlg)) break;

			if (CommonUtils.isBlank(stockCode)) {
				System.out.println("输入的股票代码不能为空！");
				continue;
			}
			String message = CommonUtils.inspectDate(startDate, endDate);
			if (message != null) {
				System.out.println(message + "！");
				continue;
			}

			try {
				execute.searchStock(stockCode, startDate, endDate, changeFlg);
			} catch (Exception ex) {
				ex.printStackTrace();
				break;
			}
		}
	}
}