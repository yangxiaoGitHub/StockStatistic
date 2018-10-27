package test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 获取父类中的所有属性和方法 工具类
 *
 */
public class SuperClassReflectionUtils {
   /**
    * 循环向上转型，获取对象的DeclaredMethod
    * @param object: 子类对象
    * @param methodName: 父类中的方法名
    * @param parameterTypes: 父类中的方法参数类型
    * @return 父类中的方法对象
    *
    */
    public static Method getDeclaredMethod(Object object, String methodName, Class<?> ... parameterTypes) {
       Method method = null;
       for (Class<?> clazz = object.getClass(); clazz!=Object.class; clazz=clazz.getSuperclass()) {
           try {
             method = clazz.getDeclaredMethod(methodName, parameterTypes);
             return method;
           } catch(Exception ex) {
             //这里什么都不要做！
           }
       }
       return null;
    }
    
    /**
     * 直接调用对象方法，而忽略修饰符（private, protected, default）
     * @param object: 子类对象
     * @param methodName: 父类中的方法名
     * @param parameterTypes: 父类中的方法参数类型
     * @param parameters: 父类中的方法参数
     * @return 父类中方法的执行结果
     *
     */
     public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] 

parameters) {
         //根据对象、方法名和对应的方法参数，通过反射调用上面的方法获取Method对象
         Method method = getDeclaredMethod(object, methodName, parameterTypes);
         //抑制Java对方法进行检查，主要是针对私有方法而言
         method.setAccessible(true);
         try {
              if (null != method) {
                 //调用object的method所代表的方法，其方法的参数是parameters
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
      * 循环向上转型，获取对象的DeclaredField
      * @param object: 子类对象
      * @param fieldName: 父类中的属性名
      * @return 父类中的属性对象
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
                //这里什么都不要做！
             }
         }
         return null;
      }

      /**
       * 直接设置对象属性值，忽略private/protected修饰符，也不过setter
       * @param objct: 子类对象
       * @param fieldName: 父类中的属性名
       * @param value：将要设置的值
       *
       */
       public static void setFieldValue(Object object, String fieldName, Object value) {
          //根据对象和属性名通过反射调用上面的方法获取Field对象
          Field field = getDeclaredField(object, fieldName);
          //抑制java对其的检查
          field.setAccessible(true);
          try {
            //将object中field所代表的值设置为value
            field.set(object, value);
          } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
          } catch(IllegalAccessException ex) {
            ex.printStackTrace();
          }
       }

       /**
        * 直接读取对象的属性，忽略private/protected修饰符，也不经过getter
        * @param object：子类对象
        * @param fieldName：父类中的属性名
        * @return：父类中的属性值
        *
        */
        public static Object getFieldValue(Object object, String fieldName) {
           //根据对象和属性名通过反射调用上面的方法获取Field对象
           Field field = getDeclaredField(object, fieldName);
           //抑制java对其的检查
           field.setAccessible(true);
           try {
             //获取object中field所代表的属性值
             return field.get(object);
           } catch(Exception ex) {
             ex.printStackTrace();
           }
           return null;
        }
}