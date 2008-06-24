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

import com.google.code.nanorm.internal.Request;
import com.google.code.nanorm.internal.config.ResultMappingConfig;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.type.TypeHandler;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 04.06.2008
 */
public class PropertyMapper {

    final private ResultMappingConfig config;

    final private Setter setter;

    final private TypeHandler<?> typeHandler;

    /**
     * @param resultClass
     * @param configs
     * @param setters
     * @param typeHandlers
     */
    public PropertyMapper(ResultMappingConfig config, Setter setter, TypeHandler<?> typeHandler) {
        this.config = config;
        this.setter = setter;
        this.typeHandler = typeHandler;
    }

    /**
     * 
     * @see com.google.code.nanorm.internal.mapping.result.ResultMapper#mapResult(java.lang.Object,
     * java.sql.ResultSet)
     */
    public final void mapResult(Request request, Object result, ResultSet rs) throws SQLException {
        Object value;
        if (config.getColumnIndex() != 0) {
            value = typeHandler.getValue(rs, config.getColumnIndex());
        } else {
            value = typeHandler.getResult(rs, config.getColumn());
        }
        if (config.getSubselect() != null) {
            value = request.getQueryDelegate()
                    .query(config.getSubselect(), new Object[] {value });
        }
        setter.setValue(result, value);
    }
}
