/**
 * Copyright (C) 2008 Ivan S. Dubrov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.nanorm.internal.introspect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.nanorm.exceptions.IntrospectionException;

/**
 * TODO: Setter/getter caching.
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public final class IntrospectUtils {

	/**
	 * Logger for logging all other events.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IntrospectUtils.class);

	private IntrospectUtils() {
		// Nothing.
	}

	/**
	 * Visit nested property path and return something as a result of visit.
	 * 
	 * @param <T> type of result
	 * @param path property path
	 * @param beanClass property bean type
	 * @param visitor property path visitor
	 * @param propertyType property type
	 * @return result of visit
	 */
	public static <T> T visitPath(String path, Class<?> beanClass, PropertyVisitor<T> visitor,
			Type[] propertyType) {
		return visitPath(path, beanClass, null, visitor, propertyType);
	}

	/**
	 * Visit nested property path and return something as a result of visit.
	 * 
	 * @param <T> type of result
	 * @param path property path
	 * @param types parameter types
	 * @param visitor property path visitor
	 * @param propertyType property type
	 * @return result of visit
	 */
	public static <T> T visitPath(String path, Type[] types, PropertyVisitor<T> visitor,
			Type[] propertyType) {
		return visitPath(path, null, types, visitor, propertyType);
	}

	/**
	 * Implemenentation of property path visitor.
	 * 
	 * @param <T> type of result
	 * @param path property path
	 * @param beanClass property bean type
	 * @param types parameter types
	 * @param visitor property path visitor
	 * @param propertyType property type
	 * @return result of visit
	 */
	private static <T> T visitPath(String path, final Class<?> origClass, final Type[] types,
			PropertyVisitor<T> visitor, Type[] propertyType) {
		Class<?> beanClass = origClass;

		PropertyNavigator nav = new PropertyNavigator(path);
		if (types == null) {
			visitor.visitBegin(beanClass, path);
		} else {
			// Parameter access
			int parameter;
			int type = nav.next();
			if (type == PropertyNavigator.PROPERTY) {
				if (!IntrospectionFactory.ZERO_PARAMETER_ALIAS.equals(nav.getProperty())) {
					throw new IllegalArgumentException(
							"First element in the parameter property path must be either "
									+ "'value' or parameter index. Property path: " + path);
				}
				parameter = 0;
			} else if(type == PropertyNavigator.INDEX) {
				parameter = nav.getIndex() - 1;
			} else {
				throw new IllegalStateException("Unexpected parameter property path: " + path);
			}
			// TODO: Bounds check!

			// Emulate parameters as Object[] as the instance type
			visitor.visitBegin(Object[].class, path);

			// ...and indexing which returns Object instance
			Class<?> res = visitor.visitIndex(0, parameter, nav.hasNext(), Object[].class);

			beanClass = (res != null) ? res : (Class<?>) types[parameter];
		}

		Type type = beanClass;
		while (nav.hasNext()) {
			int pos = nav.getPosition();

			int token = nav.next();
			if (token == PropertyNavigator.INDEX) {
				// TODO: Could be generic array!
				if (!beanClass.isArray()) {
					throw new IllegalArgumentException("Array expected at property "
							+ path.substring(0, pos) + "(full property is '" + path
							+ "'). Actual type was '" + beanClass + '\'');
				}
				Class<?> propClass = visitor.visitIndex(pos, nav.getIndex(), nav.hasNext(),
						beanClass);

				if (propClass == null) {
					propClass = beanClass.getComponentType();
				}

				beanClass = propClass;
				type = beanClass;
			} else if (token == PropertyNavigator.PROPERTY) {
				if (beanClass.isPrimitive()) {
					throw new IllegalArgumentException("Primitive type " + beanClass
							+ " found at property '" + path.substring(0, pos)
							+ "' (full property is '" + path + "'), starting from type "
							+ beanClass);
				}
				java.lang.reflect.Method getter = findGetter(beanClass, nav.getProperty());

				Class<?> propClass = visitor.visitProperty(pos, nav.getProperty(), getter, nav
						.hasNext(), beanClass);

				if (propClass == null) {
					// Resolve the return type using the current context
					type = TypeOracle.resolve(getter.getGenericReturnType(), type);

					// Find out concrete Class instance behind the generics
					propClass = TypeOracle.resolveClass(type);
				} else {
					type = propClass;
				}

				beanClass = propClass;
			} else {
				throw new IllegalStateException("Unexpected token type " + token
						+ " while following property path " + path);
			}
		}
		if (propertyType != null) {
			propertyType[0] = type;
		}
		return visitor.visitEnd();
	}

	/**
	 * Find getter method of the bean.
	 * 
	 * @param clazz bean class
	 * @param property property
	 * @return getter method
	 */
	public static Method findGetter(Class<?> clazz, String property) {
		String name = "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
		try {
			return clazz.getMethod(name);
		} catch (NoSuchMethodException e) {
			name = "is" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
			try {
				Method method = clazz.getMethod(name);
				if (method.getReturnType() == boolean.class) {
					return method;
				}
			} catch (NoSuchMethodException e2) {
				// Ignore, handled later.
				LOGGER.debug("Getter for property " + property + " not found in class " + clazz);
			}
		}
		// TODO: Refer to result map or something.
		throw new IntrospectionException("Cannot find getter method for property " + property
				+ " of bean class " + clazz);
	}

	/**
	 * Find getter by case insensitive search
	 * 
	 * @param clazz bean class
	 * @param property property
	 * @return getter method
	 */
	public static Method findGetterCaseInsensitive(Class<?> clazz, String property) {
		for (Method m : clazz.getMethods()) {
			if (m.getName().equalsIgnoreCase("get" + property)) {
				return m;
			} else if (m.getName().equalsIgnoreCase("is" + property)
					&& m.getReturnType() == boolean.class) {
				return m;
			}
		}
		// TODO: Describe the context!
		throw new IntrospectionException("Cannot find getter method for property " + property
				+ " of bean class " + clazz);
	}

	/**
	 * Find setter method for bean.
	 * 
	 * @param clazz bean class
	 * @param property property
	 * @return setter method
	 */
	public static Method findSetter(Class<?> clazz, String property) {
		String name = "set" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
		for (Method method : clazz.getMethods()) {
			if (method.getParameterTypes().length == 1 && method.getName().equals(name)) {
				return method;
			}
		}
		throw new IntrospectionException("Cannot find setter method for property " + property
				+ " of bean class " + clazz);
	}
}
