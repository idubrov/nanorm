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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.code.nanorm.TypeHandlerFactory;
import com.google.code.nanorm.exceptions.ResultMapException;
import com.google.code.nanorm.internal.Key;
import com.google.code.nanorm.internal.Request;
import com.google.code.nanorm.internal.config.ResultMapConfig;
import com.google.code.nanorm.internal.config.ResultMappingConfig;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.type.TypeHandler;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 28.05.2008
 */
public class ResultMapImpl implements ResultMap {

    final private Class<?> elementClass;

    final private ResultMapConfig config;

    final private IntrospectionFactory introspectionFactory;

    final private TypeHandlerFactory typeHandlerFactory;

    private DynamicConfig dynamicConfig;
    
    final private DynamicConfig finDynamicConfig;

    public ResultMapImpl(Type resultType, ResultMapConfig config,
            IntrospectionFactory introspectionFactory, TypeHandlerFactory typeHandlerFactory) {
        this.config = config;
        this.introspectionFactory = introspectionFactory;
        this.typeHandlerFactory = typeHandlerFactory;
        this.elementClass = ResultCollectorUtil.resultClass(resultType);
        
        if (!config.isAuto()) {
            List<ResultMappingConfig> list = Arrays.asList(config.getMappings());
            finDynamicConfig = generatePropertyMappers(list);
        } else {
            finDynamicConfig = null;
        }
    }

    public void processResultSet(Request request, ResultSet rs, ResultCallback callback)
            throws SQLException {
        DynamicConfig dc;
        if (config.isAuto()) {
            synchronized (this) {
                if (dynamicConfig == null) {
                    List<ResultMappingConfig> configs = generateAutoConfig(rs.getMetaData());
                    dynamicConfig = generatePropertyMappers(configs);
                }
                dc = dynamicConfig;
            }
        } else {
            // No need to synchronize -- we initialized it in the constructor
            dc = finDynamicConfig;
        }

        Object result;
        Key key = generateRowKey(dc, rs);
        // We have a groupBy
        if (key != null) {
            result = null;
            
            // Look in the request map for objects under this key
            Map<Key, Object> map = request.getKey2Objects().get(this);
            if (map != null) {
                result = map.get(key);
            }
            // No result -- create new
            if (result == null) {
                result = createResult(request, dc.mappers, rs);
                callback.handleResult(result);

                if (map == null) {
                    map = new HashMap<Key, Object>();
                    request.getKey2Objects().put(this, map);
                }
                map.put(key, result);
            } else {
                // TODO: ????
            }
        } else {
            // We don't have a groupBy, create new result object 
            result = createResult(request, dc.mappers, rs);
            callback.handleResult(result);
        }
        
        // Always map nested maps
        for (PropertyMapper mapper : dc.nestedMappers) {
            mapper.mapResult(request, result, rs);
        }
    }

    private Object createResult(Request request, PropertyMapper[] mappers, ResultSet rs)
            throws SQLException {
        Object result;
        try {
            result = elementClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // TODO: We, probably, can bulk set those...
        for (PropertyMapper mapper : mappers) {
            mapper.mapResult(request, result, rs);
        }
        return result;
    }

    protected Key generateRowKey(DynamicConfig dc, ResultSet rs) throws SQLException {
        String[] groupBy = config.getGroupBy();
        if (groupBy != null && groupBy.length > 0) {
            Object[] key = new Object[dc.keyGenerators.length];
            for (int i = 0; i < dc.keyGenerators.length; ++i) {
                key[i] = dc.keyGenerators[i].getValue(rs);
            }
            return new Key(key);
        } else {
            return null;
        }
    }

    /**
     * Generate configuration for automapping.
     * 
     * @param meta
     * @throws SQLException
     */
    protected List<ResultMappingConfig> generateAutoConfig(ResultSetMetaData meta)
            throws SQLException {
        List<ResultMappingConfig> configs = new ArrayList<ResultMappingConfig>();

        configs.addAll(Arrays.asList(config.getMappings()));

        Set<String> usedColumns = config.isAuto() ? new HashSet<String>() : null;
        for (ResultMappingConfig mappingConfig : config.getMappings()) {
            if (mappingConfig.getColumnIndex() != 0) {
                usedColumns.add(meta.getColumnName(mappingConfig.getColumnIndex()).toLowerCase());
            } else {
                usedColumns.add(mappingConfig.getColumn().toLowerCase());
            }
        }
        for (int i = 0; i < meta.getColumnCount(); ++i) {
            String column = meta.getColumnName(i + 1).toLowerCase();

            // If column is not in the mapping config, try to automap it
            if (!usedColumns.contains(column)) {
                ResultMappingConfig config = new ResultMappingConfig();
                config.setColumn(column);

                // Find property with case-insensitive search
                PropertyDescriptor[] descriptors = PropertyUtils
                        .getPropertyDescriptors(elementClass);
                for (PropertyDescriptor descriptor : descriptors) {
                    if (descriptor.getName().equalsIgnoreCase(column)) {
                        config.setProperty(descriptor.getName());
                        break;
                    }
                }
                if (config.getProperty() == null) {
                    // FIXME: Just skip it?
                    throw new ResultMapException("No matching property for column '" + column
                            + "' was found when auto-mapping the bean " + elementClass);
                }
                configs.add(config);
            }
        }
        return configs;
    }

    final private DynamicConfig generatePropertyMappers(List<ResultMappingConfig> configs) {
        
        String[] groupBy = config.getGroupBy();

        List<PropertyMapper> mappers = new ArrayList<PropertyMapper>();
        List<PropertyMapper> nestedMappers = new ArrayList<PropertyMapper>();
        List<KeyGenerator> keyGenerators = new ArrayList<KeyGenerator>();
        for (ResultMappingConfig mappingConfig : configs) {
            Type propertyType = introspectionFactory.getPropertyType(elementClass, mappingConfig
                    .getProperty());

            Setter setter = introspectionFactory.buildSetter(elementClass, mappingConfig
                    .getProperty());
            
            // TODO: Check all groupBy's are found!
            if (groupBy != null && search(groupBy, mappingConfig.getProperty())) {
                if (mappingConfig.getResultMapConfig() != null) {
                    throw new RuntimeException("Group by does not work for nested maps");
                }

                KeyGenerator keyGen = new KeyGenerator();
                keyGen.typeHandler = typeHandlerFactory.getTypeHandler((Class<?>) propertyType);
                keyGen.config = mappingConfig;
                keyGenerators.add(keyGen);
            }

            if (mappingConfig.getResultMapConfig() != null) {
                // TODO: Hacky? Why?
                Getter getter = introspectionFactory.buildGetter(elementClass, mappingConfig.getProperty());

                ResultMap nestedMap = new ResultMapImpl(propertyType, mappingConfig
                        .getResultMapConfig(), introspectionFactory, typeHandlerFactory);
                nestedMappers.add(new NestedResultMapPropertyMapperImpl(propertyType, getter,
                        setter, nestedMap));
            } else {
                // TODO: Cast...
                // TODO: This will fail if collection property is not mapped as nested map!
                TypeHandler<?> typeHandler = typeHandlerFactory
                        .getTypeHandler((Class<?>) propertyType);
                mappers.add(new PropertyMapperImpl(mappingConfig, setter, typeHandler));
            }
        }
        DynamicConfig dc = new DynamicConfig();
        dc.mappers = mappers.toArray(new PropertyMapper[mappers.size()]);
        dc.nestedMappers = nestedMappers.toArray(new PropertyMapper[nestedMappers.size()]);
        dc.keyGenerators = keyGenerators.toArray(new KeyGenerator[keyGenerators.size()]);
        return dc;
    }

    private boolean search(String[] array, String value) {
        for (String str : array) {
            if (str.equals(value)) {
                return true;
            }
        }
        return false;
    }
    
    private static class DynamicConfig {
        public PropertyMapper[] mappers;
        
        public KeyGenerator[] keyGenerators;
        
        public PropertyMapper[] nestedMappers;
    }

    private static class KeyGenerator {
        public TypeHandler<?> typeHandler;

        public ResultMappingConfig config;

        public Object getValue(ResultSet rs) throws SQLException {
            if (config.getColumnIndex() != 0) {
                return typeHandler.getValue(rs, config.getColumnIndex());
            } else {
                return typeHandler.getResult(rs, config.getColumn());
            }
        }
    }
}
