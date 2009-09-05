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

package com.google.code.nanorm.internal.config;

import java.lang.reflect.Type;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.util.ToStringBuilder;

/**
 * Parameter mapping configuration.
 * 
 * @author Ivan Dubrov
 */
public final class ParameterMappingConfig {

    private final Type type;

    private final Getter getter;

    private final Setter setter;

    /**
     * Constructor.
     * 
     * @param type parameter type
     * @param getter getter for retrieving parameter value from array of
     * arguments
     * @param setter setter for setting parameter value
     */
    public ParameterMappingConfig(Type type, Getter getter, Setter setter) {
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    /** @return the getter */
    public Getter getGetter() {
        return getter;
    }

    /** @return the type */
    public Type getType() {
        return type;
    }

    /** @return the setter */
    public Setter getSetter() {
        return setter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("type", type).append("getter", getter).append(
                "setter", setter).toString();
    }
}
