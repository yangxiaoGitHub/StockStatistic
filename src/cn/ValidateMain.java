package cn;

import cn.implement.ValidateDailyStockData;
import cn.implement.ValidateDetailStockData;
import cn.implement.ValidateInformationStockData;
import cn.implement.ValidateOriginalStockData;
import cn.implement.ValidateStatisticDetailStockData;
import cn.implement.ValidateStatisticStockData;

public class ValidateMain {

	public static void main(String[] args) {
		try {
			ValidateStatisticStockData validateStaData = new ValidateStatisticStockData();
			ValidateStatisticDetailStockData validateStaDetailData = new ValidateStatisticDetailStockData();
			ValidateOriginalStockData validateOriginalData = new ValidateOriginalStockData();
			ValidateDailyStockData validateDailyData = new ValidateDailyStockData();
			ValidateInformationStockData validateInformationData = new ValidateInformationStockData();
			ValidateDetailStockData validateDetailData = new ValidateDetailStockData();
			//验证原始股票数据(original_stock_)
			validateOriginalData.validateOriginalStockData();
			//验证每日股票数据(daily_stock_)
			validateDailyData.validateDailyStockData();
			//验证统计股票数据(statistic_stock_)
			validateStaData.validateStatisticStockData();
			//验证统计股票详细数据(statistic_detail_stock_)
			validateStaDetailData.validateStatisticDetailStockData();
			//验证股票信息数据(information_stock_)
			//validateInformationData.validateInformationStockData();
			//验证股票详细信息数据(detail_stock_)
			//validateDetailData.validateDetailStockData();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
