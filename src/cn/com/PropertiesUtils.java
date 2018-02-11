package cn.com;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import cn.log.Log;

public class PropertiesUtils {
	private static String fileName = "/client.properties";
	private static Properties pro = new Properties();
	static Log log = Log.getLoger();
	static {
		try {
			InputStream in = PropertiesUtils.class.getResourceAsStream(fileName);
			pro.load(in);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
	}
	
	public static String getProperty(String name) {
		if (pro.containsKey(name)) {
			return pro.getProperty(name).trim();
		} else {
			return DataUtils._BLANK;
		}
	}
	
	public static Properties getStockNameProperties() {
		
		Properties properties = new Properties(); 
        Iterator<Entry<Object, Object>> itr = pro.entrySet().iterator();
        while (itr.hasNext()){
        	Entry<Object, Object> entry = (Entry<Object, Object>)itr.next();
        	String stockCode = entry.getKey().toString();
        	String stockName = entry.getValue().toString();
        	if (DataUtils.isNumeric(stockCode)) {
        		properties.put(stockCode, stockName);
        	}
        }
		return properties;
	}
	
	public static Properties getNowPriceAndCirculValueProperties() {

		Properties properties = new Properties(); 
        Iterator<Entry<Object, Object>> itr = pro.entrySet().iterator();
        while (itr.hasNext()){
        	Entry<Object, Object> entry = (Entry<Object, Object>)itr.next();
        	String stockAliasCode = entry.getKey().toString();
        	String stockPriceValue = entry.getValue().toString();
        	if (CommonUtils.isStockAliasCode(stockAliasCode)) {
        		properties.put(stockAliasCode, stockPriceValue);
        	}
        }
		return properties;
	}
}
