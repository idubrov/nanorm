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

package com.google.code.nanorm.exceptions;

/**
 * Data exception.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class DataException extends GenericException {

    /**
     * Serial version.
     */
    private static final long serialVersionUID = 4502054067418456999L;

    /**
     * Constructor.
     * 
     * @param message message
     * @param cause cause
     */
    public DataException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param message message
     */
    public DataException(String message) {
        super(message);
    }

}
