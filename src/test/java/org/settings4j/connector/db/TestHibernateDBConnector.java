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
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.hibernate.SessionFactory;
import org.settings4j.Connector;
import org.settings4j.SettingsInstance;
import org.settings4j.SettingsRepository;
import org.settings4j.UtilTesting;
import org.settings4j.connector.db.dao.SettingsDAO;
import org.settings4j.connector.db.dao.hibernate.ConfigurationByteArray;
import org.settings4j.connector.db.dao.hibernate.SettingsDAOHibernate;

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
        SettingsInstance rootSettings = settingsRepository.getSettings();
        List connectors = rootSettings.getConnectors();
        
        
        // there are three Connectors configured:
        // "HibernateDBConnector", "SystemPropertyConnector", "ClasspathConnector"
        assertEquals(3, connectors.size());
        // the first one is the "HibernateDBConnector"
        Connector connector = (Connector)connectors.get(0);
        assertEquals("HibernateDBConnector", connector.getName());

        HibernateDBConnector hibernateDBConnector = (HibernateDBConnector) rootSettings.getConnector("HibernateDBConnector");
        SettingsDAO settingsDAO = hibernateDBConnector.getSettingsDAO();
        
        String stringValue = rootSettings.getString("test");
        assertNull(stringValue);
        
        setString(settingsDAO, "test", "Hello World");
        stringValue = rootSettings.getString("test");
        assertEquals("Hello World", stringValue);
        

        byte[] byteArrayValue = rootSettings.getContent("test");
        assertNull(byteArrayValue);
        
        setContent(settingsDAO, "test2", "Hello World".getBytes("UTF-8"));
        byteArrayValue = rootSettings.getContent("test2");
        assertNotNull(byteArrayValue);
        assertEquals("Hello World", new String(byteArrayValue, "UTF-8"));
        
    }

    private void setString(SettingsDAO settingsDAO, String key, String stringValue) {
        SettingsDTO settingsDTO = new SettingsDTO();
        settingsDTO.setKey(key);
        settingsDTO.setStringValue(stringValue);
        settingsDAO.store(settingsDTO);
    }

    private void setContent(SettingsDAO settingsDAO, String key, byte[] contentValue) {
        SettingsDTO settingsDTO = new SettingsDTO();
        settingsDTO.setKey(key);
        settingsDTO.setContentValue(contentValue);
        settingsDAO.store(settingsDTO);
    }
    
    public void testHibernateDAO(){
        SettingsDAOHibernate settingsDAO = new SettingsDAOHibernate();
        ConfigurationByteArray configuration = new ConfigurationByteArray();

        // Test Hibernate-Configuration
        configuration.configure("org/settings4j/hibernate.cfg.xml");
        // Default SettingsDTO Mappingfile
        configuration.addResource("org/settings4j/connector/db/SettingsDTO.hbm.xml");
        
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        settingsDAO.setSessionFactory(sessionFactory);
        
        // getById who doesn't exists must throw an Exception
        try {
            settingsDAO.getById(new Long(1));
            fail("No settingsObject should be found.");
        } catch (RuntimeException e) {
            assertEquals("'class org.settings4j.connector.db.SettingsDTO' object with id '1' not found...", e.getMessage());
        }
        
        // store settings Object
        SettingsDTO settingsDTO = new SettingsDTO();
        settingsDTO.setKey("test");
        settingsDTO.setStringValue("testValue");
        settingsDAO.store(settingsDTO);

        // get By Key
        settingsDTO = settingsDAO.getByKey("test");
        assertNotNull(settingsDTO);
        assertEquals("testValue", settingsDTO.getStringValue());

        // get By Id
        settingsDTO = settingsDAO.getById(settingsDTO.getId());
        assertNotNull(settingsDTO);
        assertEquals("testValue", settingsDTO.getStringValue());

        // remove By Id
        settingsDAO.remove(settingsDTO.getId());

        // get By Key must return NULL
        settingsDTO = settingsDAO.getByKey("test");
        assertNull(settingsDTO);
        
        
    }
}
