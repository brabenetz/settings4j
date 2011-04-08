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

import org.settings4j.Settings4j;

import junit.framework.TestCase;

public class TestDefaultFallbackConfiguration extends TestCase{
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(TestDefaultFallbackConfiguration.class);
    
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("testDefaultProperty", "HelloWorld");
    }

    protected void tearDown() throws Exception {
        System.getProperties().remove("testDefaultProperty");
        super.tearDown();
    }

    /**
     * Test parsing of defaultsettings4j.xml (FALLBACK-Configuration)
     */
    public void testDefaultSettings4jConfig(){
        
        LOG.debug("#### AFTER THE FOLLOWING LINE SETTINGS4J WILL BE CONFIGURED THE FIRST TIME WITH THE FALLBACK-FILE #####");
        assertEquals("HelloWorld", Settings4j.getString("testDefaultProperty"));
        LOG.debug("#### FINISH CONFIGURATION #####");
        
        //check if there is no Exception thrown:
        assertNull(Settings4j.getString("xyz"));
        assertNull(Settings4j.getContent("xyz"));
        assertNull(Settings4j.getObject("xyz"));

        // the Default settings Configuration is readonly
        
        assertEquals(3, Settings4j.getConnectors().size());
    }
}
