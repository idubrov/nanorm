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

import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;

/**
 * Test properties mappings validation.
 * @author Ivan Dubrov
 */
public class TestPropertyNavigation extends TestConfigValidationBase {

	
	private interface Mapper1 {
		// Nothing...
	}


	private interface Mapper11 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Mapping(property = "dummy", columnIndex = 23, column = "testcolumn") })
		int selectSome(int id);
	}

	/**
	 * Test result map referenced as nested map, but the referee is not mapped.
	 */
	@Test
	public void testPropertyValidation1() {
		try {
			new NanormConfiguration().configure(Mapper1.class, Mapper11.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertContains(e, "23");
			assertContains(e, "testcolumn");
			assertContains(e, "testmap");
			assertContains(e, "mapper11");
		}
	}

	private interface Mapper12 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Mapping(property = "", columnIndex = 1) })
		int selectSome(int id);
	}

	/**
	 * Test result map referenced as nested map, but the referee is not mapped.
	 */
	@Test
	public void testPropertyValidation2() {
		try {
			new NanormConfiguration().configure(Mapper1.class, Mapper12.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertContains(e, "empty");
			assertContains(e, "property");
			assertContains(e, "testmap");
			assertContains(e, "mapper12");
		}
	}

}
