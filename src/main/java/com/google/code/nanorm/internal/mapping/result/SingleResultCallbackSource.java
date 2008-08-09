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

import com.google.code.nanorm.ResultCallback;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * Implementation of {@link ResultCallbackSource} that sets the result
 * to the property using the setter provided.
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class SingleResultCallbackSource implements ResultCallbackSource {
    
    private final Setter setter;
    
    private final Object sourceName;
    
    /**
     * Constructor.
     * 
     * @param setter setter for property this result callback will set
     * @param sourceName name of the source which will provide the data. Used for error messages generation.
     */
    public SingleResultCallbackSource(Setter setter, Object sourceName) {
        this.setter = setter;
        this.sourceName = sourceName;
    }

    /**
     * {@inheritDoc}
     */
    public ResultCallback<Object> forInstance(final Object instance) {
        return new ResultCallback<Object>() {
            private boolean set;
            
            /**
             * {@inheritDoc}
             */
            public void handleResult(Object obj) {
                if(set) {
                    throw new IllegalStateException("Single result expected for " + sourceName);
                }
                setter.setValue(instance, obj);
                set = true;
            }

			public void finish() {
				// Nothing to do, we set data when it came.
			}
        };
    }
}
