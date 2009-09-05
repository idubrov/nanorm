/**
 * Copyright (C) 2008, 2009 Ivan S. Dubrov
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
 * Key for bean accessor, used for locating the generated accessors in the
 * cache.
 * 
 * @author Ivan Dubrov
 * @version 1.0 20.06.2008
 */
public class AccessorKey {
    private final Class<?> beanClass;

    private final String path;

    private final Type[] types;

    private final boolean isSetter;

    /**
     * Constructor.
     * 
     * @param beanClass bean class
     * @param path property path
     * @param isSetter if accessor is setter
     */
    public AccessorKey(Class<?> beanClass, String path, boolean isSetter) {
        this.beanClass = beanClass;
        this.types = null;
        this.path = path;
        this.isSetter = isSetter;
    }

    /**
     * Constructor.
     * 
     * @param types types this property getter is generated for.
     * @param path property path
     * @param isSetter if accessor is setter
     */
    public AccessorKey(Type[] types, String path, boolean isSetter) {
        this.beanClass = null;
        this.types = types.clone();
        this.path = path;
        this.isSetter = isSetter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beanClass == null) ? 0 : beanClass.hashCode());
        result = prime * result + (isSetter ? 1231 : 1237);
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + Arrays.hashCode(types);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccessorKey other = (AccessorKey) obj;
        if (beanClass == null) {
            if (other.beanClass != null) {
                return false;
            }
        } else if (!beanClass.equals(other.beanClass)) {
            return false;
        }
        if (isSetter != other.isSetter) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        if (!Arrays.equals(types, other.types)) {
            return false;
        }
        return true;
    }
}
