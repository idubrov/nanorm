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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.code.nanorm.exceptions.ConfigurationException;
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
	private static final Pattern PATTERN = Pattern
			.compile("([^#$]*)([$#]\\{[^}]+\\})");

	/**
	 * SQL template.
	 */
	private final String sql;

	/**
	 * Output SQL.
	 */
	private final StringBuilder sqlBuilder;

	/** List of getters */
	private final List<Getter> gettersList;

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
		this.gettersList = null;
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
		this.sqlBuilder = new StringBuilder();
		this.gettersList = new ArrayList<Getter>();
		this.sql = sql;
		this.introspectionFactory = introspectionFactory;
		configureTypes(types, sqlBuilder, gettersList);
	}

	/**
	 * {@inheritDoc}
	 */
	public BoundFragment bindParameters(Object[] parameters) {
		if (gettersList == null) {
			List<Getter> getters = new ArrayList<Getter>();
			StringBuilder builder = new StringBuilder();

			configureTypes(typesFromParameters(parameters), builder, getters);
			return new BoundFragmentImpl(builder.toString(), getters,
					parameters);
		}
		return new BoundFragmentImpl(sqlBuilder.toString(), gettersList,
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
	 * @param getters getters list to fill
	 */
	private void configureTypes(Type[] types, StringBuilder builder,
			List<Getter> getters) {
		Matcher matcher = PATTERN.matcher(sql);
		int end = 0;
		while (matcher.find()) {
			int count = matcher.groupCount();
			if (count > 0) {
				builder.append(matcher.group(1));
			}
			if (count > 1) {
				String prop = matcher.group(2);
				if (prop.charAt(0) == '$') {
					// TODO: Add support for lists, which should expand into something like (?, ?, ?, ?)
					builder.append('?');
					prop = prop.substring(2, prop.length() - 1);
					try {
						getters.add(introspectionFactory.buildParameterGetter(
							types, prop));
					} catch(IllegalArgumentException e) {
						throw new ConfigurationException("Failed to create parameter getter for "
								+ " (failed property marked by $[]): "
								+ sql.substring(0, matcher.start(2)) + "$["
								+ prop + ']' + sql.substring(matcher.end(2)), e);
					}
				}
			}
			end = matcher.end(0);
		}
		builder.append(sql, end, sql.length());
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
