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

package com.google.code.nanorm.internal.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.google.code.nanorm.TypeHandler;

/**
 * Type handler for <code>byte</code>.
 * 
 * @author Ivan Dubrov
 * @version 1.0 31.05.2008
 */
public class ByteTypeHandler implements TypeHandler<Byte> {

    /**
     * @see com.google.code.nanorm.TypeHandler#getValue(java.sql.ResultSet, int)
     */
    public Byte getValue(ResultSet rs, int column) throws SQLException {
        final byte value = rs.getByte(column);
        return rs.wasNull() ? null : value;
    }

    /**
     * @see com.google.code.nanorm.TypeHandler#getValue(java.sql.ResultSet,
     * java.lang.String)
     */
    public Byte getValue(ResultSet rs, String column) throws SQLException {
        final byte value = rs.getByte(column);
        return rs.wasNull() ? null : value;
    }

    /**
     * {@inheritDoc}
     */
    public Byte getValue(CallableStatement cs, int index) throws SQLException {
        final byte value = cs.getByte(index);
        return cs.wasNull() ? null : value;
    }

    /**
     * {@inheritDoc}
     */
    public void setParameter(PreparedStatement st, int column, Object value) throws SQLException {
        if (value == null) {
            st.setNull(column, Types.TINYINT);
        } else {
            st.setByte(column, (Byte) value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getSqlType() {
        return Types.TINYINT;
    }
}
