package cn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.com.CommonUtils;
import cn.implement.HandleDetailStockData;
import cn.implement.OtherData;

public class OtherMain {

	public static void main(String[] args) {
		try {
			OtherData other = new OtherData();
			//HandleDetailStockData handleDetailStockData = new HandleDetailStockData();
			//对原始股票数据进行MD5加密
			//other.handleOriginalMD5();
			//统计每日股票数据到statistic_stock_表中
			other.statisticDailyStock();
			//增加或更新所有股票数据到all_stock_表中
			//other.handleAllStock();
			//解析所有股票信息表(all_information_stock_)中的股票信息到所有股票详细信息表(all_detail_stock_)中(一般不用)
			//handleDetailStockData.handleAllStockDetail();
			//更新所有股票信息表(all_information_stock_)中的num_字段
			//other.updateNumOfAllInformationStock();
			//统计每日股票数据的涨跌次数到statistic_stock_表中(一般不使用)
			//other.statisticUpAndDownToStatisticStock();
			//计算所有股票(all_stock_)的流通股
		//	  handleAllCirculationStock();
			//计算每日所有股票(all_detail_stock_)的换手率
			//handleDetailStockData.handleAllDetailStockTurnoverRate();
			//计算每日选择股票(detail_stock_)的换手率
			//handleDetailStockData.handDetailStockTurnoverRate();
			//导入下载的股票数据到股票历史数据表(history_stock_)中
			//other.handleDownloadDailyData();
			//填补每日股票信息表(daily_stock_)中的涨跌幅(change_rate_)和换手率(turnover_rate_)
			//other.handleChangeRateAndTurnoverRateInDailyStock();
			//填补股票详细信息表(detail_stock_)中的换手率(turnover_rate_)
			//handleDetailStockData.handleTurnoverRateInDetailStock();
			//增加所有股票详细信息表(all_detail_stock_)的记录(数据来源于历史股票信息表(history_stock_)，股票日期大于2017-01-01)
		//	handleDetailStockData.addAllDetailStockFromHistoryStock();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	private static void handleAllCirculationStock() throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		OtherData other = new OtherData();
		while (true) {
			System.out.println("所有股票表(all_stock_)中流通股和流通值都不相同时，请选择提示更新(0:不更新, 1:更新)：0");
			String handleFlg = br.readLine();
			if (CommonUtils.isEnd(handleFlg))
				break;
			other.handleAllCirculationStock(handleFlg);
		}
	}
}
