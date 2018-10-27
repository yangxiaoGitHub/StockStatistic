package cn.aop.cglib;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.aop.After;
import cn.aop.Aspect;
import cn.aop.Before;

public class Reflect {
    Map<String,String> map ;   //������Ƿ������Լ���ע��
    Map<String,String> clazzMap;
    
	public Reflect() {
		map = new HashMap<>();
		clazzMap = new HashMap<>();
		getAnnotationClass();
	}

    public Map<String, String> getMap() { //���ﷵ�ص����Ѿ�ȫ����õ�map����ProxyUtilʹ��
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
						Before before = method.getAnnotation(Before.class); // ��ȡע�� ,�����������clazz.getAnnotation()��ȡ������ע��
						String beforeValue = before.value();
						map.put(method.getName() + "-" + clazzName + "-" + "before", beforeValue.substring(0, beforeValue.length() - 2)); // ������Ƿ�������ע����
						// System.out.println(method.getName()+ "-"+clazzName+"-"+"before");  //������  beforeSing-reflec.aop.cglibtest.Player
						// System.out.println(beforeValue.substring(0,beforeValue.length()-2)+"------------------beforeValue");
					}
					if (method.isAnnotationPresent(After.class)) {
						After after = method.getAnnotation(After.class); // ��ȡע�� ,�����������clazz.getAnnotation()��ȡ������ע��
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
//        map.put(headStr,endStr); // �����Է�ֹ
//    }
}
