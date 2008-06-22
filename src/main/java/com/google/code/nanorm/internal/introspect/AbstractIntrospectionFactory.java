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

import java.lang.reflect.Type;

import org.objectweb.asm.commons.Method;

import com.google.code.nanorm.exceptions.IntrospectionException;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public abstract class AbstractIntrospectionFactory implements IntrospectionFactory {

    /**
     * {@inheritDoc}
     */
    public java.lang.reflect.Type getPropertyType(Class<?> clazz, String path) {
        PropertyNavigator nav = new PropertyNavigator(path);

        Type type = clazz;
        while (!nav.isLast()) {
            int pos = nav.getPosition();
            int token = nav.next();
            if (token == PropertyNavigator.INDEX) {
                // TODO: Could be generic array!
                if (!clazz.isArray()) {
                    throw new IllegalArgumentException("Array expected at property "
                            + path.substring(0, pos) + "(full property is " + path
                            + "). Actual type was " + clazz);
                }
                clazz = clazz.getComponentType();
                type = clazz;
            } else if (token == PropertyNavigator.PROPERTY) {
                java.lang.reflect.Method getter = findGetter(clazz, nav.getProperty());

                // Resolve the return type using the current context
                type = TypeOracle.resolve(getter.getGenericReturnType(), type);

                // Find out concrete Class instance behind the generics
                clazz = TypeOracle.resolveClass(type);
            } else {
                throw new IllegalStateException("Unexpected token type " + token
                        + " while following property path " + path);
            }
        }
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public java.lang.reflect.Type getParameterType(java.lang.reflect.Method method, String path) {
        return getParameterType(method.getGenericParameterTypes(), path);
    }

    /**
     * {@inheritDoc}
     */
    public java.lang.reflect.Type getParameterType(java.lang.reflect.Type[] types, String path) {
        int pos = path.indexOf('.');
        if (pos == -1) {
            pos = path.length();
        }
        String context = path.substring(0, pos);

        int parameter;
        if (context.equals(ZERO_PARAMETER_ALIAS)) {
            parameter = 0;
        } else {
            parameter = Integer.parseInt(context) - 1;
        }

        if (pos == path.length()) {
            return types[parameter];
        }
        String subpath = path.substring(pos + 1);
        return getPropertyType((Class<?>) types[parameter], subpath);
    }

    protected java.lang.reflect.Method findGetter(Class<?> clazz, String property) {
        try {
            String name = "get" + Character.toUpperCase(property.charAt(0))
                    + property.substring(1);
            try {
                return clazz.getMethod(name);
            } catch (NoSuchMethodException e) {
                // TODO: Hack!
                name = "is" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
                return clazz.getMethod(name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected java.lang.reflect.Method findSetter(Class<?> clazz, String property) {
        String name = "set" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
            if (method.getParameterTypes().length == 1 && method.getName().equals(name)) {
                return method;
            }
        }
        throw new IntrospectionException("Cannot find setter method " + clazz + "." + name
                + "(<any type>)");
    }
}
