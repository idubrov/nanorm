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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.nanorm.TypeHandler;
import com.google.code.nanorm.internal.Request;
import com.google.code.nanorm.internal.config.PropertyMappingConfig;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * Class that maps given column from result set to the property.
 * 
 * @author Ivan Dubrov
 * @version 1.0 04.06.2008
 */
public class PropertyMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyMapper.class);

    private final PropertyMappingConfig config;

    private final Setter setter;

    private final TypeHandler<?> typeHandler;

    /**
     * Constructor.
     * 
     * @param config property mapping configuration
     * @param setter property setter
     * @param typeHandler type handler for property
     */
    public PropertyMapper(PropertyMappingConfig config, Setter setter, TypeHandler<?> typeHandler) {
        this.config = config;
        this.setter = setter;
        this.typeHandler = typeHandler;
    }

    /**
     * Map the {@link ResultSet} row onto the result object.
     * 
     * @param request {@link Request} instance. Used for executing subqueries.
     * @param result result object instance
     * @param rs result set
     * @throws SQLException SQL exception from result set
     */
    public final void mapResult(Request request, Object result, ResultSet rs) throws SQLException {
        Object value;

        if (config.getColumnIndex() != 0) {
            value = typeHandler.getValue(rs, config.getColumnIndex());
        } else {
            value = typeHandler.getValue(rs, config.getColumn());
        }
        if (config.getSubselect() != null) {
            value = request.getQueryDelegate()
                    .query(config.getSubselect(), new Object[] {value });
        }
        // TODO: Log property being mapped
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Mapped property {} to value {}", config.getProperty(), value);
        }
        Type type = setter.getType();

        // If value is null and type is primitive type, we need to set default
        // value
        if (value == null && type instanceof Class<?> && ((Class<?>) type).isPrimitive()) {
            // XXX: Is that right place for null -> default conversion?
            if (type == boolean.class) {
                value = false;
            } else if (type == byte.class) {
                value = (byte) 0;
            } else if (type == short.class) {
                value = (short) 0;
            } else if (type == int.class) {
                value = (int) 0L;
            } else if (type == long.class) {
                value = (long) 0;
            } else if (type == float.class) {
                value = (float) 0.0f;
            } else if (type == double.class) {
                value = (double) 0.0;
            } else if (type == char.class) {
                value = (char) 0;
            }
        }
        setter.setValue(result, value);
    }
}
