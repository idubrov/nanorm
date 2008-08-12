package com.google.code.nanorm.test.config;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.Insert;
import com.google.code.nanorm.annotations.SelectKey;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.config.NanormConfiguration;
import com.google.code.nanorm.exceptions.ConfigurationException;

/**
 * Test validation for generated keys.
 * 
 * @author Ivan Dubrov
 */
public class TestGeneratedKeyValidation extends TestConfigValidationBase {
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
