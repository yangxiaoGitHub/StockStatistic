package cn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import cn.com.CommonUtils;
import cn.implement.ImportAllDetailStockData;

public class ImportDetailMain {
	
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			ImportAllDetailStockData importDetailData = new ImportAllDetailStockData();

			while(true) {
				System.out.println("�����뵼���Ʊ������(��ʽ:2000-01-01)��");
				String stockDate = br.readLine();
				if (CommonUtils.isEnd(stockDate)) break;
				if (CommonUtils.isBlank(stockDate)) {
					System.out.println("����Ĺ�Ʊ���ڲ���Ϊ�գ�");
					continue;
				}
				try {
					importDetailData.importAllStockDetail(stockDate);
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
