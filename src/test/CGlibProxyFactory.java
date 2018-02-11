package test;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CGlibProxyFactory implements MethodInterceptor {
	private Object targetObject;

	public Object createProxyInstance(Object targetObject) {
		this.targetObject = targetObject;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.targetObject.getClass());
		enhancer.setCallback(this);
		return enhancer.create();
	}

	/**
	 * proxy���������
	 * method�����صķ���
	 * args�������Ĳ���
     * methodProxy�������Ĵ������
	 */
	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		PersonServiceBean bean = (PersonServiceBean) this.targetObject;
		Object result = null;
		if (bean.getUser() != null) {
			try {
				// beforeAdvice(); -->ǰ��֪ͨ
				result = methodProxy.invoke(this.targetObject, args);
				// afterAdvice(); -->����֪ͨ
			} catch (Exception ex) {
				// exceptionAdvice(); -->����֪ͨ
				ex.printStackTrace();
			} finally {
				// finallyAdvice(); -->����֪ͨ
			}
		}
		return result;
	}
}
