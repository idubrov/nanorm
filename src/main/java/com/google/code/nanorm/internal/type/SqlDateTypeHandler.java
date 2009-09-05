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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.google.code.nanorm.TypeHandler;

/**
 * Type handler for {@link java.sql.Date}
 * 
 * @author Ivan Dubrov
 */
public class SqlDateTypeHandler implements TypeHandler<Date> {

    /**
     * {@inheritDoc}
     */
    public Date getValue(ResultSet rs, int column) throws SQLException {
        return rs.getDate(column);
    }

    /**
     * {@inheritDoc}
     */
    public Date getValue(ResultSet rs, String column) throws SQLException {
        return rs.getDate(column);
    }

    /**
     * {@inheritDoc}
     */
    public Date getValue(CallableStatement cs, int index) throws SQLException {
        return cs.getDate(index);
    }

    /**
     * {@inheritDoc}
     */
    public void setParameter(PreparedStatement st, int column, Object value) throws SQLException {
        st.setDate(column, (Date) value);
    }

    /**
     * {@inheritDoc}
     */
    public int getSqlType() {
        return Types.DATE;
    }
}
