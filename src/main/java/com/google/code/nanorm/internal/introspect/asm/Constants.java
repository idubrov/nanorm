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

import com.google.code.nanorm.internal.FactoryImpl;
import com.google.code.nanorm.internal.QueryDelegate;
import com.google.code.nanorm.internal.config.StatementConfig;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public class Constants {
    final static Method MAPPER_CTOR = Method
            .getMethod("void <init>(com.google.code.nanorm.internal.QueryDelegate, "
                    + "com.google.code.nanorm.internal.config.StatementConfig[])");

    final static Method GETTER_CTOR = Method.getMethod("void <init>(java.lang.reflect.Type)");

    final static Method GET_VALUE = Method
            .getMethod("java.lang.Object getValue(java.lang.Object)");

    final static Method SET_VALUE = Method
            .getMethod("void setValue(java.lang.Object, java.lang.Object)");

    final static Method CTOR = Method.getMethod("void <init>()");

    final static Type JL_REFLECT_TYPE_TYPE = Type.getType(java.lang.reflect.Type.class);

    final static Type OBJECT_TYPE = Type.getType(Object.class);

    final static Type VOID_TYPE = Type.getType(void.class);

    final static Type OBJECT_ARR_TYPE = Type.getType(Object[].class);

    final static Type NPE_TYPE = Type.getType(NullPointerException.class);

    final static Method NPE_CTOR = Method.getMethod("void <init>(java.lang.String)");

    final static Method SUBSTRING = Method.getMethod("String substring(int, int)");

    final static Method CONCAT = Method.getMethod("String concat(String)");

    final static Type STRING_TYPE = Type.getType(String.class);

    final static Type QUERY_DELEGATE_TYPE = Type.getType(QueryDelegate.class);

    final static Method QUERY_METHOD = Method
            .getMethod("Object query(com.google.code.nanorm.internal.config.StatementConfig, Object[])");

    final static Type STATEMENT_CONFIGS_ARR_TYPE = Type.getType(StatementConfig[].class);

}
