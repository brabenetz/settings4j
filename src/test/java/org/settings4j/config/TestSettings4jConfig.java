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
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.settings4j.Connector;
import org.settings4j.Settings4jInstance;
import org.settings4j.Settings4jRepository;
import org.settings4j.UtilTesting;
import org.settings4j.settings.SettingsManager;

public class TestSettings4jConfig extends AbstractTestSettings4jConfig{

    /**
     * Test parsing of defaultsettings4j.xml (FALLBACK-Configuration)
     */
    public void testDefaultSettings4jConfig(){
        Settings4jRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository(SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);
        
        Settings4jInstance settings = settingsRepository.getSettings();
        
        //the rootSettings have three Connectors
        assertEquals(3, settings.getConnectors().size());

        //check if there is no Exception thrown:
        assertNull(settings.getString("xyz"));
        assertNull(settings.getContent("xyz"));
        assertNull(settings.getObject("xyz"));
        
        assertEquals(3, settings.getConnectors().size());
    }
    
    public void testCorruptConfig(){
        Settings4jRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigCorrupt.xml");

        assertEquals(0, settingsRepository.getSettings().getConnectors().size());
    }
    

    public void testFSConfigTempFolder() throws Exception {
        Settings4jRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigFSTempfolder.xml");

        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        Settings4jInstance settings = settingsRepository.getSettings();
        File tmpFolder = UtilTesting.getTmpFolder();
        File fileXyz = new File(tmpFolder, "xyz");
        assertFalse(fileXyz.exists());
        FileUtils.writeStringToFile(fileXyz, "abc", "UTF-8");
        assertTrue(fileXyz.exists());

        FileUtils.writeStringToFile(new File(tmpFolder, "xyz2"), "abc2", "UTF-8");
        
        //every settings have read access to the same FSConnector.
        assertEquals("abc", settings.getString("xyz"));
        assertEquals("abc2", settings.getString("xyz2"));

        assertEquals(2, settings.getConnectors().size());
        
        Connector connector;
        connector = settings.getConnector("FSConnector");
        assertNotNull(connector);
        connector = settings.getConnector("SystemPropertyConnector");
        assertNotNull(connector);
        connector = settings.getConnector("nichtVorhanden");
        assertNull(connector);
        
    }
    
    public void testFSConfigTestFolder() throws Exception {
        Settings4jRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigFSTestfolder.xml");
        
        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        Settings4jInstance settings = settingsRepository.getSettings();
        File testFolder = UtilTesting.getTestFolder();
        File fileXyz = new File(testFolder, "xyz");
        assertFalse(fileXyz.exists());
        FileUtils.writeStringToFile(fileXyz, "abc", "UTF-8");
        assertTrue(fileXyz.exists());
        
        FileUtils.writeStringToFile(new File(testFolder, "xyz2"), "abc2", "UTF-8");
        
        //every settings have read access to the same FSConnector.
        assertEquals("abc", settings.getString("xyz"));
        assertEquals("abc2", settings.getString("xyz2"));

        assertEquals(1, settings.getConnectors().size());
    }
    
    /**
     * Test ObjectReolver with cached Connector
     */
    public void testObjectResolverConfig(){

        
        testCaching("org/settings4j/config/testConfigObjectResolver-nocaching.xml", //
            "org/settings4j/objectresolver/test1", //
            false); // no caching!
        
    }
    
    /**
     * Test ObjectReolver with cached Connector
     */
    public void testObjectResolverConfig1(){

        
        testCaching("org/settings4j/config/testConfigObjectResolver1.xml", //
            "org/settings4j/objectresolver/test1", //
            true); // The FSConnector is cached!
        
    }
    
    /**
     * Test ObjectReolver with cached ObjectReolver
     */
    public void testObjectResolverConfig2(){
        
        testCaching("org/settings4j/config/testConfigObjectResolver2.xml", //
            "org/settings4j/objectresolver/test2", //
            true); // The JavaXMLBeansObjectResolver is cached!
    }
    
    /**
     * Test ObjectReolver with cached Object-Property-config
     */
    public void testObjectResolverConfig3(){
        
        testCaching("org/settings4j/config/testConfigObjectResolver-nocaching.xml", //
            "org/settings4j/objectresolver/test2", //
            true); // The test2.properties is cached set to true!
    }


    private void testCaching(String settingsRepositoryUrl, String objectKey, boolean mustBeTheSame) {
        Settings4jRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository(settingsRepositoryUrl);

        Settings4jInstance settings = settingsRepository.getSettings();
        
        Map result1 = (Map)settings.getObject(objectKey);
        assertNotNull(result1);
        Map result2 = (Map)settings.getObject(objectKey);
        if (mustBeTheSame) {
            assertTrue(result1 == result2);
        } else {
            assertTrue(result1 != result2);
        }
    }
    /**
     * Test ObjectReolver with cached Object-Property-config
     * 
     * @throws Exception if an error occurs.
     */
    public void testObjectResolverConfig4Spring() throws Exception {
        
        Settings4jRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigObjectResolver4Spring.xml");

        String key1 = "org/settings4j/objectresolver/testSpring1"; // spring-configuration

        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        Settings4jInstance settings1 = settingsRepository.getSettings();
        
        // the propety-file "org/settings4j/objectresolver/test1.properties must exists"
        DataSource dataSource = (DataSource)settings1.getObject(key1);
        assertNotNull(dataSource);
        
        // test DataSource
        Connection conn = null;
        try{
            conn = dataSource.getConnection();
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
    
    public void testPropertyFileConfig(){
        Settings4jRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigPropertyFile.xml");

        Settings4jInstance settings = settingsRepository.getSettings();

        //every settings have read access to the same FSConnector.
        assertEquals("Value from Property-File", settings.getString("xyz"));

        assertEquals(2, settings.getConnectors().size());
    }
}
