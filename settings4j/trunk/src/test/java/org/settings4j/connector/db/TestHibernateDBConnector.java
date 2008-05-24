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

package org.settings4j.connector.db;

import java.io.File;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.settings4j.Settings;
import org.settings4j.SettingsRepository;
import org.settings4j.UtilTesting;

public class TestHibernateDBConnector extends TestCase {

    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(TestHibernateDBConnector.class);
    
    protected void setUp() throws Exception {
        File testFolder = UtilTesting.getTestFolder();
        LOG.info("Use test Folder: " + testFolder.getAbsolutePath());
        FileUtils.deleteDirectory(testFolder);
        super.setUp();
    }

    protected void tearDown() throws Exception {
        File testFolder = UtilTesting.getTestFolder();
        FileUtils.deleteDirectory(testFolder);
        super.tearDown();
    }
    
    public void testHibernateDBConnector1() throws UnsupportedEncodingException{
        SettingsRepository settingsRepository = UtilTesting.getConfiguredSettingsRepository("org/settings4j/connector/db/testHibernateDBConnector1.xml");
        Settings rootSettings = settingsRepository.getRootSettings();
        
        String stringValue = rootSettings.getString("test");
        assertNull(stringValue);
        
        rootSettings.setString("test", "Hello World");
        stringValue = rootSettings.getString("test");
        assertEquals("Hello World", stringValue);
        

        byte[] byteArrayValue = rootSettings.getContent("test");
        assertNull(byteArrayValue);
        
        rootSettings.setContent("test", "Hello World".getBytes("UTF-8"));
        byteArrayValue = rootSettings.getContent("test");
        assertNotNull(byteArrayValue);
        assertEquals("Hello World", new String(byteArrayValue, "UTF-8"));
        
    }
}
