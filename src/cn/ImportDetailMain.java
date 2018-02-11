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
				System.out.println("请输入导入股票的日期(格式:2000-01-01)：");
				String stockDate = br.readLine();
				if (CommonUtils.isEnd(stockDate)) break;
				if (CommonUtils.isBlank(stockDate)) {
					System.out.println("输入的股票日期不能为空！");
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
