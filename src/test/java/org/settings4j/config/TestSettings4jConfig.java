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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.settings4j.Settings;
import org.settings4j.SettingsRepository;
import org.settings4j.UtilTesting;
import org.settings4j.exception.NoWriteableConnectorFoundException;
import org.settings4j.settings.HierarchicalSettings;
import org.settings4j.settings.SettingsManager;

public class TestSettings4jConfig extends TestCase{
    
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(TestSettings4jConfig.class);
    
    protected void setUp() throws Exception {
        File tmpFolder = UtilTesting.getTmpFolder();
        LOG.info("Use temporary Folder: " + tmpFolder.getAbsolutePath());
        FileUtils.deleteDirectory(tmpFolder);
        
        File testFolder = UtilTesting.getTestFolder();
        LOG.info("Use test Folder: " + testFolder.getAbsolutePath());
        FileUtils.deleteDirectory(testFolder);
        super.setUp();
    }

    protected void tearDown() throws Exception {
        File tmpFolder = UtilTesting.getTmpFolder();
        FileUtils.deleteDirectory(tmpFolder);
        File testFolder = UtilTesting.getTestFolder();
        FileUtils.deleteDirectory(testFolder);
        super.tearDown();
    }

    /**
     * Test parsing of defaultsettings4j.xml (FALLBACK-Configuration)
     */
    public void testDefaultSettings4jConfig(){
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository(SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);
        
        HierarchicalSettings rootSeetings = (HierarchicalSettings)settingsRepository.getRootSettings();
        
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
            fail("must throw an NoWriteableConnectorFoundException");
        } catch (NoWriteableConnectorFoundException e) {
            assertEquals("Content 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }

        try {
            rootSeetings.setContent("xyz", "xyz".getBytes());
            fail("must throw an NoWriteableConnectorFoundException");
        } catch (NoWriteableConnectorFoundException e) {
            assertEquals("Content 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }

        try {
            rootSeetings.setObject("xyz", "xyz");
            fail("must throw an NoWriteableConnectorFoundException");
        } catch (NoWriteableConnectorFoundException e) {
            assertEquals("Content 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }
        
        assertEquals(3, settingsRepository.getConnectorCount());
    }
    
    public void testCorruptConfig(){
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigCorrupt.xml");

        assertEquals(0, settingsRepository.getConnectorCount());
    }
    

    public void testFSConfigTempFolder(){
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigFSTempfolder.xml");

        Settings mycompanySeetings = settingsRepository.getSettings("com.mycompany");

        // check if there is a Exception thrown:
        // only com.mycompany.myapp and above can write
        try {
            mycompanySeetings.setString("xyz", "abc");
            fail("must throw an NoWriteableConnectorFoundException");
        } catch (NoWriteableConnectorFoundException e) {
            assertEquals(NoWriteableConnectorFoundException.NO_WRITEABLE_CONNECTOR_FOUND_1, e.getKey());
            assertEquals("Content 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }
        
        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        Settings settings1 = settingsRepository.getSettings("com.mycompany.myapp");
        File tmpFolder = UtilTesting.getTmpFolder();
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

        assertEquals(2, settingsRepository.getConnectorCount());
    }
    
    public void testFSConfigTestFolder(){
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigFSTestfolder.xml");

        Settings mycompanySeetings = settingsRepository.getSettings("com.mycompany");

        // check if there is a Exception thrown:
        // only com.mycompany.myapp and above can write
        try {
            mycompanySeetings.setString("xyz", "abc");
            fail("must throw an NoWriteableConnectorFoundException");
        } catch (NoWriteableConnectorFoundException e) {
            assertEquals(NoWriteableConnectorFoundException.NO_WRITEABLE_CONNECTOR_FOUND_1, e.getKey());
            assertEquals("Content 'xyz' cannot be writen. No writeable Connector found", e.getMessage());
        }
        
        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        Settings settings1 = settingsRepository.getSettings("com.mycompany.myapp");
        File testFolder = UtilTesting.getTestFolder();
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

        assertEquals(1, settingsRepository.getConnectorCount());
    }
    
    /**
     * Test ObjectReolver with cached Connector
     */
    public void testObjectResolverConfig1(){
        
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigObjectResolver1.xml");

        String key1 = "org/settings4j/objectResolver/test1";
        String key2 = "org/settings4j/objectResolver/test2";
        testObjectReolver(settingsRepository, key1, key2);
    }
    
    /**
     * Test ObjectReolver with cached ObjectReolver
     */
    public void testObjectResolverConfig2(){
        
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigObjectResolver2.xml");

        String key1 = "org/settings4j/objectResolver/test1";
        String key2 = "org/settings4j/objectResolver/test2";
        testObjectReolver(settingsRepository, key1, key2);
    }
    
    /**
     * Test ObjectReolver with cached Object-Property-config
     */
    public void testObjectResolverConfig3(){
        
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigObjectResolver3.xml");

        String key1 = "org/settings4j/objectResolver/test2"; // property with cached==true
        String key2 = "org/settings4j/objectResolver/test3";
        testObjectReolver(settingsRepository, key1, key2);
    }
    
    /**
     * Test ObjectReolver with cached Object-Property-config
     */
    public void testObjectResolverConfig4Spring() throws Exception{
        
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigObjectResolver4Spring.xml");

        String key1 = "org/settings4j/objectResolver/testSpring1"; // spring-configuration
        String key2 = "org/settings4j/objectResolver/test1"; // XML-Bean-Configuration

        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        Settings settings1 = settingsRepository.getSettings("com.mycompany.myapp");
        
        // the propety-file "org/settings4j/objectResolver/test1.properties must exists"
        DataSource dataSource = (DataSource)settings1.getObject(key1);
        assertNotNull(dataSource);
        
        settings1.setObject(key2, dataSource);

        DataSource dataSource2 = (DataSource)settings1.getObject(key2);
        assertNotNull(dataSource2);
        assertTrue(dataSource != dataSource2);

        
        // test DataSource
        Connection conn = null;
        try{
            conn = dataSource2.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("create Table test ( name VARCHAR )");
            pstmt.execute();
            pstmt.close();

            pstmt = conn.prepareStatement("insert into test(name) values (?)");
            pstmt.setString(1, "Hello World");
            pstmt.execute();
            pstmt.close();
            
            pstmt = conn.prepareStatement("select * from test");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            String result = rs.getString(1);
            rs.close();
            pstmt.close();

            pstmt = conn.prepareStatement("drop Table test");
            pstmt.execute();
            pstmt.close();
            
            assertEquals("Hello World", result);
            
        } finally {
            if (conn != null){
                conn.close();
            }
        }
        
        
        
    }

    private void testObjectReolver(SettingsRepository settingsRepository, String key1, String key2 ) {
        // the propety-file "org/settings4j/objectResolver/test1.properties" must exists
        
        Settings mycompanySeetings = settingsRepository.getSettings("com.mycompany");

        // check if there is a Exception thrown:
        // only com.mycompany.myapp and above can write
        try {
            mycompanySeetings.setObject(key1, new HashMap());
            fail("must throw an NoWriteableConnectorFoundException");
        } catch (NoWriteableConnectorFoundException e) {
            assertEquals(NoWriteableConnectorFoundException.NO_WRITEABLE_CONNECTOR_FOUND_1, e.getKey());
            assertEquals(key1, e.getArgs()[0].toString());
            assertEquals("Content '" + key1 + "' cannot be writen. No writeable Connector found", e.getMessage());
        }
        
        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        Settings settings1 = settingsRepository.getSettings("com.mycompany.myapp");
        
        File testFolder = UtilTesting.getTestFolder();
        File fileKey1 = new File(testFolder, key1);
        assertFalse(fileKey1.exists());

        Map testData =  new HashMap();
        List testList = new ArrayList();
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        String testValue3 = "testValue3";
        String testValue4 = "testValue4";
        testList.add(testValue1);
        testList.add(testValue2);
        testList.add(testValue3);
        testList.add(testValue4);
        testData.put("irgendwas", "blablablablablabla");
        testData.put("liste", testList);
        
        // the propety-file "org/settings4j/objectResolver/test1.properties must exists"
        settings1.setObject(key1, testData);
        assertTrue(fileKey1.exists());
        
        // The FSConnector is cached! The Objects must be the same.
        Map result = (Map)settings1.getObject(key1);
        assertTrue(testData == result);
        assertEquals("blablablablablabla", result.get("irgendwas"));
        Object liste = result.get("liste");
        assertNotNull(liste);
        assertTrue(liste instanceof List);
        assertEquals(4, ((List)liste).size());
        
        // Copy the content from key2 to key1
        byte[] content2 = settings1.getContent(key2);
        settings1.setContent(key1, content2);

        // The Cached Connector should have cleared the cache
        result = (Map)settings1.getObject(key1);
        assertFalse(testData == result);
        assertEquals("blablablaNEUblablabla", result.get("irgendwasNeues"));
        liste = result.get("liste");
        assertNotNull(liste);
        assertTrue(liste instanceof List);
        assertEquals(1, ((List)liste).size());
    }
    
    public void testPropertyFileConfig(){
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigPropertyFile.xml");

        Settings seetings = settingsRepository.getSettings("com.mycompany");

        //every settings have read access to the same FSConnector.
        assertEquals("Value from Property-File", seetings.getString("xyz"));

        assertEquals(2, settingsRepository.getConnectorCount());
    }
}
