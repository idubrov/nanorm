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

import com.google.code.nanorm.annotations.ResultMap;

/**
 * Bean for storing subselect reference info for configuration post-processing.
 * 
 * @author Ivan Dubrov
 */
public class SubselectConfig {
	private StatementKey subselectKey;
	
	private PropertyMappingConfig propertyMapping;
	
	private Class<?> mapper;
	
	private ResultMap resultMap;
	
	/**
	 * Constructor.
	 * @param subselectKey subselect statement key
	 * @param propertyMapping property mapping that uses the subselect
	 * @param mapper mapper declaring the result map
	 * @param resultMap result map declaring the property mapping
	 */
	public SubselectConfig(StatementKey subselectKey, PropertyMappingConfig propertyMapping, Class<?> mapper, ResultMap resultMap) {
		this.subselectKey = subselectKey;
		this.propertyMapping = propertyMapping;
		this.mapper = mapper;
		this.resultMap = resultMap;
	}
	
	/**
	 * Getter for statement key.
	 * @return statement key
	 */
	public StatementKey getSubselectKey() {
		return subselectKey;
	}
	
	/**
	 * Getter for property mapping.
	 * @return property mapping;
	 */
	public PropertyMappingConfig getPropertyMapping() {
		return propertyMapping;
	}
	
	/**
	 * Getter for mapper.
	 * @return mapper
	 */
	public Class<?> getMapper() {
		return mapper;
	}
	
	/**
	 * Getter for result map that declares the property with subselect.
	 * @return result map that declares the property with subselect. 
	 */
	public ResultMap getResultMap() {
		return resultMap;
	}
}