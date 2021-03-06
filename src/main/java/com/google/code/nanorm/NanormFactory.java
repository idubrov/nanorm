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

package com.google.code.nanorm;

import java.sql.Connection;

import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.internal.config.InternalConfiguration;

/**
 * Central interface of the Nanorm library. Used for creating the mappers and
 * opening the sessions.
 * 
 * Factory instances are thread-safe, you can invoke any methods from any
 * thread. However, the session opened by <code>openSession</code> is bound to
 * the current thread only.
 * 
 * Mapper instances are also thread-safe, however, their methods are executed in
 * the context of session for the current thread. Before invoking any method on
 * mapper instance, you should open the session for current thread using the
 * factory the mapper is bound to. Mapper instances are intrinsically bound to
 * the factory that created them.
 * 
 * @author Ivan Dubrov
 */
public interface NanormFactory {
    /**
     * Create mapper for given interface. Note that mappers are intrinsically
     * bound to the factory that created it.
     * 
     * Mapper instances are thread-safe, though, they use session opened on the
     * current thread they are invoked on. The session should be opened using
     * the factory that created the mapper. In other words, mapper uses the
     * session that was opened on the current thread by the mapper factory.
     * 
     * Note that mappers created by separate factories are completely isolated
     * from each other.
     * 
     * @param <T> mapper type
     * @param iface iface with mapper configuration
     * @return mapper
     * @throws ConfigurationException mapper configuration is invalid
     */
    <T> T createMapper(Class<T> iface) throws ConfigurationException;

    /**
     * Open session on current thread.
     * 
     * @return session
     */
    Session openSession();

    /**
     * Open session on current thread. The session will use the connection
     * provided as a parameter. No transaction management will be performed on
     * this session, the transactions should be managed externally.
     * 
     * @param conn connection
     * @return session
     */
    Session openSession(Connection conn);

    /**
     * Get internal factory configuration. Internal method that should not be
     * used by clients.
     * 
     * TODO: Remove it. Provide other way to get the mapping data from the
     * Nanorm.
     * 
     * @return internal factory configuration.
     */
    InternalConfiguration getInternalConfiguration();
}
