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
package com.google.code.nanorm.internal.config;

import java.lang.reflect.Method;
import java.util.Set;

import com.google.code.nanorm.annotations.Property;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Scalar;
import com.google.code.nanorm.annotations.SelectKey;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.internal.util.Messages;

/**
 * Utility methods to validate configuration.
 * 
 * @author Ivan Dubrov
 */
public class Validation {

	/**
	 * Validate that every property mentioned in the groupBy is explicitly
	 * configured.
	 * 
	 * @param mapper mapper
	 * @param method mapper method result map is applied to
	 * @param resultMap result map
	 * @param propnames configured property names
	 * @throws ConfigurationException configuration is invalid
	 */
	static void validateGroupBy(Class<?> mapper, Method method, ResultMap resultMap,
			Set<String> propnames) throws ConfigurationException {
		assert (resultMap != null);

		for (String prop : resultMap.groupBy()) {
			if (!propnames.contains(prop)) {
				throw new ConfigurationException(Messages.groupByPropertyMissing(prop, mapper,
						method, resultMap));
			}
		}
	}

	/**
	 * Validate usage of {@link SelectKey} annotation.
	 * 
	 * @param selectKey {@link SelectKey} annotation
	 * @param mapper mapper interface
	 * @param method mapper method
	 * @throws ConfigurationException configuration is invalid
	 */
	static void validateSelectKey(SelectKey selectKey, Class<?> mapper, Method method)
			throws ConfigurationException {
		if (selectKey != null && selectKey.type() == SelectKeyType.BEFORE) {
			if (selectKey.value().length() == 0) {
				throw new ConfigurationException(Messages.beforeKeyWithoutSQL(mapper, method));
			}

			if (selectKey.property().length() == 0) {
				throw new ConfigurationException(Messages.beforeKeyWithoutProperty(mapper, method));
			}
		}
	}

	/**
	 * Validate property mapping.
	 * 
	 * @param mapping property mapping annotation
	 * @param mapper mapper interface
	 * @param resultMap result map
	 * @throws ConfigurationException configuration is invalid
	 */
	static void validatePropertyMapping(Property mapping, Class<?> mapper, ResultMap resultMap)
			throws ConfigurationException {
		if (mapping.columnIndex() != 0 && mapping.column().length() > 0) {
			throw new ConfigurationException(Messages.multipleColumn(mapping, mapper, resultMap));
		}

		if (mapping.value().length() == 0) {
			throw new ConfigurationException(Messages.emptyProperty(mapping, mapper, resultMap));
		}

		if (mapping.nestedMap().value().length() > 0) {
			if (mapping.subselect().length() > 0) {
				throw new ConfigurationException(Messages.bothSubselectNested(mapping, mapper,
						resultMap));
			}

			for (String prop : resultMap.groupBy()) {
				if (mapping.value().equals(prop)) {
					throw new ConfigurationException(Messages.bothNestedGroupBy(mapping, mapper,
							resultMap));
				}
			}
		}

		if (mapping.subselectMapper() != Object.class && "".equals(mapping.subselect())) {
			throw new ConfigurationException(Messages.subselectMapperWithoutSubselect(mapping,
					mapper, resultMap));
		}
	}

	static void validateMapAnnotations(Class<?> mapper, Method method) {
		Object[] vals = new Object[3];
		String[] names = {ResultMap.class.getSimpleName(), ResultMapRef.class.getSimpleName(), Scalar.class.getSimpleName() };

		vals[0] = method.getAnnotation(ResultMap.class);
		vals[1] = method.getAnnotation(ResultMapRef.class);
		vals[2] = method.getAnnotation(Scalar.class);

		for (int i = 0; i < 3; ++i) {
			if (vals[i] != null && vals[(i + 1) % 3] != null) {
				throw new ConfigurationException(Messages.mutuallyExclusive(mapper, method,
						names[i], names[(i + 1) % 3]));
			}
		}
	}
}
