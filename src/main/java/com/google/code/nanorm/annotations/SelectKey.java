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
 * TODO: Javadoc 
 * 
 * @author Ivan Dubrov
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectKey {

	/**
	 * SQL query for selecting the key (either key to be used for row insertion
	 * or key generated after insertion).
	 */
	String value() default "";

	/**
	 * Type of the select key statement. By default assume that query selects
	 * generated key and therefore should be executed after the insert
	 * operation.
	 */
	SelectKeyType type() default SelectKeyType.AFTER;

	/**
	 * Path to the key property. The generated/pre-generated key is set to this property.
	 * 
	 * <b>Note</b>: Mandatory for the {@link SelectKeyType#BEFORE} select key type, since this is
	 * the only way to pass the pre-generated key to the insert statement.  
	 * 
	 * TODO: Check this is provided for BEFORE type!
	 */
	String property() default "";
}
