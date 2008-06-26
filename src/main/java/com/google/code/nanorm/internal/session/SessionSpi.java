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

/**
 * Session service provider interface. Provides methods for getting connections
 * and transactions control.
 * 
 * The instances of session service provider are not thread-safe. They should be
 * used in one thread only.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public interface SessionSpi {

    /**
     * Get connection for performing a query.
     * @return JDBC connection instance
     */
    Connection getConnection();

    /**
     * Release connection after performing a query.
     * @param conn connection
     */
    void releaseConnection(Connection conn);

    /**
     * Commit transaction.
     */
    void commit();

    /**
     * Rollback transaction.
     */
    void rollback();

    /**
     * Finish session.
     */
    void end();

    /**
     * Does this session implementation allows running multiple queries at the
     * same time.
     * 
     * This could be false in JDBC transaction management/externally provided
     * connection case if underlying driver does not allow multiple active
     * result sets for single connection.
     * @return if this session allows running multiple queries at the same time
     */
    boolean isAllowMultipleQueries();

}
