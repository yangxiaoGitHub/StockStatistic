package cn.com;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import cn.db.bean.BaseStock;
import cn.log.Log;

public class CommonUtils {
	static Log log = Log.getLoger();
	
	public static boolean isEnd(String param) {
		if ("end".equalsIgnoreCase(param)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isBlank(String obj) {
		if (obj==null || obj.trim().equals("")) 
			return true;
		else 
			return false;
	}

	public static boolean isBlank(List<String> duplicateStockList) {
		if (duplicateStockList==null || duplicateStockList.size()==0)
			return true;
		else
			return false;
	}
	
	public static List<String> getDuplicateStocks(String[] stockArray) {
		
        List<String> duplicateStockList = new ArrayList<String>();
        List<String> singleStockList = new ArrayList<String>();
        for(String stockCode : stockArray) {
            if(singleStockList.contains(stockCode)) 
            	duplicateStockList.add(stockCode);
            else 
            	singleStockList.add(stockCode);
        }
        return duplicateStockList;
	}
	
	public static String getDuplicateMessage(List<String> duplicateStockList) {

		String stockCodesMessage = "";
		for (String stockCode : duplicateStockList) {
			stockCodesMessage += ", " + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")";
		}
		stockCodesMessage = stockCodesMessage.substring(1);
		return stockCodesMessage;
	}

	public static String getActualStockName(List<String[]> actualStockList) {
		
		String result = "";
		for (String[] stock : actualStockList) {
			result += "," + stock[0] + "(" + stock[2] + ")";
		}
		return result.substring(1, result.length());
	}

	public static String getErrorStockName(List<String[]> errorStockList) {

		String result = "";
		for (String[] stock : errorStockList) {
			result += "," + stock[0] + "(" + stock[1] + ")";
		}
		return result.substring(1, result.length());
	}
	
	public static String getErrorStockMessage(List<String[]> errorStockList) {
		
		String result = "";
		for (String[] stock : errorStockList) {
			result += "," + stock[0] + "(" + PropertiesUtils.getProperty(stock[0])  + ")：" + stock[1];
		}
		return result.substring(1, result.length());
	}
	
	public static String getActualStockMessage(List<String[]> actualStockList) {
		
		String result = "";
		for (String[] stock : actualStockList) {
			result += "," + stock[0] + "(" + PropertiesUtils.getProperty(stock[0]) + ")：" + stock[2];
		}
		return result.substring(1, result.length());
	}

	public static List<String[]> listErrorStockName(String[] codeArray, String[] nameArray) {
		
		List<String[]> errorStockList = new ArrayList<String[]>();
		for (int index=0; index<codeArray.length; index++) {
			String actualName = PropertiesUtils.getProperty(codeArray[index]);
			actualName = StringUtils.deleteWhitespace(actualName).replace("Ａ", "A");
			if (!actualName.equals(nameArray[index])) {
				String[] errorStock = new String[3];
				errorStock[0] = codeArray[index];
				errorStock[1] = nameArray[index];
				errorStock[2] = actualName;
				errorStockList.add(errorStock);
			}
		}
		return errorStockList;
	}
	
	public static boolean compareTurnoverRate(Double inputTurnoverRate, Double realTurnoverRate) {

		double difference = inputTurnoverRate - realTurnoverRate;
		if (Math.abs(difference) <= DataUtils.CONSTANT_ZERO_DOT_ONE) return true;
		else return false;
	}

	public static String isExistStockCode(String[] codeArray) {
		
		List<String> codeList = new ArrayList<String>();
		for (String stockCode : codeArray) {
			String stockName = PropertiesUtils.getProperty(stockCode);
			if (CommonUtils.isBlank(stockName)) codeList.add(stockCode);
		}
		if (codeList.size() != 0) {
			return StringUtils.join(codeList.toArray(), ",");
		}
		return null;
	}

	public static String checkDate(String startDate, String endDate) {
		if (!isValidDate(startDate)) {
			return "输入的开始日期格式不正确！";
		}
		if (!isValidDate(endDate)) {
			return "输入的结束日期格式不正确！";
		}
		return null;
	}

	public static String inspectDate(String startDate, String endDate) {

		if (startDate.trim().equals("") && endDate.trim().equals("")) {
			return null;
		}
		if ((startDate.trim().equals("") && !endDate.trim().equals(""))
			|| !startDate.trim().equals("") && endDate.trim().equals("")) {
			return "输入的开始日期和结束日期同时为空，或者同时不为空！";
		}
		if (!isValidDate(startDate)) {
			return "输入的开始日期格式不正确！";
		}
		if (!isValidDate(endDate)) {
			return "输入的结束日期格式不正确！";
		}
		return null;
	}
	
    /** 
     * 判断时间格式必须为"YYYY-MM-dd"
     * 
     */  
    public static boolean isValidDate(String sDate) {  
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
        try{  
            Date date = (Date)formatter.parse(sDate);  
            return sDate.equals(formatter.format(date));  
        }catch(Exception e){
            return false;  
        }  
    }

    /**
     * 将字符串编码成16进制数字
     * 
     */
	public static String stringToHex(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString().trim();
	}

	/**
	 * 16进制直接转换成为字符串
	 *
	 */
	public static String hexToString(String hexStr) {
	    String str = "0123456789ABCDEF";
	    char[] hexs = hexStr.toCharArray();
	    byte[] bytes = new byte[hexStr.length() / 2];
	    int n;
	    for (int i = 0; i < bytes.length; i++) {
	        n = str.indexOf(hexs[2 * i]) * 16;
	        n += str.indexOf(hexs[2 * i + 1]);
	        bytes[i] = (byte) (n & 0xff);
	    }
	    return new String(bytes);
	}
	
	public static boolean compareDate(Date date1, Date date2) {

		if (date1==null || date2==null) return false;
        if (date1.getTime() == date2.getTime()) {
            return true;
        } else { 
        	return false;
        }
    }
	
	/**
	 * 将bytes数组转换成十六进制
	 * 
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	/**
	 * 将十六进制转换成bytes数组
	 * 
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 根据字符串获得需要补充的空格串 
	 *
	 */
	public static String getSpaceByString(String stockName) {
		String spaces = "";
		int nameLength = getByteLength(stockName);
		for (int index=0; index<(8-nameLength); index++) {
			spaces += " ";
		}
		return spaces;
	}
	
	/**
	 * 判断一个字符串的字节长度
	 *
	 */
	public static int getByteLength(String value) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}
	
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String supplyNumber(int num, int size) {
		String result = "";
		int numBit = String.valueOf(num).length();
		int sizeBit = String.valueOf(size).length();
		int differ = sizeBit - numBit;
		for (int index=0; index<differ; index++) {
			result += "0";
		}
		return result+num;
	}

	public static boolean isZeroOrNull(Double value) {
		if (value==null || value.floatValue()==0) return true;
		else return false;
	}
	
	public static String getRequestByTimeOut(final String stockCode) {
		
	    final ExecutorService exec = Executors.newSingleThreadExecutor();
	    Callable<String> call = new Callable<String>() {  
	        public String call() throws Exception {  
	        	String urlRequestInfo = getURLRequestInfo(stockCode);
	            return urlRequestInfo;
	        }  
	    };

	    boolean successFlg = false;
	    String requestInfo = null;
	    try {
	    	System.out.print(DateUtils.DateTimeToString(new Date()) + "--获取股票(" + stockCode + ")的url信息...");
	        Future<String> future = exec.submit(call);  
	        // set method getURLRequestInfo timeout to 20 seconds  
	        requestInfo = future.get(1000*20, TimeUnit.MILLISECONDS); 
		   successFlg = true;
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        log.loger.error(ex);
	    }
	    exec.shutdown();
	    if (successFlg) System.out.print("获取url信息成功！");
	    else System.out.print("获取url信息失败！");
	    return requestInfo;  
	}
	
	@SuppressWarnings({ "deprecation", "resource" })
	public static String getURLRequestInfo(String stockCode) {

		String returnInfo = null;
		HttpGet request = null;
		HttpClient client = null;
		HttpResponse response = null;
		try {
			String searchStockCode = getSearchStockCode(stockCode);
			String url = PropertiesUtils.getProperty("stockURL") + searchStockCode;
			request = new HttpGet(url);
			client = new DefaultHttpClient();
			response = client.execute(request);
			// 得到结果,进行解释
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				// 连接成功
				String strResponse = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
				String stockInfo = strResponse.substring(strResponse.indexOf('"') + 1, strResponse.lastIndexOf('"'))+","+stockCode;
				String[] stockInfoArray = stockInfo.split(",");
				if (stockInfoArray.length == 34) {
					returnInfo = stockInfo;
				} else  {
					System.out.println("股票(" + stockCode + ")数据URL请求连接失败,返回值为: " + stockInfo);
					log.loger.info("股票(" + stockCode + ")数据URL请求连接失败,返回值为: " + stockInfo);
				}
			} else {
				System.out.println("股票(" + stockCode + ")获取状态码为:" + statusCode + ", URL连接失败！");
				log.loger.info("股票(" + stockCode + ")获取状态码为:" + statusCode + ", URL连接失败！");
			}
		} catch (ClientProtocolException ex) {
			System.out.println("股票(" + stockCode + ")数据URL请求出现异常！");
			log.loger.info("股票(" + stockCode + ")数据URL请求出现异常！");
			ex.printStackTrace();
			log.loger.error(ex);
		} catch (IOException ex) {
			System.out.println("股票(" + stockCode + ")数据URL请求出现异常！");
			log.loger.info("股票(" + stockCode + ")数据URL请求出现异常！");
			ex.printStackTrace();
			log.loger.error(ex);
		} finally {
			if (request != null) {
				request.abort();
				request = null;
			}
			if (client != null) client = null;
			if (response != null) response = null;
		}
		return returnInfo;
	}
	
	private static String getSearchStockCode(String stockCode) {
		String searchStockCode = "";
		if (stockCode.substring(0, 1).equals("6")) {
			searchStockCode = "sh" + stockCode;
		} else {
			searchStockCode = "sz" + stockCode;
		}
		return searchStockCode;
	}
	
	public static String getStockCodeByAliasCode(String stockAliasCode) {
		
		String stockCode = null;
		if (stockAliasCode.contains("SH") || stockAliasCode.contains("SZ"))
			stockCode = stockAliasCode.substring(2);
		else 
			stockCode = stockAliasCode;
		return stockCode;
	}
	
	/**
	 * List转换成Map
	 *
	 */
	public static Map<String, BaseStock> convertListToMap(List<? extends BaseStock> list) {
		
		Map<String, BaseStock> map = new HashMap<String, BaseStock>();
		for (BaseStock stock : list) {
			map.put(stock.getStockCode(), stock);
		}
		return map;
	}
	
/*	public static Map<String, StatisticStock> convertListToMap(List<StatisticStock> list) {
		
		Map<String, StatisticStock> map = new HashMap<String, StatisticStock>();
		for (StatisticStock stock : list) {
			map.put(stock.getStockCode(), stock);
		}
		return map;
	}*/

	public static boolean isLegalDate(String sDate, Date maxStockDate) {
		
		Date limitDate = DateUtils.String2Date("2015-01-01");
		if (maxStockDate != null) limitDate = maxStockDate;
		Date stockDate = DateUtils.String2Date(sDate);
		if (stockDate.compareTo(limitDate) > 0) return true;
		else return false;
	}
}
