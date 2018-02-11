package cn.com;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
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

import cn.db.bean.AllStock;
import cn.db.bean.BaseStock;
import cn.db.bean.DailyStock;
import cn.db.bean.DetailStock;
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
		if (obj == null || obj.trim().equals(DataUtils._BLANK))
			return true;
		else
			return false;
	}
	
	public static boolean isJsonBlank(String jsonObj) {

		if (jsonObj==null || jsonObj.trim().equals(DataUtils._BLANK))
			return true;
		boolean jsonFlg = true;
		Map<String, ? extends Object> jsonMap = JsonUtils.getMapByJson(jsonObj);
		for (Object value : jsonMap.values()) {
			if (null!=value || !DataUtils._BLANK.equals(value)) {
				jsonFlg = false;
				break;
			}
		}
		return jsonFlg;
	}

	public static boolean isBlank(List<? extends Object> dataList) {
		if (dataList == null || dataList.size() == 0)
			return true;
		else
			return false;
	}
	
	public static boolean isBlank(String[] values) {
		if (values==null || values.length==0 || DataUtils._BLANK.equals(values[0]))
			return true;
		else
			return false;
	}
	
	public static boolean validateSortFlg(String sortFlg, Integer beginInt, Integer endInt) {
		if (!DataUtils.isNumeric(sortFlg)) return false;
		if (sortFlg.length()!=1) return false;
		Integer intSortFlg = Integer.valueOf(sortFlg);
		if (intSortFlg.compareTo(beginInt)<0 || intSortFlg.compareTo(endInt)>0) return false;
		return true;
	}

	public static List<String> getDuplicateStocks(String[] stockArray) {

		List<String> duplicateStockList = new ArrayList<String>();
		List<String> singleStockList = new ArrayList<String>();
		for (String stockCode : stockArray) {
			if (singleStockList.contains(stockCode))
				duplicateStockList.add(stockCode);
			else
				singleStockList.add(stockCode);
		}
		return duplicateStockList;
	}
	
	public static String addSpaceInMiddle(String para) {
		int length = para.length();
		char[] value = new char[length << 1];
		for (int i = 0, j = 0; i < length; ++i, j = i << 1) {
			value[j] = para.charAt(i);
			value[1 + j] = ' ';
		}
		return new String(value);
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
			result += "," + stock[0] + "(" + PropertiesUtils.getProperty(stock[0]) + ")：" + stock[1];
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
		for (int index = 0; index < codeArray.length; index++) {
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

	public static String isExistStockCode(String[] codeArray) {

		List<String> codeList = new ArrayList<String>();
		for (String stockCode : codeArray) {
			String stockName = PropertiesUtils.getProperty(stockCode);
			if (CommonUtils.isBlank(stockName))
				codeList.add(stockCode);
		}
		if (codeList.size() != 0) {
			return StringUtils.join(codeList.toArray(), ",");
		}
		return null;
	}
	
	/**
	 * 根据stockCode从List中获取AllStock
	 * 
	 */
	public static AllStock getAllStockByStockCode(String stockCode, List<AllStock> allStockList) {

		AllStock allStock = null;
		for (AllStock stock : allStockList) {
			String code = stock.getStockCode();
			if (code.equals(stockCode)) {
				allStock = stock;
				break;
			}
		}
		return allStock;
	}
	
	/**
	 * 验证网上获取的股票信息与实际股票信息是否一致(股票名称和开盘价)
	 *
	 */
	public static boolean validateStockData(String stockCode, String stockName, Double todayOpen) {

		boolean validateFlg = true;
		String realStockName = PropertiesUtils.getProperty(stockCode);
		if (!CommonUtils.isBlank(realStockName)) {
			String realStockName_first = realStockName.substring(0, 1);
			String detailStockName_first = stockName.substring(0, 1);
			if (!realStockName_first.equals(detailStockName_first))
				validateFlg = false;
			if (todayOpen > DataUtils._TODAY_OPEN_LIMIT)
				validateFlg = false;
		}
		return validateFlg;
	}

	/**
	 * 判断是否是股票别名
	 *
	 */
	public static boolean isStockAliasCode(String stockAliasCode) {

		if (stockAliasCode.contains(DataUtils._SH_CAPITAL) || stockAliasCode.contains(DataUtils._SZ_CAPITAL)) {
			String stockCode = stockAliasCode.substring(2);
			if (DataUtils.isNumeric(stockCode)) return true;
			else return false;
		} else {
			return false;
		}
	}
	
	/**
	 * 根据字符串获得需要补充的空格串 
	 *
	 */
	public static String getSpaceByString(String stockName) {
		String spaces = "";
		int nameLength = getByteLength(stockName);
		for (int index = 0; index < (8 - nameLength); index++) {
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

	public static String getRequestByTimeOut(final String stockCode) {

		final ExecutorService exec = Executors.newSingleThreadExecutor();
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String urlRequestInfo = getURLRequestInfo(stockCode);
				return urlRequestInfo;
			}
		};

		String requestInfo = null;
		try {
			System.out.print(DateUtils.dateTimeToString(new Date(), DateUtils.TIME_FORMAT) + "--获取股票(" + stockCode + ")的url信息...");
			Future<String> future = exec.submit(call);
			// set method getURLRequestInfo timeout to 20 seconds  
			String stockInfo = future.get(1000 * 20, TimeUnit.MILLISECONDS);
			if (isValidateUrlData(stockCode, stockInfo)) {
				requestInfo = stockInfo + "," + stockCode;
				System.out.print("获取url信息成功: " + getSimpleStockInfo(stockInfo));
			} else {
				System.out.println("获取url信息失败: " + stockInfo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		exec.shutdown();
		return requestInfo;
	}

	private static String getSimpleStockInfo(String stockInfo) {
		
		String returnInfo = "";
		String[] stockInfoArray = stockInfo.split(",");
		for (int index=0; index<stockInfoArray.length; index++) {
			if (index >= 6) break;
			returnInfo += "," + stockInfoArray[index];
		}
		return returnInfo.substring(1) + "...";
	}

	private static boolean isValidateUrlData(String stockCode, String requestInfo) {
		
		if (isBlank(requestInfo)) return false; 
		String stockInfo = requestInfo + "," + stockCode;
		String[] stockInfoArray = stockInfo.split(",");
		if (stockInfoArray.length == 34) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings({ "deprecation", "resource" })
	public static String getURLRequestInfo(String stockCode) {

		HttpGet request = null;
		HttpClient client = null;
		HttpResponse response = null;
		String stockInfo = null;
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
				stockInfo = strResponse.substring(strResponse.indexOf('"') + 1, strResponse.lastIndexOf('"')); // + "," + stockCode;
			} else {
				System.out.println("股票(" + stockCode + ")获取状态码为:" + statusCode + ", URL连接失败！");
				log.loger.warn("股票(" + stockCode + ")获取状态码为:" + statusCode + ", URL连接失败！");
			}
		} catch (ClientProtocolException ex) {
			System.out.println("股票(" + stockCode + ")数据URL请求出现异常！");
			log.loger.error("股票(" + stockCode + ")数据URL请求出现异常！");
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} catch (IOException ex) {
			System.out.println("股票(" + stockCode + ")数据URL请求出现异常！");
			log.loger.error("股票(" + stockCode + ")数据URL请求出现异常！");
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			if (request != null) {
				request.abort();
				request = null;
			}
			if (client != null)
				client = null;
			if (response != null)
				response = null;
		}
		return stockInfo;
	}

	private static String getSearchStockCode(String stockCode) {
		String searchStockCode = "";
		String firstChar = stockCode.substring(0, 1);
		if (firstChar.equals(DataUtils._STRING_SIX) || firstChar.equals(DataUtils._STRING_SEVEN)) {
			searchStockCode = DataUtils._SH_SMALL + stockCode;
		} else {
			searchStockCode = DataUtils._SH_SMALL + stockCode;
		}
		return searchStockCode;
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

	public static String errorInfo(Exception exception) {  
        StringWriter sw = null;  
        PrintWriter pw = null;  
        try {  
            sw = new StringWriter();  
            pw = new PrintWriter(sw);  
            // 将出错的栈信息输出到printWriter中  
            exception.printStackTrace(pw);  
            pw.flush();  
            sw.flush();  
        } finally {  
            if (sw != null) {  
                try {
                    sw.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }
            }  
            if (pw != null) { 
                pw.close();  
            }  
        }  
        return sw.toString();  
    }
	
	/**
	 * 从字符串数组获得DailyStock对象
	 *
	 */
	public static DailyStock getDailyStockFromArray(int index, String[] codes, String[] changeRates, String[] turnoverRates, String sDate) {
		DailyStock stockData = new DailyStock();
		Date stockDate = DateUtils.stringToDate(sDate);
		Timestamp inputTime = new Timestamp(System.currentTimeMillis());
		stockData.setStockCode(codes[index]);
		stockData.setStockDate(stockDate);
		Double changeRate = Double.valueOf(changeRates[index]);
		stockData.setChangeRate(changeRate);
		if (!isBlank(turnoverRates)) {
			Double turnoverRate = Double.valueOf(turnoverRates[index]);
			stockData.setTurnoverRate(turnoverRate);
		} else {
			stockData.setTurnoverRate(DataUtils._DOUBLE_ZERO);
		}
		String changeFlg = changeRate > 0 ? "1" : "0";
		stockData.setChangeFlg(changeFlg);
		stockData.setNote(DataUtils._BLANK);
		stockData.setInputTime(inputTime);
		return stockData;
	}
    
	public static String getLineBreak() {
		return System.getProperty("line.separator");
	}

	public static boolean compareUpAndDownJson(String newUpAndDownJson, String oldUpAndDownJson) throws IOException {
		
		Map<String, Integer> newJsonMap = JsonUtils.getMapByJson(newUpAndDownJson);
		Map<String, Integer> oldJsonMap = JsonUtils.getMapByJson(oldUpAndDownJson);
		return compareMap(newJsonMap, oldJsonMap);
	}
	
	private static boolean compareMap(Map<String, Integer> newMap, Map<String, Integer> oldMap) {

		if (newMap.size() != oldMap.size()) return false;
		boolean flg = true;
		//判断键是否相同
		for (String newKey : newMap.keySet()) {
			if (!oldMap.containsKey(newKey)) {
				flg = false;
				break;
			}
		}
		//判断值是否相同
		for (Integer newValue : newMap.values()) {
			if (!oldMap.containsValue(newValue)) {
				flg = false;
				break;
			}
		}
		return flg;
	}

	/**
	 * 获得最大值和最小值
	 *
	 */
	public static Double[] getMinMaxPrices(List<DetailStock> detailStockList) {
		
		DetailStock detailStock = detailStockList.get(0);
		Double minValue = detailStock.getCurrent();
		Double maxValue = detailStock.getCurrent();
		for (DetailStock detaiStock : detailStockList) {
			Double stockPrice = detaiStock.getCurrent();
			if (stockPrice < minValue) minValue = stockPrice;
			if (stockPrice > maxValue) maxValue = stockPrice;
		}
		Double[] minMaxValues = {minValue, maxValue};
		return minMaxValues;
	}
	
	/**
	 * 根据固定长度从后面补足空格
	 *
	 */
	public static String appendSpace(String values, final int length) {
		
		String spaces = "";
		int differ = length - values.length();
		for (int index=0; index<differ; index++) {
			spaces += " ";
		}
		return values + spaces;
	}
	
	/**
	 * List是否包含DailyStock
	 * 
	 */
	public static DailyStock containStockCode(String stockCode, List<DailyStock> dailyStockList) {

		DailyStock reDailyStock = null;
		for (DailyStock dailyStock : dailyStockList) {
			String stockCode_ = dailyStock.getStockCode();
			if (stockCode.equals(stockCode_)) {
				reDailyStock = dailyStock;
				break;
			}
		}
		return reDailyStock;
	}
	
	/**
	 * 验证BaseStock类中的stockCode加密
	 *
	 */
	public static <T extends BaseStock> List<T> listErrorStockCodeDES(List<T> stockList) {

		List<T> invalidList = new ArrayList<T>();
		for (T data : stockList) {
			String decryptStockCode = DESUtils.decryptHex(data.getStockCodeDES());
			if (!decryptStockCode.equals(data.getStockCode())) {
				invalidList.add(data);
			}
		}
		return invalidList;
	}
}
