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

package com.google.code.nanorm.internal;

import java.lang.reflect.Type;
import java.util.List;

import com.google.code.nanorm.internal.introspect.Getter;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class BoundFragmentImpl implements BoundFragment {
    
    final private String sql;
    
    final private List<Getter> gettersList;
    
    final private Object[] parameters;
    
    /**
     * @param sql
     * @param gettersList
     * @param parameters
     */
    public BoundFragmentImpl(String sql, List<Getter> gettersList,
            Object[] parameters) {
        this.sql = sql;
        this.gettersList = gettersList;
        this.parameters = parameters;
    }

    /**
     * {@inheritDoc}
     */
    public void generate(StringBuilder builder, List<Object> params, List<Type> types) {
        builder.append(sql);
        for(Getter getter : gettersList) {
            params.add(getter.getValue(this.parameters));
            types.add(getter.getType());
        }
    }

}
