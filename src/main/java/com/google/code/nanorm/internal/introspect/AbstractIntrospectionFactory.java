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

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public abstract class AbstractIntrospectionFactory implements IntrospectionFactory {

    /**
     * {@inheritDoc}
     */
    public Type getPropertyType(Class<?> clazz, String path) {
        Type[] resultType = new Type[1];
        IntrospectUtils.visitPath(path, clazz, VoidPropertyVisitor.INSTANCE, resultType);
        return resultType[0];
    }

    /**
     * {@inheritDoc}
     */
    public java.lang.reflect.Type getParameterType(java.lang.reflect.Type[] types, String path) {
        Type[] resultType = new Type[1];
        IntrospectUtils.visitPath(path, types, VoidPropertyVisitor.INSTANCE, resultType);
        return resultType[0];
    }
}
