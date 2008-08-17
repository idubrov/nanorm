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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.code.nanorm.ResultCallback;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * Implementation of {@link ResultCallbackSource} that pushes the result into
 * the array in the property, identified by given getter/setter.
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class ArrayCallbackSource implements ResultCallbackSource {

	private final Getter getter;

	private final Setter setter;
	
	private final Class<?> componentClass;

	/**
	 * Constructor.
	 * 
	 * @param getter property getter
	 * @param setter property setter
	 * @param componentClass array component type
	 */
	public ArrayCallbackSource(Getter getter, Setter setter, Class<?> componentClass) {
		this.getter = getter;
		this.setter = setter;
		this.componentClass = componentClass;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public ResultCallback forInstance(final Object instance) {
		return new ResultCallback() {
			private final List<Object> list = new ArrayList<Object>();
			{
				// Populate from property
				Object[] data = (Object[]) getter.getValue(instance);
				if(data != null) {
					Collections.addAll(list, data);
				}
			}
			
			/**
			 * {@inheritDoc}
			 */
			public void handleResult(Object obj) {
				list.add(obj);
			}

			/**
			 * {@inheritDoc}
			 */
			public void commit() {
				Object[] array = (Object[]) Array.newInstance(componentClass, list.size());
				setter.setValue(instance, list.toArray(array));
			}
		};
	}
}
