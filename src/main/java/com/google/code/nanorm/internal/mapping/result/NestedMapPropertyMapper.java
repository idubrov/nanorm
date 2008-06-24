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
package com.google.code.nanorm.internal.mapping.result;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.nanorm.internal.Request;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class NestedMapPropertyMapper {

    final private ResultMap resultMap;
    
    final private ResultCallbackSource callbackSource;
    
    /**
     * @param setter
     * @param resultMap
     */
    public NestedMapPropertyMapper(Type type, Getter getter,
            Setter setter, ResultMap resultMap, Object target) {
        this.resultMap = resultMap;
        this.callbackSource = ResultCollectorUtil.createResultCallback(type, getter, setter, target);
    }

    /**
     * @see com.google.code.nanorm.internal.mapping.result.PropertyMapper#mapResult(java.lang.Object, java.sql.ResultSet)
     */
    public final void mapResult(Request request, final Object result, ResultSet rs) throws SQLException {
        ResultCallback callback = callbackSource.forInstance(result);
        resultMap.processResultSet(request, rs, callback);
    }
}
