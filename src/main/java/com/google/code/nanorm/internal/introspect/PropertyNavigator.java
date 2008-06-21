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
 * @version 1.0 21.06.2008
 */
public final class PropertyNavigator {

    public static int INDEX = 1;

    public static int PROPERTY = 2;
    
    private int index = -1;

    private String property;

    private int pos;
    
    private String path;
    
    private int token;
    
    public PropertyNavigator(String path) {
        this.path = path;
    }

    public final int getIndex() {
        return index;
    }
    
    public final String getProperty() {
        return property;
    }
    
    public final int getPosition() {
        return pos;
    }
    
    public final int getToken() {
        return token;
    }
    
    public final boolean isLast() {
        return pos == path.length();
    }
    
    public final String getPath() {
        return path;
    }
    
    public final int next() {
        // TODO: Check end
        // Skip '.' after property access
        if(token == PROPERTY && path.charAt(pos) == '.') {
            pos++;
        } else if(token == INDEX) {
            if(path.charAt(pos) != '.') {
                throw unexpected();
            }
            pos++;
        }
        
        char c = path.charAt(pos);
        if(c == '[') {
            index = parseIndex();
            property = null;
            token = INDEX;
            return token;
        } else if(Character.isJavaIdentifierStart(c)) {
            property = parseProperty();
            index = -1;
            token = PROPERTY;
            return token;
        }
        throw unexpected();
    }
    
    private int parseIndex() {
        // Skip '['
        pos++;
        int start = pos;
        while(pos < path.length()) {
            char c = path.charAt(pos);
            if(c == ']') {
                int ind = Integer.parseInt(path.substring(start, pos));
                
                // Skip ']'
                pos++;
                return ind;
            }
            if(!Character.isDigit(c)) {
                // Invalid property path
                break;
            }
            pos++;
        }
        throw unexpected();
    }
    
    private String parseProperty() {
        int start = pos;
        while(pos < path.length()) {
            char c = path.charAt(pos);
            if(!Character.isJavaIdentifierPart(c)) {
                return path.substring(start, pos);
            }
            pos++;
        }
        // Return value up to the end
        return path.substring(start);
    }
    
    private IllegalArgumentException unexpected() {
        return new IllegalArgumentException("Unexpected character at position " + pos + " in property path '" + path + '\'');
    }
}
