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
