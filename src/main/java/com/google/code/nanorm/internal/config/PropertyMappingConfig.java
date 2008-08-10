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

import com.google.code.nanorm.internal.util.ToStringBuilder;

/**
 * Property mapping configuration.
 * 
 * @author Ivan Dubrov
 * @version 1.0 28.05.2008
 */
public class PropertyMappingConfig {
    
    private String property;
    
    private String column;
    
    private int columnIndex;
    
    private ResultMapConfig nestedMapConfig;
    
    private StatementConfig subselect;
    
    /** @return Returns the property. */
    public String getProperty() {
        return property;
    }

    /** @param property The property to set. */
    public void setProperty(String property) {
        this.property = property;
    }

    /** @return Returns the column. */
    public String getColumn() {
        return column;
    }

    /** @param column The column to set. */
    public void setColumn(String column) {
        this.column = column;
    }

    /** @return Returns the columnIndex. */
    public int getColumnIndex() {
        return columnIndex;
    }

    /** @param columnIndex The columnIndex to set. */
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /** @return Returns the nestedMapConfig. */
    public ResultMapConfig getNestedMapConfig() {
        return nestedMapConfig;
    }

    /** @param nestedMapConfig The nestedMapConfig to set. */
    public void setNestedMapConfig(ResultMapConfig nestedMapConfig) {
        this.nestedMapConfig = nestedMapConfig;
    }
    
    /** @return Returns the subselect. */
    public StatementConfig getSubselect() {
        return subselect;
    }
    
    /** @param subselect The subselect to set. */
    public void setSubselect(StatementConfig subselect) {
        this.subselect = subselect;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("property", property).
            append("column", column).
            append("columnIndex", columnIndex).
            append("nestedMapConfig", nestedMapConfig).
            append("subselect", subselect).
            toString();
    }
}
