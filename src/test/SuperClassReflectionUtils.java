package test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ��ȡ�����е��������Ժͷ��� ������
 *
 */
public class SuperClassReflectionUtils {
   /**
    * ѭ������ת�ͣ���ȡ�����DeclaredMethod
    * @param object: �������
    * @param methodName: �����еķ�����
    * @param parameterTypes: �����еķ�����������
    * @return �����еķ�������
    *
    */
    public static Method getDeclaredMethod(Object object, String methodName, Class<?> ... parameterTypes) {
       Method method = null;
       for (Class<?> clazz = object.getClass(); clazz!=Object.class; clazz=clazz.getSuperclass()) {
           try {
             method = clazz.getDeclaredMethod(methodName, parameterTypes);
             return method;
           } catch(Exception ex) {
             //����ʲô����Ҫ����
           }
       }
       return null;
    }
    
    /**
     * ֱ�ӵ��ö��󷽷������������η���private, protected, default��
     * @param object: �������
     * @param methodName: �����еķ�����
     * @param parameterTypes: �����еķ�����������
     * @param parameters: �����еķ�������
     * @return �����з�����ִ�н��
     *
     */
     public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] 

parameters) {
         //���ݶ��󡢷������Ͷ�Ӧ�ķ���������ͨ�������������ķ�����ȡMethod����
         Method method = getDeclaredMethod(object, methodName, parameterTypes);
         //����Java�Է������м�飬��Ҫ�����˽�з�������
         method.setAccessible(true);
         try {
              if (null != method) {
                 //����object��method������ķ������䷽���Ĳ�����parameters
                 return method.invoke(object, parameters);
              }
         } catch(IllegalArgumentException ex) {
           ex.printStackTrace();
         } catch(IllegalAccessException ex) {
           ex.printStackTrace();
         } catch(InvocationTargetException ex) {
           ex.printStackTrace();
         }
         return null;
     }

     /**
      * ѭ������ת�ͣ���ȡ�����DeclaredField
      * @param object: �������
      * @param fieldName: �����е�������
      * @return �����е����Զ���
      *
      */
      public static Field getDeclaredField(Object object, String fieldName) {
         Field field = null;
         Class<?> clazz = object.getClass();
         for (; clazz!=Object.class; clazz=clazz.getSuperclass()) {
             try {
                field = clazz.getDeclaredField(fieldName);
                return field;
             } catch(Exception ex) {
                //����ʲô����Ҫ����
             }
         }
         return null;
      }

      /**
       * ֱ�����ö�������ֵ������private/protected���η���Ҳ����setter
       * @param objct: �������
       * @param fieldName: �����е�������
       * @param value����Ҫ���õ�ֵ
       *
       */
       public static void setFieldValue(Object object, String fieldName, Object value) {
          //���ݶ����������ͨ�������������ķ�����ȡField����
          Field field = getDeclaredField(object, fieldName);
          //����java����ļ��
          field.setAccessible(true);
          try {
            //��object��field�������ֵ����Ϊvalue
            field.set(object, value);
          } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
          } catch(IllegalAccessException ex) {
            ex.printStackTrace();
          }
       }

       /**
        * ֱ�Ӷ�ȡ��������ԣ�����private/protected���η���Ҳ������getter
        * @param object���������
        * @param fieldName�������е�������
        * @return�������е�����ֵ
        *
        */
        public static Object getFieldValue(Object object, String fieldName) {
           //���ݶ����������ͨ�������������ķ�����ȡField����
           Field field = getDeclaredField(object, fieldName);
           //����java����ļ��
           field.setAccessible(true);
           try {
             //��ȡobject��field�����������ֵ
             return field.get(object);
           } catch(Exception ex) {
             ex.printStackTrace();
           }
           return null;
        }
}