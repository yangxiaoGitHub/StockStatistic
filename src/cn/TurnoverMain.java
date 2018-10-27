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
				System.out.println("������ͳ�Ʊ�ʶ(0:���·�ͳ��, 1:��ʱ���ͳ��)��0");
				String statisticFlg = br.readLine();
				if (CommonUtils.isBlank(statisticFlg)) statisticFlg = "0";
				if (CommonUtils.isEnd(statisticFlg)) break;
				if (!CommonUtils.validateSortFlg(statisticFlg, DataUtils._INT_ZERO, DataUtils._INT_ONE)) {
					System.out.println("ѡ���ͳ�Ʊ�ʶ����ȷ:" + statisticFlg + ", Ӧ��Ϊ:0��1��");
					continue;
				}
				if (statisticFlg.equals("0")) {
					System.out.println("�����뿪ʼ�·�(��ʽ:2000-01)��");
					String startDate = br.readLine();
					if (CommonUtils.isEnd(startDate)) break;
					
					if (CommonUtils.isBlank(startDate)) {
						System.out.println("����Ŀ�ʼ���ڲ���Ϊ�գ�");
						continue;
					}
					
					String message = DateUtils.checkDate(startDate, null);
					if (message != null) {
						System.out.println(message + "��");
						continue;
					}
					try {
						analysis.statisticMonthTurnoverRate(startDate);
					} catch (Exception ex) {
						ex.printStackTrace();
						break;
					}
				} else {
					System.out.println("�����뿪ʼͳ������(��ʽ:2000-01-01)��");
					String startDate = br.readLine();
					if (CommonUtils.isEnd(startDate)) break;
					if (CommonUtils.isBlank(startDate)) {
						System.out.println("���뿪ʼͳ�����ڲ���Ϊ�գ�");
						continue;
					}
					System.out.println("���������ͳ������(��ʽ:2000-01-01)��");
					String endDate = br.readLine();
					if (CommonUtils.isEnd(endDate)) break;
					if (CommonUtils.isBlank(endDate)) {
						System.out.println("�������ͳ�����ڲ���Ϊ�գ�");
						continue;
					}

					String message = DateUtils.checkDate(startDate, endDate);
					if (message != null) {
						System.out.println(message + "��");
						continue;
					}

					System.out.println("������ͳ�Ʊ�ʶ(0:ÿ��ѡ��Ĺ�Ʊ, 1:����ѡ��Ĺ�Ʊ, 2:���е���Ĺ�Ʊ)��0");
					String changeFlg = br.readLine();
					if (CommonUtils.isBlank(changeFlg)) changeFlg = "0";
					if (CommonUtils.isEnd(changeFlg)) break;
					if (!CommonUtils.validateSortFlg(changeFlg, DataUtils._INT_ZERO, DataUtils._INT_TWO)) {
						System.out.println("ѡ��������ʶ����ȷ:" + changeFlg + ", Ӧ��Ϊ:0��2��");
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
