package cn.com;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

import com.sun.istack.internal.Nullable;

import cn.com.clazz.CachedIntrosResults;
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

	/**
	 *  判断文件是否存在
	 * 
	 */
	public static String checkFile(String filePath) {

		File file = new File(filePath);
		if (!file.exists()) {
			Exception exception = new FileNotFoundException("文件(" + filePath + ")不存在！");
			exception.printStackTrace();
			return null;
		}
		return filePath;
	}

	/**
	 *  copy相同的属性值
	 *  
	 */
/*	public static void copyProperties(Object desc, Object orgi) {

		MethodAccess descMethodAccess = methodMap.get(desc.getClass());
		if (descMethodAccess == null) {
			descMethodAccess = cache(desc);
		}

		MethodAccess orgiMethodAccess = methodMap.get(orgi.getClass());
		if (orgiMethodAccess == null) {
			orgiMethodAccess = cache(orgi);
		}

		List<String> fieldList = fieldMap.get(orgi.getClass());
		for (String field : fieldList) {
			String getKey = orgi.getClass().getName() + "." + "get" + field;
			String setKey = desc.getClass().getName() + "." + "set" + field;
			Integer setIndex = methodIndexMap.get(setKey);
			if (setIndex != null) {
				int getIndex = methodIndexMap.get(getKey);
				//参数一：需要反射的对象
				//参数二：class.getDeclaredMethods对应的index
				//参数三：对象集合
				descMethodAccess.invoke(desc, setIndex.intValue(), orgiMethodAccess.invoke(orgi, getIndex));
			}
		}
	}*/
	
	/**
	 * copy两对象相同的属性值 
	 *
	 */
	public static void copy_Properties(Object source, Object target) {

		try {
			Class<?> actualEditable = target.getClass();
			PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);

			for (PropertyDescriptor targetPd : targetPds) {
				Method writeMethod = targetPd.getWriteMethod();
				if (writeMethod != null) {
					PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
					if (sourcePd != null) {
						Method readMethod = sourcePd.getReadMethod();
						if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {

							if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
								readMethod.setAccessible(true);
							}
							Object value = readMethod.invoke(source);
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, value);
						}
					}
				}
			}
		} catch (Throwable ex) {
			Exception exception = new IOException("Could not copy properties from source to target", ex);
			exception.printStackTrace();
		}
	}

	private static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws Exception {
		
		CachedIntrosResults cr = CachedIntrosResults.forClass(clazz);
		return cr.getPropertyDescriptors();
	}
	
	/**
	 * Retrieve the JavaBeans {@code PropertyDescriptors} for the given property.
	 * @throws Exception 
	 *
	 */
	@Nullable
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) throws Exception {

		CachedIntrosResults cr = CachedIntrosResults.forClass(clazz);
		return cr.getPropertyDescriptor(propertyName);
	}
	
/*	private static MethodAccess cache(Object obj) {
		synchronized (obj.getClass()) {
			MethodAccess methodAccess = MethodAccess.get(obj.getClass());
			methodList.clear();
			List<Method> methodList = getAllMethods(obj.getClass());
			fieldList.clear();
			List<Field> fieldList = getAllFields(obj.getClass());
			List<String> validateFieldList = new ArrayList<String>();
			for (Field field : fieldList) {
				String fieldName = StringUtils.capitalize(field.getName());// 获得属性名称
				String getMethod = "get" + fieldName;
				String setMethod = "set" + fieldName;
				if (containMethod(methodList, getMethod) && containMethod(methodList, setMethod)) {
					int getIndex = methodAccess.getIndex(getMethod); // 获得get方法的下标
					int setIndex = methodAccess.getIndex(setMethod); // 获得set方法的下标
					// 将类名get方法名，方法下标注册到map中
					methodIndexMap.put(obj.getClass().getName() + "." + getMethod, getIndex);
					// 将类名set方法名，方法下标注册到map中
					methodIndexMap.put(obj.getClass().getName() + "." + setMethod, setIndex);
					validateFieldList.add(fieldName); // 将属性名称放入集合里
				}
			}
			fieldMap.put(obj.getClass(), validateFieldList); // 将类名、属性名称注册到map中
			methodMap.put(obj.getClass(), methodAccess);
			return methodAccess;
		}
	}*/
	
	private static boolean containMethod(List<Method> methodList, String methodName) {
		
		for (Method method : methodList) {
			if (method.getName().equals(methodName)) {
				return true;
			}
		}
		return false;
	}

	private static List<Field> fieldList = new ArrayList<Field>(); //存放类中的变量
	private static List<Method> methodList = new ArrayList<Method>(); //存放类中的方法

	public static List<Field> getAllFields(Class clazz) {
		
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			fieldList.add(field);
		}
		if (clazz.getSuperclass() != null && !clazz.getSuperclass().getName().equals(Object.class.getName())) {
			getAllFields(clazz.getSuperclass());
		}
		return fieldList;
	}

	public static List<Method> getAllMethods(Class clazz) {
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			method.setAccessible(true);
			methodList.add(method);
		}
		if (clazz.getSuperclass() != null && !clazz.getSuperclass().getName().equals(Object.class.getName())) {
			getAllMethods(clazz.getSuperclass());
		}
		return methodList;
	}

/*	private static Map<Class, MethodAccess> methodMap = new HashMap<Class, MethodAccess>();
	private static Map<String, Integer> methodIndexMap = new HashMap<String, Integer>();
	private static Map<Class, List<String>> fieldMap = new HashMap<Class, List<String>>();*/
}
