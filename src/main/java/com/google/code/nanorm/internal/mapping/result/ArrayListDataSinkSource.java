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

import java.util.ArrayList;
import java.util.List;

import com.google.code.nanorm.DataSink;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * Implementation of {@link DataSinkSource} that pushes the result into
 * the array list in the property, identified by given getter/setter.
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class ArrayListDataSinkSource implements DataSinkSource {

	private final Getter getter;

	private final Setter setter;

	/**
	 * Constructor.
	 * 
	 * @param getter property getter
	 * @param setter property setter
	 */
	public ArrayListDataSinkSource(Getter getter, Setter setter) {
		this.getter = getter;
		this.setter = setter;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public DataSink forInstance(final Object instance) {
		return new DataSink() {
			private List<Object> list;
			{
				// Check the list in the property and if null, create it and set
				list = (List<Object>) getter.getValue(instance);
				if (list == null) {
					list = new ArrayList<Object>();
					setter.setValue(instance, list);
				}
			}
			
			/**
			 * {@inheritDoc}
			 */
			public void handleData(Object obj) {
				list.add(obj);
			}

			public void commit() {
				// Nothing. We populate array list immediately in handleData
			}
		};
	}
}
