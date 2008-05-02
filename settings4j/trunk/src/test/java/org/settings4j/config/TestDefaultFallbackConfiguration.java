/* ***************************************************************************
 * Copyright (c) 2007 BearingPoint INFONOVA GmbH, Austria.
 *
 * This software is the confidential and proprietary information of
 * BearingPoint INFONOVA GmbH, Austria. You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with INFONOVA.
 *
 * BEARINGPOINT INFONOVA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BEARINGPOINT INFONOVA SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
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
        
        // rootSettings are extra. So the default Settings have only the Settings of this Class
        assertEquals(1, SETTINGS.getSettingsRepository().getCurrentSettingsList().size());
        
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
            assertEquals("Conntent 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }

        try {
            SETTINGS.setContent("xyz", "xyz".getBytes());
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Conntent 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }

        try {
            SETTINGS.setObject("xyz", "xyz");
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Conntent 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }
        
        assertEquals(3, SETTINGS.getSettingsRepository().getConnectorCount());
    }
}
