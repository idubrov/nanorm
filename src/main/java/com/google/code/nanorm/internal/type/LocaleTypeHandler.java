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
import java.util.Locale;

import com.google.code.nanorm.TypeHandler;

/**
 * Type handler for {@link Locale}.
 * 
 * @author Ivan Dubrov
 */
public class LocaleTypeHandler implements TypeHandler<Locale>{

	/**
	 * {@inheritDoc}
	 */
	public Locale getValue(ResultSet rs, String column) throws SQLException {
		return valueOf(rs.getString(column));
	}

	/**
	 * {@inheritDoc}
	 */
	public Locale getValue(ResultSet rs, int column) throws SQLException {
		return valueOf(rs.getString(column));
	}
	
    /**
     * {@inheritDoc}
     */
    public Locale getValue(CallableStatement cs, int index) throws SQLException {
        return valueOf(cs.getString(index));
    }

	/**
	 * {@inheritDoc}
	 */
	public void setParameter(PreparedStatement st, int column, Object parameter)
			throws SQLException {
		
		Locale locale = (Locale) parameter;
		if(parameter == null) {
			st.setNull(column, Types.VARCHAR);
		} else {
			st.setString(column, locale.toString());
		}
	}

	private Locale valueOf(String s) {
        if(s == null) {
            return null;
        }
        
        String[] tokens = s.split("_");
        Locale locale;
        if(tokens.length == 1) {
            locale = new Locale(tokens[0]);
        } else if(tokens.length == 2) {
            locale = new Locale(tokens[0], tokens[1]);
        } else {
            locale = new Locale(tokens[0], tokens[1], tokens[2]);
        }
        return locale;
    }

	/**
     * {@inheritDoc}
     */
    public int getSqlType() {
    	return Types.VARCHAR;
    }
}
