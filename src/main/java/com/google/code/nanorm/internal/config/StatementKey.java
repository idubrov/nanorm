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
package com.google.code.nanorm.internal.config;

import java.lang.reflect.Type;
import java.util.Arrays;

import com.google.code.nanorm.internal.util.ToStringBuilder;

/**
 * SQL statement key.
 * 
 * @author Ivan Dubrov
 */
public class StatementKey {

    private final Class<?> mapper;

    private final String name;

    private final Type[] parameters;

    /**
     * Constructor.
     * @param mapper mapper interface
     * @param name statement name (method name)
     * @param parameters statement parameter types
     */
    public StatementKey(Class<?> mapper, String name, Type[] parameters) {
        this.mapper = mapper;
        this.name = name;
        this.parameters = parameters;
    }

    /** @return the mapper */
    public Class<?> getMapper() {
        return mapper;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @return the parameters */
    public Type[] getParameters() {
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mapper == null) ? 0 : mapper.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + Arrays.hashCode(parameters);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StatementKey other = (StatementKey) obj;
        if (mapper == null) {
            if (other.mapper != null) {
                return false;
            }
        } else if (!mapper.equals(other.mapper)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (!Arrays.equals(parameters, other.parameters)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("mapper", mapper).append("name", name).append(
                "parameters", parameters).toString();
    }
}
