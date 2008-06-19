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
 * Not thread safe?
 *
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class TextFragment implements Fragment
{
    private final static Pattern pattern = Pattern.compile("([^#$]*)([$#]\\{[^}]+\\})");
    
    // Template
    final private String sql;
    
    // Generated
    final private StringBuilder sqlBuilder;
    
    final private List<Getter> gettersList;
    
    final private List<Type> typesList;
    
    final private Object[] parameters;

    // TODO: Configurable
    final private IntrospectionFactory introspectionFactory = new BeanUtilsIntrospectionFactory();
    
    // TODO: Derive types from parameter types, not parameter themselves
    public TextFragment(String sql) {
        this.sql = sql;
        this.sqlBuilder = null;
        this.gettersList = null;
        this.typesList = null;
        this.parameters = null;
    }
    
    /**
     * 
     */
    public TextFragment(String sql, Type[] types) {
        this.sqlBuilder = new StringBuilder();
        this.typesList = new ArrayList<Type>();
        this.gettersList = new ArrayList<Getter>();
        this.parameters = null;
        this.sql = sql;
        configureTypes(types, sqlBuilder, typesList, gettersList);
    }
    
    public BoundFragment bindParameters(Object[] parameters) {
        if(typesList == null) {
            List<Type> types = new ArrayList<Type>();
            List<Getter> getters = new ArrayList<Getter>();
            StringBuilder builder = new StringBuilder();
            
            configureTypes(typesFromParameters(parameters), builder, types, getters);
            return new BoundFragmentImpl(builder.toString(), getters, types, parameters);
        }
        return new BoundFragmentImpl(sqlBuilder.toString(), gettersList, typesList, parameters);        
    }
    
    private Type[] typesFromParameters(Object[] parameters) {
        Type[] types = new Type[parameters.length];
        for(int i = 0; i < types.length; ++i) {
            types[i] = parameters[i] == null ? Void.class : parameters[i].getClass();
        }
        return types;
    }
    
    private void configureTypes(Type[] types, StringBuilder builder, List<Type> typesLst, List<Getter> getters) {
        Matcher matcher = pattern.matcher(sql);
        int end = 0;
        while(matcher.find()) {
            int count = matcher.groupCount();
            if(count > 0) {
                builder.append(matcher.group(1));
            }
            if(count > 1) {
                String prop = matcher.group(2);
                if(prop.startsWith("$")) {
                    builder.append("?");
                    
                    prop = prop.substring(2, prop.length() - 1);
                    
                    try {
                        Getter getter = introspectionFactory.buildParameterGetter(types, prop);
                        Type type = introspectionFactory.getParameterType(types, prop);
                        typesLst.add(type);
                        getters.add(getter);
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            end = matcher.end(0);
        }
        builder.append(sql, end, sql.length());
    }
    
    // TODO: ToString?
}
