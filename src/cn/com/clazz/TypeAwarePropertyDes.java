package cn.com.clazz;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import com.sun.istack.internal.Nullable;

final class TypeAwarePropertyDes extends PropertyDescriptor {

	private final Class<?> beanClass;
	@Nullable
	private final Method readMethod;
	@Nullable
	private final Method writeMethod;
	@Nullable
	private Class<?> propertyType;
	private final Class<?> propertyEditorClass;
	
	public TypeAwarePropertyDes(Class<?> beanClass, String propertyName,
			@Nullable Method readMethod, @Nullable Method writeMethod, Class<?> propertyEditorClass)
			throws IntrospectionException {

		super(propertyName, null, null);
		this.beanClass = beanClass;
		
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;

		this.propertyType = readMethod.getReturnType();
		this.propertyEditorClass = propertyEditorClass;
	}
	
	public Class<?> getBeanClass() {
		return this.beanClass;
	}

	@Override
	@Nullable
	public Method getReadMethod() {
		return this.readMethod;
	}

	@Override
	@Nullable
	public Method getWriteMethod() {
		return this.writeMethod;
	}

	@Override
	@Nullable
	public Class<?> getPropertyType() {
		return this.propertyType;
	}

	@Override
	public Class<?> getPropertyEditorClass() {
		return this.propertyEditorClass;
	}
}
