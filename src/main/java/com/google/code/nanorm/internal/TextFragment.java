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
package com.google.code.nanorm.internal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.code.nanorm.internal.config.ParameterMappingConfig;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.util.ToStringBuilder;

/**
 * Text fragment of the SQL.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class TextFragment implements Fragment {
	/**
	 * SQL template.
	 */
	private final String sql;

	/**
	 * Output SQL.
	 */
	private final StringBuilder sqlBuilder;

	/** List of parameter mappers configs */
	private final List<ParameterMappingConfig> paramMappers;

	private final IntrospectionFactory introspectionFactory;

	/**
	 * Construct text SQL fragment not configured for parameter types. The types
	 * will be derived from the actual parameters (see
	 * {@link #bindParameters(Object[])}).
	 * 
	 * This constructor should be used only when parameter types are not known
	 * until beforehead, since it will trigger SQL template parsing and
	 * parameters introspection during the parameters binding.
	 * 
	 * Actually, we can parse sql at this moment, but this will not give any
	 * performance improvement since this constructor is usually used with
	 * {@link #bindParameters(Object[])} invoked just after it, because in
	 * dynamic case SQL is generated at the same time as parameters.
	 * 
	 * @param sql sql fragment
	 * @param introspectionFactory introspection factory to use
	 */
	public TextFragment(String sql, IntrospectionFactory introspectionFactory) {
		this.sql = sql;
		this.sqlBuilder = null;
		this.paramMappers = null;
		this.introspectionFactory = introspectionFactory;
	}

	/**
	 * Construct text SQL fragment, configured for given parameter types.
	 * 
	 * Introspects parameter types and creates getters for given types.
	 * @param sql SQL fragment
	 * @param types  parameter types
	 * @param introspectionFactory introspection factory 
	 */
	public TextFragment(String sql, Type[] types,
			IntrospectionFactory introspectionFactory) {
		this.sqlBuilder = new StringBuilder(sql.length());
		this.sql = sql;
		this.introspectionFactory = introspectionFactory;
		this.paramMappers = configureTypes(types, sqlBuilder);
	}

	/**
	 * {@inheritDoc}
	 */
	public BoundFragment bindParameters(Object[] parameters) {
		if (paramMappers == null) {
			StringBuilder builder = new StringBuilder();

			List<ParameterMappingConfig> mappers = configureTypes(typesFromParameters(parameters), 
					builder);
			return new BoundFragmentImpl(builder.toString(), mappers,
					parameters);
		}
		return new BoundFragmentImpl(sqlBuilder.toString(), paramMappers,
				parameters);
	}

	/**
	 * Derive types from actual parameters.
	 * 
	 * @param parameters parameters
	 * @return parameter types
	 */
	private Type[] typesFromParameters(Object[] parameters) {
		Type[] types = new Type[parameters.length];
		for (int i = 0; i < types.length; ++i) {
			types[i] = parameters[i] == null ? Void.class : parameters[i]
					.getClass();
		}
		return types;
	}

	/**
	 * Parse the SQL fragment and generate SQL with parameter placeholders and
	 * list of getters.
	 * 
	 * @param types parameter types
	 * @param builder builder for output SQL
	 * @param mappers parameters mapping configs
	 */
	private List<ParameterMappingConfig> configureTypes(Type[] types, StringBuilder builder) {
		List<ParameterMappingConfig> mappers = new ArrayList<ParameterMappingConfig>();
		
		int pos = 0;
		while (pos < sql.length()) {
			// TODO: Add support for out parameters!
			int start = sql.indexOf("${", pos);
			if(start == -1) {
				builder.append(sql.substring(pos));
				break;
			} 
			
			int end = sql.indexOf("}", start + 2);
			if(end == -1) {
				builder.append(sql.substring(pos));
				break;
			}
			builder.append(sql.substring(pos, start));
				
			String prop = sql.substring(start + 2, end);
			// TODO: Add support for lists, which should expand into something like (?, ?, ?, ?)
			// For, example ${prop[]} with prop = int[] { 1, 2, 3} will expand into ?, ?, ? with
			// parameters 1, 2 and 3.
			builder.append('?');
			try {
				
				Getter getter = introspectionFactory.buildParameterGetter(types, prop);
				Type type = getter.getType();
				
				ParameterMappingConfig paramMapper = new ParameterMappingConfig(type, getter, null);
				mappers.add(paramMapper);
			} catch(IllegalArgumentException e) {
				throw new IllegalArgumentException("Failed to create parameter getter for "
						+ " (failed property marked by $[]): "
						+ sql.substring(0, start) + "$["
						+ prop + ']' + sql.substring(end), e);
			}
			pos = end + 1;
		}
		return mappers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("sql",
				sql).toString();
	}
}
