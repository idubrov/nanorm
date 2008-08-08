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

import com.google.code.nanorm.exceptions.DataException;

/**
 * {@link SessionSpi} implementation that is build on top of JDBC
 * {@link DataSource}.
 * 
 * The transactions are managed externally, so {@link #commit} and
 * {@link #rollback} methods do nothing.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class ExternalSessionSpi implements SessionSpi {
    private final DataSource dataSource;

    /**
     * Constructor.
     * 
     * @param dataSource {@link DataSource} to use for the session. 
     */
    public ExternalSessionSpi(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Does nothing. Transactions are managed externally.
     */
    public void commit() {
        // Nothing.
    }

    /**
     * Does nothing.
     */
    public void end() {
        // Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch(SQLException e) {
            throw new DataException("Failed to get new connection from data source!", e);
        }
    }

    /**
     * {@inheritDoc}
     * Always returns true, since we return new connection for every request.
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
        } catch(SQLException e) {
            throw new DataException("Failed to release the connection!", e);
        }
    }

    /**
     * Does nothing. Transactions are managed externally.
     */
    public void rollback() {
        // Nothing.
    }
}
