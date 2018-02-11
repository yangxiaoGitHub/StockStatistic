package test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxyFactory implements InvocationHandler {
	private Object targetObject;

	public Object createProxyInstance(Object targetObject) {
		this.targetObject = targetObject;
		return Proxy.newProxyInstance(this.targetObject.getClass().getClassLoader(), 
									  this.targetObject.getClass().getInterfaces(), 
									  this);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		PersonService bean = (PersonService) this.targetObject;
		Object result = null;
		if (bean.getUser() != null) {
			result = method.invoke(targetObject, args);
		}
		return result;
	}
}