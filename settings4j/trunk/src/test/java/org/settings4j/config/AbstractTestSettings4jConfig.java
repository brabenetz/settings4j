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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.settings4j.Connector;
import org.settings4j.Settings;
import org.settings4j.SettingsRepository;
import org.settings4j.UtilTesting;
import org.settings4j.exception.NoWriteableConnectorFoundException;

public abstract class AbstractTestSettings4jConfig extends TestCase{
    
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(AbstractTestSettings4jConfig.class);
    
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

    protected void testObjectReolver(SettingsRepository settingsRepository, String key1, String key2 ) {
        // the propety-file "org/settings4j/objectResolver/test1.properties" must exists
        
        Settings mycompanySeetings = settingsRepository.getSettings("com.mycompany");
        String mycompanySeetingsConnector = getFirstWritableConnectorName(mycompanySeetings);
        
        // check if there is a Exception thrown:
        // only com.mycompany.myapp and above can write
        try {
            mycompanySeetings.setObject(key1, new HashMap(), mycompanySeetingsConnector);
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

        String settings1Connector = getFirstWritableConnectorName(settings1);
        // the propety-file "org/settings4j/objectResolver/test1.properties must exists"
        settings1.setObject(key1, testData, settings1Connector);
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
        settings1.setContent(key1, content2, settings1Connector);

        // The Cached Connector should have cleared the cache
        result = (Map)settings1.getObject(key1);
        assertFalse(testData == result);
        assertEquals("blablablaNEUblablabla", result.get("irgendwasNeues"));
        liste = result.get("liste");
        assertNotNull(liste);
        assertTrue(liste instanceof List);
        assertEquals(1, ((List)liste).size());
    }
    
    protected Connector getFirstWritableConnector(Settings settings){
    	 List connectors = settings.getAllConnectors();
         for (Iterator iterator = connectors.iterator(); iterator.hasNext();) {
			Connector connector = (Connector) iterator.next();
			if (!connector.isReadonly()){
				return connector;
			}
		}
        return null;
    }
    protected String getFirstWritableConnectorName(Settings settings){
    	Connector connector = getFirstWritableConnector(settings);
    	if (connector == null){
    		return null;
    	} else {
    		return connector.getName();
    	}
    }
}
