package com.google.code.nanorm.test.config;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;

/**
 * Test validation of nested maps.
 * 
 * @author Ivan Dubrov
 */
public class TestNestedMapValidation extends TestConfigValidationBase {

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
	 * Test both nested and subselect are specified.
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
}