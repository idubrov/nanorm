/**
 * Copyright (C) 2008, 2009 Ivan S. Dubrov
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
import com.google.code.nanorm.exceptions.SessionException;

/**
 * {@link SessionSpi} implementation that is build on top of JDBC
 * {@link DataSource}.
 * 
 * The session uses single JDBC connection. The transactions are managed via
 * JDBC.
 * 
 * By default, allows running multiple queries on the same connection at the
 * same time.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class JDBCSessionSpi implements SessionSpi {
    private final Connection connection;

    private final boolean isAllowMultiple;

    /**
     * Constructor.
     * 
     * @param dataSource {@link DataSource} to use for this session.
     */
    public JDBCSessionSpi(DataSource dataSource) {
        this(dataSource, true);
    }

    /**
     * Constructor.
     * 
     * @param dataSource {@link DataSource} to use for this session.
     * @param isAllowMultiple allow running multiple queries at the same time in
     * the single session.
     */
    public JDBCSessionSpi(DataSource dataSource, boolean isAllowMultiple) {
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new DataException("Failed to get connection from data source!", e);
        }
        this.isAllowMultiple = isAllowMultiple;
    }

    /**
     * {@inheritDoc}
     */
    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new SessionException("Failed to commit the JDBC transaction!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void end() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new DataException("Failed to release the connection!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAllowMultipleQueries() {
        return isAllowMultiple;
    }

    /**
     * This method does nothing. We use single connection for all requests.
     * @param conn not used
     */
    public void releaseConnection(Connection conn) {
        // Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new SessionException("Failed to rollback the JDBC transaction!", e);
        }
    }
}
