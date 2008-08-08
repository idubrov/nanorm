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
package com.google.code.nanorm.config;

import javax.sql.DataSource;

import com.google.code.nanorm.internal.session.ExternalSessionSpi;
import com.google.code.nanorm.internal.session.SessionSpi;

/**
 * Configuration for external session/transaction management.
 * TODO: Elaborate javadoc
 * 
 * @author Ivan Dubrov
 */
public class ExternalSessionConfig implements SessionConfig {
	
	private final DataSource dataSource;
	
	/**
	 * Constructor.
	 * 
	 * @param dataSource data source
	 */
	public ExternalSessionConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public SessionSpi newSessionSpi() {
		return new ExternalSessionSpi(dataSource);
	}
}
