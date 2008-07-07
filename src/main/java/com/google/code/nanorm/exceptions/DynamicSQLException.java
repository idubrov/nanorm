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

package com.google.code.nanorm.exceptions;

/**
 * Error in dynamic SQL configuration.
 * @author Ivan Dubrov
 */
public class DynamicSQLException extends GenericException {

	/** Serial version. */
	private static final long serialVersionUID = -2082442157349215702L;

	/**
	 * Constructor.
	 * @param message message
	 */
	public DynamicSQLException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @param message message
	 * @param cause cause
	 */
	public DynamicSQLException(String message, Throwable cause) {
		super(message, cause);
	}
}
