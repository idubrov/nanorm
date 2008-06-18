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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.code.nanorm.internal.introspect.BeanUtilsIntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;

public class TextStatement implements Statement
{
    private final static Pattern pattern = Pattern.compile("([^#$]*)([$#]\\{[^}]+\\})");
    
    private final static Pattern result = Pattern.compile("([^(]+)(\\([^)]+\\))?");
    
    final private StringBuilder sqlBuilder = new StringBuilder();
    
    final private List<Object> params = new ArrayList<Object>();
    
    final private Map<String, String> output = new HashMap<String, String>();
    
    final private List<Class<?>> types = new ArrayList<Class<?>>();

    // TODO: Configurable
    final private IntrospectionFactory introspectionFactory = new BeanUtilsIntrospectionFactory();
    
    // TODO: Derive types from parameter types, not parameter themselves
    public TextStatement(String sql, Object... parameters) {
        Matcher matcher = pattern.matcher(sql);
        int end = 0;
        while(matcher.find()) {
            int count = matcher.groupCount();
            if(count > 0) {
                sqlBuilder.append(matcher.group(1));
            }
            if(count > 1) {
                String prop = matcher.group(2);
                if(prop.startsWith("$")) {
                    sqlBuilder.append("?");
                    
                    prop = prop.substring(2, prop.length() - 1);
                    
                    try {
                        Getter getter = introspectionFactory.buildParameterGetter(prop);
                        Object value = getter.getValue(parameters);
                        if(value == null) {
                            types.add(Void.class);
                        } else {
                            types.add(value.getClass());
                        }
                        params.add(value);
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                } else { // Starts with '#'
                    prop = prop.substring(2, prop.length() - 1);
                    
                    Matcher m = result.matcher(prop);
                    if(!m.matches()) {
                        throw new RuntimeException("!!!");
                    }
                    String property = m.group(1);
                    String column = m.group(2);
                    if(m.groupCount() > 1 && column != null) {
                        column = column.substring(1, column.length() - 1);
                    } else {
                        column = property;
                    }
                    sqlBuilder.append(column);
                    output.put(property, column);
                }
            }
            end = matcher.end(0);
        }
        sqlBuilder.append(sql, end, sql.length());
    }
    
    public List<Object> fillParameters()
    {
        return params;
    }

    public void generate(StringBuilder builder, List<Object> parameters, List<Class<?>> types)
    {
        builder.append(sqlBuilder);
        parameters.addAll(params);
        types.addAll(this.types);
    }
    
    // TODO: ToString?
}
