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

import com.google.code.nanorm.SQLSource;

/**
 * <p>
 * Stored procedure call statement marker. Query method marked by this
 * annotation will be treated as method performing stored procedure invocation.
 * </p>
 * <p>
 * For static SQL query, set the {@link #value()} property, for dynamic SQL use
 * the {@link #sqlSource()} (see {@link SQLSource} for more details about the
 * dynamic SQL). Note that these two properties are mutually exclusive.
 * </p>
 * 
 * TODO: Out parameters.
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@Target( {ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Call {
    /**
     * SQL statement. This is mutually exclusive with {@link #sqlSource()}.
     */
    String value();

    /**
     * SQL generator. This is mutually exclusive with {@link #value()}.
     */
    Class<? extends SQLSource> sqlSource() default SQLSource.class;
}
