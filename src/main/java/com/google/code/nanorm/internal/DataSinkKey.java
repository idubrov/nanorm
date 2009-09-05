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

package com.google.code.nanorm.internal;

import com.google.code.nanorm.internal.mapping.result.DataSinkSource;

/**
 * Key in the cache for {@link com.google.code.nanorm.DataSink} instances.
 * 
 * @author Ivan Dubrov
 */
public final class DataSinkKey {

    private final DataSinkSource source;

    private final Object target;

    /**
     * Constructor.
     * @param source data sink source
     * @param target sink target
     */
    public DataSinkKey(DataSinkSource source, Object target) {
        this.source = source;
        this.target = target;
    }

    /** @return the source */
    public DataSinkSource getSource() {
        return source;
    }

    /** @return the target */
    public Object getTarget() {
        return target;
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
        if (obj.getClass() != DataSinkKey.class) {
            return false;
        }
        DataSinkKey other = (DataSinkKey) obj;

        // Use identity equality
        return other.source == source && other.target == target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }
}
