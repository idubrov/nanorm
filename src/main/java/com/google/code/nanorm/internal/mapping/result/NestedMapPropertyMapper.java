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

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.nanorm.ResultCallback;
import com.google.code.nanorm.internal.Request;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * Nested map property mapper. Invokes nested map and pushes the result into the
 * property identified by given getter and setter.
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class NestedMapPropertyMapper {

	private final ResultMap resultMap;

	private final ResultCallbackSource callbackSource;

	/**
	 * Constructor.
	 * 
	 * @param getter property getter
	 * @param setter property setter
	 * @param resultMap nested result map
	 * @param source any object that identifies the mapping source. Used for
	 *            messages generation.
	 */
	public NestedMapPropertyMapper(Getter getter, Setter setter,
			ResultMap resultMap, Object source) {
		this.resultMap = resultMap;
		this.callbackSource = ResultCollectorUtil.createResultCallback(getter,
				setter, source);
	}

	/**
	 * Map the current result set row onto the property. Invokes the nested
	 * result map and pushes the result into the property.
	 * 
	 * @param request request variables
	 * @param result result object
	 * @param rs result set 
	 * @throws SQLException propagated from result set invocations
	 */
	public final void mapResult(Request request, final Object result,
			ResultSet rs) throws SQLException {
		
		// TODO: Not optimal to create and finish right now!
		// This will force array recreation!
		// TODO: Cache whem in request instead and finish on request end.
		ResultCallback<Object> callback = callbackSource.forInstance(result);
		resultMap.processResultSet(request, rs, callback);
		callback.finish();
	}
}
