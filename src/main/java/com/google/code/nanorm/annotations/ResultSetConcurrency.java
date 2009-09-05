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
 * Result set concurrency.
 * 
 * @author Ivan Dubrov
 */
public enum ResultSetConcurrency {

    /**
     * The constant indicating the concurrency mode for a
     * <code>ResultSet</code> object that may NOT be updated.
     * @see java.sql.ResultSet#CONCUR_READ_ONLY
     */
    CONCUR_READ_ONLY,

    /**
     * The constant indicating the concurrency mode for a
     * <code>ResultSet</code> object that may be updated.
     * @see java.sql.ResultSet#CONCUR_UPDATABLE
     */
    CONCUR_UPDATABLE;
}
