package test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;

public class ReflectionUtilsTest {

	static void getClassFieldAndMethod(Class cur_class) {
		String class_name = cur_class.getName();
		Field[] obj_fields = cur_class.getDeclaredFields();
		for (Field field : obj_fields) {
			field.setAccessible(true);
			System.out.println(class_name + ":" + field.getName());
		}
		
		Method[] obj_methods = cur_class.getDeclaredMethods();
		for (Method method : obj_methods) {
			method.setAccessible(true);
			if (!class_name.equals("java.lang.Object"))
				System.out.println(class_name + "---" + method.getName());
		}
		
		if (cur_class.getSuperclass() != null) {
			getClassFieldAndMethod(cur_class.getSuperclass());
		}
	}
	
	static void getObjField(Object obj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		Class cur_class = obj.getClass();
		getClassFieldAndMethod(cur_class);
		Field vfield = cur_class.getDeclaredField("value");
		vfield.setAccessible(true);
		char[] value = (char[]) vfield.get(obj);
		System.out.println(Arrays.toString(value));
	}
	
	@Test
	public void testAllFields() {
		
		Object obj = new Son();
		try {
			getObjField(obj);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	   /**
	    * 测试获取父类的各个方法对象
	    *
	    */
	    @Test
	    public void testGetDeclaredMethod() {
	       Object obj = new Son();
	       //获取公共方法名
	       Method publicMethod = SuperClassReflectionUtils.getDeclaredMethod(obj, "publicMethod");
	       System.out.println(publicMethod.getName());

	       //获取默认方法名
	       Method defaultMethod = SuperClassReflectionUtils.getDeclaredMethod(obj, "defaultMethod");
	       System.out.println(defaultMethod.getName());

	       //获取私有方法名
	       Method privateMethod = SuperClassReflectionUtils.getDeclaredMethod(obj, "privateMethod");
	       System.out.println(privateMethod.getName());
	    }

	    /**
	     * 测试调用父类的方法
	     * @throws Exception
	     *
	     */
	     @Test
	     public void testInvokeMethod() throws Exception {
	        Object obj = new Son();
	        //调用父类的公共方法
	        SuperClassReflectionUtils.invokeMethod(obj, "publicMethod", null, null);
	        //调用父类的默认方法
	        SuperClassReflectionUtils.invokeMethod(obj, "defaultMethod", null, null);
	        //调用父类的被保护方法
	        SuperClassReflectionUtils.invokeMethod(obj, "defaultMethod", null, null);
	        //调用父类的私有方法
	        SuperClassReflectionUtils.invokeMethod(obj, "privateMethod", null, null);
	     }

	     /**
	      * 测试获取父类的各个属性名
	      *
	      */
	      @Test
	      public void testGetDeclaredField() {
	         Object obj = new Son();
	         //获取公共属性名
	         Field publicField = SuperClassReflectionUtils.getDeclaredField(obj, "publicField");
	         System.out.println(publicField.getName());

	         //获取公共属性名
	         Field defaultField = SuperClassReflectionUtils.getDeclaredField(obj, "defaultField");
	         System.out.println(defaultField.getName());

	         //获取公共属性名
	         Field protectedField = SuperClassReflectionUtils.getDeclaredField(obj, "protectedField");
	         System.out.println(protectedField.getName());

	         //获取公共属性名
	         Field privateField = SuperClassReflectionUtils.getDeclaredField(obj, "privateField");
	         System.out.println(privateField.getName());
	      }

	      @Test
	      public void testSetFieldValue() {
	         Object obj = new Son();
	         System.out.println("原来的各个属性的值：");
	         System.out.println("publicField = " + SuperClassReflectionUtils.getFieldValue(obj, "publicField"));
	         System.out.println("defaultField = " + SuperClassReflectionUtils.getFieldValue(obj, "defaultField"));
	         System.out.println("protectedField = " + SuperClassReflectionUtils.getFieldValue(obj, "protectedField"));
	         System.out.println("privateField = " + SuperClassReflectionUtils.getFieldValue(obj, "privateField"));
	         SuperClassReflectionUtils.setFieldValue(obj, "publicField", "a");
	         SuperClassReflectionUtils.setFieldValue(obj, "defaultField", "b");
	         SuperClassReflectionUtils.setFieldValue(obj, "protectedField", "c");
	         SuperClassReflectionUtils.setFieldValue(obj, "privateField", "d");

	         System.out.println("************************************");
	         System.out.println("将属性值改变后的各个属性值：");
	         System.out.println("publicField = " + SuperClassReflectionUtils.getFieldValue(obj, "publicField"));
	         System.out.println("defaultField = " + SuperClassReflectionUtils.getFieldValue(obj, "defaultField"));
	         System.out.println("protectedField = " + SuperClassReflectionUtils.getFieldValue(obj,"protectedField"));
	         System.out.println("privateField = " + SuperClassReflectionUtils.getFieldValue(obj, "privateField"));
	      }
	}