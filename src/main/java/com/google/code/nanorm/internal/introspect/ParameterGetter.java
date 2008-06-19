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

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 06.06.2008
 */
public class ParameterGetter implements Getter {
    
    private String path;
    
    private IntrospectionFactory factory;
    
    public ParameterGetter(IntrospectionFactory factory, String path) {
        this.factory = factory;
        this.path = path;
    }

    /**
     * @see com.google.code.nanorm.internal.introspect.Getter#getValue(java.lang.Object)
     */
    public Object getValue(Object instance) {
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
        Object value = ((Object[]) instance)[parameter];
        
        if(pos == path.length()) {
            return value;
        }
        String subpath = path.substring(pos + 1);
        Getter getter = 
            factory.buildGetter(value != null ? value.getClass() : void.class, subpath);
        return getter.getValue(value);
    }

}
