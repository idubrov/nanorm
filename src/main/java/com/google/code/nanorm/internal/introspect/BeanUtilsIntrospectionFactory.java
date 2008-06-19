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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.PropertyUtils;

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
        return new BeanUtilsGetter(path);
    }

    /**
     * @see com.google.code.nanorm.internal.introspect.IntrospectionFactory#buildSetter(java.lang.Class, java.lang.String)
     */
    public Setter buildSetter(Class<?> beanClass, String path) {
        return new BeanUtilsSetter(path);
    }
    
    public Type getPropertyType(Class<?> bean, String property) {
        String[] path = property.split("\\.");

        Type type = bean;
        for(int i = 0; i < path.length; ++i) {
            // TODO: Move this to factory
            // TODO: This actually should be that way... need to write test on it.
            //Class<?> clazz = ResultCollectorUtil.resultClass(type);
            Class<?> clazz = (Class<?>) type;
            type = findPropertyType(clazz, path[i]);
        }
        return type;
    }
    
    protected Type findPropertyType(Class<?> bean, String property) {
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

    /**
     * @see com.google.code.nanorm.internal.introspect.IntrospectionFactory#buildParameterGetter(java.lang.String)
     */
    public Getter buildParameterGetter(String path) {
        return new ParameterGetter(this, path);
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
        // TODO: Copy paste!!!!!!!!
        int pos = path.indexOf('.');
        if (pos == -1) {
            pos = path.length();
        }
        String context = path.substring(0, pos);

        int parameter;
        if (context.equals("value")) {
            parameter = 0;
        } else {
            parameter = Integer.parseInt(context) - 1;
        }
        Type type = types[parameter];
        
        if(pos == path.length()) {
            return type;
        }
        String subpath = path.substring(pos + 1);
        // TODO: Cast!!!!!
        return getPropertyType((Class<?>) type, subpath);
    }

}
