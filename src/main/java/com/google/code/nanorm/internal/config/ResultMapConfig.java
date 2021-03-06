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

import com.google.code.nanorm.internal.util.ToStringBuilder;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 29.05.2008
 */
public class ResultMapConfig {

    private final String id;

    private PropertyMappingConfig[] mappings;

    private boolean auto;

    /**
     * Scalar result mapping (row is mapped to the object via appropriate
     * {@link com.google.code.nanorm.TypeHandler})
     */
    private boolean scalar;

    private String[] groupBy;

    /**
     * Constructor.
     * 
     * @param id result map config
     */
    public ResultMapConfig(String id) {
        this.id = id;
    }

    /** @return Returns the resultMappingsConfigs. */
    public PropertyMappingConfig[] getMappings() {
        return mappings;
    }

    /** @param resultMappingsConfigs The resultMappingsConfigs to set. */
    public void setMappings(PropertyMappingConfig[] resultMappingsConfigs) {
        this.mappings = resultMappingsConfigs;
    }

    /**
     * Get if this result map should auto map the columns not specified in the
     * mapping explicitly.
     * 
     * @return auto map mode.
     */
    public boolean isAuto() {
        return auto;
    }

    /**
     * Set if this result map should auto map the columns not specified in the
     * mapping explicitly.
     * 
     * @param auto auto map mode
     */
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    /**
     * Get if this result map is scalar mapping (first column is mapped to the
     * object using the {@link com.google.code.nanorm.TypeHandlerFactory}.
     * 
     * @return boolean if result should be mapped as a scalar
     */
    public boolean isScalar() {
        return scalar;
    }

    /**
     * Mark this result map configuration as scalar mapping.
     * 
     * @param scalar scalar mapping flag
     */
    public void setScalar(boolean scalar) {
        this.scalar = scalar;
    }

    /**
     * Get the list of properties to group results by.
     * 
     * @return list of properties to group results by.
     */
    public String[] getGroupBy() {
        return groupBy;
    }

    /**
     * Set the list of properties to group results by.
     * 
     * @param groupBy list of properties to group results by.
     */
    public void setGroupBy(String[] groupBy) {
        this.groupBy = groupBy;
    }

    /** @return Returns the id. */
    public String getId() {
        return id;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("auto", auto).append("groupBy",
                groupBy).append("mappings", mappings).toString();
    }
}
