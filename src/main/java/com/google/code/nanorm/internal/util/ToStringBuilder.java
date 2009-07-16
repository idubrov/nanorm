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
package com.google.code.nanorm.internal.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to generate pretty string representations.
 * 
 * @see Object#toString()
 * @author Ivan Dubrov
 */
public class ToStringBuilder {
    private final Object object;

    private final StringBuilder builder;

    private final Set<Object> visited;

    private boolean first;

    /**
     * Constructor.
     * @param object object to generate to string for
     */
    public ToStringBuilder(Object object) {
        this.object = object;
        this.builder = new StringBuilder();
        this.visited = new HashSet<Object>();
        this.first = true;

        if (object != null) {
            builder.append(object.getClass().getSimpleName());
            builder.append('@');
            builder.append(System.identityHashCode(object));
            builder.append('[');
        }
    }

    /**
     * Append object field
     * @param field field name
     * @param value value
     * @return this
     */
    public ToStringBuilder append(String field, Object value) {
        if (visited.contains(value)
                && !(value instanceof Number || value instanceof String
                        || value instanceof Boolean || value instanceof Character)) {
            builder.append(System.identityHashCode(value));
        } else if (value == null) {
            appendField(field);
            builder.append("<null>");
        } else {
            visited.add(value);
            appendField(field);
            builder.append(value);
        }
        return this;
    }

    /**
     * Append array field
     * @param field field name
     * @param arr array
     * @return this
     */
    public ToStringBuilder append(String field, Object[] arr) {
        appendField(field);
        builder.append('{');
        boolean oldfirst = first;
        first = true;
        if (arr != null) {
            for (Object item : arr) {
                append(null, item);
            }
        }
        first = oldfirst;
        builder.append('}');
        return this;
    }

    /**
     * Append Class field
     * @param field field name
     * @param value value
     * @return this
     */
    public ToStringBuilder append(String field, Class<?> value) {
        appendField(field);
        builder.append(value.getSimpleName());
        return this;
    }

    /**
     * Append String field
     * @param field field name
     * @param value value
     * @return this
     */
    public ToStringBuilder append(String field, String value) {
        appendField(field);
        builder.append('"');
        builder.append(value);
        builder.append('"');
        return this;
    }

    /**
     * Append boolean field
     * @param field field name
     * @param value value
     * @return this
     */
    public ToStringBuilder append(String field, boolean value) {
        appendField(field);
        builder.append(value);
        return this;
    }

    /**
     * Append byte field
     * @param field field name
     * @param value value
     * @return this
     */
    public ToStringBuilder append(String field, byte value) {
        appendField(field);
        builder.append(value);
        return this;
    }

    /**
     * Append char field
     * @param field field name
     * @param value value
     * @return this
     */
    public ToStringBuilder append(String field, char value) {
        appendField(field);
        builder.append(value);
        return this;
    }

    /**
     * Append short field
     * @param field field name
     * @param value value
     * @return this
     */
    public ToStringBuilder append(String field, short value) {
        appendField(field);
        builder.append(value);
        return this;
    }

    /**
     * Append int field
     * @param field field name
     * @param value value
     * @return this
     */
    public ToStringBuilder append(String field, int value) {
        appendField(field);
        builder.append(value);
        return this;
    }

    /**
     * Append long field
     * @param field field name
     * @param value value
     * @return this
     */
    public ToStringBuilder append(String field, long value) {
        appendField(field);
        builder.append(value);
        return this;
    }

    private void appendField(String field) {
        if (first) {
            first = false;
        } else {
            builder.append(',');
        }
        if (field != null) {
            builder.append(field);
            builder.append('=');
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        if (object == null) {
            builder.append("<null>");
        } else {
            builder.append(']');
        }
        return builder.toString();
    }
}
