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

import java.lang.reflect.Method;

/**
 * Method configuration for mapper builder.
 * 
 * @author Ivan Dubrov
 */
class MethodConfig {
    /**
     * Index in the array of statement configurations which are passed to the
     * mapper constructor.
     */
    private int index;

    /**
     * Mapper method.
     */
    private Method method;

    /**
     * Constructor.
     * @param index index
     * @param method method
     */
    public MethodConfig(Method method, int index) {
        this.index = index;
        this.method = method;
    }

    /**
     * Getter for method.
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Getter for method.
     * @return method.
     */
    public Method getMethod() {
        return method;
    }
}