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

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.google.code.nanorm.internal.FactoryImpl;
import com.google.code.nanorm.internal.QueryDelegate;
import com.google.code.nanorm.internal.config.InternalConfiguration;
import com.google.code.nanorm.internal.config.StatementConfig;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 04.06.2008
 */
public interface IntrospectionFactory {
    
    String ZERO_PARAMETER_ALIAS = "value";
    
    Setter buildSetter(Class<?> beanClass, String path);
    
    Getter buildGetter(Class<?> beanClass, String path);

    // TODO: Pass types...
    Getter buildParameterGetter(Type[] types, String path);
    
    Type getPropertyType(Class<?> beanClass, String property);
    
    Type getParameterType(Method method, String path);
    
    Type getParameterType(Type[] types, String path);
    
    <T> T createMapper(Class<T> interfaze, InternalConfiguration config, QueryDelegate delegate);
}
