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
	 * proxy：代理对象
	 * method：拦截的方法
	 * args：方法的参数
     * methodProxy：方法的代理对象
	 */
	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		PersonServiceBean bean = (PersonServiceBean) this.targetObject;
		Object result = null;
		if (bean.getUser() != null) {
			try {
				// beforeAdvice(); -->前置通知
				result = methodProxy.invoke(this.targetObject, args);
				// afterAdvice(); -->后置通知
			} catch (Exception ex) {
				// exceptionAdvice(); -->例外通知
				ex.printStackTrace();
			} finally {
				// finallyAdvice(); -->最终通知
			}
		}
		return result;
	}
}
