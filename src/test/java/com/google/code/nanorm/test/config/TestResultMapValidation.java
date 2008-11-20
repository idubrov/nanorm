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
import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.Property;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Scalar;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;

/**
 * Test result map configurations.
 * @author Ivan Dubrov
 */
public class TestResultMapValidation {
	
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
	
	@ResultMap(id = "refmap")
	private interface Mapper3 {
		@Select("SELECT 1")
		@ResultMapRef(declaringClass = Mapper3.class, value = "refmap")
		@ResultMap(id = "somemap")
		int selectSome(int id);
	}

	/**
	 * TEST: Try configuring mapper interface with method having both {@link ResultMap} and
	 * {@link ResultMapRef} specified.
	 * 
	 * EXPECT: Configuration exception is thrown that contains certain
	 * information strings.
	 */
	@Test
	public void testResultMap1() {
		try {
			new NanormConfiguration().configure(Mapper3.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "'ResultMap' annotation", "'ResultMapRef' annotation", "mutually exclusive", "Mapper3");
		}
	}
	
	@ResultMap(id = "refmap")
	private interface Mapper4 {
		@Select("SELECT 1")
		@ResultMapRef(declaringClass = Mapper3.class, value = "refmap")
		@Scalar
		int selectSome(int id);
	}

	/**
	 * TEST: Try configuring mapper interface with method having both {@link Scalar} and
	 * {@link ResultMapRef} specified.
	 * 
	 * EXPECT: Configuration exception is thrown that contains certain
	 * information strings.
	 */
	@Test
	public void testResultMap2() {
		try {
			new NanormConfiguration().configure(Mapper4.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "'Scalar' annotation", "'ResultMapRef' annotation", "mutually exclusive", "Mapper4");
		}
	}
	
	private interface Mapper5 {
		@Select("SELECT 1")
		@Scalar
		@ResultMap(id = "somemap")
		int selectSome(int id);
	}

	/**
	 * TEST: Try configuring mapper interface with method having both {@link ResultMap} and
	 * {@link Scalar} specified.
	 * 
	 * EXPECT: Configuration exception is thrown that contains certain
	 * information strings.
	 */
	@Test
	public void testResultMap3() {
		try {
			new NanormConfiguration().configure(Mapper5.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "'ResultMap' annotation", "'Scalar' annotation", "mutually exclusive", "Mapper5");
		}
	}
	
	public static class Bean {
		public String getSome() { return "some"; }
		public void setSome(String arg) { }
	}
	
	private interface Mapper6 {
		@Select("SELECT 1")
		@ResultMap(id = "somemap", mappings = {
			@Property("some"),
			@Property("some")
		})
		Bean selectSome(int id);
	}

	/**
	 * TEST: Try configuring mapper interface with method having {@link ResultMap} with
	 * property 'some' mapped two times.
	 * 
	 * EXPECT: Configuration exception is thrown that contains certain
	 * information strings.
	 */
	@Test
	public void testResultMap4() {
		try {
			new NanormConfiguration().configure(Mapper6.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "property", "mapped", "twice", "Mapper6", "somemap");
		}
	}
}
