package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestMain {
	public static void main(String[] args) {
		URL ur = null;
		try {
			// �Ѻ���Ʊ������ʷ�ӿ�
			// ur = new
			// URL("http://q.stock.sohu.com/hisHq?code=cn_300228&start=20130930&end=20131231&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp");
			// ���˹�Ʊ������ʷ�ӿ�
			ur = new URL("http://biz.finance.sina.com.cn/stock/flash_hq/kline_data.php?&rand=random(10000)&symbol=sh600000&end_date=20150809&begin_date=20000101&type=plain");
			HttpURLConnection uc = (HttpURLConnection) ur.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(ur.openStream(), "GBK"));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}