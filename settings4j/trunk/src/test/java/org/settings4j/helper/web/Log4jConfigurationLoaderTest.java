/* ***************************************************************************
 * Copyright (c) 2012 Brabenetz Harald, Austria.
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

package org.settings4j.helper.web;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;
import org.settings4j.test.InMemoryLog4jAppender;
import org.settings4j.test.TestUtils;
import org.springframework.mock.web.MockServletContext;


/**
 * TEstSuite for {@link DefaultPropertiesLoader}.
 * 
 * @author brabenetz
 */
public class Log4jConfigurationLoaderTest extends TestCase {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(Log4jConfigurationLoaderTest.class);

    private static final String LOG4J_CONFIG1_PROPERTIES = "org/settings4j/helper/web/log4j-Config.properties";
    private static final String LOG4J_CONFIG_XML = "org/settings4j/helper/web/log4j-Config1.xml";
    private static final String LOG4J_CONFIG2_XML = "org/settings4j/helper/web/log4j-Config2.xml";
    private static final String LOG4J_CONFIG3_XML = //
            "file:./src/test/resources/org/settings4j/helper/web/log4j-Config3.xml";
    private static final String LOG4J_CONFIG4_CORRUPT_XML = "org/settings4j/helper/web/log4j-Config4-corrupt.xml";

    private static final String LOG4J_CONFIG_KEY = "myDummyKey";
    private static final String LOG4J_DOCUMENT_BUILDER_FACTORY = "javax.xml.parsers.DocumentBuilderFactory";

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        TestUtils.reconfigureSettings4jWithDefaultConfig();
        
        // clearProperties
        TestUtils.clearSystemProperties(new String[]{LOG4J_CONFIG_KEY, LOG4J_DOCUMENT_BUILDER_FACTORY});
        
        // Configure Log4j
        DOMConfigurator.configure(Loader.getResource("org/settings4j/helper/web/log4j-Config-Default.xml"));
    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        TestUtils.clearSystemProperties(new String[]{LOG4J_CONFIG_KEY, LOG4J_DOCUMENT_BUILDER_FACTORY});
        DOMConfigurator.configure(Loader.getResource("log4j.xml"));
        InMemoryLog4jAppender.linesClear();
    }

    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationEmptyProperties() {
        new Log4jConfigurationLoader().initLog4jConfiguration(new MockServletContext());

        // assert PreReQuirements
        InMemoryLog4jAppender.linesClear();

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG-DEFAULT]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }

    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationEmptyLog4jPath() {
        // Prepare servletContext with Default Properties as InitParameter.
        initLog4jConfigurationHappyPath(null);
        
        // assert PreReQuirements
        InMemoryLog4jAppender.linesClear();

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG-DEFAULT]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }

    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationHappypathDefault() {

        // Prepare servletContext with Default Properties as InitParameter.
        initLog4jConfigurationHappyPath(LOG4J_CONFIG_XML);

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG1]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }

    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationHappypathCustom() {

        System.setProperty(LOG4J_CONFIG_KEY, LOG4J_CONFIG2_XML); // custom Config.

        initLog4jConfigurationHappyPath(LOG4J_CONFIG_XML);

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG2]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }

    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationHappypathCustomFile() {

        System.setProperty(LOG4J_CONFIG_KEY, LOG4J_CONFIG3_XML); // custom Config.

        initLog4jConfigurationHappyPath(LOG4J_CONFIG_XML);

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG3]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }
    
    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationInvalideLog4jFactory() {

        System.setProperty(LOG4J_DOCUMENT_BUILDER_FACTORY, "NotExistintg"); //
        
        initLog4jConfigurationHappyPath(LOG4J_CONFIG_XML);

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG-DEFAULT]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }
    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationInvalideClasspathFile() {

        System.setProperty(LOG4J_CONFIG_KEY, "notExisitng.xml"); // custom Config.

        initLog4jConfigurationHappyPath(LOG4J_CONFIG_XML);

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG-DEFAULT]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }

    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationInvalideUrl() {

        System.setProperty(LOG4J_CONFIG_KEY, "file:notExisitng.xml"); // custom Config.

        initLog4jConfigurationHappyPath(LOG4J_CONFIG_XML);

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG-DEFAULT]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }

    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationCorruptXml() {

        System.setProperty(LOG4J_CONFIG_KEY, LOG4J_CONFIG4_CORRUPT_XML); // custom Config.

        initLog4jConfigurationHappyPath(LOG4J_CONFIG_XML);

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG-DEFAULT]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }

    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    public void testInitLog4jConfigurationlog4jProperty() {

        System.setProperty(LOG4J_CONFIG_KEY, LOG4J_CONFIG1_PROPERTIES); // custom Config.

        initLog4jConfigurationHappyPath(LOG4J_CONFIG_XML);

        // Log SomeThing
        LOG.info("test");

        // validate Result
        assertEquals(1, InMemoryLog4jAppender.linesSize());
        assertEquals("[CONFIG-PROPERTIES]  INFO test", InMemoryLog4jAppender.linesGet(0).toString().trim());

    }

    private void initLog4jConfigurationHappyPath(final String log4jConfigXml) {
        final MockServletContext servletContext = prepareServletContext(//
            log4jConfigXml, //
            LOG4J_CONFIG_KEY);

        // start test: initLog4jConfiguration from ServletContext initParameter
        final Log4jConfigurationLoader log4jConfigurationLoader = new Log4jConfigurationLoader();
        log4jConfigurationLoader.initLog4jConfiguration(servletContext);

        // assert PreReQuirements
        InMemoryLog4jAppender.linesClear();
    }

    private MockServletContext prepareServletContext(final String log4DefaultConfiguration,
            final String log4ConfigurationKey) {
        final StringBuffer propertiesString = new StringBuffer();
        if (StringUtils.isNotEmpty(log4DefaultConfiguration)) {
            propertiesString.append(log4ConfigurationKey);
            propertiesString.append("=");
            propertiesString.append(log4DefaultConfiguration);
        }

        final MockServletContext servletContext = new MockServletContext();
        servletContext.addInitParameter(DefaultPropertiesLoader.DEFAULT_PROPERTIES, propertiesString.toString());
        servletContext.addInitParameter(Log4jConfigurationLoader.LOG4J_CONFIG_SETTINGS4JKEY, log4ConfigurationKey);
        return servletContext;
    }
}
