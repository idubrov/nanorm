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

package com.google.code.nanorm.internal.introspect.asm;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Utilities to resolve generic types using the reflection. Used for resolving
 * concrete types while generating the property accessors for nested properties.
 * 
 * <code>
 * // Generic type:
 * public class Wrapper&lt;T&gt; {
 *     private T value;
 *     
 *     public T getValue() {
 *         return value;
 *     }
 *     
 *     public void setValue(T value) {
 *         this.value = value;
 *     }
 * }
 * 
 * // Type that uses wrapper:
 * public class Bean {
 *     private Wrapper&lt;String&gt item;
 *     
 *     public Wrapper&lt;String&gt getItem() {
 *         return item;
 *     }
 *     
 *     public void setItem(Wrapper&lt;String&gt item) {
 *         this.item = item;
 *     }
 * }
 * 
 * // Code to resolve getValue actual type
 * Class&lt;?&gt; clazz = Bean.class;
 * Type returnType = clazz.getMethod("getItem").getGenericReturnType();
 * ParameterizedType pt = new ResolvedParameterizedType(clazz);
 * 
 * // Resolve return type of getItem method
 * pt = resolve(returnType, pt);
 * 
 * // Resolve return type of getValue method 
 * returnType = Wrapper.class.getMethod("getValue").getGenericReturnType();
 * pt = resolve(returnType, pt);
 * 
 * // Now pt.getRawType() will be String.class. 
 * </code>
 * 
 * @author Ivan Dubrov
 * @version 1.0 20.06.2008
 */
public class TypeOracle {

    /**
     * Resolve type in given context. Replaces all {@link TypeVariable}
     * instances using the actual parameters of the context type.
     * 
     * Propagates as much type information as possible from the context
     * parameter (which is usually the specialization of the declaring class) to
     * the given type parameter (which is any type used inside the generic
     * declaring class) and returns it as {@link ParameterizedType}.
     * 
     * @param type type to resolve
     * @param context context type (specialized generic or concrete class)
     * @return resolved type
     */
    public static Type resolve(Type type, Type context) {
        // First, we wrap context into ParameterizedType for simplicity
        // In fact, it could be only concrete type ({@link Class}) or
        // parameterized type ({@link ParameterizedType}).
        ParameterizedType resolved;
        if (context instanceof Class<?>) {
            Class<?> clazz = (Class<?>) context;
            resolved = resolveImpl(type, new ResolvedParameterizedType(clazz));
        } else if (context instanceof ParameterizedType) {
            resolved = resolveImpl(type, (ParameterizedType) context);
        } else {
            throw new RuntimeException("Not supported!");
        }

        // If we got parameterized type with zero actual parameters,
        // that means it is concrete type, simply unwrap it.
        if (resolved.getActualTypeArguments().length == 0) {
            return resolved.getRawType();
        }
        return resolved;
    }

    /**
     * Resolve type using the context.
     * 
     * @see #resolve(Type, Type)
     * @param type type to resolve.
     * @param context context type
     * @return
     */
    private static ParameterizedType resolveImpl(Type type, ParameterizedType context) {
        if (type instanceof Class<?>) {
            // If type is concrete class -- nothing to resolve, all type info is known
            return new ResolvedParameterizedType((Class<?>) type);
        } else if (type instanceof TypeVariable<?>) {
            // If type is type variable -- resolve it
            TypeVariable<?> tv = (TypeVariable<?>) type;

            Type t = resolveTypeVariable(tv, context);
            if (t instanceof ParameterizedType) {
                return (ParameterizedType) t;
            } else if (t instanceof Class<?>) {
                return new ResolvedParameterizedType((Class<?>) t);
            } else {
                throw new RuntimeException("Not supported");
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;

            // First resolve raw type
            // TODO: Pass context?
            Class<?> resolvedRawType = resolveRawType(pt.getRawType());
            
            // Recursively resolve actual type arguments
            Type[] resolvedArguments = recursivelyResolve(pt.getActualTypeArguments(), context);
            return new ResolvedParameterizedType(resolvedRawType, resolvedArguments);
        } else {
            throw new RuntimeException("Not supported!");
        }
    }

    private static Type[] recursivelyResolve(Type[] arguments, ParameterizedType context) {
        Type[] res = new Type[arguments.length];
        for (int i = 0; i < res.length; ++i) {
            if (arguments[i] instanceof Class<?>) {
                // If type is class -- nothing to resolve
                res[i] = arguments[i];
            } else if (arguments[i] instanceof TypeVariable<?>) {
                TypeVariable<?> tv = (TypeVariable<?>) arguments[i];
                res[i] = resolveTypeVariable(tv, context);
            } else if (arguments[i] instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) arguments[i];
                Type[] subargs = recursivelyResolve(pt.getActualTypeArguments(), context);

                Class<?> resolvedRawType = resolveRawType(pt.getRawType());
                res[i] = new ResolvedParameterizedType(resolvedRawType, subargs);
            }
        }
        return res;
    }

    public static Class<?> resolveRawType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return resolveRawType(pt.getRawType());
        } else {
            throw new RuntimeException("Not supported!");
        }
    }

    private static Type resolveTypeVariable(TypeVariable<?> tv, ParameterizedType owner) {
        // TODO: Cast! Should resolve it as well!
        Class<?> ownerRaw = resolveRawType(owner.getRawType());
        TypeVariable<?>[] params = ownerRaw.getTypeParameters();
        for (int i = 0; i < params.length; ++i) {
            if (params[i].equals(tv)) {
                Type argument = owner.getActualTypeArguments()[i];
                return argument;
            }
        }
        // Cannot resolve
        // TODO: Try to derive from bounds.
        return null;
    }
}
