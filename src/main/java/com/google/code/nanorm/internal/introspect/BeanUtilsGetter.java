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
package com.google.code.nanorm.internal.introspect;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class BeanUtilsGetter implements Getter {

    final private String property;
    
    final private Class<?> clazz;

    /**
     * 
     */
    public BeanUtilsGetter(Class<?> clazz, String path) {
        this.clazz = clazz;
        this.property = path;
    }
    
    public Object getValue(Object instance) {
        try {
            return PropertyUtils.getNestedProperty(instance, property);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Type getType() {
        String[] paths = property.split("\\.");

        Type type = clazz;
        for(int i = 0; i < paths.length; ++i) {
            // TODO: Move this to factory
            // TODO: This actually should be that way... need to write test on it.
            //Class<?> clazz = ResultCollectorUtil.resultClass(type);
            Class<?> clazz = (Class<?>) type;
            type = findPropertyType(clazz, paths[i]);
        }
        return type;
    }
    
    private Type findPropertyType(Class<?> bean, String property) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean);
        for(PropertyDescriptor desc : descriptors) {
            if(desc.getName().equals(property)) {
                // TODO: Check read method the same way it is done in getPropertyType!
                Type type = desc.getReadMethod().getGenericReturnType();
                if(type instanceof ParameterizedType) {
                    return type;
                } else {
                    return desc.getPropertyType();
                }
            }
        }
        throw new RuntimeException("NO PROPERTY FOUND!");
    }
}
