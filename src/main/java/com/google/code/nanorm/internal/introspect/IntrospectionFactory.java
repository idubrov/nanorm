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

package com.google.code.nanorm.internal.introspect;

import java.lang.reflect.Type;

import com.google.code.nanorm.internal.QueryDelegate;
import com.google.code.nanorm.internal.config.InternalConfiguration;

/**
 * Factory used for building getters/setters for nested properties and
 * parameters, creating mapper instances for given query delegate and
 * configuration and for introspecting properties types.
 * 
 * @author Ivan Dubrov
 * @version 1.0 04.06.2008
 */
public interface IntrospectionFactory {

    /**
     * Shortcut for first parameter in parameter property path.
     */
    String ZERO_PARAMETER_ALIAS = "value";

    /**
     * Check if given introspection factory supports abstract classes or not.
     * @return {@literal true} if given introspection factory supports abstract
     * classes, otherwise only interface-driven mappers are supported.
     */
    boolean isAbstractClassesSupported();

    /**
     * Build setter instance.
     * 
     * @param beanClass bean class
     * @param path nested property path
     * @return setter instance
     */
    Setter buildSetter(Class<?> beanClass, String path);

    /**
     * Build getter instance.
     * 
     * @param beanClass bean class
     * @param path nested property path
     * @return getter instance
     */
    Getter buildGetter(Class<?> beanClass, String path);

    /**
     * Build parameter getter instance.
     * 
     * @param types parameter types
     * @param path nested property path
     * @return getter instance
     */
    Getter buildParameterGetter(Type[] types, String path);

    /**
     * Build parameter setter instance. TODO: Write test on this!
     * 
     * @param types parameter types
     * @param path nested property path
     * @return setter instance
     */
    Setter buildParameterSetter(Type[] types, String path);

    /**
     * Get property type.
     * 
     * @param beanClass bean class
     * @param property nested property path
     * @return property type
     */
    Type getPropertyType(Class<?> beanClass, String property);

    /**
     * Get property type from parameter-based property path.
     * 
     * @param types parameter types
     * @param path nested property path
     * @return property type
     */
    Type getParameterType(Type[] types, String path);

    /**
     * Create mapper instance.
     * 
     * @param <T> mapper type
     * @param interfaze mapper interface
     * @param config mapper configuration
     * @param delegate query delegate
     * @return mapper instance
     */
    <T> T createMapper(Class<T> interfaze, InternalConfiguration config, QueryDelegate delegate);
}
