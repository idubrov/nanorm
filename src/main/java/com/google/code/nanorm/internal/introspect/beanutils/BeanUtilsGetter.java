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
package com.google.code.nanorm.internal.introspect.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class BeanUtilsGetter implements Getter {

    final private String property;
    
    final private Class<?> clazz;
    
    final private IntrospectionFactory factory;

    /**
     * 
     */
    public BeanUtilsGetter(IntrospectionFactory factory, Class<?> clazz, String path) {
        this.factory = factory;
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
       return factory.getPropertyType(clazz, property);
    }
}
