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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.code.nanorm.ResultCallback;
import com.google.code.nanorm.SQLSource;
import com.google.code.nanorm.TypeHandlerFactory;
import com.google.code.nanorm.annotations.Insert;
import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapList;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.annotations.SelectKey;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.annotations.Source;
import com.google.code.nanorm.annotations.Update;
import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.internal.DynamicFragment;
import com.google.code.nanorm.internal.Fragment;
import com.google.code.nanorm.internal.TextFragment;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.mapping.result.DefaultRowMapper;
import com.google.code.nanorm.internal.mapping.result.RowMapper;
import com.google.code.nanorm.internal.mapping.result.ScalarRowMapper;
import com.google.code.nanorm.internal.util.ToStringBuilder;

/**
 * TODO: Merge processing and searching. Maybe, lazy loading (when referenced).
 * 
 * TODO: Thread safety?
 * 
 * @author Ivan Dubrov
 * @version 1.0 29.05.2008
 */
public class InternalConfiguration {

	private final Set<Class<?>> mapped;

	/**
	 * Result map id is [package].[class]#id
	 */
	private final Map<String, ResultMapConfig> resultMapsConfig;

	/**
	 * Statement id is [package].[class]#method
	 */
	private final Map<StatementKey, StatementConfig> statementsConfig;

	private final List<ResultMappingConfig> postProcessList;

	private final TypeHandlerFactory typeHandlerFactory;

	private final IntrospectionFactory introspectionFactory;

	/**
	 * Constructor.
	 * 
	 * @param typeHandlerFactory type handler factory
	 * @param introspectionFactory introspection factory
	 */
	public InternalConfiguration(TypeHandlerFactory typeHandlerFactory,
			IntrospectionFactory introspectionFactory) {
		resultMapsConfig = new HashMap<String, ResultMapConfig>();
		statementsConfig = new HashMap<StatementKey, StatementConfig>();
		mapped = new HashSet<Class<?>>();
		postProcessList = new ArrayList<ResultMappingConfig>();

		// TODO: Should be configurable
		this.typeHandlerFactory = typeHandlerFactory;
		this.introspectionFactory = introspectionFactory;
	}

	/**
	 * Get statement configuration for method.
	 * 
	 * @param method method
	 * @return statement configuration for method
	 */
	private StatementConfig getStatementConfig(StatementKey key) {
		StatementConfig statementConfig = statementsConfig.get(key);
		if (statementConfig == null) {
			throw new ConfigurationException("Missing configuration for method '" + key + "'");
		}
		return statementConfig;
	}

	/**
	 * Get statement configuration for method.
	 * 
	 * @param mapper mapper interface
	 * @param name statement name (method name)
	 * @param parameters statement parameter types
	 * @return statement configuration for method
	 */
	public StatementConfig getStatementConfig(Class<?> mapper, String name, Type[] parameters) {
		StatementKey key = new StatementKey(mapper, name, parameters);
		return getStatementConfig(key);
	}

	/**
	 * Configure mapper. TODO: Elaborate Javadoc!
	 * 
	 * @param mapper mapper interface
	 */
	public void configure(Class<?> mapper) {
		synchronized (this) {
			if (mapped.add(mapper)) {
				processResultMaps(mapper);
				for (Method method : mapper.getMethods()) {
					processMethod(mapper, method);
				}

				postProcess();
			}
		}
	}

	/**
	 * Post process the configuration, resolving the references of subselects.
	 */
	private void postProcess() {
		// TODO: Check statement return type matches the property type
		for (ResultMappingConfig rmc : postProcessList) {
			StatementKey key = rmc.getSubselectKey();
			if (key != null) {
				StatementConfig stConfig = statementsConfig.get(key);
				// TODO: Better implementation, probably nested hash maps
				if (stConfig == null && key.getParameters() == null) {
					// Parameters types are not known, let's use any statement
					// with
					// matching mapper and statement name
					for (Map.Entry<StatementKey, StatementConfig> entry : statementsConfig
							.entrySet()) {
						StatementKey key2 = entry.getKey();
						if (key.getMapper() == key2.getMapper()
								&& key.getName().equals(key2.getName())) {
							stConfig = entry.getValue();
							break;
						}
					}
				}
				if (stConfig == null) {
					throw new ConfigurationException("Invalid subselect " + rmc.getSubselectKey()
							+ " on result mapping " + rmc + " (result map id is "
							+ rmc.getResultMapConfig().getId() + ')');
				}
				rmc.setSubselect(stConfig);
			}
		}
		postProcessList.clear();
	}

	/**
	 * Process given method and generate statement configuration from it.
	 * 
	 * TODO: Validate we don't have more than one from insert, select, update
	 * and delete.
	 * 
	 * @param method method to gather configuration from
	 */
	private void processMethod(Class<?> mapper, Method method) {
		StatementKey key = new StatementKey(mapper, method.getName(), method
				.getGenericParameterTypes());

		if (statementsConfig.containsKey(key)) {
			// TODO: Log debug message "Query method '" + key + "' is already
			// configured!";
			return;
		}

		// TODO: Invoke method.getGeneryParameters once!
		StatementConfig stConfig = new StatementConfig(key);

		ResultMapConfig config = getResultMapConfig(method);

		// TODO: Check we have only one of those!
		Select select = method.getAnnotation(Select.class);
		Update update = method.getAnnotation(Update.class);
		Insert insert = method.getAnnotation(Insert.class);
		Source source = method.getAnnotation(Source.class);
		String sql = null;
		boolean isUpdate = (update != null);
		boolean isInsert = (insert != null);
		if (select != null) {
			sql = select.value();
		} else if (insert != null) {
			sql = insert.value();
		} else if (update != null) {
			sql = update.value();
		} else if (source != null) {
			Class<? extends SQLSource> sqlSource = source.value();
			Fragment builder = new DynamicFragment(sqlSource, introspectionFactory);
			stConfig.setStatementBuilder(builder);
		} else {
			// Skip method
			// TODO: Logging!
			return;
		}
		if (sql != null) {
			// TODO: Batch case!
			Fragment builder = new TextFragment(sql, method.getGenericParameterTypes(),
					introspectionFactory);
			stConfig.setStatementBuilder(builder);
			stConfig.setUpdate(isUpdate);
			stConfig.setInsert(isInsert);
		}
		// TODO: Check sql is not empty!

		// Configure select key statement
		SelectKey selectKey = method.getAnnotation(SelectKey.class);
		if (selectKey != null) {
			// TODO: Check that for 'before' key we have a SQL to execute!
			StatementKey selectKeyKey = new StatementKey(mapper, method.getName() + ":key", method
					.getGenericParameterTypes());

			StatementConfig selectKeySt = new StatementConfig(selectKeyKey);
			if(selectKey.value().length() > 0) {
				selectKeySt.setStatementBuilder(new TextFragment(selectKey.value(), method
					.getGenericParameterTypes(), introspectionFactory));
			}
			selectKeySt.setParameterTypes(method.getGenericParameterTypes());
			selectKeySt.setResultType(method.getGenericReturnType());
			selectKeySt.setRowMapper(new ScalarRowMapper(method.getGenericReturnType(),
					typeHandlerFactory));
			
			if (!selectKey.property().equals("")) {
				Setter keySetter = introspectionFactory.buildParameterSetter(method
						.getGenericParameterTypes(), selectKey.property());
				stConfig.setKeySetter(keySetter);
			}

			stConfig.setSelectKeyType(selectKey.type());
			stConfig.setSelectKey(selectKeySt);
		}

		// For update we always use method return value, but for select we try
		// to find ResultCallback if return type is void
		// TODO: Should this work for generated keys as well?
		Type returnType = null;
		if (select != null && method.getReturnType() == void.class) {
			int pos = searchResultCallback(method);
			if (pos == -1) {
				throw new ConfigurationException("Cannot deduce return type for query method "
						+ method);
			}
			
			stConfig.setCallbackIndex(pos);
			
			ParameterizedType pt = (ParameterizedType) method.getGenericParameterTypes()[pos];
			returnType = pt.getActualTypeArguments()[0];
			
		} else {
			returnType = method.getGenericReturnType();
		}
		stConfig.setRowMapper(createRowMapper(returnType, config));
		stConfig.setResultType(returnType);

		stConfig.setParameterTypes(method.getGenericParameterTypes());

		statementsConfig.put(key, stConfig);
	}

	// TODO: Move to separate helper class
	private int searchResultCallback(Method method) {
		// Try to find ResultCallback
		Type[] types = method.getGenericParameterTypes();
		for (int i = 0; i < types.length; ++i) {
			if (types[i] instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) types[i];
				if (pt.getRawType() == ResultCallback.class) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param type result type to map result set row to
	 * @param config result map configuration
	 * @return
	 */
	private RowMapper createRowMapper(Type type, ResultMapConfig config) {
		// For primitive types we simply return the mapped value of first column
		if (type instanceof Class<?> && ((Class<?>) type).isPrimitive()) {
			return new ScalarRowMapper(type, typeHandlerFactory);
		}
		// TODO: If config is null, automap it?
		return new DefaultRowMapper(type, config, introspectionFactory, typeHandlerFactory);
	}

	/**
	 * Process result maps defined for given class.
	 * 
	 * @param clazz class to gather result maps from
	 */
	private void processResultMaps(Class<?> clazz) {
		ResultMap classResultMap = clazz.getAnnotation(ResultMap.class);

		if (classResultMap != null) {
			processResultMap(clazz, classResultMap);
		}

		ResultMapList classResultMapList = clazz.getAnnotation(ResultMapList.class);
		if (classResultMapList != null) {
			for (ResultMap classResultMap2 : classResultMapList.value()) {
				processResultMap(clazz, classResultMap2);
			}
		}

		for (Class<?> interfaze : clazz.getInterfaces()) {
			processResultMaps(interfaze);
		}
	}

	/**
	 * Process the result map annotation.
	 * 
	 * @param clazz declaring class
	 * @param resultMap result map annotation
	 */
	private void processResultMap(Class<?> clazz, ResultMap resultMap) {
		ResultMapConfig cfg = createResultMapConfig(clazz, resultMap);
		resultMapsConfig.put(cfg.getId(), cfg);
	}

	/**
	 * Get result map config for given method.
	 * 
	 * @param method method
	 * @return
	 */
	private ResultMapConfig getResultMapConfig(Method method) {
		ResultMap resultMap = method.getAnnotation(ResultMap.class);
		ResultMapRef ref = method.getAnnotation(ResultMapRef.class);
		if (resultMap == null) {
			// Try to find the map with id = "" (default map)for 
			ResultMapConfig resultMapConfig = findResultMap(method.getDeclaringClass(),
					ref != null ? ref.value() : "");
			if (resultMapConfig == null) {
				// We tried to find default map and no one was found -- use
				// automapping
				if (ref == null) {
					return createResultMapConfig(method.getDeclaringClass(), null);
				}
				throw new ConfigurationException("Missing result map reference '" + ref.value()
						+ "', referenced from '" + method.getDeclaringClass().getName() + "#"
						+ method.getName() + "'");
			}
			return resultMapConfig;
		}
		return createResultMapConfig(method.getDeclaringClass(), resultMap);
	}

	/**
	 * Create {@link ResultMapConfig} instance from {@link RowMapper}
	 * annotation.
	 * 
	 * TODO: Validate we don't have nested map with "select" property at the
	 * same time
	 * 
	 * @param clazz
	 * @param resultMap
	 * @return
	 */
	private ResultMapConfig createResultMapConfig(Class<?> clazz, ResultMap resultMap) {
		List<ResultMappingConfig> mappings = new ArrayList<ResultMappingConfig>();
		boolean auto = true;
		if (resultMap != null) {
			for (Mapping mapping : resultMap.mappings()) {
				ResultMappingConfig resMapping = new ResultMappingConfig();

				resMapping.setProperty(mapping.property());
				resMapping.setColumn(mapping.column());
				resMapping.setColumnIndex(mapping.columnIndex());
				if (resMapping.getColumnIndex() == 0
						&& (resMapping.getColumn() == null || "".equals(resMapping.getColumn()))) {
					resMapping.setColumn(resMapping.getProperty());
				}
				if (!"".equals(mapping.resultMap().value())) {
					ResultMapConfig nestedMapConfig = findResultMap(clazz, mapping.resultMap()
							.value());
					if (nestedMapConfig == null) {
						// TODO: Name and location!
						throw new ConfigurationException("Nested map " + mapping.resultMap() 
								+ "not found in class " + clazz + " (referenced by mapping for property " 
								+ mapping.property() + " in class " + clazz + ")!");
					}
					resMapping.setResultMapConfig(nestedMapConfig);
				}
				// TODO: Validate method present!
				if (!"".equals(mapping.subselect())) {
					// TODO: Map it?
					Class<?> mapper = mapping.subselectMapper() != Object.class ? mapping
							.subselectMapper() : clazz;

					// null parameters mean that parameters are not known.
					// in that case, any matching method will be used
					// FIXME: Derive parameters from the property type!
					resMapping.setSubselectKey(new StatementKey(mapper, mapping.subselect(), null));

					postProcessList.add(resMapping);
				}
				mappings.add(resMapping);
			}
			auto = resultMap.auto();
		}

		String id;
		if (resultMap != null) {
			id = clazz.getName() + "#" + resultMap.id();
		} else {
			// TODO: Generated!
			id = clazz.getName() + "#(auto)";
		}

		ResultMapConfig config = new ResultMapConfig(id);
		config.setMappings(mappings.toArray(new ResultMappingConfig[mappings.size()]));
		config.setAuto(auto);
		if (resultMap != null) {
			config.setGroupBy(resultMap.groupBy());
		}
		return config;
	}

	/**
	 * Find result map config with given reference id.
	 * 
	 * @param clazz declaring class
	 * @param refId reference id
	 * @return result map config
	 */
	public ResultMapConfig findResultMap(Class<?> clazz, String refId) {
		String key = clazz.getName() + "#" + refId;

		ResultMapConfig resultMapConfig = resultMapsConfig.get(key);
		if (resultMapConfig == null) {
			for (Class<?> interfaze : clazz.getInterfaces()) {
				resultMapConfig = findResultMap(interfaze, refId);
				if (resultMapConfig != null) {
					break;
				}
			}
		}
		return resultMapConfig;
	}

	/** @return Returns the typeHandlerFactory. */
	public TypeHandlerFactory getTypeHandlerFactory() {
		return typeHandlerFactory;
	}

	/** @return Returns the introspectionFactory. */
	public IntrospectionFactory getIntrospectionFactory() {
		return introspectionFactory;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("resultMapsConfig", resultMapsConfig).append(
				"statementsConfig", statementsConfig).append("typeHandlerFactory",
				typeHandlerFactory).append("introspectionFactory", introspectionFactory).toString();
	}
}
