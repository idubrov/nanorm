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

import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;

/**
 * Test validation of different aspects of configuration.
 * 
 * @author Ivan Dubrov
 */
public class TestOtherValidation {

	private interface Mapper1 {
		@Select("SELECT 1")
		int selectSome(int id);
		
		int selectOther(int id);
	}

	/**
	 * TEST: Configure mapper interface that has a method without configuration, create mapper
	 * and invoke the method.
	 * 
	 * EXPECT: Configuration exception that contains certain information
	 * strings.
	 */
	@Test
	public void testMissingConfig() {
		try {
			NanormConfiguration config = new NanormConfiguration();
			config.configure(Mapper1.class);
			Mapper1 mapper = config.buildFactory().createMapper(Mapper1.class);
			
			mapper.selectOther(111);
			Assert.fail();
		} catch (ConfigurationException e) {
			assertContains(e, "method", "does not have", "configuration", "Mapper1", "selectOther");
		}
	}

}
