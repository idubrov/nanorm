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
package com.google.code.nanorm.internal.mapping.result;

import java.util.ArrayList;
import java.util.List;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class ArrayListCallbackSource implements ResultCallbackSource {

    final private Getter getter;
    
    final private Setter setter;
    
    /**
     * @param instance
     * @param getter
     * @param setter
     */
    public ArrayListCallbackSource(Getter getter, Setter setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * @see com.google.code.nanorm.internal.mapping.result.ResultCallbackSource#forInstance(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public ResultCallback forInstance(final Object instance) {
        return new ResultCallback() {
            private List<Object> list;
            {
                list = (List<Object>) getter.getValue(instance);
                if(list == null) {
                    list = new ArrayList<Object>();
                    setter.setValue(instance, list);
                }
            }
            /**
             * @see com.google.code.nanorm.internal.mapping.result.ResultCallback#handleResult(java.lang.Object)
             */
            public void handleResult(Object obj) {
                list.add(obj);
            }
        };
    }
}
