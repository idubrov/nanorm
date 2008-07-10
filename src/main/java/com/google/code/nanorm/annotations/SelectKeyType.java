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

/**
 * Type of the select key statement.
 * 
 * @author Ivan Dubrov
 */
public enum SelectKeyType {
	/**
	 * This type means that select key statement generates new key that should
	 * be used for inserting the row. This type makes the mapper to invoke the
	 * select key statement before executing the insert query, then to set the
	 * result of this query to the key property and after that to invoke the
	 * actual insert query.
	 */
	BEFORE,

	/**
	 * This type means that select key statement returns the key generated for
	 * the last insert statement. This types makes the mapper to invoke the
	 * insert statement first, then to invoke select key statement, set the
	 * generated key to the key property and return it.
	 */
	AFTER
}
