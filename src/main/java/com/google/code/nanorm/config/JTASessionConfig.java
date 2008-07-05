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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import com.google.code.nanorm.exceptions.SessionException;
import com.google.code.nanorm.internal.session.JTASessionSpi;
import com.google.code.nanorm.internal.session.SessionSpi;

/**
 * JTA session/transaction management configuration.
 * @author Ivan Dubrov
 */
public class JTASessionConfig implements SessionConfig {
	
	private final DataSource dataSource;
	
	private final UserTransaction userTransaction;
	
	/**
	 * Constructor.
	 * @param dataSource data source
	 * @param utName user transaction JNDI name
	 */
	public JTASessionConfig(DataSource dataSource, String utName) {
		this.dataSource = dataSource;
		try {
			InitialContext initCtx = new InitialContext();
			this.userTransaction = (UserTransaction) initCtx.lookup(utName);
		} catch (NamingException e) {
			throw new SessionException("Failed to get JTA transaction!", e);
		}

		if (this.userTransaction == null) {
			throw new IllegalArgumentException(
					"JTA Transaction could not be found under name " + utName);
		}

	}
	
	/**
	 * {@inheritDoc}
	 */
	public SessionSpi newSessionSpi() {
		return new JTASessionSpi(dataSource, userTransaction);
	}
}
