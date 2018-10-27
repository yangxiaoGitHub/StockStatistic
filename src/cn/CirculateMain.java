package cn;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.implement.AnalysisCirculateData;

public class CirculateMain {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			AnalysisCirculateData analysis = new AnalysisCirculateData();
			while(true) {
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
					analysis.analysisAllStockCirculate(startDate);
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
