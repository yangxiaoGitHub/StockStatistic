package cn;

import cn.implement.ValidateDailyStockData;
import cn.implement.ValidateStatisticStockData;
import cn.implement.ValidateOriginalStockData;

public class ValidateMain {

	public static void main(String[] args) {
		try {
			ValidateStatisticStockData validate = new ValidateStatisticStockData();
			ValidateOriginalStockData validateOriginalData = new ValidateOriginalStockData();
			ValidateDailyStockData validateDailyData = new ValidateDailyStockData();
			//验证原始股票数据(original_stock_)
			validateOriginalData.validateOriginal();
			//验证每日股票数据(daily_stock_)
			validateDailyData.validateDaily();
			//验证统计股票数据(statistic_stock_)
			validate.validateStatistic();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
