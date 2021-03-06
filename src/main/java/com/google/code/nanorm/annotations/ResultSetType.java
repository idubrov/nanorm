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

package com.google.code.nanorm.annotations;

/**
 * Result set type options.
 * 
 * @author Ivan Dubrov
 */
public enum ResultSetType {
    /**
     * The constant indicating the type for a <code>ResultSet</code> object
     * whose cursor may move only forward.
     * @see java.sql.ResultSet#TYPE_FORWARD_ONLY
     */
    TYPE_FORWARD_ONLY,

    /**
     * The constant indicating the type for a <code>ResultSet</code> object
     * that is scrollable but generally not sensitive to changes to the data
     * that underlies the <code>ResultSet</code>.
     * @see java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE
     */
    TYPE_SCROLL_INSENSITIVE,

    /**
     * The constant indicating the type for a <code>ResultSet</code> object
     * that is scrollable and generally sensitive to changes to the data
     * that underlies the <code>ResultSet</code>.
     * @see java.sql.ResultSet#TYPE_SCROLL_SENSITIVE
     */
    TYPE_SCROLL_SENSITIVE;
}
