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

package com.google.code.nanorm.internal;

/**
 * SQL fragment not bound to any parameters. Instances of this interface are
 * thread-safe.
 * 
 * Can be bound to parameters to get {@link BoundFragment} instance.
 * 
 * @see BoundFragment
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public interface Fragment {
    /**
     * Bind parameters to the SQL fragment.
     * @param parameters parameters
     * @return SQL fragment, bound to the concrete parameters
     */
    BoundFragment bindParameters(Object[] parameters);
}
