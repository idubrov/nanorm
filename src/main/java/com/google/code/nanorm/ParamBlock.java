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
 * Dynamic SQL generation block with parameter.
 * 
 * Typically the implementations of this interface are declared as an anonymous
 * classes inside the {@link SQLSource} generator method.
 * 
 * @author Ivan Dubrov
 * @param <T> parameter type
 */
public interface ParamBlock<T> {
	/**
	 * Apply block generator to given parameter. Typically this method will
	 * invoke generator methods of {@link SQLSource} instance.
	 * 
	 * @param parameter parameter value.
	 */
	void generate(T parameter);
}
