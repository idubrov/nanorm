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

package com.google.code.nanorm.test.config;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;

/**
 * Test result map configurations.
 * @author Ivan Dubrov
 */
public class TestResultMapValidation extends TestConfigValidationBase {
	
	private interface Mapper1 {
		// Nothing...
	}

	private interface Mapper2 {
		@Select("SELECT 1")
		@ResultMapRef(declaringClass = Mapper1.class, value = "refmap")
		int selectSome(int id);
	}

	/**
	 * Test missing result map reference.
	 */
	@Test
	public void testResultMapRefValidation() {
		try {
			new NanormConfiguration().configure(Mapper1.class, Mapper2.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "result map", "not found", "refmap", "Mapper1", "Mapper2");
		}
	}
}