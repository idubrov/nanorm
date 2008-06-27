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
package com.google.code.nanorm.internal.introspect.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.google.code.nanorm.exceptions.IntrospectionException;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.PropertyNavigator;

/**
 * Reflection-based implementation of parameter getter.
 * 
 * @author Ivan Dubrov
 * @version 1.0 06.06.2008
 */
public class ReflectParameterGetter implements Getter {

	private final String path;

	private final ReflectIntrospectionFactory factory;

	private final Type[] types;

	/**
	 * Constructor.
	 * 
	 * @param factory introspection factory
	 * @param types parameter types
	 * @param path property path
	 */
	public ReflectParameterGetter(ReflectIntrospectionFactory factory, Type[] types,
			String path) {
		this.factory = factory;
		this.types = types;
		this.path = path;
	}

	/**
	 * @see com.google.code.nanorm.internal.introspect.Getter#getValue(java.lang.Object)
	 */
	public Object getValue(final Object instance) {
		// Parameter access
		// TODO: Move to property navigator?
		int pos = path.indexOf('.');
		if (pos == -1) {
			pos = path.length();
		}
		String context = path.substring(0, pos);

		int parameter;
		if (context.equals(IntrospectionFactory.ZERO_PARAMETER_ALIAS)) {
			parameter = 0;
		} else {
			parameter = Integer.parseInt(context) - 1;
		}
		Object current = Array.get(instance, parameter);

		PropertyNavigator nav = new PropertyNavigator(path, pos + 1);
		while (!nav.hasNext()) {
			pos = nav.getPosition();

			int token = nav.next();
			if (token == PropertyNavigator.INDEX) {
				current = Array.get(current, nav.getIndex());
			} else if (token == PropertyNavigator.PROPERTY) {
				Method getter = factory.lookupGetter(current.getClass(), nav
						.getProperty());

				try {
					current = getter.invoke(current);
				} catch (Exception e) {
					throw new IntrospectionException("Failed to get property "
							+ path.substring(0, pos) + " of property path "
							+ path, e);
				}
			} else {
				throw new IllegalStateException("Unexpected token type "
						+ token + " while following property path " + path);
			}
		}
		return current;
	}

	/**
	 * {@inheritDoc}
	 */
	public Type getType() {
		return factory.getParameterType(types, path);
	}

}
