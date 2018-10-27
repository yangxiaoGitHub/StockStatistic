package cn.com.clazz;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;

import com.sun.istack.internal.Nullable;

public final class CachedIntrosResults {

	/** The BeanInfo object for the introspected bean class. */
	private final BeanInfo beanInfo;
	
	/** PropertyDescriptor objects keyed by property name String. */
	private final Map<String, PropertyDescriptor> propertyDescriptorCache;

	/**
	 * Create a new CachedIntrospectionResults instance for the given class.
	 *
	 */
	private CachedIntrosResults(Class<?> beanClass) throws Exception {
		try {
			this.beanInfo = Introspector.getBeanInfo(beanClass);
			this.propertyDescriptorCache = new LinkedHashMap<>();

			// This call is slow so we do it once.
			PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if (Class.class == beanClass &&
						("classLoader".equals(pd.getName()) ||  "protectionDomain".equals(pd.getName()))) {
					// Ignore Class.getClassLoader() and getProtectionDomain() methods - nobody needs to bind to those
					continue;
				}
				pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
				this.propertyDescriptorCache.put(pd.getName(), pd);
			}

			// Explicitly check implemented interfaces for setter/getter methods as well,
			// in particular for Java 8 default methods...
			Class<?> currClass = beanClass;
			while (currClass != null && currClass != Object.class) {
				introspectInterfaces(beanClass, currClass);
				currClass = currClass.getSuperclass();
			}
		}
		catch (IntrospectionException ex) {
			throw new IntrospectionException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]");
		}
	}
	
	private void introspectInterfaces(Class<?> beanClass, Class<?> currClass) throws IntrospectionException, IOException {
		for (Class<?> ifc : currClass.getInterfaces()) {
				for (PropertyDescriptor pd : Introspector.getBeanInfo(ifc).getPropertyDescriptors()) {
					PropertyDescriptor existingPd = this.propertyDescriptorCache.get(pd.getName());
					if (existingPd == null ||
							(existingPd.getReadMethod() == null && pd.getReadMethod() != null)) {
						pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
						this.propertyDescriptorCache.put(pd.getName(), pd);
					}
				}
				introspectInterfaces(ifc, ifc);
		}
	}
	
	/**
	 * Create CachedIntrospectionResults for the given bean class.
	 *
	 */
	public static CachedIntrosResults forClass(Class<?> beanClass) throws Exception {

		CachedIntrosResults results = new CachedIntrosResults(beanClass);
		ConcurrentMap<Class<?>, CachedIntrosResults> classCacheToUse = new ConcurrentHashMap<>(64);
		CachedIntrosResults existing = classCacheToUse.putIfAbsent(beanClass, results);
		return (existing != null ? existing : results);
	}
	
	private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(Class<?> beanClass, PropertyDescriptor pd) throws IntrospectionException {
		try {
			return new TypeAwarePropertyDes(beanClass, pd.getName(), pd.getReadMethod(), pd.getWriteMethod(),
					pd.getPropertyEditorClass());
		} catch (IntrospectionException ex) {
			throw new IntrospectionException("Failed to re-introspect class [" + beanClass.getName() + "]");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors() throws IntrospectionException {
		PropertyDescriptor[] pds = new PropertyDescriptor[this.propertyDescriptorCache.size()];
		int i = 0;
		for (PropertyDescriptor pd : this.propertyDescriptorCache.values()) {
			pds[i] = (pd instanceof TypeAwarePropertyDes ? pd :
					buildGenericTypeAwarePropertyDescriptor(getBeanClass(), pd));
			i++;
		}
		return pds;
	}
	
	@Nullable
	public PropertyDescriptor getPropertyDescriptor(String name) throws IntrospectionException {
		PropertyDescriptor pd = this.propertyDescriptorCache.get(name);
		if (pd == null && StringUtils.isBlank(name)) {
			// Same lenient fallback checking as in Property...
			pd = this.propertyDescriptorCache.get(StringUtils.uncapitalize(name));
			if (pd == null) {
				pd = this.propertyDescriptorCache.get(StringUtils.capitalize(name));
			}
		}
		return (pd == null || pd instanceof TypeAwarePropertyDes ? pd :
				buildGenericTypeAwarePropertyDescriptor(getBeanClass(), pd));
	}
	
	Class<?> getBeanClass() {
		return this.beanInfo.getBeanDescriptor().getBeanClass();
	}
}
