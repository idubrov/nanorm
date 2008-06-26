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

import com.google.code.nanorm.exceptions.SessionException;

/**
 * {@link SessionSpi} implementation that is build on top of externally provided
 * connection.
 * 
 * The connection is not closed at the session end. All queries are executed
 * using this single connection.
 * 
 * By default, allows running multiple queries on the same connection at the
 * same time.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class SingleConnSessionSpi implements SessionSpi {
    final private Connection connection;

    final private boolean isAllowMultiple;

    /**
     * Constructor.
     * @param connection connection to use for this session.
     */
    public SingleConnSessionSpi(Connection connection) {
        this(connection, true);
    }

    /**
     * Constructor.
     * @param connection connection to use for this session.
     * @param isAllowMultiple allow running multiple queries at the same time in the single session.
     */
    public SingleConnSessionSpi(Connection connection, boolean isAllowMultiple) {
        this.connection = connection;
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
     * This method does nothing. The connection is provided externaly, therefore
     * we don't close it.
     */
    public void end() {
        // Nothing.
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
