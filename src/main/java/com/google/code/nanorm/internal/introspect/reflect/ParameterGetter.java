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

import java.lang.reflect.Type;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectUtils;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 06.06.2008
 */
public class ParameterGetter implements Getter {
    
    final private String path;
    
    final private IntrospectionFactory factory;
    
    final private Type[] types;
    
    public ParameterGetter(IntrospectionFactory factory, Type[] types, String path) {
        this.factory = factory;
        this.types = types;
        this.path = path;
    }

    /**
     * @see com.google.code.nanorm.internal.introspect.Getter#getValue(java.lang.Object)
     */
    public Object getValue(Object instance) {
        ReflectAccessor accessor = new ReflectAccessor(instance);
        return IntrospectUtils.visitPath(path, types, accessor, null);
    }

    /**
     * {@inheritDoc}
     */
    public Type getType() {
        return factory.getParameterType(types, path);
    }

}
