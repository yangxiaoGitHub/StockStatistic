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
	 * ���һ���������Ե��ֽ���
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
	 * ��ö������Ե�ֵ
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
	 *  �ж��ļ��Ƿ����
	 * 
	 */
	public static String checkFile(String filePath) {

		File file = new File(filePath);
		if (!file.exists()) {
			Exception exception = new FileNotFoundException("�ļ�(" + filePath + ")�����ڣ�");
			exception.printStackTrace();
			return null;
		}
		return filePath;
	}

	/**
	 *  copy��ͬ������ֵ
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
				//����һ����Ҫ����Ķ���
				//��������class.getDeclaredMethods��Ӧ��index
				//�����������󼯺�
				descMethodAccess.invoke(desc, setIndex.intValue(), orgiMethodAccess.invoke(orgi, getIndex));
			}
		}
	}*/
	
	/**
	 * copy��������ͬ������ֵ 
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
				String fieldName = StringUtils.capitalize(field.getName());// �����������
				String getMethod = "get" + fieldName;
				String setMethod = "set" + fieldName;
				if (containMethod(methodList, getMethod) && containMethod(methodList, setMethod)) {
					int getIndex = methodAccess.getIndex(getMethod); // ���get�������±�
					int setIndex = methodAccess.getIndex(setMethod); // ���set�������±�
					// ������get�������������±�ע�ᵽmap��
					methodIndexMap.put(obj.getClass().getName() + "." + getMethod, getIndex);
					// ������set�������������±�ע�ᵽmap��
					methodIndexMap.put(obj.getClass().getName() + "." + setMethod, setIndex);
					validateFieldList.add(fieldName); // ���������Ʒ��뼯����
				}
			}
			fieldMap.put(obj.getClass(), validateFieldList); // ����������������ע�ᵽmap��
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

	private static List<Field> fieldList = new ArrayList<Field>(); //������еı���
	private static List<Method> methodList = new ArrayList<Method>(); //������еķ���

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
