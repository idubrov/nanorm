package com.google.code.nanorm.test.config;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;

/**
 * Test subselect validation.
 * @author Ivan Dubrov
 */
public class TestSubselectValidation extends TestConfigValidationBase {

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
}
