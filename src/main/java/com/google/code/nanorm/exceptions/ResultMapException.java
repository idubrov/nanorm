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
 *
 * @author Ivan Dubrov
 * @version 1.0 29.05.2008
 */
public class ResultMapException extends ConfigurationException {

    /**
     * 
     */
    private static final long serialVersionUID = -5608536307680213988L;

    /**
     * 
     */
    public ResultMapException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public ResultMapException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ResultMapException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ResultMapException(Throwable cause) {
        super(cause);
    }
}
