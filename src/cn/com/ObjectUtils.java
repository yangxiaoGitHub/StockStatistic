package cn.com;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.log.Log;

public class ObjectUtils {
	static Log log = Log.getLoger();

	/**
	 * 获得一个对象属性的字节流
	 *
	 */
	public static void printProperties(Object entityName) throws Exception {

		Class clas = entityName.getClass();
		Field[] fields = clas.getDeclaredFields();
		System.out.println("----------------" + entityName.getClass().getName() + "'s name and value of properties-----------------");
		for (Field field : fields) {
			Object value = invokeMethod(entityName, field.getName(), null);
			if (value != null) {
				System.out.println(field.getName() + ": \t" + value + "\t");
			}
		}
	}

	/**
	 * 获得对象属性的值
	 *
	 */
	private static Object invokeMethod(Object owner, String methodName, Object[] args) throws Exception {

		Class ownerClass = owner.getClass();
		methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
		Method method = null;
		try {
			method = ownerClass.getMethod("get" + methodName);
		} catch (NoSuchMethodException ex) {
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
			return null;
		}
		return method.invoke(owner);
	}
}
