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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 29.05.2008
 */
public class ResultMapConfig {
    
    private String id;

    private ResultMappingConfig[] mappings;

    private boolean auto;

    private String[] groupBy;
    
    /**
     * 
     */
    public ResultMapConfig(String id) {
        this.id = id;
    }

    /** @return Returns the resultMappingsConfigs. */
    public ResultMappingConfig[] getMappings() {
        return mappings;
    }

    /** @param resultMappingsConfigs The resultMappingsConfigs to set. */
    public void setMappings(ResultMappingConfig[] resultMappingsConfigs) {
        this.mappings = resultMappingsConfigs;
    }

    /** @return Returns the auto. */
    public boolean isAuto() {
        return auto;
    }

    /** @param auto The auto to set. */
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    /** @return Returns the groupBy. */
    public String[] getGroupBy() {
        return groupBy;
    }

    /** @param groupBy The groupBy to set. */
    public void setGroupBy(String[] groupBy) {
        this.groupBy = groupBy;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("id", id).
            append("auto", auto).
            append("groupBy", groupBy).
            append("mappings", mappings).
            toString();
    }
}
