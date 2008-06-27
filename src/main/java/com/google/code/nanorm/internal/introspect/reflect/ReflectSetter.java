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
import com.google.code.nanorm.internal.introspect.PropertyNavigator;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * Reflection-based nested property setter.
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class ReflectSetter implements Setter {
    
    final private String path;
    
    final private ReflectIntrospectionFactory factory;

    /**
     * Constructor.
     * @param factory introspection factory
     * @param path property path
     */
    public ReflectSetter(ReflectIntrospectionFactory factory, String path) {
        this.path = path;
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final Object instance, Object toSet) {
        PropertyNavigator nav = new PropertyNavigator(path);

        Object current = instance;
        while (!nav.hasNext()) {
            int pos = nav.getPosition();
            
            int token = nav.next();
            if (token == PropertyNavigator.INDEX) {
                if(nav.hasNext()) {
                    Array.set(current, nav.getIndex(), toSet);
                } else {
                    current = Array.get(current, nav.getIndex());
                }
            } else if (token == PropertyNavigator.PROPERTY) {
                if(nav.hasNext()) {
                    Method setter = factory.lookupSetter(current.getClass(), nav.getProperty());
                    try {
                        setter.invoke(current, toSet);
                    } catch (Exception e) {
                        throw new IntrospectionException("Failed to set property "
                                + path.substring(0, pos) + " of property path " + path, e);
                    }
                } else {
                    Method getter = factory.lookupGetter(current.getClass(), nav.getProperty());
                    
                    try {
                        current = getter.invoke(current);
                    } catch (Exception e) {
                        throw new IntrospectionException("Failed to get property "
                                + path.substring(0, pos) + " of property path " + path, e);
                    }
                }
            } else {
                throw new IllegalStateException("Unexpected token type " + token
                        + " while following property path " + path);
            }
        }
    }
}
