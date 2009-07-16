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

package com.google.code.nanorm.internal.mapping.parameter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.google.code.nanorm.TypeHandler;
import com.google.code.nanorm.TypeHandlerFactory;
import com.google.code.nanorm.internal.config.ParameterMappingConfig;

/**
 * Parameter mapper. Maps IN/OUT parmaters to {@link PreparedStatement}/
 * {@link CallableStatement}.
 * 
 * @author Ivan Dubrov
 */
public final class ParameterMapper {

    private final int index;

    private final Object[] args;

    private final ParameterMappingConfig config;

    /**
     * Constructor.
     * 
     * @param config parameter mapping configuration
     * @param index parameter index
     * @param args argument to bind to
     */
    public ParameterMapper(ParameterMappingConfig config, int index, Object[] args) {
        this.config = config;
        this.index = index;
        this.args = args;
    }

    /**
     * Map IN parameter to {@link PreparedStatement}.
     * 
     * @param factory type handler factory
     * @param ps prepared statement to set parameters to
     * @throws SQLException exception while setting parameter
     */
    public void mapParameterIn(TypeHandlerFactory factory, PreparedStatement ps)
            throws SQLException {
        // IN parameter
        if (config.getGetter() != null) {
            TypeHandler<?> typeHandler = factory.getTypeHandler(config.getType());

            Object value = config.getGetter().getValue(args);
            typeHandler.setParameter(ps, index, value);
        }
    }

    /**
     * TODO: Check out is used only for @Call
     * 
     * Map OUT parameter from {@link CallableStatement}.
     * 
     * @param factory type handler factory
     * @param cs callable statement to get parameter from
     * @throws SQLException exception while getting value
     */
    public void mapParameterOut(TypeHandlerFactory factory, CallableStatement cs)
            throws SQLException {
        // OUT parameter
        if (config.getSetter() != null) {
            TypeHandler<?> typeHandler = factory.getTypeHandler(config.getType());

            // XXX: NULL handling for primitive types?
            Object value = typeHandler.getValue(cs, index);
            config.getSetter().setValue(args, value);
        }
    }

    /**
     * Register OUT parameter.
     * @param factory type handler factory
     * @param cs callable statement
     * @throws SQLException exception while registering parameter
     */
    public void registerOutParameter(TypeHandlerFactory factory, CallableStatement cs)
            throws SQLException {
        if (config.getSetter() != null) {
            TypeHandler<?> typeHandler = factory.getTypeHandler(config.getType());

            cs.registerOutParameter(index, typeHandler.getSqlType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.valueOf(config.getGetter().getValue(args));
    }
}
