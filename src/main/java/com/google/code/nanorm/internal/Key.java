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
package com.google.code.nanorm.internal;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public final class Key {
    private Object[] tuple;

    /**
     * @param tuple
     */
    public Key(Object[] tuple) {
        super();
        this.tuple = tuple;
    }

   
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int code = 0;
        for(int i = 0; i < tuple.length; ++i) {
            code |= tuple[i].hashCode();
        }
        return code;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(obj.getClass() != Key.class) {
            return false;
        }
        Key other = (Key) obj;
        if(tuple.length != other.tuple.length) {
            return false;
        }
        for(int i = 0; i < tuple.length; ++i) {
            if(tuple[i] != other.tuple[i]) {
                if(tuple[i] == null) {
                    return false;
                }
                if(!tuple[i].equals(other.tuple[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("tuple", tuple).
            toString();
    }
}
