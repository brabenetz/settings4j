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

import org.settings4j.Settings;
import org.settings4j.SettingsRepository;
import org.settings4j.UtilTesting;

public class TestSettings4jAdditivity extends AbstractTestSettings4jConfig{
    
    public void testAdditivityConfig(){
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigAdditivity.xml");
        
        /*
         * First Test: Root-Config:
         * Connector: SystemProperties
         * Mapping: 
         *    <entry key="com/mycompany/moduleX/webserviceXY" ref-key="global/webserviceXY"/>
         *    <entry key="com/mycompany/moduleY/webserviceXY" ref-key="global/webserviceXY"/>
         */
        // 1. Create Property and get Settings
        System.setProperty("global/webserviceXY", "https://xyz");
        Settings mycompanySeetings = settingsRepository.getSettings("com.mycompany");
        
        assertEquals("https://xyz", mycompanySeetings.getString("com/mycompany/moduleX/webserviceXY"));
        assertEquals("https://xyz", mycompanySeetings.getString("com/mycompany/moduleY/webserviceXY"));
        assertEquals("https://xyz", mycompanySeetings.getString("global/webserviceXY"));

        // The mapping "x" to "global/webserviceXY" doesn't exists for the Root-Settings
        assertNull(mycompanySeetings.getString("X"));

        /*
         * Second Test: SubSettings-Config with addictivity:
         * Connector: FSConnector
         * Mapping: 
         *    <entry key="X" ref-key="global/webserviceXY"/>
         *    <entry key="Y" ref-key="global/webserviceXY"/>
         */
        // 1. get Settings
        Settings settings1 = settingsRepository.getSettings("com.mycompany.module2.webserviceManager");

        assertNull(settings1.getString("com/mycompany/moduleX/webserviceXY"));
        assertNull(settings1.getString("com/mycompany/moduleY/webserviceXY"));
        assertNull(settings1.getString("global/webserviceXY"));
        assertNull(settings1.getString("X"));
        assertNull(settings1.getString("Y"));

        String settings1Connector = getFirstWritableConnectorName(settings1);
        
        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        File testFolder = UtilTesting.getTestFolder();
        File fileXy = new File(testFolder, "global/webserviceXY");
        assertFalse(fileXy.exists());
        settings1.setString("X", "https://uvw", settings1Connector); // "X" is mapped to "global/webserviceXY"
        assertTrue(fileXy.exists());
        
        assertEquals("https://uvw", settings1.getString("X"));
        assertEquals("https://uvw", settings1.getString("Y"));
        assertEquals("https://uvw", settings1.getString("global/webserviceXY"));
        assertNull(settings1.getString("com/mycompany/moduleX/webserviceXY"));
        assertNull(settings1.getString("com/mycompany/moduleY/webserviceXY"));

    }
}
