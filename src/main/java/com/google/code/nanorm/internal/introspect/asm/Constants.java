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

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import com.google.code.nanorm.internal.QueryDelegate;
import com.google.code.nanorm.internal.config.StatementConfig;

/**
 * Internal constants for ASM-based code.
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public interface Constants {
    /**
     * Mapper constructor.
     */
    static final Method MAPPER_CTOR = Method
            .getMethod("void <init>(com.google.code.nanorm.internal.QueryDelegate, "
                    + "com.google.code.nanorm.internal.config.StatementConfig[])");

    /**
     * Accessor constructor.
     */
    static final Method ACCESSOR_CTOR = Method.getMethod("void <init>(java.lang.reflect.Type)");

    /**
     * {@link com.google.code.nanorm.internal.introspect.Getter#getValue}
     * method.
     */
    static final Method GET_VALUE = Method
            .getMethod("java.lang.Object getValue(java.lang.Object)");

    /**
     * {@link com.google.code.nanorm.internal.introspect.Setter#setValue(Object, Object)}
     * method.
     */
    static final Method SET_VALUE = Method
            .getMethod("void setValue(java.lang.Object, java.lang.Object)");

    /**
     * Parameterless constructor.
     */
    static final Method CTOR = Method.getMethod("void <init>()");

    /**
     * {@link java.lang.reflect.Type}
     */
    static final Type JL_REFLECT_TYPE_TYPE = Type.getType(java.lang.reflect.Type.class);

    /**
     * {@link Object}
     */
    static final Type OBJECT_TYPE = Type.getType(Object.class);

    /**
     * Array of {@link Object}
     */
    static final Type OBJECT_ARR_TYPE = Type.getType(Object[].class);

    /**
     * {@link NullPointerException}
     */
    static final Type NPE_TYPE = Type.getType(NullPointerException.class);

    /**
     * {@link NullPointerException} constructor.
     */
    static final Method NPE_CTOR = Method.getMethod("void <init>(java.lang.String)");

    /**
     * {@link String#substring(int, int)}
     */
    static final Method SUBSTRING = Method.getMethod("String substring(int, int)");

    /**
     * {@link String#concat(String)}
     */
    static final Method CONCAT = Method.getMethod("String concat(String)");

    /**
     * {@link String}
     */
    static final Type STRING_TYPE = Type.getType(String.class);

    /**
     * {@link QueryDelegate}
     */
    static final Type QUERY_DELEGATE_TYPE = Type.getType(QueryDelegate.class);

    /**
     * {@link QueryDelegate#query(StatementConfig, Object[])} method.
     */
    static final Method QUERY_METHOD = Method
            .getMethod("Object query(com.google.code.nanorm.internal.config.StatementConfig, Object[])");

    /**
     * Array of {@link StatementConfig}.
     */
    static final Type STATEMENT_CONFIGS_ARR_TYPE = Type.getType(StatementConfig[].class);
}
