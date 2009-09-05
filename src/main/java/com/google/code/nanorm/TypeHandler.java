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

package com.google.code.nanorm;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Performs mapping data from {@link ResultSet} to Java objects and from Java
 * objects to {@link PreparedStatement} parameters.
 * 
 * @author Ivan Dubrov
 * @version 1.0 31.05.2008
 * @param <T> type this type handler works for
 */
public interface TypeHandler<T> {

    /**
     * Get column mapped value.
     * 
     * @param rs {@link ResultSet} instance
     * @param column column
     * @return mapped value.
     * @throws SQLException any SQL error
     */
    T getValue(ResultSet rs, int column) throws SQLException;

    /**
     * Get column mapped value.
     * 
     * @param rs {@link ResultSet} instance
     * @param column column
     * @return mapped value.
     * @throws SQLException any SQL error
     */
    T getValue(ResultSet rs, String column) throws SQLException;

    /**
     * Get OUT parameter.
     * 
     * @param cs {@link CallableStatement} instance
     * @param index parameter index
     * @return mapped value.
     * @throws SQLException any SQL error
     */
    T getValue(CallableStatement cs, int index) throws SQLException;

    /**
     * Set parameter for given {@link PreparedStatement}.
     * 
     * @param st {@link PreparedStatement} instance.
     * @param column column to set parameter to.
     * @param value parameter value
     * @throws SQLException any SQL error
     */
    void setParameter(PreparedStatement st, int column, Object value) throws SQLException;

    /**
     * Get SQL type.
     * 
     * @return SQL type
     */
    int getSqlType();
}
