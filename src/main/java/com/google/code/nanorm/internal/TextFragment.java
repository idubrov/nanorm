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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.code.nanorm.internal.introspect.BeanUtilsIntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;

/**
 * Text fragment of the SQL.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class TextFragment implements Fragment {
    private final static Pattern pattern = Pattern.compile("([^#$]*)([$#]\\{[^}]+\\})");

    // Template
    final private String sql;

    // Generated
    final private StringBuilder sqlBuilder;

    /** List of getters */
    final private List<Getter> gettersList;

    // TODO: Configurable
    final private IntrospectionFactory introspectionFactory = new BeanUtilsIntrospectionFactory();

    /**
     * Construct text SQL fragment not configured for parameter types. The types
     * will be derived from the actual parameters (see
     * {@link #bindParameters(Object[])}).
     * 
     * This constructor should be used only when parameter types are not known
     * until beforehead, since it will trigger SQL template parsing and
     * parameters introspection during the parameters binding.
     * 
     * Actually, we can parse sql at this moment, but this will not give any performance improvement
     * since this constructor is usually used with {@link #bindParameters(Object[])} invoked just
     * after it, because in dynamic case SQL is generated at the same time as parameters.
     * 
     * TODO: Think of skipping TextFragment creation for this case and creation of bound fragment directly.
     * 
     * @param sql
     */
    public TextFragment(String sql) {
        this.sql = sql;
        this.sqlBuilder = null;
        this.gettersList = null;
    }

    /**
     * Construct text SQL fragment, configured for given parameter types.
     * 
     * Introspects parameter types and creates getters for given types.
     */
    public TextFragment(String sql, Type[] types) {
        this.sqlBuilder = new StringBuilder();
        this.gettersList = new ArrayList<Getter>();
        this.sql = sql;
        configureTypes(types, sqlBuilder, gettersList);
    }

    public BoundFragment bindParameters(Object[] parameters) {
        if (gettersList == null) {
            List<Getter> getters = new ArrayList<Getter>();
            StringBuilder builder = new StringBuilder();

            configureTypes(typesFromParameters(parameters), builder, getters);
            return new BoundFragmentImpl(builder.toString(), getters, parameters);
        }
        return new BoundFragmentImpl(sqlBuilder.toString(), gettersList, parameters);
    }

    private Type[] typesFromParameters(Object[] parameters) {
        Type[] types = new Type[parameters.length];
        for (int i = 0; i < types.length; ++i) {
            types[i] = parameters[i] == null ? Void.class : parameters[i].getClass();
        }
        return types;
    }

    private void configureTypes(Type[] types, StringBuilder builder, List<Getter> getters) {
        Matcher matcher = pattern.matcher(sql);
        int end = 0;
        while (matcher.find()) {
            int count = matcher.groupCount();
            if (count > 0) {
                builder.append(matcher.group(1));
            }
            if (count > 1) {
                String prop = matcher.group(2);
                if (prop.startsWith("$")) {
                    builder.append("?");
                    prop = prop.substring(2, prop.length() - 1);
                    getters.add(introspectionFactory.buildParameterGetter(types, prop));
                }
            }
            end = matcher.end(0);
        }
        builder.append(sql, end, sql.length());
    }

    // TODO: ToString?
}
