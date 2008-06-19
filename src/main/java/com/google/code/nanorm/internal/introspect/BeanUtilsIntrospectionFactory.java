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

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 04.06.2008
 */
public class BeanUtilsIntrospectionFactory implements IntrospectionFactory {

    /**
     * @see com.google.code.nanorm.internal.introspect.IntrospectionFactory#buildGetter(java.lang.Class, java.lang.String)
     */
    public Getter buildGetter(Class<?> beanClass, String path) {
        return new BeanUtilsGetter(beanClass, path);
    }

    /**
     * @see com.google.code.nanorm.internal.introspect.IntrospectionFactory#buildSetter(java.lang.Class, java.lang.String)
     */
    public Setter buildSetter(Class<?> beanClass, String path) {
        return new BeanUtilsSetter(path);
    }
    
    // TODO: Remove this method!
    public Type getPropertyType(Class<?> bean, String property) {
        return buildGetter(bean, property).getType();
    }
    
   

    /**
     * @see com.google.code.nanorm.internal.introspect.IntrospectionFactory#buildParameterGetter(java.lang.String)
     */
    public Getter buildParameterGetter(Type[] types, String path) {
        return new ParameterGetter(this, types, path);
    }

    /**
     * {@inheritDoc}
     */
    public Type getParameterType(Method method, String path) {
        return getParameterType(method.getGenericParameterTypes(), path);
    }

    /**
     * {@inheritDoc}
     */
    public Type getParameterType(Type[] types, String path) {
        return buildParameterGetter(types, path).getType();

    }

}
