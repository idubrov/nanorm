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
package com.google.code.nanorm.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class StaticStatementBuilder implements StatementBuilder {
    
    final private String sql;
    
    final private Method method;
    
    final private TextStatement template;
    
    public StaticStatementBuilder(String sql, Method method) {
        this.sql = sql;
        this.method = method;
        this.template = new TextStatement(sql).bindTypes(method.getGenericParameterTypes());
    }
    
    /**
     * @see com.google.code.nanorm.internal.StatementBuilder#generateStatement(java.lang.Object[])
     */
    public Statement generateStatement(Object[] parameters) {
        return template.bindParameters(parameters);
    }

    /**
     * @see com.google.code.nanorm.internal.StatementBuilder#getReturnType()
     */
    public Type getReturnType() {
        return method.getGenericReturnType();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("sql", sql).
            append("method", method).
            toString();
    }
}
