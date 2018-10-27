package cn.aop.cglib;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.aop.After;
import cn.aop.Aspect;
import cn.aop.Before;

public class Reflect {
    Map<String,String> map ;   //存入的是方法名以及其注解
    Map<String,String> clazzMap;
    
	public Reflect() {
		map = new HashMap<>();
		clazzMap = new HashMap<>();
		getAnnotationClass();
	}

    public Map<String, String> getMap() { //这里返回的是已经全部存好的map方面ProxyUtil使用
        return map;
    }

	public void getAnnotationClass() {
		//String clazzName = "cn.aop.cglib.Player";
		String clazzName = "cn.log.Message";
		try {
			Class<?> clazz = Class.forName(clazzName, false, Thread.currentThread().getContextClassLoader());
			if (clazz.isAnnotationPresent(Aspect.class)) {
				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(Before.class)) {
						Before before = method.getAnnotation(Before.class); // 获取注解 ,在这里如果是clazz.getAnnotation()获取的是类注解
						String beforeValue = before.value();
						map.put(method.getName() + "-" + clazzName + "-" + "before", beforeValue.substring(0, beforeValue.length() - 2)); // 存入的是方法名和注解名
						// System.out.println(method.getName()+ "-"+clazzName+"-"+"before");  //输出结果  beforeSing-reflec.aop.cglibtest.Player
						// System.out.println(beforeValue.substring(0,beforeValue.length()-2)+"------------------beforeValue");
					}
					if (method.isAnnotationPresent(After.class)) {
						After after = method.getAnnotation(After.class); // 获取注解 ,在这里如果是clazz.getAnnotation()获取的是类注解
						String afterValue = after.value();
						map.put(method.getName() + "-" + clazzName + "-" + "after", afterValue.substring(0, afterValue.length() - 2));
						// System.out.println(afterValue);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
	public static void main(String args[]) {

		try {
			Reflect reflect = new Reflect();
			reflect.getAnnotationClass();
			for (String key : reflect.map.keySet()) {
				System.out.println(key + "-----" + reflect.map.get(key));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
//    private void divisionValue(String name) throws ClassNotFoundException {
//        String headStr=name.substring(0,name.lastIndexOf("."));
//        String endStr=name.substring(name.lastIndexOf(".")+1,name.length());
//        map.put(headStr,endStr); // 存入以防止
//    }
}
