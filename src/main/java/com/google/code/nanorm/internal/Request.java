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

import java.util.HashMap;
import java.util.Map;

import com.google.code.nanorm.ResultCallback;
import com.google.code.nanorm.internal.mapping.result.ResultCallbackSource;
import com.google.code.nanorm.internal.util.ToStringBuilder;

/**
 * Storage for request-scoped data.
 * 
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class Request {

	private final QueryDelegate queryDelegate;

	private final Map<Object, Map<Key, Object>> key2Objects;

	private Object result;

	private final Map<ResultCallbackKey, ResultCallback<Object>> callbacks;

	/**
	 * Constructor.
	 * 
	 * @param queryDelegate query delegate instance
	 */
	public Request(QueryDelegate queryDelegate) {
		this.queryDelegate = queryDelegate;
		this.callbacks = new HashMap<ResultCallbackKey, ResultCallback<Object>>();
		this.key2Objects = new HashMap<Object, Map<Key, Object>>();
	}

	/** @return Returns the result. */
	public Object getResult() {
		return result;
	}

	/** @param result The result to set. */
	public void setResult(Object result) {
		this.result = result;
	}

	/** @return Returns the key. */
	public Map<Object, Map<Key, Object>> getKey2Objects() {
		return key2Objects;
	}

	/** @return Returns the queryDelegate. */
	public QueryDelegate getQueryDelegate() {
		return queryDelegate;
	}

	/**
	 * Search the result callback in the request cache.
	 * 
	 * @param source result callback source
	 * @param target result callback target
	 * @return result callback
	 */
	public ResultCallback<Object> searchCallback(ResultCallbackSource source, Object target) {
		
		ResultCallbackKey key = new ResultCallbackKey(source, target);
		ResultCallback<Object> callback = callbacks.get(key);
		
		if (callback == null) {
			callback = source.forInstance(target);
			callbacks.put(key, callback);
		}
		return callback;
	}
	
	/**
	 * Commit all result callbacks and clear the cache.
	 */
	public void commitCallbacks() {
		for(ResultCallback<Object> c : callbacks.values()) {
			c.commit();
		}
		callbacks.clear();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("result", result)
				.append("key2Objects", key2Objects).append("queryDelegate", queryDelegate)
				.toString();
	}
}
