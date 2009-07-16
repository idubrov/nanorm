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

import java.util.NoSuchElementException;

/**
 * Property path parser.
 * 
 * @author Ivan Dubrov
 * @version 1.0 21.06.2008
 */
public final class PropertyNavigator {

    /**
     * Property path element type. Indexing operation.
     */
    public static final int INDEX = 1;

    /**
     * Property path element type. Path access operation.
     */
    public static final int PROPERTY = 2;

    private int index = -1;

    private String property;

    private int pos;

    private final String path;

    private int elementType;

    /**
     * Constructor.
     * 
     * @param path property path
     * @param pos position to start parse from
     */
    public PropertyNavigator(String path, int pos) {
        this.path = path;
        this.pos = pos;
    }

    /**
     * Constructor.
     * 
     * @param path property path
     */
    public PropertyNavigator(String path) {
        this.path = path;
    }

    /**
     * Index value (for indexing access)
     * 
     * @return index value
     */
    public int getIndex() {
        return index;
    }

    /**
     * Property name (for property access)
     * 
     * @return property name
     */
    public String getProperty() {
        return property;
    }

    /**
     * Get position in the property path.
     * 
     * @return position in the property path.
     */
    public int getPosition() {
        return pos;
    }

    /**
     * Get type of last parsed property path element.
     * 
     * @return type of last parsed property path element.
     */
    public int getElementType() {
        return elementType;
    }

    /**
     * Check if property path has next property path element.
     * 
     * @return if property path has next property path element.
     */
    public boolean hasNext() {
        return pos < path.length();
    }

    /**
     * Get property path.
     * 
     * @return property path
     */
    public String getPath() {
        return path;
    }

    /**
     * Parse next property path element.
     * 
     * @return element type
     */
    public int next() {
        if (pos >= path.length()) {
            throw new NoSuchElementException("Property path " + path + " has no more elements!");
        }

        // Skip '.' after property access
        if (elementType == PROPERTY && path.charAt(pos) == '.') {
            pos++;
        } else if (elementType == INDEX) {
            if (path.charAt(pos) != '.') {
                throw unexpected();
            }
            pos++;
        }

        char c = path.charAt(pos);
        if (c == '[' || Character.isDigit(c)) {
            index = parseIndex(c == '[');
            property = null;
            elementType = INDEX;
            return elementType;
        } else if (Character.isJavaIdentifierStart(c)) {
            property = parseProperty();
            index = -1;
            elementType = PROPERTY;
            return elementType;
        }
        throw unexpected();
    }

    /**
     * Parse index from the property path. Index could be either with brackets
     * (e.g, prop[1]) or without (e.g, prop.2);
     * 
     * @param brackets if [ and ] are used
     * @return
     */
    private int parseIndex(boolean brackets) {
        if (brackets) {
            // Skip '['
            pos++;
        }
        int start = pos;
        while (pos < path.length()) {
            char c = path.charAt(pos);
            if ((brackets && c == ']') || (!brackets && c == '.')) {
                int ind = Integer.parseInt(path.substring(start, pos));

                if (brackets) {
                    // Skip ']'
                    pos++;
                }
                return ind;
            }
            if (!Character.isDigit(c)) {
                // Invalid property path
                throw unexpected();
            }
            pos++;
        }
        if (!brackets) {
            int ind = Integer.parseInt(path.substring(start));
            return ind;
        }
        throw unexpected();
    }

    /**
     * Parse property access.
     * 
     * @return property name
     */
    private String parseProperty() {
        int start = pos;
        while (pos < path.length()) {
            char c = path.charAt(pos);
            if (!Character.isJavaIdentifierPart(c)) {
                return path.substring(start, pos);
            }
            pos++;
        }
        // Return value up to the end
        return path.substring(start);
    }

    /**
     * Create exception.
     * 
     * @return exception
     */
    private IllegalArgumentException unexpected() {
        return new IllegalArgumentException("Unexpected character at position " + pos
                + " in property path '" + path + '\'');
    }
}
