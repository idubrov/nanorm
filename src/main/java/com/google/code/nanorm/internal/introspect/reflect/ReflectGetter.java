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

import java.lang.reflect.Type;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectUtils;
import com.google.code.nanorm.internal.introspect.PropertyVisitor;
import com.google.code.nanorm.internal.introspect.VoidPropertyVisitor;

/**
 * Reflection-based nested property getter.
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class ReflectGetter implements Getter {
	
	private final String path;

	private final Class<?> beanClass;
	
	private final Type[] types;
	
	private final ReflectIntrospectionFactory factory;

	/**
	 * Constructor.
	 * 
	 * @param factory factory
	 * @param beanClass bean class (for regular getter)
	 * @param types palameter types (for parameter getter)
	 * @param path property path
	 */
	public ReflectGetter(ReflectIntrospectionFactory factory, Class<?> beanClass, Type[] types,
			String path) {
		this.factory = factory;
		this.beanClass = beanClass;
		this.types = types;
		this.path = path;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValue(final Object instance) {
		PropertyVisitor<Object> visitor = new ReflectPropertyVisitor(factory, instance);
		if(types != null) {
			return IntrospectUtils.visitPath(path, types, visitor, null);
		}
		return IntrospectUtils.visitPath(path, beanClass, visitor, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public Type getType() {
		Type[] type = new Type[1];
		if(types != null) {
			IntrospectUtils.visitPath(path, types, VoidPropertyVisitor.INSTANCE, type);
		} else {
			IntrospectUtils.visitPath(path, beanClass, VoidPropertyVisitor.INSTANCE, type);
		}
		return type[0];
	}
}
