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

import com.google.code.nanorm.internal.introspect.Setter;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class SingleResultCallbackSource implements ResultCallbackSource {
    
    private final Setter setter;
    
    private final Object targetName;
    
    /**
     * @param instance
     * @param setter
     */
    public SingleResultCallbackSource(Setter setter, Object targetName) {
        this.setter = setter;
        this.targetName = targetName;
    }

    /**
     * @see com.google.code.nanorm.internal.mapping.result.ResultCallbackSource#forInstance(java.lang.Object)
     */
    public ResultCallback forInstance(final Object instance) {
        return new ResultCallback() {
            private boolean set;
            
            /**
             * @see com.google.code.nanorm.internal.mapping.result.ResultCollector#handleResult(java.lang.Object)
             */
            public void handleResult(Object obj) {
                if(set) {
                    throw new RuntimeException("Single result expected for " + targetName);
                }
                setter.setValue(instance, obj);
                set = true;
            }
        };
    }
}
