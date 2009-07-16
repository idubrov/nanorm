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
package com.google.code.nanorm.internal.mapping.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.nanorm.DataSink;
import com.google.code.nanorm.internal.Request;

/**
 * Primary interface used for mapping the row into the Java object.
 * 
 * @see com.google.code.nanorm.internal.FactoryImpl
 * @see DefaultRowMapper
 * @author Ivan Dubrov
 * @version 1.0 28.05.2008
 */
public interface RowMapper {

    /**
     * Process the result set row.
     * 
     * @param request request variables
     * @param rs result set
     * @param callback callback used for pushing the result object
     * @throws SQLException any exception from the result set
     */
    void processResultSet(Request request, ResultSet rs, DataSink<Object> callback)
            throws SQLException;
}
