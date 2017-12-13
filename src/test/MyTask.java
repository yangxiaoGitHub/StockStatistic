package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class MyTask {
	public void run() {
		List<String> list = null;
		try {
			list = setStockCode();
			getStockTextinfo(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getStockTextinfo(List<String> list) throws Exception {
		boolean bl = false;
		// List<String> list = setStockCode();
		Iterator<String> it = list.iterator();
		HttpClient client = new DefaultHttpClient();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
		System.out.println("----------start：=" + getDate());
		while (it.hasNext()) {
			String str1 = it.next();
			String url = "http://hq.sinajs.cn/list=" + str1;
			HttpGet request = new HttpGet(url);
			HttpResponse response;
			try {
				response = client.execute(request);
				// 得到结果,进行解释
				if (response.getStatusLine().getStatusCode() == 200) {
					// 连接成功
					String strResponse = EntityUtils.toString(
							response.getEntity(), HTTP.UTF_8);// 将响应结果转换为String类型
					String str123 = str1
							+ ','
							+ strResponse.substring(
									strResponse.indexOf('"') + 1,
									strResponse.lastIndexOf('"'));
					String[] strlength = str123.split(",");
					if (strlength.length == 34) {
						String str_1 = str123 + ','
								+ sdf1.format(new java.util.Date()) + '\n';
						doWriteText(str_1);
					}
					bl = true;
				} else {
					// 没有请求成功
					System.out.println("请求的连接状态没有ok");
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			request.abort();
		}

		System.out.println("----------end：=" + getDate());
		return bl;
	}

	public List<String> setStockCode() throws Exception {

		List<String> alist = new ArrayList<String>(); // 从文本存到List
		File file = new File("D:/stock_data.txt");// 读文件
		FileInputStream inputstream;
		BufferedReader reader = null;
		String strtemp = "";
		try {
			inputstream = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(inputstream));
			int j = 0;
			while ((strtemp = reader.readLine()) != null) {
				alist.add(strtemp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return alist;
	}

	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
		String sdate = sdf.format(new java.util.Date());
		return sdate;
	}

	public boolean doWriteText(String str) throws Exception {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String sdate = sdf1.format(new java.util.Date());
		String filepath = "d:/data/" + sdate + ".dat";
		File file = new File(filepath);
		if (!file.exists())
			file.createNewFile();
		FileOutputStream out = new FileOutputStream(file, true);
		out.write(str.getBytes("GBK"));
		out.close();
		return true;
	}

	public void Command() {
		try {
			String shpath = "/root/stock/stock_d/ImpStock_d.sh"; // 程序路径
			Process process = null;
			String command1 = shpath;
			process = Runtime.getRuntime().exec(command1);

			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
