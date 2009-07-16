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
package com.google.code.nanorm.internal.introspect.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import com.google.code.nanorm.exceptions.IntrospectionException;
import com.google.code.nanorm.internal.introspect.PropertyVisitor;

/**
 * Property visitor that uses reflection to follow given property path on
 * provided object instance.
 * 
 * @author Ivan Dubrov
 */
public class ReflectPropertyVisitor implements PropertyVisitor<Object> {

    private final ReflectIntrospectionFactory factory;

    private final boolean isSetter;

    private final Object value;

    private String path;

    private Object instance;

    /**
     * Constructor.
     * 
     * @param factory factory
     * @param instance instance
     */
    public ReflectPropertyVisitor(ReflectIntrospectionFactory factory, Object instance) {
        this.instance = instance;
        this.factory = factory;
        this.isSetter = false;
        this.value = null;
    }

    /**
     * Constructor.
     * 
     * @param factory factory
     * @param instance instance
     * @param value value to set
     */
    public ReflectPropertyVisitor(ReflectIntrospectionFactory factory, Object instance,
            Object value) {
        this.instance = instance;
        this.factory = factory;
        this.isSetter = true;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public void visitBegin(Class<?> beanClass, String p) {
        this.path = p;
    }

    /**
     * {@inheritDoc}
     */
    public Object visitEnd() {
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> visitIndex(int pos, int index, boolean hasNext, Class<?> beanClass) {
        if (!hasNext && isSetter) {
            Array.set(instance, index, value);
        } else {
            instance = Array.get(instance, index);
        }
        return instance != null ? instance.getClass() : null;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> visitProperty(int pos, String property, Method getter, boolean hasNext,
            Class<?> beanClass) {
        if (!hasNext && isSetter) {
            Method setter = factory.lookupSetter(instance.getClass(), property);
            try {
                setter.invoke(instance, value);
            } catch (Exception e) {
                throw new IntrospectionException("Failed to set property "
                        + path.substring(0, pos) + " of property path " + path, e);
            }
        } else {
            try {
                instance = getter.invoke(instance);
            } catch (Exception e) {
                throw new IntrospectionException("Failed to get property "
                        + path.substring(0, pos) + " of property path " + path, e);
            }
        }
        return instance != null ? instance.getClass() : null;
    }
}