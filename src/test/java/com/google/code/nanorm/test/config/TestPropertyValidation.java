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

import static com.google.code.nanorm.test.common.Utils.assertContains;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.annotations.Property;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.test.beans.Publication;


/**
 * Test properties mappings validation.
 * @author Ivan Dubrov
 */
public class TestPropertyValidation {

	
	private interface Mapper1 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Property(value = "dummy", columnIndex = 23, column = "testcolumn") })
		int selectSome(int id);
	}

	/**
	 * Test both column and column index are not specified.
	 */
	@Test
	public void testPropertyValidation1() {
		try {
			new NanormConfiguration().configure(Mapper1.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "23", "testcolumn", "testmap", "Mapper1");
		}
	}

	private interface Mapper2 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Property(value = "", columnIndex = 1) })
		int selectSome(int id);
	}

	/**
	 * Test property name is not empty.
	 */
	@Test
	public void testPropertyValidation2() {
		try {
			new NanormConfiguration().configure(Mapper2.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "empty", "property", "testmap", "mapper2");
		}
	}
	
	private interface Mapper4 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Property(value = "article") })
		Publication selectSome(int id);
	}

	/**
	 * Test all directly mapped properties have type handlers.
	 */
	@Test
	public void testPropertyValidation3() {
		try {
			new NanormConfiguration().configure(Mapper4.class);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertContains(e, "type", "handler", "Article");
		}
	}

}
