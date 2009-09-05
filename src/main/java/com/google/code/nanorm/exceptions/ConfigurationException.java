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
 * Configuration exception.
 * 
 * @author Ivan Dubrov
 * @version 1.0 29.05.2008
 */
public class ConfigurationException extends GenericException {

    /**
     * Serial version.
     */
    private static final long serialVersionUID = -8730647955287796474L;

    /**
     * Constructor.
     */
    public ConfigurationException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message message
     * @param cause cause
     */
    public ConfigurationException(String message, Throwable cause) { // NOPMD by
                                                                     // Ivan
                                                                     // Dubrov
                                                                     // on
                                                                     // 22.08.08
                                                                     // 18:46
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param message message
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param cause cause.
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
