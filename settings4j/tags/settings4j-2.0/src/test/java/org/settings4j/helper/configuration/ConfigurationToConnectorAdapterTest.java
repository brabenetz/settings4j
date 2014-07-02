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

package org.settings4j.helper.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;
import org.junit.Test;
import org.settings4j.ConnectorPositions;
import org.settings4j.Settings4jRepository;
import org.settings4j.connector.SystemPropertyConnector;
import org.settings4j.test.TestUtils;


public class ConfigurationToConnectorAdapterTest {

    private static final String TEST_VALUE_KEY = "com/myProject/myModule/myTestValue.variantA";

    @Test
    public void testAdapterINIConfigWithSlashDelimiter() throws Exception {
        final Settings4jRepository testSettings = createSimpleSettings4jConfig();

        // start test => create adapter and add to Settings4jRepository
        final HierarchicalINIConfiguration configuration = addINIConfiguration(//
            testSettings, "myIniConfigConnector", "iniConfigWithSlashDelimiter.ini", "/");

        // configure some values
        configuration.setProperty(TEST_VALUE_KEY, "Hello Windows World");
        configuration.save();

        // validate result
        assertThat(testSettings.getSettings().getString(TEST_VALUE_KEY), is("Hello Windows World"));


        final Set<String> sections = configuration.getSections();
        assertThat(sections, hasSize(1));
        // The HierarchicalINIConfiguration implementation uses only the first child elements as sections.
        assertThat(sections.iterator().next(), is("com"));

    }

    @Test
    public void testAdapterINIConfigWithDefaultDelimiterAndDotValue() throws Exception {
        final Settings4jRepository testSettings = createSimpleSettings4jConfig();

        // start test => create adapter and add to Settings4jRepository
        final HierarchicalINIConfiguration configuration = addINIConfiguration(//
            testSettings, "myIniConfigConnector", "iniConfigWithDefaultDelimiter.ini", ".");

        // configure some values
        configuration.setProperty(TEST_VALUE_KEY, "Hello Windows World");
        configuration.save();

        // validate result
        assertThat(testSettings.getSettings().getString(TEST_VALUE_KEY), is("Hello Windows World"));


        final Set<String> sections = configuration.getSections();
        assertThat(sections, hasSize(1));
        assertThat(sections.iterator().next(), is("com/myProject/myModule/myTestValue"));

    }

    @Test
    public void testAdapterINIConfigWithDefaultDelimiterWithoutDotValue() throws Exception {
        String testValueKey = "com/myProject/myModule/myTestValue";
        final Settings4jRepository testSettings = createSimpleSettings4jConfig();

        // start test => create adapter and add to Settings4jRepository
        final HierarchicalINIConfiguration configuration = addINIConfiguration(//
            testSettings, "myIniConfigConnector", "iniConfigWithDefaultDelimiter.ini", ".");

        // configure some values
        configuration.setProperty(testValueKey, "Hello Windows World");
        configuration.save();

        // validate result
        assertThat(testSettings.getSettings().getString(testValueKey), is("Hello Windows World"));


        final Set<String> sections = configuration.getSections();
        assertThat(sections, hasSize(1));
        assertThat(sections.iterator().next(), is(nullValue())); // Global Config

    }

    @Test
    public void testAdapterXmlConfigWithDefaultDelimiter() throws Exception {
        final Settings4jRepository testSettings = createSimpleSettings4jConfig();

        // start test => create adapter and add to Settings4jRepository
        final XMLConfiguration configuration = addXmlConfiguration(//
            testSettings, "myXmlConfigConnector", "xmlConfigWithDefaultDelimiter.xml", "/");

        // configure some values
        configuration.setProperty(TEST_VALUE_KEY, "Hello Windows World");
        configuration.save();

        // validate result
        assertThat(testSettings.getSettings().getString(TEST_VALUE_KEY), is("Hello Windows World"));
        assertThat(testSettings.getSettings().getString("unknown/Value"), is(nullValue()));

    }

    @Test
    public void testAdapterPropertiesConfig() throws Exception {
        final Settings4jRepository testSettings = createSimpleSettings4jConfig();

        // start test => create adapter and add to Settings4jRepository
        final PropertiesConfiguration configuration = addPropertiesConfiguration(//
            testSettings, "myPropConfigConnector", "propertiesConfig.properties");

        // configure some values
        configuration.setProperty(TEST_VALUE_KEY, "Hello Windows World");
        configuration.save();

        // validate result
        assertThat(testSettings.getSettings().getString(TEST_VALUE_KEY), is("Hello Windows World"));
        assertThat(testSettings.getSettings().getString("unknown/Value"), is(nullValue()));

    }

    private Settings4jRepository createSimpleSettings4jConfig() {
        return TestUtils.getConfiguredSettingsRepository(//
            "org/settings4j/helper/configuration/simpleSettings4jConfig.xml");
    }

    private HierarchicalINIConfiguration addINIConfiguration(final Settings4jRepository testSettings,
            final String connectorName, final String fileName, final String propertyDelimiter)
            throws ConfigurationException {
        ConfigurationToConnectorAdapter connector = (ConfigurationToConnectorAdapter) testSettings.getSettings()//
            .getConnector(connectorName);
        if (connector == null) {
            final File iniConfig = new File(TestUtils.getTestFolder(), "helper/configuration/" + fileName);
            iniConfig.delete();
            HierarchicalINIConfiguration configuration = new HierarchicalINIConfiguration(iniConfig);
            final DefaultExpressionEngine expressionEngine = new DefaultExpressionEngine();
            expressionEngine.setPropertyDelimiter(propertyDelimiter);
            configuration.setExpressionEngine(expressionEngine);

            connector = new ConfigurationToConnectorAdapter(connectorName, configuration);

            testSettings.getSettings().addConnector(//
                connector, ConnectorPositions.afterLast(SystemPropertyConnector.class));

        }
        return (HierarchicalINIConfiguration) connector.getConfiguration();
    }

    private XMLConfiguration addXmlConfiguration(final Settings4jRepository testSettings, final String connectorName,
            final String fileName, final String propertyDelimiter) throws ConfigurationException {
        ConfigurationToConnectorAdapter connector = (ConfigurationToConnectorAdapter) testSettings.getSettings()//
            .getConnector(connectorName);
        if (connector == null) {
            final File iniConfig = new File(TestUtils.getTestFolder(), "helper/configuration/" + fileName);
            iniConfig.delete();
            XMLConfiguration configuration = new XMLConfiguration(iniConfig);
            final DefaultExpressionEngine expressionEngine = new DefaultExpressionEngine();
            expressionEngine.setPropertyDelimiter(propertyDelimiter);
            configuration.setExpressionEngine(expressionEngine);

            connector = new ConfigurationToConnectorAdapter(connectorName, configuration);

            testSettings.getSettings().addConnector(connector, //
                ConnectorPositions.firstValid(//
                    ConnectorPositions.afterLast(SystemPropertyConnector.class), //
                    ConnectorPositions.atFirst() // if no SystemPropertyConnector is configured.
                    )//
                );

        }
        return (XMLConfiguration) connector.getConfiguration();
    }

    private PropertiesConfiguration addPropertiesConfiguration(final Settings4jRepository testSettings,
            final String connectorName, final String fileName) throws ConfigurationException {
        ConfigurationToConnectorAdapter connector = (ConfigurationToConnectorAdapter) testSettings.getSettings()//
            .getConnector(connectorName);
        if (connector == null) {
            final File iniConfig = new File(TestUtils.getTestFolder(), "helper/configuration/" + fileName);
            iniConfig.delete();
            PropertiesConfiguration configuration = new PropertiesConfiguration(iniConfig);

            connector = new ConfigurationToConnectorAdapter(connectorName, configuration);

            testSettings.getSettings().addConnector(//
                connector, ConnectorPositions.afterLast(SystemPropertyConnector.class));

        }
        return (PropertiesConfiguration) connector.getConfiguration();
    }
}
