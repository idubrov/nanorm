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

/**
 * Interface for processing the data. Typical implementation puts the data into
 * property of the target (sets to the property, puts into the collection in the
 * property, puts into the array).
 * 
 * Instances could push the data to the destination immediately or collect the
 * data and push it when {@link #commitData} is called.
 * 
 * Instances are not thread-safe.
 * 
 * Clients could implement this interface and pass it as a parameter of query
 * method with <code>void</code> return value. In that case, instead of
 * returning the data from the method, framework will push the mapped objects
 * into the provided data sink.
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 * @param <T> type of values this callback expects
 */
public interface DataSink<T> {

	/**
	 * Push the data into the sink.
	 * 
	 * @param obj data
	 */
	void pushData(T obj);

	/**
	 * Commit the data into the destination from the internal caches.
	 */
	void commitData();
}
