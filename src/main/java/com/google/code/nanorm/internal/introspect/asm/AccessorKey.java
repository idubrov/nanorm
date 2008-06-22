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

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 20.06.2008
 */
public class AccessorKey {
    final private Class<?> beanClass;
    
    final private String path;
    
    final private Type[] types;
    
    /**
     * @param beanClass
     * @param path
     */
    public AccessorKey(Class<?> beanClass, String path) {
        this.beanClass = beanClass;
        this.types = null;
        this.path = path;
    }
    
    /**
     * @param types
     * @param path
     */
    public AccessorKey(Type[] types, String path) {
        this.beanClass = null;
        this.types = types;
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beanClass == null) ? 0 : beanClass.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + Arrays.hashCode(types);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AccessorKey other = (AccessorKey) obj;
        if (beanClass != other.beanClass) {
            return false;
        }
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (!Arrays.equals(types, other.types))
            return false;
        return true;
    }
}
