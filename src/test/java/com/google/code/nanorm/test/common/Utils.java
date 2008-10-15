package com.google.code.nanorm.test.common;

import junit.framework.Assert;

/**
 * Utility methods for tests.
 * @author Ivan Dubrov
 */
public class Utils {

	/**
	 * Check that exception message contains given keywords.
	 * @param e exception
	 * @param keywords keywords
	 */
	public static void assertContains(Exception e, String... keywords) {
		for(String keyword : keywords) {
			Assert.assertTrue(e.getMessage().toLowerCase().contains(keyword.toLowerCase()));
		}
	}
}
