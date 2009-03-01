package org.settings4j.settings;

import junit.framework.TestCase;

public class DefaultFilterTest extends TestCase {
	public void testDefaultFilterInclude(){
		DefaultFilter defaultFilter = new DefaultFilter();
		defaultFilter.addInclude("org/settings4j/config/testConfigFilter1\\.txt");

		assertTrue(defaultFilter.isValid("org/settings4j/config/testConfigFilter1.txt"));
		assertFalse(defaultFilter.isValid("xyz"));
	}
	public void testDefaultFilterExclude(){
		DefaultFilter defaultFilter = new DefaultFilter();
		defaultFilter.addExclude("org/settings4j/config/testConfigFilter1\\.txt");

		assertFalse(defaultFilter.isValid("org/settings4j/config/testConfigFilter1.txt"));
		assertTrue(defaultFilter.isValid("xyz"));
	}
}
