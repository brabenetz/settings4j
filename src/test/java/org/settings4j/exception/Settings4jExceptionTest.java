/* ***************************************************************************
 * Copyright (c) 2008 Brabenetz Harald, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *****************************************************************************/

package org.settings4j.exception;

import junit.framework.TestCase;

public class Settings4jExceptionTest extends TestCase {
	public void testSettings4jException() {
		final String errorKey = "error.no.writeable.connector.found";
		Settings4jException settings4jException;

		settings4jException = new Settings4jException(errorKey);
		assertNotNull(settings4jException.toString());
		assertTrue(settings4jException.toString().indexOf(errorKey) > -1);
		assertEquals("Content '{0}' cannot be writen. No writeable Connector found", settings4jException.getMessage());

		settings4jException = new Settings4jException(errorKey, new RuntimeException("test"));
		assertEquals("Content '{0}' cannot be writen. No writeable Connector found", settings4jException.getMessage());

		settings4jException = new Settings4jException(errorKey, new String[] { "arg1" });
		assertNotNull(settings4jException.toString());
		assertTrue(settings4jException.toString().indexOf(errorKey) > -1);
		assertTrue(settings4jException.toString().indexOf("Param") > -1);
		assertEquals("Content 'arg1' cannot be writen. No writeable Connector found", settings4jException.getMessage());

		settings4jException = new Settings4jException(errorKey, new String[] { "arg1" }, new RuntimeException("test"));
		assertEquals("Content 'arg1' cannot be writen. No writeable Connector found", settings4jException.getMessage());
	}
}
