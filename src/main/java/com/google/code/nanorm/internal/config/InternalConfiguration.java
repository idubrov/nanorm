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
package com.google.code.nanorm.internal.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.code.nanorm.SQLSource;
import com.google.code.nanorm.TypeHandlerFactory;
import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapList;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.annotations.Source;
import com.google.code.nanorm.exceptions.ResultMapException;
import com.google.code.nanorm.exceptions.StatementConfigException;
import com.google.code.nanorm.internal.DynamicFragment;
import com.google.code.nanorm.internal.Fragment;
import com.google.code.nanorm.internal.TextFragment;
import com.google.code.nanorm.internal.introspect.BeanUtilsIntrospectionFactory;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.mapping.result.ResultMapImpl;
import com.google.code.nanorm.internal.type.TypeHandlerFactoryImpl;

/**
 * TODO: Merge processing and searching. Maybe, lazy loading (when referenced). 
 * @author Ivan Dubrov
 * @version 1.0 29.05.2008
 */
public class InternalConfiguration {
    
    // Result map id is <package>.<class>#id
    final private Map<String, ResultMapConfig> resultMapsConfig;
    
    // Statement id is <package>.<class>#method
    final private Map<String, StatementConfig> statementsConfig;
    
    final private TypeHandlerFactory typeHandlerFactory;
    
    final private IntrospectionFactory introspectionFactory;
    
    
    /**
     * 
     */
    public InternalConfiguration(TypeHandlerFactory typeHandlerFactory, IntrospectionFactory introspectionFactory) {
        resultMapsConfig = new ConcurrentHashMap<String, ResultMapConfig>();
        statementsConfig = new ConcurrentHashMap<String, StatementConfig>();
        
        // TODO: Should be configurable
        this.typeHandlerFactory = typeHandlerFactory;
        this.introspectionFactory = introspectionFactory;
    }
    
    public StatementConfig getStatementConfig(Method method) {
        String key = method.getDeclaringClass().getName() + "#" + method.getName(); 
        StatementConfig statementConfig = statementsConfig.get(key);
        if(statementConfig == null) {
            throw new StatementConfigException("Missing configuration for method '" + key + "'"); 
        }
        return statementConfig;
    }
    
    // TODO: Should it be synchronized?
    // TODO: Check we already configured given mapper
    public synchronized void configure(Class<?> mapper) {
        processResultMaps(mapper);
        for (Method method : mapper.getMethods()) {
            processMethod(method);
        }
        
        // TODO: Configure supeinterfaces
    }
    
    protected void processMethod(Method method) {
        String key = method.getDeclaringClass().getName() + "#" + method.getName(); 
        if(statementsConfig.containsKey(key)) {
            // TODO: Log debug message "Query method '" + key + "' is already configured!";
            return;
        }
        
        StatementConfig stConfig = new StatementConfig();
        
        ResultMapConfig config = getResultMapConfig(method);
        stConfig.setResultMapper(new ResultMapImpl(method.getGenericReturnType(), config, 
                introspectionFactory, typeHandlerFactory));
        stConfig.setResultType(method.getGenericReturnType());
        
        Select select = method.getAnnotation(Select.class);
        Source source = method.getAnnotation(Source.class);
        if(select != null) {
            Fragment builder = new TextFragment(select.value(), method.getGenericParameterTypes());
            stConfig.setStatementBuilder(builder);
        } else if(source != null) {
            Class<? extends SQLSource> sqlSource = source.value();
            Fragment builder = new DynamicFragment(sqlSource);
            stConfig.setStatementBuilder(builder);
        } else {
            // Skip method
            // TODO: Logging!
            return;
        }
        statementsConfig.put(key, stConfig);
    }
    
    protected void processResultMaps(Class<?> clazz) {
        ResultMap classResultMap = clazz.getAnnotation(ResultMap.class);
        
        if(classResultMap != null) {
            processResultMap(clazz, classResultMap);
        }
        
        ResultMapList classResultMapList =
            clazz.getAnnotation(ResultMapList.class);
        if(classResultMapList != null) {
            for(ResultMap classResultMap2 : classResultMapList.value()) {
                processResultMap(clazz, classResultMap2);
            }
        }
        
        for(Class<?> interfaze : clazz.getInterfaces()) {
            processResultMaps(interfaze);
        }
    }
    
    protected void processResultMap(Class<?> clazz, ResultMap resultMap) {
        // TODO: Check id!
        String key = clazz.getName() + "#" + resultMap.id();
        resultMapsConfig.put(key, createResultMapConfig(clazz, resultMap));
    }
    
    protected ResultMapConfig getResultMapConfig(Method method) {
        ResultMap resultMap = method.getAnnotation(ResultMap.class);
        ResultMapRef ref = method.getAnnotation(ResultMapRef.class);
        if(resultMap == null) {
            // Try to find the map with id = "" (default map)
            ResultMapConfig resultMapConfig = 
                findResultMap(method.getDeclaringClass(), ref != null ? ref.value() : "");
            if(resultMapConfig == null) {
                // We tried to find default map and no one was found -- use automapping 
                if(ref == null) {
                    return createResultMapConfig(method.getDeclaringClass(), null);
                }
                throw new ResultMapException("Missing result map reference '" + 
                        ref.value() + 
                        "', referenced from '" +
                        method.getDeclaringClass().getName() + "#" +
                        method.getName() + "'");
            }
            return resultMapConfig;
        }
        return createResultMapConfig(method.getDeclaringClass(), resultMap);
    }
    
    /**
     * Class that contains given {@link ResultMap}
     * @param clazz
     * @param resultMap
     * @return
     */
    protected ResultMapConfig createResultMapConfig(Class<?> clazz, ResultMap resultMap) {
        List<ResultMappingConfig> mappings = new ArrayList<ResultMappingConfig>();
        boolean auto = true;
        if(resultMap != null) {
            for(Mapping mapping : resultMap.mappings()) {
                ResultMappingConfig pm = new ResultMappingConfig();
                
                pm.setProperty(mapping.property());
                pm.setColumn(mapping.column());
                pm.setColumnIndex(mapping.columnIndex());
                if(pm.getColumnIndex() == 0 && 
                        (pm.getColumn() == null || "".equals(pm.getColumn()))) {
                    pm.setColumn(pm.getProperty());
                }
                if(!"".equals(mapping.resultMap().value())) {
                    ResultMapConfig nestedMapConfig = 
                        findResultMap(clazz, mapping.resultMap().value());
                    if(nestedMapConfig == null) {
                        throw new RuntimeException("Nested map not found!");
                    }
                    pm.setResultMapConfig(nestedMapConfig);
                }
                mappings.add(pm);
            }
            auto = resultMap.auto();
        }
        
        String id;
        if(resultMap != null) {
            id = clazz.getName() + "#" + resultMap.id();    
        } else {
            // TODO: Generated!
            id = clazz.getName() + "#(auto)";
        }
        
        ResultMapConfig config = new ResultMapConfig(id);
        config.setMappings(mappings.toArray(new ResultMappingConfig[mappings.size()]));
        config.setAuto(auto);
        if(resultMap != null) {
            config.setGroupBy(resultMap.groupBy());
        }
        return config;
    }
    
    protected ResultMapConfig findResultMap(Class<?> clazz, String refId) {
        String key = clazz.getName() + "#" + refId;
        
        ResultMapConfig resultMapConfig = resultMapsConfig.get(key);
        if(resultMapConfig == null) {
            for(Class<?> interfaze : clazz.getInterfaces()) {
                resultMapConfig = findResultMap(interfaze, refId);
                if(resultMapConfig != null) {
                    break;
                }
            }
        }
        return resultMapConfig;
    }
    
    /** @return Returns the typeHandlerFactory. */
    public TypeHandlerFactory getTypeHandlerFactory() {
        return typeHandlerFactory;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("resultMapsConfig", resultMapsConfig).
            append("statementsConfig", statementsConfig).
            append("typeHandlerFactory", typeHandlerFactory).
            append("introspectionFactory", introspectionFactory).
            toString();
    }
}
