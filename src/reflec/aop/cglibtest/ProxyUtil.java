package reflec.aop.cglibtest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * �����������Cglib�����MethodInterceptor�ӿ��е�intercept�����ľ�����
 * Created by szh on 2017/4/22.
 */
public class ProxyUtil {
    //    ProxyEntity proxyEntity;
//
//    public ProxyUtil(ProxyEntity proxyEntity) {
//        this.proxyEntity = proxyEntity;
//    }
    /*
    ���س����е�beforeע��
    ����invokesuper����
    �������е�after����
     */
    Reflect reflect;

    public ProxyUtil() throws ClassNotFoundException {
        reflect = new Reflect();
    }

    public void getMethod(String name) {
        Map<String, String> map = new HashMap<>();

    }

    //�÷����������
    public Object generateEntity(ProxyEntity proxyEntity) throws Throwable {
//        System.out.println("����beforeע��ķ���"); //
//        Map<String,String> methodMap =reflect.getMap();
//        for(Map.Entry<String,String> map :methodMap.entrySet() ){
//            if(map.getValue().equals(proxyEntity.getClazz().toString().substring()))
//        }
//        System.out.println(proxyEntity.getMethodProxy().toString());
        String proxyMethodValue = proxyEntity.getMethod().toString().substring(proxyEntity.getMethod().toString().lastIndexOf(" ") + 1, proxyEntity.getMethod().toString().indexOf("("));
//        System.out.println(proxyMethodValue); //reflec.aop.cglibtest.Music.sing
        Map<String, String> methodMap = reflect.getMap();
        for (Map.Entry<String, String> map : methodMap.entrySet()) {
            if (map.getValue().equals(proxyMethodValue)) {
                String[] str = mapKeyDivision(map.getKey());
                if (str[2].equals("before")) {
                    Class<?> clazz = Class.forName(str[1], false, Thread.currentThread().getContextClassLoader()); // ���ظ���
                    Method method = clazz.getDeclaredMethod(str[0]);
                    method.invoke(clazz.newInstance(), null); // ��һ����Ҫԭʼ����
                }
            }
        }
//        System.out.println(proxyEntity.getClazz().toString().subSequence(6,proxyEntity.getClazz().toString().length()));
        //����������Ϊ�޷��ܺý������֪ͨ
        return doAfter(proxyEntity,methodMap);
    }
    private Object  doAfter(ProxyEntity proxyEntity,Map<String,String> map) throws Throwable {
        Object object = proxyEntity.getMethodProxy().invokeSuper(proxyEntity.getObject(), proxyEntity.getArgs());  // ���÷���
        String proxyMethodValue = proxyEntity.getMethod().toString().substring(proxyEntity.getMethod().toString().lastIndexOf(" ") + 1, proxyEntity.getMethod().toString().indexOf("("));
        for(Map.Entry<String,String> aMap:map.entrySet()){
            if (aMap.getValue().equals(proxyMethodValue)){
                String[] str =mapKeyDivision(aMap.getKey());
                    if(str[2].equals("after")){
                        Class<?> clazz = Class.forName(str[1], false, Thread.currentThread().getContextClassLoader()); // ���ظ���
                        Method method = clazz.getDeclaredMethod(str[0]);
                        method.invoke(clazz.newInstance(), null); // ��һ����Ҫԭʼ����
                    }
                }
            }
        return object;
    }

    private String[] mapKeyDivision(String value) {
//        String value="beforeSing-reflec.aop.cglibtest.Player-before";
        String[] str = new String[10];
        str[0] = value.substring(0, value.indexOf("-"));
        str[1] = value.substring(value.indexOf("-") + 1, value.lastIndexOf("-"));
        str[2]=value.substring(value.lastIndexOf("-")+1,value.length());
        return str;
        /*
             beforeSing-reflec.aop.cglibtest.Player
             beforeSing
             reflec.aop.cglibtest.Player
         */
    }

    public void mapTest(){
        String value="beforeSing-reflec.aop.cglibtest.Player-before";
        String[] str = new String[10];
        str[0] = value.substring(0, value.indexOf("-"));
        str[1] = value.substring(value.indexOf("-") + 1, value.lastIndexOf("-"));
        str[2]=value.substring(value.lastIndexOf("-")+1,value.length());
        System.out.println(str[0]);
        System.out.println(str[1]);
        System.out.println(str[2]);
        /*
             reflec.aop.cglibtest.Music.sing------------------beforeValue
              beforeSing
             reflec.aop.cglibtest.Player
             before
         */
    }
    
	public static void main(String[] args) {

		try {
			ProxyUtil proxy = new ProxyUtil();
			proxy.mapTest();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
