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
package com.google.code.nanorm;

import com.google.code.nanorm.internal.mapping.result.ResultCallbackSource;
import com.google.code.nanorm.internal.mapping.result.ResultMap;

/**
 * Interface for processing the {@link ResultMap} result values.
 * 
 * Instances are not thread-safe.
 * 
 * @see ResultCallbackSource
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 * @param <T> type of values this callback expects
 */
public interface ResultCallback<T> {

	/**
	 * Handle the result.
	 * 
	 * @param obj result
	 */
	void handleResult(T obj);

	/**
	 * No more data will be availabel, so flush the data to the destination.
	 */
	void finish();
}
