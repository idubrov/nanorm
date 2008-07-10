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

/**
 * Event-based interface of property path visitor.
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 * @param <T> property visitor result type
 */
public interface PropertyVisitor<T> {

	/**
	 * Property path beginning for case when property path is applied to the
	 * regular instance.
	 * 
	 * @param beanClass type this property path is applied to.
	 * @param path property path
	 */
	void visitBegin(Class<?> beanClass, String path);

	/**
	 * Visit property access.
	 * 
	 * @param pos position in the property path (for better error reporting).
	 * @param property property name
	 * @param getter property getter
	 * @param isLast is that last element in the property path
	 * @param beanClass instance type
	 * @return hint to the caller, type of the visited property. Could be null
	 *         (if no information is available).
	 */
	Class<?> visitProperty(int pos, String property, Method getter,
			boolean isLast, Class<?> beanClass);

	/**
	 * Visit indexing operator.
	 * 
	 * @param pos position in the property path (for better error reporting)
	 * @param index position in the array
	 * @param isLast is that last element in the property path
	 * @param beanClass instance type
	 * @return hint to the caller, type of the visited array element. Could be
	 *         null (if no information is available).
	 */
	Class<?> visitIndex(int pos, int index, boolean isLast, Class<?> beanClass);

	/**
	 * Finish the property path visiting and return the result.
	 * 
	 * @return result
	 */
	T visitEnd();
}
