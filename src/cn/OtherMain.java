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
			//��ԭʼ��Ʊ���ݽ���MD5����
			//other.handleOriginalMD5();
			//ͳ��ÿ�չ�Ʊ���ݵ�statistic_stock_����
			other.statisticDailyStock();
			//���ӻ�������й�Ʊ���ݵ�all_stock_����
			//other.handleAllStock();
			//�������й�Ʊ��Ϣ��(all_information_stock_)�еĹ�Ʊ��Ϣ�����й�Ʊ��ϸ��Ϣ��(all_detail_stock_)��(һ�㲻��)
			//handleDetailStockData.handleAllStockDetail();
			//�������й�Ʊ��Ϣ��(all_information_stock_)�е�num_�ֶ�
			//other.updateNumOfAllInformationStock();
			//ͳ��ÿ�չ�Ʊ���ݵ��ǵ�������statistic_stock_����(һ�㲻ʹ��)
			//other.statisticUpAndDownToStatisticStock();
			//�������й�Ʊ(all_stock_)����ͨ��
		//	  handleAllCirculationStock();
			//����ÿ�����й�Ʊ(all_detail_stock_)�Ļ�����
			//handleDetailStockData.handleAllDetailStockTurnoverRate();
			//����ÿ��ѡ���Ʊ(detail_stock_)�Ļ�����
			//handleDetailStockData.handDetailStockTurnoverRate();
			//�������صĹ�Ʊ���ݵ���Ʊ��ʷ���ݱ�(history_stock_)��
			//other.handleDownloadDailyData();
			//�ÿ�չ�Ʊ��Ϣ��(daily_stock_)�е��ǵ���(change_rate_)�ͻ�����(turnover_rate_)
			//other.handleChangeRateAndTurnoverRateInDailyStock();
			//���Ʊ��ϸ��Ϣ��(detail_stock_)�еĻ�����(turnover_rate_)
			//handleDetailStockData.handleTurnoverRateInDetailStock();
			//�������й�Ʊ��ϸ��Ϣ��(all_detail_stock_)�ļ�¼(������Դ����ʷ��Ʊ��Ϣ��(history_stock_)����Ʊ���ڴ���2017-01-01)
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
			System.out.println("���й�Ʊ��(all_stock_)����ͨ�ɺ���ֵͨ������ͬʱ����ѡ����ʾ����(0:������, 1:����)��0");
			String handleFlg = br.readLine();
			if (CommonUtils.isEnd(handleFlg))
				break;
			other.handleAllCirculationStock(handleFlg);
		}
	}
}
