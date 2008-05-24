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

package org.settings4j.config;

import org.settings4j.Settings;

import junit.framework.TestCase;

public class TestDefaultFallbackConfiguration extends TestCase{
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(TestDefaultFallbackConfiguration.class);
    
    private static final Settings SETTINGS = Settings.getSettings(TestDefaultFallbackConfiguration.class);

    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("testDefaultProperty", "HelloWorld");
    }

    protected void tearDown() throws Exception {
        System.clearProperty("testDefaultProperty");
        super.tearDown();
    }

    /**
     * Test parsing of defaultsettings4j.xml (FALLBACK-Configuration)
     */
    public void testDefaultSettings4jConfig(){
        
        LOG.debug("#### AFTER THE FOLLOWING LINE SETTINGS4J WILL BE CONFIGURED THE FIRST TIME WITH THE FALLBACK-FILE #####");
        assertEquals("HelloWorld", SETTINGS.getString("testDefaultProperty"));
        LOG.debug("#### FINISH CONFIGURATION #####");
        
        //check if there is no Exception thrown:
        assertNull(SETTINGS.getString("xyz"));
        assertNull(SETTINGS.getContent("xyz"));
        assertNull(SETTINGS.getObject("xyz"));

        //check if there is a Exception thrown:
        try {
            SETTINGS.setString("xyz", "xyz");
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Content 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }

        try {
            SETTINGS.setContent("xyz", "xyz".getBytes());
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Content 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }

        try {
            SETTINGS.setObject("xyz", "xyz");
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Content 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }
        
        assertEquals(3, SETTINGS.getSettingsRepository().getConnectorCount());
    }
}
