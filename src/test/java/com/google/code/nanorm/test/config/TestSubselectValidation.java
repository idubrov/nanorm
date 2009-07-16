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
import com.google.code.nanorm.annotations.Scalar;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;

/**
 * Test subselect validation.
 * 
 * @author Ivan Dubrov
 */
public class TestSubselectValidation {

	private interface Mapper8 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Property(value = "dummy", columnIndex = 1, subselect = "selectById") })
		int selectSome(int id);
	}

	/**
	 * TEST: Configure mapper interface that has a property with subselect which
	 * refers to unexistent query method.
	 * 
	 * EXPECT: Configuration exception that contains certain information
	 * strings.
	 */
	@Test
	public void testSubselectValidation1() {
		try {
			new NanormConfiguration().configure(Mapper8.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "not found", "Mapper8", "dummy", "testmap", "selectById");
		}
	}

	private interface Mapper9 {
		// Nothing...
	}

	private interface Mapper10 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Property(value = "dummy", columnIndex = 1, subselect = "selectById", subselectMapper = Mapper9.class) })
		int selectSome(int id);
	}

	/**
	 * TEST: Configure mapper interface that has a property with subselect which
	 * refers to unexistent query method.
	 * 
	 * EXPECT: Configuration exception that contains certain information
	 * strings.
	 */
	@Test
	public void testSubselectValidation2() {
		try {
			new NanormConfiguration().configure(Mapper9.class, Mapper10.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "not found", "Mapper9", "Mapper10", "dummy", "testmap", "selectById");
		}
	}

	private interface Mapper11 {
		@Select("SELECT 1")
		int selectSome(int id, int id2);

		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Property(value = "dummy", columnIndex = 1, subselect = "selectSome") })
		int selectOther(int id);
	}

	/**
	 * TEST: Configure mapper interface with subselect query having more than
	 * one parameter.
	 * 
	 * EXPECT: Configuration exception is thrown that contains certain
	 * information strings.
	 */
	@Test
	public void testSubselectParameters1() {
		try {
			new NanormConfiguration().configure(Mapper11.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "subselect", "exactly one", "Mapper11", "selectSome", "dummy");
		}
	}

	private interface Mapper12 {
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Property(value = "dummy", columnIndex = 1, subselectMapper = Mapper9.class) })
		int selectSome(int id);
	}

	/**
	 * TEST: Configure mapper interface that has a property with subselectMapper
	 * specified, but without subselect specified.
	 * 
	 * EXPECT: Configuration exception is thrown that contains certain
	 * information strings.
	 */
	@Test
	public void testSubselectValidation3() {
		try {
			new NanormConfiguration().configure(Mapper9.class, Mapper12.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "without specifying subselect", "Mapper9", "Mapper12", "dummy",
					"testmap", "subselectMapper");
		}
	}
	
	private interface Mapper13 {
		@Select("SELECT 1")
		@Scalar
		int selectById(int id);
		
		@Select("SELECT 1")
		@ResultMap(id = "testmap", mappings = { @Property(value = "dummy", subselect = "selectById") })
		int selectSome(int id);
	}

	/**
	 * TEST: Configure mapper interface that has a property with subselect which
	 * do not have explicit column name nor column index specified.
	 * 
	 * EXPECT: Configuration exception that contains certain information
	 * strings.
	 */
	@Test
	public void testSubselectValidation4() {
		try {
			new NanormConfiguration().configure(Mapper13.class);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "explicitly", "column", "Mapper13", "dummy", "testmap");
		}
	}

}
