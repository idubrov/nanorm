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

public class ResolvedParameterizedType implements ParameterizedType {
    
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