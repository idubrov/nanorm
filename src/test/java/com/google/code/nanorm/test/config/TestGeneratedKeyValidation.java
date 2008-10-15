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

import com.google.code.nanorm.annotations.Insert;
import com.google.code.nanorm.annotations.SelectKey;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;
import static com.google.code.nanorm.test.common.Utils.assertContains;

/**
 * Test validation for generated keys.
 * 
 * @author Ivan Dubrov
 */
public class TestGeneratedKeyValidation {
	private interface Mapper1 {
		@Insert("INSERT INTO table(id) VALUES ${1}")
		@SelectKey(type = SelectKeyType.BEFORE, property = "dummy")
		void insertSome(int id);
	}

	/**
	 * Test SQL is specified for BEFORE generated key.
	 */
	@Test
	public void testGeneratedKeyValidation1() {
		try {
			new NanormConfiguration().configure(Mapper1.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "SQL");
			assertContains(e, "insertSome");
			assertContains(e, "Mapper1");
		}
	}

	private interface Mapper2 {
		@Insert("INSERT INTO table(id) VALUES ${1}")
		@SelectKey(type = SelectKeyType.BEFORE, value = "SELECT 1")
		void insertSome(int id);
	}

	/**
	 * Test property name is specified for BEFORE generated key.
	 */
	@Test
	public void testGeneratedKeyValidation2() {
		try {
			new NanormConfiguration().configure(Mapper2.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "insertSome");
			assertContains(e, "Mapper2");
			assertContains(e, "property");
		}
	}

}
