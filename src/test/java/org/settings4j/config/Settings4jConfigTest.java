/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2016 Brabenetz Harald, Austria
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
package org.settings4j.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.settings4j.Connector;
import org.settings4j.Settings4jInstance;
import org.settings4j.Settings4jRepository;
import org.settings4j.settings.SettingsManager;
import org.settings4j.test.TestUtils;

/**
 * TestCases for {@link Settings4jInstance}.
 */
public class Settings4jConfigTest extends AbstractTestSettings4jConfig {
    private static final String KEY_FILE_PROPERTY_PATH = "org/settings4j/junit/filePropertyPath";

    public void teardown() {
        System.clearProperty(KEY_FILE_PROPERTY_PATH);
    }

    /**
     * Test parsing of defaultsettings4j.xml (FALLBACK-Configuration).
     */
    @Test
    public void testDefaultSettings4jConfig() {
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository(SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);

        final Settings4jInstance settings = settingsRepository.getSettings();

        // the rootSettings have four Connectors
        final int expectedConnectorSize = 5;
        assertThat(settings.getConnectors(), hasSize(expectedConnectorSize));

        // check if there is no Exception thrown:
        assertThat(settings.getString("xyz"), is(nullValue()));
        assertThat(settings.getContent("xyz"), is(nullValue()));
        assertThat(settings.getObject("xyz"), is(nullValue()));

        assertThat(settings.getConnectors(), hasSize(expectedConnectorSize));
    }

    /**
     * test corrupt config  xml file.
     *
     */
    @Test
    public void testCorruptConfig() {
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository("org/settings4j/config/testConfigCorrupt.xml");

        assertThat(settingsRepository.getSettings().getConnectors(), hasSize(0));
    }


    /**
     * test for {@link org.settings4j.connector.FSConnector}.
     *
     * @throws Exception in case of an error.
     */
    @Test
    public void testFSConfigTempFolder() throws Exception {
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository("org/settings4j/config/testConfigFSTempfolder.xml");

        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        final Settings4jInstance settings = settingsRepository.getSettings();
        final File tmpFolder = TestUtils.getTmpFolder();
        final File fileXyz = new File(tmpFolder, "xyz");
        Assert.assertFalse(fileXyz.exists());
        FileUtils.writeStringToFile(fileXyz, "abc", "UTF-8");
        Assert.assertTrue(fileXyz.exists());

        FileUtils.writeStringToFile(new File(tmpFolder, "xyz2"), "abc2", "UTF-8");

        // every settings have read access to the same FSConnector.
        assertThat(settings.getString("xyz"), is("abc"));
        assertThat(settings.getString("xyz2"), is("abc2"));

        assertThat(settings.getConnectors(), hasSize(2));

        Connector connector;
        connector = settings.getConnector("FSConnector");
        assertThat(connector, is(notNullValue()));
        connector = settings.getConnector("SystemPropertyConnector");
        assertThat(connector, is(notNullValue()));
        connector = settings.getConnector("nichtVorhanden");
        assertThat(connector, is(nullValue()));

    }

    /**
     * test for {@link org.settings4j.connector.FSConnector}.
     *
     * @throws Exception in case of an error.
     */
    @Test
    public void testFSConfigTestFolder() throws Exception {
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository("org/settings4j/config/testConfigFSTestfolder.xml");

        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        final Settings4jInstance settings = settingsRepository.getSettings();
        final File testFolder = TestUtils.getTestFolder();
        final File fileXyz = new File(testFolder, "xyz");
        Assert.assertFalse(fileXyz.exists());
        FileUtils.writeStringToFile(fileXyz, "abc", "UTF-8");
        Assert.assertTrue(fileXyz.exists());

        FileUtils.writeStringToFile(new File(testFolder, "xyz2"), "abc2", "UTF-8");

        // every settings have read access to the same FSConnector.
        assertThat(settings.getString("xyz"), is("abc"));
        assertThat(settings.getString("xyz2"), is("abc2"));

        assertThat(settings.getConnectors(), hasSize(1));
    }

    /**
     * Test ObjectReolver with cached Connector.
     */
    @Test
    public void testObjectResolverConfig() {


        testCaching("org/settings4j/config/testConfigObjectResolver-nocaching.xml", //
            "org/settings4j/objectresolver/test1", //
            false); // no caching!

    }

    /**
     * Test ObjectReolver with cached Connector.
     */
    @Test
    public void testObjectResolverConfig1() {


        testCaching("org/settings4j/config/testConfigObjectResolver1.xml", //
            "org/settings4j/objectresolver/test1", //
            true); // The FSConnector is cached!

    }

    /**
     * Test ObjectReolver with cached ObjectReolver.
     */
    @Test
    public void testObjectResolverConfig2() {

        testCaching("org/settings4j/config/testConfigObjectResolver2.xml", //
            "org/settings4j/objectresolver/test2", //
            true); // The JavaXMLBeansObjectResolver is cached!
    }

    /**
     * Test ObjectReolver with cached Object-Property-config.
     */
    @Test
    public void testObjectResolverConfig3() {

        testCaching("org/settings4j/config/testConfigObjectResolver-nocaching.xml", //
            "org/settings4j/objectresolver/test2", //
            true); // The test2.properties is cached set to true!
    }


    private void testCaching(final String settingsRepositoryUrl, final String objectKey, final boolean mustBeTheSame) {
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository(settingsRepositoryUrl);

        final Settings4jInstance settings = settingsRepository.getSettings();

        final Map<?, ?> result1 = (Map<?, ?>) settings.getObject(objectKey);
        assertThat(result1, is(notNullValue()));
        final Map<?, ?> result2 = (Map<?, ?>) settings.getObject(objectKey);
        if (mustBeTheSame) {
            Assert.assertTrue(result1 == result2);
        } else {
            Assert.assertTrue(result1 != result2);
        }
    }

    /**
     * Test ObjectReolver with cached Object-Property-config.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testObjectResolverConfig4Spring() throws Exception {

        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository("org/settings4j/config/testConfigObjectResolver4Spring.xml");

        final String key1 = "org/settings4j/objectresolver/testSpring1"; // spring-configuration

        // store values into the default java temporary directory with subfolder "Settings4j"
        // String tmpdir = System.getProperty("java.io.tmpdir");
        final Settings4jInstance settings1 = settingsRepository.getSettings();

        // the propety-file "org/settings4j/objectresolver/test1.properties must exists"
        final DataSource dataSource = (DataSource) settings1.getObject(key1);
        assertThat(dataSource, is(notNullValue()));

        // test DataSource
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("create Table test ( name VARCHAR(255) )");
            pstmt.execute();
            pstmt.close();

            pstmt = conn.prepareStatement("insert into test(name) values (?)");
            pstmt.setString(1, "Hello World");
            pstmt.execute();
            pstmt.close();

            pstmt = conn.prepareStatement("select * from test");
            final ResultSet rs = pstmt.executeQuery();
            rs.next();
            final String result = rs.getString(1);
            rs.close();
            pstmt.close();

            pstmt = conn.prepareStatement("drop Table test");
            pstmt.execute();
            pstmt.close();

            assertThat(result, is("Hello World"));

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * test {@link org.settings4j.connector.PropertyFileConnector#setPropertyFromPath(String)}.
     */
    @Test
    public void testPropertyFileConfig() {
        System.setProperty(KEY_FILE_PROPERTY_PATH, "classpath:org/settings4j/config/propertyFile.properties");
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository("org/settings4j/config/testConfigPropertyFile.xml");

        final Settings4jInstance settings = settingsRepository.getSettings();

        // every settings have read access to the same FSConnector.
        assertThat(settings.getString("xyz"), is("Value from Property-File"));
        assertThat(settings.getConnectors(), hasSize(2));
    }

    /**
     * test {@link org.settings4j.connector.PropertyFileConnector#setPropertyFromPath(String)} with classpath url.
     */
    @Test
    public void testPropertyFileConfigFromPath1() {
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository("org/settings4j/config/testConfigPropertyFileFromPath1.xml");

        final Settings4jInstance settings = settingsRepository.getSettings();

        // every settings have read access to the same FSConnector.
        assertThat(settings.getString("xyz"), is("Value from Property-File"));
        assertThat(settings.getConnectors(), hasSize(1));
    }

    /**
     * test {@link org.settings4j.connector.PropertyFileConnector#setPropertyFromPath(String)} with filepath url.
     */
    @Test
    public void testPropertyFileConfigFromPath2() {
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository("org/settings4j/config/testConfigPropertyFileFromPath2.xml");

        final Settings4jInstance settings = settingsRepository.getSettings();

        // every settings have read access to the same FSConnector.
        assertThat(settings.getString("xyz"), is("Value from Property-File"));
        assertThat(settings.getConnectors(), hasSize(1));
    }

    /**
     * test {@link org.settings4j.connector.PropertyFileConnector#setPropertyFromPath(String)} with empty String.
     */
    @Test
    public void testPropertyFileConfigFromPathException1() {
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository("org/settings4j/config/testConfigPropertyFileFromPathException1.xml");

        final Settings4jInstance settings = settingsRepository.getSettings();

        assertThat(settings.getString("xyz"), is(nullValue()));
        assertThat(settings.getConnectors(), hasSize(1));
    }

    /**
     * test {@link org.settings4j.connector.PropertyFileConnector#setPropertyFromPath(String)} without prefix.
     */
    @Test
    public void testPropertyFileConfigFromPathException2() {
        final Settings4jRepository settingsRepository = TestUtils
            .getConfiguredSettingsRepository("org/settings4j/config/testConfigPropertyFileFromPathException2.xml");

        final Settings4jInstance settings = settingsRepository.getSettings();

        assertThat(settings.getString("xyz"), is(nullValue()));
        assertThat(settings.getConnectors(), hasSize(1));
    }
}
