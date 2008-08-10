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
import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.annotations.SelectKey;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;

/**
 * Test configuration validation messages.
 * @author Ivan Dubrov
 */
public class TestConfigValidation {

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

	private interface Mapper3 {
		@Select("SELECT 1")
		@ResultMap(mappings = { @Mapping(property = "dummy", nestedMap = @ResultMapRef("notexist")) })
		void selectSome(int id);
	}

	/**
	 * Test not existent result map referenced as nested map.
	 */
	@Test
	public void testNestedMapValidation1() {
		try {
			new NanormConfiguration().configure(Mapper3.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "selectSome");
			assertContains(e, "Mapper3");
			assertContains(e, "nested");
			assertContains(e, "notexist");
			assertContains(e, "dummy");
			assertContains(e, "property");
		}
	}

	private interface Mapper4 {
		// Nothing...
	}

	private interface Mapper5 {
		@Select("SELECT 1")
		@ResultMap(mappings = { @Mapping(property = "dummy", nestedMap = @ResultMapRef(value = "notexist", declaringClass = Mapper4.class)) })
		void selectSome(int id);
	}

	/**
	 * Test not existent result map referenced as nested map.
	 */
	@Test
	public void testNestedMapValidation2() {
		try {
			new NanormConfiguration().configure(Mapper4.class, Mapper5.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "selectSome");
			assertContains(e, "Mapper4");
			assertContains(e, "Mapper5");
			assertContains(e, "nested");
			assertContains(e, "notexist");
			assertContains(e, "dummy");
			assertContains(e, "property");
		}
	}

	private interface Mapper6 {
		// Nothing...
	}

	private interface Mapper7 {
		@Select("SELECT 1")
		@ResultMap(mappings = { @Mapping(property = "dummy", nestedMap = @ResultMapRef(value = "notexist", declaringClass = Mapper6.class)) })
		void selectSome(int id);
	}

	/**
	 * Test result map referenced as nested map, but the referee is not mapped.
	 */
	@Test
	public void testNestedMapValidation3() {
		try {
			new NanormConfiguration().configure(Mapper7.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "not configured");
			assertContains(e, "Mapper6");
			assertContains(e, "notexist");
		}
	}

	private interface Mapper8 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Mapping(property = "dummy", subselect = "selectById") })
		int selectSome(int id);
	}

	/**
	 * Test result map referenced as nested map, but the referee is not mapped.
	 */
	@Test
	public void testSubselectValidation1() {
		try {
			new NanormConfiguration().configure(Mapper8.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "not found");
			assertContains(e, "Mapper8");
			assertContains(e, "dummy");
			assertContains(e, "testmap");
			assertContains(e, "selectById");
		}
	}

	private interface Mapper9 {
		// Nothing...
	}

	private interface Mapper10 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Mapping(property = "dummy", subselect = "selectById", subselectMapper = Mapper9.class) })
		int selectSome(int id);
	}

	/**
	 * Test result map referenced as nested map, but the referee is not mapped.
	 */
	@Test
	public void testSubselectValidation2() {
		try {
			new NanormConfiguration().configure(Mapper9.class, Mapper10.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertContains(e, "not found");
			assertContains(e, "Mapper9");
			assertContains(e, "Mapper10");
			assertContains(e, "dummy");
			assertContains(e, "testmap");
			assertContains(e, "selectById");
		}
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
			new NanormConfiguration().configure(Mapper9.class, Mapper11.class);
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
			new NanormConfiguration().configure(Mapper9.class, Mapper12.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertContains(e, "empty");
			assertContains(e, "property");
			assertContains(e, "testmap");
			assertContains(e, "mapper12");
		}
	}

	@ResultMap(id = "refmap")
	private interface Mapper13 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Mapping(property = "dummy", 
				subselect = "selectById", nestedMap = @ResultMapRef("nestedmap")) })
		int selectSome(int id);

		@Select("SELECT 1")
		int selectById(int id);
	}

	/**
	 * Test result map referenced as nested map, but the referee is not mapped.
	 */
	@Test
	public void testSubselectNestedValidation1() {
		try {
			new NanormConfiguration().configure(Mapper13.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "both");
			assertContains(e, "Mapper13");
			assertContains(e, "dummy");
			assertContains(e, "testmap");
			assertContains(e, "nestedmap");
			assertContains(e, "selectById");
		}
	}

	private void assertContains(Exception e, String str) {
		Assert.assertTrue(e.getMessage().toLowerCase().contains(str.toLowerCase()));
	}
}
