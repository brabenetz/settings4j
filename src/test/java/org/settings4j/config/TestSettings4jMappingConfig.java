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
import org.settings4j.SettingsRepository;
import org.settings4j.UtilTesting;

public class TestSettings4jMappingConfig extends AbstractTestSettings4jConfig{
    
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(TestSettings4jMappingConfig.class);
    
    public void testMapping(){
        LOG.debug("run TestSettings4jMappingConfig.testMapping");
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/config/testConfigMapping.xml");

        Settings mycompanySeetings = settingsRepository.getSettings("com.mycompany");
        
        String webservice = "http://settings4j.org/XY";
        System.setProperty("global/webserviceXY", webservice);
        
        String moduleXWS = mycompanySeetings.getString("com/mycompany/moduleX/webserviceXY");
        String moduleYWS = mycompanySeetings.getString("com/mycompany/moduleY/webserviceXY");

        assertEquals(webservice, moduleXWS);
        assertEquals(webservice, moduleYWS);
        
    }
}
