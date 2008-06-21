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
package com.google.code.nanorm.internal.introspect.beanutils;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.code.nanorm.internal.introspect.Setter;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class BeanUtilsSetter implements Setter {

    final private String path;

    /**
     * 
     */
    public BeanUtilsSetter(String path) {
        this.path = path;
    }

    /**
     * @see com.google.code.nanorm.internal.introspect.Setter#setValue(java.lang.Object)
     */
    public void setValue(Object instance, Object toSet) {
        try {
            if(PropertyUtils.isWriteable(instance, path)) {
                PropertyUtils.setNestedProperty(instance, path, toSet);
            } else {
                throw new RuntimeException("NO PROPERTY " + path + " FOUND!!!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
