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

import java.lang.reflect.Method;

/**
 * Void visitor. Does nothing besides collecting the actual property type.
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public final class VoidPropertyVisitor implements PropertyVisitor<Void> {
    
	/**
	 * Empty visitor instance. Does nothing.
	 */
    public final static VoidPropertyVisitor INSTANCE = new VoidPropertyVisitor();
    
    private VoidPropertyVisitor() {
        // Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void visitBegin(Class<?> beanClass, String path) {
        // Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public Void visitEnd() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void visitIndex(int pos, int index, boolean isLast, Class<?> beanClass) {
        // Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void visitProperty(int pos, String property, Method getter, boolean isLast,
            Class<?> beanClass) {
        // Nothing.
    }

}
