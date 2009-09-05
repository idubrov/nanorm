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

package com.google.code.nanorm.internal.mapping.result;

import com.google.code.nanorm.DataSink;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.util.Messages;

/**
 * Implementation of {@link DataSinkSource} that sets the result to the property
 * using the setter provided.
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class SingleDataSinkSource implements DataSinkSource {

    private final Setter setter;

    private final Object location;

    /**
     * Constructor.
     * 
     * @param setter setter for property this data sink will set
     * @param location location that identifies the context error occurred.
     */
    public SingleDataSinkSource(Setter setter, Object location) {
        this.setter = setter;
        this.location = location;
    }

    /**
     * {@inheritDoc}
     */
    public DataSink<Object> forInstance(final Object instance) {
        return new DataSink<Object>() {
            private boolean set;

            /**
             * {@inheritDoc}
             */
            public void pushData(Object obj) {
                if (set) {
                    throw new IllegalStateException(Messages.singleResultExpected(location));
                }
                setter.setValue(instance, obj);
                set = true;
            }

            public void commitData() {
                // Nothing to do, we set data when it came.
            }
        };
    }
}
