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

package com.google.code.nanorm.internal.session;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.google.code.nanorm.exceptions.DataException;
import com.google.code.nanorm.exceptions.SessionException;

/**
 * {@link SessionSpi} implementation that is build on top of JDBC
 * {@link DataSource}.
 * 
 * The transactions are managed via JTA.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class JTASessionSpi implements SessionSpi {
	private final DataSource dataSource;

	private final UserTransaction userTransaction;

	private boolean commmitted;

	private boolean newTransaction;

	/**
	 * Constructor.
	 * 
	 * @param dataSource {@link DataSource} to use for this session.
	 * @param userTransaction {@link UserTransaction} instance
	 * 
	 */
	public JTASessionSpi(DataSource dataSource, UserTransaction userTransaction) {
		this.dataSource = dataSource;

		this.userTransaction = userTransaction;
		
		try {
			newTransaction = (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION);
		} catch (Exception e) {
			throw new SessionException("Failed to check transaction status!", e);
		}
		if (newTransaction) {
			try {
				userTransaction.begin();
			} catch (Exception e) {
				throw new SessionException("Failed to start new transaction!",
						e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		if (newTransaction) {
			try {
				userTransaction.commit();
			} catch (Exception e) {
				throw new SessionException("Cannot commit JTA transaction!", e);
			}
		}
		commmitted = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void end() {
		// Since we started the transaction, we should finish it.
		if (newTransaction) {
			rollback();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Connection getConnection() {
		try {
			Connection connection = dataSource.getConnection();
			if (connection.getAutoCommit()) {
				connection.setAutoCommit(false);
			}
			return connection;
		} catch (SQLException e) {
			throw new DataException(
					"SQL exception occured while getting connection!", e);
		}
	}

	/**
	 * Always returns true, since we return new connection for every request.
	 * 
	 * @return always true
	 */
	public boolean isAllowMultipleQueries() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void releaseConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new DataException("Error releasing the connection!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback() {
		if (!commmitted) {
			try {
				if (newTransaction) {
					userTransaction.rollback();
				} else {
					userTransaction.setRollbackOnly();
				}
			} catch (SystemException e) {
				throw new SessionException(
						"Failed to rollback the JTA transaction!", e);
			}
		}
	}
}
