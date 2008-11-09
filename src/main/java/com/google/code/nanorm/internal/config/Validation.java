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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import com.google.code.nanorm.annotations.Property;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.SelectKey;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.internal.util.Messages;

/**
 * Utility methods to validate configuration.
 * 
 * @author Ivan Dubrov
 */
@SuppressWarnings("deprecation")
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
		if (selectKey != null) {

			if (selectKey.type() == SelectKeyType.BEFORE) {
				if (selectKey.value().length() == 0) {
					throw new ConfigurationException(Messages.beforeKeyWithoutSQL(mapper, method));
				}

				if (selectKey.property().length() == 0) {
					throw new ConfigurationException(Messages.beforeKeyWithoutProperty(mapper,
							method));
				}
			}

			if (method.getGenericReturnType() == void.class && selectKey.property().length() == 0) {
				throw new ConfigurationException(Messages.voidReturnWithoutProperty(mapper, method));
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

		if (!"".equals(mapping.subselect()) && mapping.columnIndex() == 0
				&& mapping.column().length() == 0) {
			throw new ConfigurationException(Messages.subselectNoColumn(mapping, mapper, resultMap));
		}

	}

	/**
	 * Validate given mutually exclusive annotations are not used together (only
	 * one could be not <code>null</code>).
	 * 
	 * @param mapper mapper interface
	 * @param method mapper method
	 * @param anns annotations
	 */
	static void validateExclusive(Class<?> mapper, Method method, Annotation... anns) {
		for (int i = 0; i < anns.length; ++i) {
			for (int j = i + 1; j < anns.length; ++j) {
				validateExclusive(mapper, method, anns[i], anns[j]);
			}
		}
	}

	/**
	 * Validate annotations that configure mapping ({@link ResultMap},
	 * {@link ResultMapRef} and {@Scalar}).
	 * 
	 * @param mapper mapper interface
	 * @param method mapper method
	 * @param ann1 first annotation to check
	 * @param ann2 second annotation to check
	 */
	static void validateExclusive(Class<?> mapper, Method method, Annotation ann1, Annotation ann2) {
		if (ann1 != null && ann2 != null) {
			throw new ConfigurationException(Messages.mutuallyExclusive(mapper, method, ann1
					.annotationType().getSimpleName(), ann2.annotationType().getSimpleName()));
		}
	}
}
