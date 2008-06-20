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

package com.google.code.nanorm.test.generics;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.junit.Test;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 20.06.2008
 */
public class TestGenerics {
    
    @Test
    public void testSome() throws Exception {
//        Class<?> clazz = Owner.class;
//        
//        Method m = clazz.getMethod("getItem");
//        
//        Type type = m.getGenericReturnType();
//        ParameterizedType pt = (ParameterizedType) type;
        
        ParameterizedType pt = new ResolvedParameterizedType(Owner.class);
        String[] path = "getItem.getValue.getValue.getModel".split("\\.");
        for(int i = 0; i < path.length; ++i) {
            // TODO: Check!
            Class<?> clazz = (Class<?>) pt.getRawType();
            Method m = clazz.getMethod(path[i]);
            Type t = m.getGenericReturnType();
            pt = resolve(t, pt);
        }
        
        System.err.println(pt.getRawType());
    }
    
    private ParameterizedType resolve(Type type, ParameterizedType owner) {
        if(type instanceof Class<?>) {
            return new ResolvedParameterizedType((Class<?>) type);
        } else if(type instanceof TypeVariable<?>) {
            TypeVariable<?> tv = (TypeVariable<?>) type;
            
            Type t = resolveTypeVariable(owner, tv);
            if(t instanceof ParameterizedType) {
                return (ParameterizedType) t;
            } else if(t instanceof Class<?>) {
                return new ResolvedParameterizedType((Class<?>) t);
            } else {
                throw new RuntimeException("Not supported");
            }
        } else if(type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            
            // TODO: Resolve raw type as well!!
            Class<?> resolvedRawType = resolveRawType(pt.getRawType());
            Type[] resolvedArguments = recursivelyResolve(pt.getActualTypeArguments(), pt);
            return new ResolvedParameterizedType(resolvedRawType, resolvedArguments);
        } else {
            throw new RuntimeException("Not supported!");
        }
    }
    
    private Type[] recursivelyResolve(Type[] arguments, ParameterizedType owner) {
        Type[] res = new Type[arguments.length];
        for(int i = 0; i < res.length; ++i) {
            if(arguments[i] instanceof Class<?>) {
                res[i] = arguments[i];
            } else if(arguments[i] instanceof TypeVariable<?>) {
                TypeVariable<?> tv = (TypeVariable<?>) arguments[i];
                res[i] = resolveTypeVariable(owner, tv);
            } else if(arguments[i] instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) arguments[i];
                Type[] subargs = recursivelyResolve(pt.getActualTypeArguments(), owner);
                
                Class<?> resolvedRawType = resolveRawType(pt.getRawType());
                res[i] = new ResolvedParameterizedType(resolvedRawType, subargs);
            }
        }
        return res;
    }
    
    private Class<?> resolveRawType(Type type) {
        if(type instanceof Class<?>) {
            return (Class<?>) type;
        } else if(type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return resolveRawType(pt.getRawType());
        } else {
            throw new RuntimeException("Not supported!");
        }
    }
    
    private Type resolveTypeVariable(ParameterizedType owner, TypeVariable<?> tv) {
        // TODO: Cast! Should resolve it as well!
        Class<?> ownerRaw = resolveRawType(owner.getRawType());
        TypeVariable<?>[] params = ownerRaw.getTypeParameters(); 
        for(int i = 0; i < params.length; ++i) {
            if(params[i].equals(tv)) {
                Type argument = owner.getActualTypeArguments()[i];
                return argument;
            }
        }
        // Cannot resolve
        // TODO: Try to derive from bounds.
        return null;
    }
    
    private class ResolvedParameterizedType implements ParameterizedType {
        
        private Type rawType;
        
        private Type[] actualTypeArguments = new Type[0];
        
        /**
         * 
         */
        public ResolvedParameterizedType(Class<?> rawType) {
            this.rawType = rawType;
        }
        
        /**
         * 
         */
        public ResolvedParameterizedType(Class<?> rawType, Type[] actualTypeArguments) {
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments;
        }

        /**
         * {@inheritDoc}
         */
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        /**
         * {@inheritDoc}
         */
        public Type getOwnerType() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Type getRawType() {
            return rawType;
        }
        
    }

}
