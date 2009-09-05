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

/**
 * Result map configuration.
 * 
 * FIXME: More elaborated javadoc!
 * 
 * <p>
 * This annotation is mutually exclusive with {@link Scalar} annotation (which
 * has opposite meaning) or with {@link ResultMapRef} annotation (which has
 * similar meaning).
 * </p>
 * 
 * @author Ivan Dubrov
 * @version 1.0 28.05.2008
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.METHOD, ElementType.TYPE })
public @interface ResultMap {

    /**
     * Result map identifier.
     */
    String id() default "";

    /**
     * Property mappings.
     */
    Property[] mappings() default {};

    /**
     * Properties that will be used for grouping together several rows of the
     * results.
     */
    String[] groupBy() default {};

    /**
     * Automatically map all columns that were not mapped explicitly.
     */
    boolean auto() default false;
}
