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

import com.google.code.nanorm.exceptions.IntrospectionException;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public final class IntrospectUtils {
    
    private IntrospectUtils() {
        // Nothing.
    }
    
    public static <T> T visitPath(String path, Class<?> beanClass, PropertyVisitor<T> visitor, Type[] finalType) {
        return visitPath(path, beanClass, null, visitor, finalType);
    }
    
    public static <T> T visitPath(String path, Type[] types, PropertyVisitor<T> visitor, Type[] finalType) {
        return visitPath(path, null, types, visitor, finalType);
    }
    
    private static <T> T visitPath(String path, Class<?> beanClass, Type[] types, PropertyVisitor<T> visitor, Type[] finalType) {
        if(types == null) {
            visitor.visitBegin(beanClass, path);
        } else {
            // Parameter access
            int pos = path.indexOf('.');
            if (pos == -1) {
                pos = path.length();
            }
            String context = path.substring(0, pos);

            int parameter;
            if (context.equals(IntrospectionFactory.ZERO_PARAMETER_ALIAS)) {
                parameter = 0;
            } else {
                parameter = Integer.parseInt(context) - 1;
            }
            if(pos != path.length()) {
                path = path.substring(pos + 1);
            } else {
                path = "";
            }
            beanClass = (Class<?>) types[parameter];

            // Emulate parameters as Object[] as the instance type
            visitor.visitBegin(Object[].class, path);
            
            // ...and indexing which returns Object instance
            visitor.visitIndex(0, parameter, false, Object[].class, Object.class);
        }
        
        PropertyNavigator nav = new PropertyNavigator(path);

        Type type = beanClass;
        while (!nav.hasNext()) {
            int pos = nav.getPosition();
            
            int token = nav.next();
            if (token == PropertyNavigator.INDEX) {
                // TODO: Could be generic array!
                if (!beanClass.isArray()) {
                    throw new IllegalArgumentException("Array expected at property "
                            + path.substring(0, pos) + "(full property is " + path
                            + "). Actual type was " + beanClass);
                }
                Class<?> propClass = beanClass.getComponentType();
                visitor.visitIndex(pos, nav.getIndex(), nav.hasNext(), beanClass, propClass);
                
                beanClass = propClass;
                type = beanClass;
            } else if (token == PropertyNavigator.PROPERTY) {
                java.lang.reflect.Method getter = findGetter(beanClass, nav.getProperty());

                // Resolve the return type using the current context
                type = TypeOracle.resolve(getter.getGenericReturnType(), type);

                // Find out concrete Class instance behind the generics
                Class<?> propClass = TypeOracle.resolveClass(type);
                
                visitor.visitProperty(pos, nav.getProperty(), getter, nav.hasNext(), beanClass, propClass);
                beanClass = propClass;
            } else {
                throw new IllegalStateException("Unexpected token type " + token
                        + " while following property path " + path);
            }
        }
        if(finalType != null) {
            finalType[0] = type;
        }
        return visitor.visitEnd();
    }   

    public static java.lang.reflect.Method findGetter(Class<?> clazz, String property) {
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

    public static java.lang.reflect.Method findGetterCaseInsensitive(Class<?> clazz, String property) {
        for(Method m : clazz.getDeclaredMethods()) {
            String name = "get" + property;
            if(m.getName().equalsIgnoreCase("get" + property)) {
                return m;
            } else if(m.getName().equalsIgnoreCase("is" + property)) {
                // TODO: Should check only for booleans!
                return m;
            } 
        }
        return null;
    }

    public static java.lang.reflect.Method findSetter(Class<?> clazz, String property) {
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
