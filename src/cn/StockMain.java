package cn;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cn.com.CommonUtils;
import cn.implement.HandleOriginalAndDailyData;
import cn.implement.ValidateOriginalStockData;

public class StockMain {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			HandleOriginalAndDailyData handle = new HandleOriginalAndDailyData();

			while(true) {
				System.out.println("����������(��ʽ:2000-01-01)��");
				String date = br.readLine();
				if (CommonUtils.isEnd(date)) break;
				System.out.println("�������Ʊ����(����Զ��Ÿ���)��");
				String codes = br.readLine();
				if (CommonUtils.isEnd(codes)) break;
				System.out.println("�������Ʊ����(����Զ��Ÿ���)��");
				String names = br.readLine();
				if (CommonUtils.isEnd(names)) break;
				System.out.println("�������Ʊ�ǵ���(����Զ��Ÿ���)��");
				String changeRates = br.readLine();
				if (CommonUtils.isEnd(changeRates)) break;
				System.out.println("�������Ʊ������(����Զ��Ÿ���)��");
				String turnoverRates = br.readLine();
				if (CommonUtils.isEnd(turnoverRates)) break;

				if (CommonUtils.isBlank(date) ||
					CommonUtils.isBlank(codes) ||
					CommonUtils.isBlank(names) ||
					CommonUtils.isBlank(changeRates)) {
					System.out.println("��������ݲ���Ϊ�գ�");
					continue;
				}
				ValidateOriginalStockData validateOriginalStockData = new ValidateOriginalStockData();
				String message = validateOriginalStockData.checkInputStockData(date, codes, names, changeRates, turnoverRates);
				if (message != null) {
					System.out.println(message);
					continue;
				}
				try {
					handle.handleOriginalStockData(date, codes, changeRates, turnoverRates);
					handle.handleDailyStockData(date, codes, changeRates, turnoverRates);
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
