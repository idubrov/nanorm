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

package com.google.code.nanorm.internal.mapping.result;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.nanorm.DataSink;
import com.google.code.nanorm.TypeHandler;
import com.google.code.nanorm.TypeHandlerFactory;
import com.google.code.nanorm.internal.Request;

/**
 * Implementation of the {@link RowMapper} that simply returns the first column
 * from the current row of {@link ResultSet}.
 * 
 * @author Ivan Dubrov
 */
public class ScalarRowMapper implements RowMapper {

    private final TypeHandler<?> typeHandler;

    /**
     * Constructor.
     * 
     * @param type result type
     * @param typeHandlerFactory type handler factory
     */
    public ScalarRowMapper(Type type, TypeHandlerFactory typeHandlerFactory) {
        this.typeHandler = typeHandlerFactory.getTypeHandler(type);
    }

    /**
     * {@inheritDoc}
     */
    public void processResultSet(Request request, ResultSet rs, DataSink<Object> callback)
            throws SQLException {
        // TODO: Check we have only one column?
        callback.pushData(typeHandler.getValue(rs, 1));
    }
}
