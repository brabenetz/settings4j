/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2015 Brabenetz Harald, Austria
 * ===============================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.settings4j.helper.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;
import org.settings4j.Connector;
import org.settings4j.Settings4j;
import org.springframework.mock.web.MockServletContext;


/**
 * TEstSuite for {@link DefaultPropertiesLoader}.
 *
 * @author brabenetz
 */
public class DefaultPropertiesLoaderTest {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultPropertiesLoaderTest.class);

    private static final int DEFAULT_CONNECTOR_COUNT = 5;

    @Before
    public void setUp() throws Exception {
        final Connector webXmlConnector = Settings4j.getSettingsRepository().getSettings().getConnector(
            DefaultPropertiesLoader.CONNECTOR_NAME);

        // remove unitTest Connector if exists.
        if (webXmlConnector != null) {
            LOG.info("connector found: " + webXmlConnector.getName());
            // settings4j will be configured with the default-fallback-config if no connector exists:
            // org/settings4j/config/defaultsettings4j.xml
            Settings4j.getSettingsRepository().getSettings().removeAllConnectors();
        }
    }

    /**
     * The normal usage of the DefaultPropertiesLoader: Load default Properties from ServletContext and add Connector to
     * Settings4j.
     */
    @Test
    public void testInitDefaultPropertiesHappypath() {

        // Prepare servletContext with Default Properties as InitParameter.
        final MockServletContext servletContext = prepareServletContext();

        // SystemConnector exist and can overwrite the custom default values connector from web.xml.
        System.setProperty("test5", "newValue5");

        // validate pre-requirements
        assertThat(Settings4j.getConnectors(), hasSize(DEFAULT_CONNECTOR_COUNT));
        assertThat(Settings4j.getString("test1"), is(nullValue()));
        assertThat(Settings4j.getString("test2"), is(nullValue()));
        assertThat(Settings4j.getString("test3"), is(nullValue()));
        assertThat(Settings4j.getString("a/b/test4"), is(nullValue()));
        assertThat(Settings4j.getString("test5"), is("newValue5"));

        // start test: initDefaultProperties from ServletContext initParameter
        final DefaultPropertiesLoader defaultPropertiesLoader = new DefaultPropertiesLoader();
        defaultPropertiesLoader.initDefaultProperties(servletContext);


        // validate Result
        assertThat(Settings4j.getConnectors(), hasSize(DEFAULT_CONNECTOR_COUNT + 1));
        assertThat(Settings4j.getString("test1"), is("value1"));
        assertThat(Settings4j.getString("test2"), is("value2"));
        assertThat(Settings4j.getString("test3"), is("value3"));
        assertThat(Settings4j.getString("a/b/test4"), is("value4"));
        assertThat(Settings4j.getString("test5"), is("newValue5")); // from SystemPropertyConnector
    }

    /**
     * Check if duplicate Call only adds one Connector.
     */
    @Test
    public void testMulitbleInitDefaultProperties() {

        // Prepare servletContext with Default Properties as InitParameter.
        final MockServletContext servletContext = prepareServletContext();

        assertThat(Settings4j.getConnectors(), hasSize(DEFAULT_CONNECTOR_COUNT));

        // start test one time: initDefaultProperties from ServletContext initParameter
        new DefaultPropertiesLoader().initDefaultProperties(servletContext);

        // start test two time: initDefaultProperties from ServletContext initParameter
        new DefaultPropertiesLoader().initDefaultProperties(servletContext);

        // validate Result (connector should only be one time added)
        assertThat(Settings4j.getConnectors(), hasSize(DEFAULT_CONNECTOR_COUNT + 1));


    }

    /**
     * Check if the Call without InitParameter doesn't add a Connector.
     */
    @Test
    public void testInitDefaultPropertiesWithoutInitParameter() {

        // Prepare servletContext without InitParameter.
        final MockServletContext servletContext = new MockServletContext();

        assertThat(Settings4j.getConnectors(), hasSize(DEFAULT_CONNECTOR_COUNT));

        // start test one time: initDefaultProperties from ServletContext initParameter
        new DefaultPropertiesLoader().initDefaultProperties(servletContext);

        // validate Result (no connector should be added)
        assertThat(Settings4j.getConnectors(), hasSize(DEFAULT_CONNECTOR_COUNT));


    }

    private MockServletContext prepareServletContext() {
        final StringBuffer propertiesString = new StringBuffer();
        propertiesString.append("test1=value1\n");
        propertiesString.append("    test2=value2\n"); // with whiteSpaces
        propertiesString.append("\ttest3=value3\n"); // with Tabs
        propertiesString.append("\t  a/b/test4=value4\n"); // with taps, Whitespace and prefixPath
        propertiesString.append("test5=value5\n");

        final MockServletContext servletContext = new MockServletContext();
        servletContext.addInitParameter(DefaultPropertiesLoader.DEFAULT_PROPERTIES, propertiesString.toString());
        return servletContext;
    }
}
