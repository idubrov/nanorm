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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.code.nanorm.ResultCallback;
import com.google.code.nanorm.TypeHandlerFactory;
import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.internal.Key;
import com.google.code.nanorm.internal.Request;
import com.google.code.nanorm.internal.config.ResultMapConfig;
import com.google.code.nanorm.internal.config.ResultMappingConfig;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectUtils;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.type.TypeHandler;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 28.05.2008
 */
public class ResultMapImpl implements ResultMap {

	final private Class<?> elementClass;

	final private ResultMapConfig config;

	final private IntrospectionFactory introspectionFactory;

	final private TypeHandlerFactory typeHandlerFactory;

	private DynamicConfig dynamicConfig;

	final private DynamicConfig finDynamicConfig;

	public ResultMapImpl(Type resultType, ResultMapConfig config,
			IntrospectionFactory introspectionFactory,
			TypeHandlerFactory typeHandlerFactory) {
		this.config = config;
		this.introspectionFactory = introspectionFactory;
		this.typeHandlerFactory = typeHandlerFactory;
		this.elementClass = ResultCollectorUtil.resultClass(resultType);

		if (!config.isAuto()) {
			List<ResultMappingConfig> list = Arrays
					.asList(config.getMappings());
			finDynamicConfig = generatePropertyMappers(list);
		} else {
			finDynamicConfig = null;
		}
	}

	public void processResultSet(Request request, ResultSet rs,
			ResultCallback callback) throws SQLException {
		DynamicConfig dc;
		if (config.isAuto()) {
			synchronized (this) {
				if (dynamicConfig == null) {
					List<ResultMappingConfig> configs = generateAutoConfig(rs
							.getMetaData());
					dynamicConfig = generatePropertyMappers(configs);
				}
				dc = dynamicConfig;
			}
		} else {
			// No need to synchronize -- we initialized it in the constructor
			dc = finDynamicConfig;
		}

		Object result;
		Key key = generateRowKey(dc, rs);
		// We have a groupBy
		if (key != null) {
			result = null;

			// Look in the request map for objects under this key
			Map<Key, Object> map = request.getKey2Objects().get(this);
			if (map != null) {
				result = map.get(key);
			}
			// No result -- create new
			if (result == null) {
				result = createResult(request, dc.mappers, rs);
				callback.handleResult(result);

				if (map == null) {
					map = new HashMap<Key, Object>();
					request.getKey2Objects().put(this, map);
				}
				map.put(key, result);
			} else {
				// TODO: ????
			}
		} else {
			// We don't have a groupBy, create new result object
			result = createResult(request, dc.mappers, rs);
			callback.handleResult(result);
		}

		// Always map nested maps
		for (NestedMapPropertyMapper mapper : dc.nestedMappers) {
			mapper.mapResult(request, result, rs);
		}
	}

	private Object createResult(Request request, PropertyMapper[] mappers,
			ResultSet rs) throws SQLException {
		Object result;
		try {
			result = elementClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// TODO: We, probably, can bulk set those...
		for (PropertyMapper mapper : mappers) {
			mapper.mapResult(request, result, rs);
		}
		return result;
	}

	/**
	 * Generate a key that identifies current result row.
	 * @param dc dynamic configuration
	 * @param rs result set
	 * @return key that identifies current result row.
	 * @throws SQLException propagated from result set operations
	 */
	private Key generateRowKey(DynamicConfig dc, ResultSet rs)
			throws SQLException {
		String[] groupBy = config.getGroupBy();
		if (groupBy != null && groupBy.length > 0) {
			Object[] key = new Object[dc.valueGetters.length];
			for (int i = 0; i < dc.valueGetters.length; ++i) {
				key[i] = dc.valueGetters[i].getValue(rs);
			}
			return new Key(key);
		}
		return null;
	}

	/**
	 * Generate configuration for automapping.
	 * 
	 * @param meta result set metainformation
	 * @return collection of result mapping configurations
	 * @throws SQLException
	 */
	private List<ResultMappingConfig> generateAutoConfig(ResultSetMetaData meta)
			throws SQLException {
		List<ResultMappingConfig> configs = new ArrayList<ResultMappingConfig>();

		configs.addAll(Arrays.asList(config.getMappings()));

		Set<String> usedColumns = config.isAuto() ? new HashSet<String>()
				: null;
		for (ResultMappingConfig mappingConfig : config.getMappings()) {
			if (mappingConfig.getColumnIndex() != 0) {
				usedColumns.add(meta.getColumnName(
						mappingConfig.getColumnIndex()).toLowerCase());
			} else {
				usedColumns.add(mappingConfig.getColumn().toLowerCase());
			}
		}
		for (int i = 0; i < meta.getColumnCount(); ++i) {
			String column = meta.getColumnName(i + 1).toLowerCase();

			// If column is not in the mapping config, try to automap it
			if (!usedColumns.contains(column)) {
				ResultMappingConfig config = new ResultMappingConfig();
				config.setColumn(column);

				// Find property with case-insensitive search
				Method getter = IntrospectUtils.findGetterCaseInsensitive(
						elementClass, column);
				if (getter.getName().startsWith("get")) {
					String prop = Character.toLowerCase(getter.getName()
							.charAt(3))
							+ getter.getName().substring(4);
					config.setProperty(prop);
				} else if (getter.getName().startsWith("is")) {
					String prop = Character.toLowerCase(getter.getName()
							.charAt(2))
							+ getter.getName().substring(3);
					config.setProperty(prop);
				}
				if (config.getProperty() == null) {
					// FIXME: Just skip it?
					throw new ConfigurationException(
							"No matching property for column '" + column
									+ "' was found when auto-mapping the bean "
									+ elementClass);
				}
				configs.add(config);
			}
		}
		return configs;
	}

	/**
	 * Generate dynamic configuration (used for quick mapping) from the
	 * collection of result mapping configuration.
	 * 
	 * @param configs
	 * @return dynamic configuration
	 */
	private DynamicConfig generatePropertyMappers(
			List<ResultMappingConfig> configs) {

		String[] groupBy = config.getGroupBy();

		List<PropertyMapper> mappers = new ArrayList<PropertyMapper>();
		List<NestedMapPropertyMapper> nestedMappers = new ArrayList<NestedMapPropertyMapper>();
		List<ValueGetter> keyGenerators = new ArrayList<ValueGetter>();
		
		// TODO: Check we haven't mapped one property twice!
		for (ResultMappingConfig mappingConfig : configs) {
			Type propertyType = introspectionFactory.getPropertyType(
					elementClass, mappingConfig.getProperty());

			Setter setter = introspectionFactory.buildSetter(elementClass,
					mappingConfig.getProperty());

			// TODO: Check all groupBy's are found!
			if (groupBy != null && search(groupBy, mappingConfig.getProperty())) {
				if (mappingConfig.getResultMapConfig() != null) {
					throw new RuntimeException(
							"Group by does not work for nested maps");
				}

				ValueGetter keyGen = new ValueGetter();
				keyGen.typeHandler = typeHandlerFactory
						.getTypeHandler(propertyType);
				keyGen.config = mappingConfig;
				keyGenerators.add(keyGen);
			}

			if (mappingConfig.getSubselect() != null) {
				Type[] parameterTypes = mappingConfig.getSubselect()
						.getParameterTypes();
				if (parameterTypes == null || parameterTypes.length != 1) {
					throw new ConfigurationException(
							"Invalid subselect statement "
									+ "for property "
									+ mappingConfig.getProperty()
									+ " of "
									+ "result map "
									+ config.getId()
									+ " with id "
									+ mappingConfig.getSubselect().getId()
									+ ", subselect statement must have exactly one parameter");
				}
				TypeHandler<?> typeHandler = typeHandlerFactory
						.getTypeHandler(parameterTypes[0]);
				mappers.add(new PropertyMapper(mappingConfig, setter,
						typeHandler));
			} else if (mappingConfig.getResultMapConfig() != null) {
				// TODO: Hacky? Why?
				Getter getter = introspectionFactory.buildGetter(elementClass,
						mappingConfig.getProperty());

				ResultMap nestedMap = new ResultMapImpl(propertyType,
						mappingConfig.getResultMapConfig(),
						introspectionFactory, typeHandlerFactory);
				nestedMappers.add(new NestedMapPropertyMapper(getter, setter,
						nestedMap, mappingConfig));
			} else {
				// TODO: This will fail if collection property is not mapped as
				// nested map!
				TypeHandler<?> typeHandler = typeHandlerFactory
						.getTypeHandler(propertyType);
				mappers.add(new PropertyMapper(mappingConfig, setter,
						typeHandler));
			}
		}
		DynamicConfig dc = new DynamicConfig();
		dc.mappers = mappers.toArray(new PropertyMapper[mappers.size()]);
		dc.nestedMappers = nestedMappers
				.toArray(new NestedMapPropertyMapper[nestedMappers.size()]);
		dc.valueGetters = keyGenerators.toArray(new ValueGetter[keyGenerators
				.size()]);
		return dc;
	}

	private boolean search(String[] array, String value) {
		for (String str : array) {
			if (str.equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Dynamic result map configuration. Used for quick mapping of the result
	 * set.
	 * 
	 * @author Ivan Dubrov
	 */
	private static class DynamicConfig {
		public PropertyMapper[] mappers;

		public ValueGetter[] valueGetters;

		public NestedMapPropertyMapper[] nestedMappers;
		
		DynamicConfig() {
			// Nothing...
		}
	}

	/**
	 * Helper class for retrieving the data from result set for generating a row
	 * key (used for grouping several result rows into one).
	 * 
	 * @author Ivan Dubrov
	 */
	private static class ValueGetter {
		public TypeHandler<?> typeHandler;

		public ResultMappingConfig config;
		
		// Constructor;
		public ValueGetter() {
			// Nothing..
		}

		public Object getValue(ResultSet rs) throws SQLException {
			if (config.getColumnIndex() != 0) {
				return typeHandler.getValue(rs, config.getColumnIndex());
			}
			return typeHandler.getResult(rs, config.getColumn());
		}
	}
}
