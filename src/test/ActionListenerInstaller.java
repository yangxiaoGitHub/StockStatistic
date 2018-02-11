package test;

import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ActionListenerInstaller {
	public static void processAnnotations(Object obj) {
		try {
			Class cl = obj.getClass();
			for (Method method : cl.getDeclaredMethods()) {
				ActionListenerFor action = method.getAnnotation(ActionListenerFor.class);
				if (action != null) {
					Field field = cl.getDeclaredField(action.source());
					field.setAccessible(true);
					addListener(field.get(obj), obj, method);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void addListener(Object source, final Object param, final Method method)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		InvocationHandler handler = new InvocationHandler() {
			public Object invoke(Object proxy, Method method_, Object[] args) throws Throwable {
				return method.invoke(param);
			}
		};

		Object listener = Proxy.newProxyInstance(null, new Class[] { java.awt.event.ActionListener.class }, handler);
		Method adder = source.getClass().getMethod("addActionListener", ActionListener.class);
		adder.invoke(source, listener);
	}
}
