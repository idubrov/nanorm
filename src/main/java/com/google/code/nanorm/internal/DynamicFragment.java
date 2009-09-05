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

package com.google.code.nanorm.internal;

import java.lang.reflect.Method;

import com.google.code.nanorm.SQLSource;
import com.google.code.nanorm.exceptions.DynamicSQLException;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.util.ToStringBuilder;

/**
 * Dynamic SQL fragment. The actual SQL is generated by the {@link SQLSource}
 * instance, those class is given in the configuration.
 * 
 * @author Ivan Dubrov
 * @version 1.0 29.05.2008
 */
public class DynamicFragment implements Fragment {

    private final Class<? extends SQLSource> sqlSource;

    private final IntrospectionFactory introspectionFactory;

    /**
     * Constructor.
     * 
     * @param sqlSource statement SQL builder class
     * @param introspectionFactory introspection factory
     */
    public DynamicFragment(Class<? extends SQLSource> sqlSource,
            IntrospectionFactory introspectionFactory) {
        this.sqlSource = sqlSource;
        this.introspectionFactory = introspectionFactory;
    }

    /**
     * @see com.google.code.nanorm.internal.Fragment#bindParameters(java.lang.Object[])
     */
    public BoundFragment bindParameters(Object[] parameters) {
        for (Method method : sqlSource.getMethods()) {
            if (method.getName().equals(SQLSource.GENERATOR_METHOD)) {
                try {
                    SQLSource source = sqlSource.newInstance();
                    source.setReflFactory(introspectionFactory);
                    // TODO: Test parameter types
                    method.invoke(source, parameters);
                    return source;
                } catch (Exception e) {
                    throw new DynamicSQLException(
                            "Failed to create SQL source and invoke generator method", e);
                }
            }
        }
        throw new DynamicSQLException("Dynamic SQL generator method "
                + SQLSource.GENERATOR_METHOD + " not found in dynamic SQL source " + sqlSource
                + "(check you have public method named '" + SQLSource.GENERATOR_METHOD
                + "' in dynamic SQL source class)");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("sqlSource", sqlSource).toString();
    }
}
