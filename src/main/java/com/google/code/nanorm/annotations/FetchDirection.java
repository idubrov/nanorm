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
 * Result set direction options.
 * 
 * @author Ivan Dubrov
 */
public enum FetchDirection {
    /**
     * The constant indicating that the rows in a result set will be 
     * processed in a forward direction; first-to-last.
     * This constant is used by the method <code>setFetchDirection</code>
     * as a hint to the driver, which the driver may ignore.
     * @see java.sql.ResultSet#FETCH_FORWARD
     */
    FORWARD,

    /**
     * The constant indicating that the rows in a result set will be 
     * processed in a reverse direction; last-to-first.
     * This constant is used by the method <code>setFetchDirection</code>
     * as a hint to the driver, which the driver may ignore.
     * @see java.sql.ResultSet#FETCH_REVERSE
     */
    REVERSE,


    /**
     * The constant indicating that the order in which rows in a 
     * result set will be processed is unknown.
     * This constant is used by the method <code>setFetchDirection</code>
     * as a hint to the driver, which the driver may ignore.
     * @see java.sql.ResultSet#FETCH_UNKNOWN
     */
    UNKNOWN;
}
