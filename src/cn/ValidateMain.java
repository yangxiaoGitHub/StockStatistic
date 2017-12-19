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
			//��֤ԭʼ��Ʊ����(original_stock_)
			validateOriginalData.validateOriginalStockData();
			//��֤ÿ�չ�Ʊ����(daily_stock_)
			validateDailyData.validateDailyStockData();
			//��֤ͳ�ƹ�Ʊ����(statistic_stock_)
			validate.validateStatisticStockData();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
