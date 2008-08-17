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
package com.google.code.nanorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO: Check this annotation is not applied directly to the method or type!
 * 
 * Property mapping configuration.
 * 
 * @author Ivan Dubrov
 * @version 1.0 28.05.2008
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
	/**
	 * Property name.
	 */
	String value();

	/**
	 * Column name.
	 */
	String column() default "";

	/**
	 * Column index.
	 */
	int columnIndex() default 0;

	/**
	 * Nested result map reference.
	 */
	ResultMapRef nestedMap() default @ResultMapRef();

	/**
	 * Subselect query.
	 */
	String subselect() default "";

	/**
	 * Subselect query mapper. By default, assume that subselect query is in the
	 * same mapper interface.
	 */
	Class<?> subselectMapper() default Object.class;
}