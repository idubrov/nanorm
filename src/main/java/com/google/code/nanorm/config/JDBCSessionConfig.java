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

import com.google.code.nanorm.internal.session.JDBCSessionSpi;
import com.google.code.nanorm.internal.session.SessionSpi;

/**
 * JDBC session/transaction management configuration.
 * TODO: Elaborate
 * 
 * @author Ivan Dubrov
 */
public class JDBCSessionConfig implements SessionConfig {
	
	private final DataSource dataSource;
	
	private final boolean allowMultiple = true;
	
	/**
	 * Constructor.
	 * @param dataSource datasource
	 */
	public JDBCSessionConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public SessionSpi newSessionSpi() {
		return new JDBCSessionSpi(dataSource, allowMultiple);
	}
}
