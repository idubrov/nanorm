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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.google.code.nanorm.TypeHandler;

/**
 * Type handler for <code>float</code>.
 * 
 * @author Ivan Dubrov
 * @version 1.0 31.05.2008
 */
public class FloatTypeHandler implements TypeHandler<Float> {

    /**
     * {@inheritDoc}
     */
    public Float getValue(ResultSet rs, int column) throws SQLException {
        final float value = rs.getFloat(column);
        return rs.wasNull() ? null : value;
    }

    /**
     * {@inheritDoc}
     */
    public Float getValue(ResultSet rs, String column) throws SQLException {
        final float value = rs.getFloat(column);
        return rs.wasNull() ? null : value;
    }

    /**
     * {@inheritDoc}
     */
    public Float getValue(CallableStatement cs, int index) throws SQLException {
        final float value = cs.getFloat(index);
        return cs.wasNull() ? null : value;
    }

    /**
     * {@inheritDoc}
     */
    public void setParameter(PreparedStatement st, int column, Object value) throws SQLException {
        if (value == null) {
            st.setNull(column, Types.REAL);
        } else {
            st.setFloat(column, (Float) value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getSqlType() {
        return Types.REAL;
    }
}
