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
	 * <p>
	 * Name of the mapper method to use for subselect query. Instead of mapping
	 * the column value to the property, the subselect method is invoked with
	 * column value as a parameter. Then the result object is set to the
	 * property.
	 * </p>
	 * <p>
	 * Mapper method is searched by the name. It must have exactly one
	 * parameter. There should be a {@link com.google.code.nanorm.TypeHandler}
	 * registered that is able to convert column value to the subselect method
	 * parameter.
	 * </p>
	 * <p>
	 * Unless {@link #subselectMapper()} is specified, the mapper method is
	 * searched in the same mapper interface.
	 * </p>
	 */
	String subselect() default "";

	/**
	 * <p>
	 * Mapper interface to search for subselect mapper method. Name of the
	 * subselect method is specified in {@link #subselect()} property.
	 * </p>
	 * <p>
	 * By default, subselect mapper method is searched in the same mapper
	 * interface.
	 * </p>
	 */
	Class<?> subselectMapper() default Object.class;
}
