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
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 06.06.2008
 */
public class ParameterGetter implements Getter {
    
    final private String path;
    
    final private IntrospectionFactory factory;
    
    final private int index;
    
    final private Type[] types;
    
    public ParameterGetter(IntrospectionFactory factory, Type[] types, String path) {
        this.factory = factory;
        this.types = types;
        
        int pos = path.indexOf('.');
        if (pos == -1) {
            pos = path.length();
        }
        String context = path.substring(0, pos);

        int parameter;
        if (context.equals("value")) {
            parameter = 0;
        } else {
            parameter = Integer.parseInt(context) - 1;
        }
        this.index = parameter;
        this.path = pos == path.length() ? null : path.substring(pos + 1);
    }

    /**
     * @see com.google.code.nanorm.internal.introspect.Getter#getValue(java.lang.Object)
     */
    public Object getValue(Object instance) {
        Object value = ((Object[]) instance)[index];
        
        if(path == null) {
            return value;
        }
        Getter getter = 
            factory.buildGetter(value != null ? value.getClass() : void.class, path);
        return getter.getValue(value);
    }

    /**
     * {@inheritDoc}
     */
    public Type getType() {
        Type type = types[index];
        if(path == null) {
            return type;
        }
        return factory.getPropertyType((Class<?>) type, path);
    }

}
