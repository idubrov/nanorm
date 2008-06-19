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
package com.google.code.nanorm.internal.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 31.05.2008
 */
public class DoubleTypeHandler implements TypeHandler<Double> {

    /**
     * @see com.google.code.nanorm.internal.type.TypeHandler#getResult(java.sql.ResultSet, int)
     */
    public Double getResult(ResultSet rs, int column) throws SQLException {
        return rs.getDouble(column);
    }

    /**
     * @see com.google.code.nanorm.internal.type.TypeHandler#getResult(java.sql.ResultSet, java.lang.String)
     */
    public Double getResult(ResultSet rs, String column) throws SQLException {
        return rs.getDouble(column);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setResult(PreparedStatement st, int column, Object value) throws SQLException {
        if(value == null) {
            st.setNull(column, Types.DOUBLE);
        } else {
            st.setDouble(column, (Double) value);
        }
    }
}
