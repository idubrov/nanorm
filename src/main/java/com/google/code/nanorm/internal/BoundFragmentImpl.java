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

import java.util.List;

import com.google.code.nanorm.internal.config.ParameterMappingConfig;
import com.google.code.nanorm.internal.mapping.parameter.ParameterMapper;

/**
 * Implementation of {@link BoundFragment}. This implementation is based on list
 * of getters which are used to retrieve types and values of the parameters.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class BoundFragmentImpl implements BoundFragment {

	private final String sql;

	private final List<ParameterMappingConfig> paramMappers;

	private final Object[] parameters;

	/**
	 * Constructor.
	 * 
	 * @param sql sql fragment
	 * @param paramMappers list of unbound parameter mappers
	 * @param parameters parameters
	 */
	public BoundFragmentImpl(String sql, List<ParameterMappingConfig> paramMappers,
			Object[] parameters) {
		this.sql = sql;
		this.paramMappers = paramMappers;
		this.parameters = parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	public void generate(StringBuilder builder, List<ParameterMapper> params) {
		builder.append(sql);
		for (ParameterMappingConfig config : paramMappers) {
			params.add(new ParameterMapper(config, params.size() + 1, parameters));
		}
	}

}
