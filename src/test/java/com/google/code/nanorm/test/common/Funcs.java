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

package com.google.code.nanorm.test.common;

/**
 * Functions to be used for stored procedures call test.
 * 
 * @author Ivan Dubrov
 */
public class Funcs {

    /**
     * Return concatenation of two strings.
     * 
     * @param a first string
     * @param b second string
     * @return concatenation of two strings
     */
    public static String concat(String a, String b) {
        return a + b;
    }

    /**
     * Return concatenation of two strings. Set the result to the string holder.
     * 
     * @param a first string
     * @param b second string
     * @param holder string holder to set result to
     * @return concatenation of two strings
     */
    public static String concat2(String a, String b, StringHolder holder) {
        holder.setValue(a + b);
        return holder.getValue();
    }
}
