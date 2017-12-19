package cn.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonUtils {

	// 将String转换成JSON
	public static String stringToJson(String key, String value) {
		JSONObject object = new JSONObject();
		object.put(key, value);
		return object.toString();
	}

	// 将Map转换成JSON
	public static String getJsonByMap(Map<String, ? extends Object> map) {
		JSONObject object = new JSONObject();
		for (String key : map.keySet())
			object.put(key, map.get(key));
		return object.toString();
	}

	// 从一个JSON 对象字符格式中得到一个java对象
	public static Object getObjectByJsonString(String jsonString, Class pojoCalss) {
		Object pojo;
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		pojo = JSONObject.toBean(jsonObject, pojoCalss);
		return pojo;
	}

	// 从json 表达式中获取一个map
	public static <T extends Object> Map<String, T> getMapByJson(String jsonString) {

		Map<String, T> returnMap = new HashMap<String, T>();
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Iterator<String> keyIter = jsonObject.keys();
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			T value = (T) jsonObject.get(key);
			returnMap.put(key, value);
		}
		return returnMap;
	}

	// 从json对象集合表达式中得到一个java对象列表
	public static List getListByJson(String jsonString, Class pojoClass) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		Object pojoValue;
		List list = new ArrayList();
		for (int i = 0; i < jsonArray.size(); i++) {
			jsonObject = jsonArray.getJSONObject(i);
			pojoValue = JSONObject.toBean(jsonObject, pojoClass);
			list.add(pojoValue);
		}
		return list;
	}

	// 从json数组中解析出java字符串数组
	public static Object[] getStringArrayByJsonByTable(String jsonString) {
		JSONObject jsonObj = JSONObject.fromObject(jsonString);
		JSONArray jsonarr = jsonObj.getJSONArray("Table");
		return jsonarr.toArray();
	}
}
