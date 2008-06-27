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
package com.google.code.nanorm.internal.mapping.result;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.code.nanorm.exceptions.IntrospectionException;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * Utility class for finding the result class based on the provided type and
 * creating {@link ResultCallbackSource} to be used fo pushing the results into
 * the property.
 * 
 * For example, result bean type for <code>java.util.List&lt;Bean&gt</code> will
 * be <code>Bean</code>
 * 
 * Types currently supported:
 * <ul>
 * <li>{@link Collection}</li>
 * <li>{@link List}</li>
 * <li>{@link ArrayList}</li>
 * <li>regular bean</li>
 * </ul>
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class ResultCollectorUtil {

	/**
	 * Create result callback for property identified by given getter and
	 * setter.
	 * 
	 * @param getter getter
	 * @param setter setter
	 * @param mappingSource any object that identifies the source of the
	 *            results. Used for better error reporting.
	 * @return result callback source
	 */
	public static ResultCallbackSource createResultCallback(
			Getter getter, Setter setter, Object mappingSource) {
		Type type = getter.getType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			if (pt.getRawType() instanceof Class) {
				Class<?> rawClass = (Class<?>) pt.getRawType();
				if (rawClass == Collection.class || rawClass == List.class
						|| rawClass == ArrayList.class) {
					return new ArrayListCallbackSource(getter, setter);
				}
			}
		} else {
			return new SingleResultCallbackSource(setter, mappingSource);
		}
		throw new IntrospectionException("Unexpected result type for mapping "
				+ mappingSource);
	}

	/**
	 * Get the result bean class (unwrap it from the collection generic). Result
	 * bean is the type of the object produced by result map.
	 * 
	 * For example, result bean type for
	 * <code>java.util.List&lt;Bean&gt</code> will be <code>Bean</code>.
	 * 
	 * @param resultType result type
	 * @return result bean class
	 */
	public static Class<?> resultClass(Type resultType) {
		Class<?> resultClass = null;
		if (resultType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) resultType;
			if (pt.getRawType() instanceof Class) {
				Class<?> rawClass = (Class<?>) pt.getRawType();
				if (rawClass == List.class || rawClass == Collection.class
						|| rawClass == ArrayList.class) {
					Type beanType = pt.getActualTypeArguments()[0];
					if (beanType instanceof Class<?>) {
						resultClass = (Class<?>) beanType;
					} else {
						throw new IntrospectionException(
								"Parameter for result generic must be concrete class! Result generic type is "
										+ resultType);
					}
				}
			}
		} else if (resultType instanceof Class<?>) {
			resultClass = (Class<?>) resultType;
		}
		if (resultClass == null) {
			throw new IntrospectionException("Result type not supported! Got type "
					+ resultType);
		}
		return resultClass;
	}
}
