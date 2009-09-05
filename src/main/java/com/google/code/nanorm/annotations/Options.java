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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;

/**
 * <p>
 * The annotation that allows to specify statement and result set options. Note
 * that they are applied only to the main query and not to the key selection
 * queries.
 * </p>
 * <p>
 * Note that options could be set for the mapper class/interface or to the
 * method. Options set to the mapper class/interface are used for all queries.
 * Options set to the concrete method used only when executing this method.
 * </p>
 * <p>
 * Opions applied to the method completely override the class-scoped options.
 * For example, applying empty {@code @Options()} annotation sets the default
 * options for the query method.
 * </p>
 * 
 * @author Ivan Dubrov
 */
@Target( {ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Options {

    /**
     * Specify the {@link ResultSet} fetch size.
     * @return fetch size to use for the result set. 0 means the default size.
     */
    int fetchSize() default 0;

    /**
     * Specify the {@link ResultSet} direction.
     * @return result set direction
     */
    FetchDirection direction() default FetchDirection.FORWARD;

    /**
     * <p>
     * Specify result set direction.
     * </p>
     * <p>
     * Note: does not have effect if JDBC-driven auto-generated keys are used.
     * </p>
     * @return result set type
     */
    ResultSetType resultSetType() default ResultSetType.TYPE_FORWARD_ONLY;

    /**
     * <p>
     * Specify result set concurrency.
     * </p>
     * <p>
     * Note: does not have effect if JDBC-driven auto-generated keys are used.
     * </p>
     * @return result set concurrency.
     */
    ResultSetConcurrency concurrency() default ResultSetConcurrency.CONCUR_READ_ONLY;
}
