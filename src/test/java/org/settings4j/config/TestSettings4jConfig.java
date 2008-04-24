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

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.settings4j.Settings;
import org.settings4j.SettingsRepository;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.settings.DefaultSettings;
import org.settings4j.settings.HierarchicalSettingsRepository;
import org.settings4j.settings.SettingsManager;

public class TestSettings4jConfig extends TestCase{
    
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(TestSettings4jConfig.class);
    
    protected void setUp() throws Exception {
        File tmpFolder = getTmpFolder();
        LOG.info("Use temporary Folder: " + tmpFolder.getAbsolutePath());
        FileUtils.deleteDirectory(tmpFolder);
        
        File testFolder = getTestFolder();
        LOG.info("Use test Folder: " + testFolder.getAbsolutePath());
        FileUtils.deleteDirectory(testFolder);
        super.setUp();
    }

    protected void tearDown() throws Exception {
        File tmpFolder = getTmpFolder();
        FileUtils.deleteDirectory(tmpFolder);
        File testFolder = getTestFolder();
        FileUtils.deleteDirectory(testFolder);
        super.tearDown();
    }

    /**
     * Test parsing of defaultsettings4j.xml (FALLBACK-Configuration)
     */
    public void testDefaultSettings4jConfig(){
        SettingsRepository settingsRepository = getConfiguredSettingsRepository(SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);
        
        Settings rootSeetings = settingsRepository.getRootSettings();
        
        // rootSettings are extra. So the default Settings have no settings
        assertEquals(0, settingsRepository.getCurrentSettingsList().size());

        // create a Custom Settings-Object for unittest
        // normal usecase:
        // Settings SETTINGS = Settings.getSettings(MyClass.class);
        Settings mycompanySeetings = settingsRepository.getSettings("com.mycompany");

        // now we have a Settings inside the settingsRepository
        assertEquals(1, settingsRepository.getCurrentSettingsList().size());
        
        
        //the rootSettings have three Connectors
        assertEquals(3, rootSeetings.getConnectors().size());

        //check if there is no Exception thrown:
        assertNull(rootSeetings.getString("xyz"));
        assertNull(rootSeetings.getContent("xyz"));
        assertNull(rootSeetings.getObject("xyz"));
        assertNull(mycompanySeetings.getString("xyz"));
        assertNull(mycompanySeetings.getContent("xyz"));
        assertNull(mycompanySeetings.getObject("xyz"));

        //check if there is a Exception thrown:
        try {
            rootSeetings.setString("xyz", "xyz");
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Conntent 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }

        try {
            rootSeetings.setContent("xyz", "xyz".getBytes());
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Conntent 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }

        try {
            rootSeetings.setObject("xyz", "xyz");
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Conntent 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }
    }
    
    public void testCorruptConfig(){
        getConfiguredSettingsRepository("org/settings4j/config/testConfigCorrupt.xml");
    }
    

    public void testFSConfigTempFolder(){
        SettingsRepository settingsRepository = getConfiguredSettingsRepository("org/settings4j/config/testConfigFSTempfolder.xml");

        Settings mycompanySeetings = settingsRepository.getSettings("com.mycompany");

        // check if there is a Exception thrown:
        // only com.mycompany.myapp and above can write
        try {
            mycompanySeetings.setString("xyz", "abc");
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Conntent 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }
        
        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        Settings settings1 = settingsRepository.getSettings("com.mycompany.myapp");
        File tmpFolder = getTmpFolder();
        File fileXyz = new File(tmpFolder, "xyz");
        assertFalse(fileXyz.exists());
        settings1.setString("xyz", "abc");
        assertTrue(fileXyz.exists());

        Settings settings2 = settingsRepository.getSettings("com.mycompany.myapp.subcomponent");
        settings2.setString("xyz2", "abc2");
        
        //every settings have read access to the same FSConnector.
        assertEquals("abc", settings2.getString("xyz"));
        assertEquals("abc2", settings1.getString("xyz2"));
        assertEquals("abc2", mycompanySeetings.getString("xyz2"));
        
    }
    
    public void testFSConfigTestFolder(){
        SettingsRepository settingsRepository = getConfiguredSettingsRepository("org/settings4j/config/testConfigFSTestfolder.xml");

        Settings mycompanySeetings = settingsRepository.getSettings("com.mycompany");

        // check if there is a Exception thrown:
        // only com.mycompany.myapp and above can write
        try {
            mycompanySeetings.setString("xyz", "abc");
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Conntent 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }
        
        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        Settings settings1 = settingsRepository.getSettings("com.mycompany.myapp");
        File testFolder = getTestFolder();
        File fileXyz = new File(testFolder, "xyz");
        assertFalse(fileXyz.exists());
        settings1.setString("xyz", "abc");
        assertTrue(fileXyz.exists());

        Settings settings2 = settingsRepository.getSettings("com.mycompany.myapp.subcomponent");
        settings2.setString("xyz2", "abc2");
        
        //every settings have read access to the same FSConnector.
        assertEquals("abc", settings2.getString("xyz"));
        assertEquals("abc2", settings1.getString("xyz2"));
        assertEquals("abc2", mycompanySeetings.getString("xyz2"));
        
    }
    
    public void testPropertyFileConfig(){
        SettingsRepository settingsRepository = getConfiguredSettingsRepository("org/settings4j/config/testConfigPropertyFile.xml");

        Settings seetings = settingsRepository.getSettings("com.mycompany");

        //every settings have read access to the same FSConnector.
        assertEquals("Value from Property-File", seetings.getString("xyz"));
        
    }
    
    private SettingsRepository getConfiguredSettingsRepository(String classpathUrl){

        URL url = ClasspathContentResolver.getResource(classpathUrl);
        SettingsRepository settingsRepository = new HierarchicalSettingsRepository(new DefaultSettings("root"));
        DOMConfigurator.configure(url, settingsRepository);
        return settingsRepository;
    }
    private static File getTmpFolder(){
        String tmpdir = System.getProperty("java.io.tmpdir");
        File tmpFolder = new File(tmpdir + "Settings4jUnittest");
        return tmpFolder;
    }
    
    private static File getTestFolder(){
        File testFolder = new File("test/Settings4jUnittest");
        return testFolder;
    }
}
