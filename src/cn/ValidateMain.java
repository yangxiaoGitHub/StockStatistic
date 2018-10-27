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
			//��֤ԭʼ��Ʊ����(original_stock_)
			validateOriginalData.validateOriginalStockData();
			//��֤ÿ�չ�Ʊ����(daily_stock_)
			validateDailyData.validateDailyStockData();
			//��֤ͳ�ƹ�Ʊ����(statistic_stock_)
			validateStaData.validateStatisticStockData();
			//��֤ͳ�ƹ�Ʊ��ϸ����(statistic_detail_stock_)
			validateStaDetailData.validateStatisticDetailStockData();
			//��֤��Ʊ��Ϣ����(information_stock_)
			//validateInformationData.validateInformationStockData();
			//��֤��Ʊ��ϸ��Ϣ����(detail_stock_)
			//validateDetailData.validateDetailStockData();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
