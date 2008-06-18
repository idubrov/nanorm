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

import com.google.code.nanorm.SQLSource;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 29.05.2008
 */
public class DynamicStatementBuilder implements StatementBuilder {
    
    final private Class<? extends SQLSource> statementClass;
    
    /**
     * 
     */
    public DynamicStatementBuilder(Class<? extends SQLSource> statementClass) {
        this.statementClass = statementClass;
    }

    /**
     * @see com.google.code.nanorm.internal.StatementBuilder#generateStatement(java.lang.Object[])
     */
    public Statement generateStatement(Object[] parameters) {
        Class<?>[] types = new Class<?>[parameters.length];
        // TODO: Support for primitive types
        for(int i = 0; i < parameters.length; ++i) {
            if(parameters[i] == null) {
                types[i] = Void.class;
            } else {
                types[i] = parameters[i].getClass();
            }
        }
        try {
            for(Method method : statementClass.getMethods()) {
                if(method.getName().equals("sql")) {
                    SQLSource source = statementClass.newInstance();
                    method.invoke(source, parameters);
                    return source;
                }
            }
            throw new RuntimeException("METHOD NOT FOUND");
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DYNAMIC SQL ERROR");
        }
    }

    // TODO: To string
}
