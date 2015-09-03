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

package org.settings4j.helper.spring;

import java.util.Map;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;
import org.settings4j.connector.PreferencesConnector;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.objectresolver.SpringConfigObjectResolver;

import junit.framework.TestCase;


/**
 * TestSuite for {@link Settings4jPlaceholderConfigurer}.
 *
 * @author brabenetz
 */
public class Settings4jPlaceholderConfigurerTest extends TestCase {


    private static final String SYSTEM_PROPERTY_TEST_1 = "a/b/test-1";
    private static final String SYSTEM_PROPERTY_TEST_2 = "a/b/test-2";
    private static final String SYSTEM_PROPERTY_TEST_3 = "a/b/test-3";
    private static final String PREF_UNITTEST_NODE = "org/settings4j/unittest";

    /** {@inheritDoc} */
    @Override
    protected void setUp() throws Exception {
        removeUnitTestNode(Preferences.userRoot());
        removeUnitTestNode(Preferences.systemRoot());
        Properties props = System.getProperties();
        props.remove(SYSTEM_PROPERTY_TEST_1);
        props.remove(SYSTEM_PROPERTY_TEST_2);
        props.remove(SYSTEM_PROPERTY_TEST_3);
        System.setProperties(props);
    }

    private void removeUnitTestNode(final Preferences userRoot) throws BackingStoreException {
        if (userRoot.nodeExists(PREF_UNITTEST_NODE)) {
            userRoot.node(PREF_UNITTEST_NODE).removeNode();
        }
    }

    /**
     * TestCase for the normal use case in Spring configuration.
     * <p>
     * See /src/test/resources/org/settings4j/helper/spring/Settings4jPlaceholderConfigurerHappyPath
     * </p>
     */
    public void testHappyPath() {
        // Example system-Config
        System.setProperty("org/settings4j/helper/spring/test1", "Hallo World");

        // load the example Spring-Config
        final Map result = (Map) getObjectFromSpringConfig(//
            "org/settings4j/helper/spring/Settings4jPlaceholderConfigurerHappyPath");

        // validate Result
        assertEquals("Hallo World", result.get("MapEntry1"));
    }

    /**
     * TestCase for the complex use case in Spring configuration with prefix and default values.
     */
    public void testHappyPathComplex() {
        // Example system-Config
        System.setProperty("org/settings4j/unittest/testPlaceholderConfigurerHappyPathComplex/test1", "Hallo World");
        PreferencesConnector prefConnector = new PreferencesConnector();
        prefConnector.setString(//
            "org/settings4j/unittest/testPlaceholderConfigurerHappyPathComplex/test2", "Hallo World 2");

        // load the example Spring-Config
        final Map result = (Map) getObjectFromSpringConfig(//
            "org/settings4j/helper/spring/Settings4jPlaceholderConfigurerHappyPathComplex");

        // validate Result
        assertEquals("Hallo World", result.get("MapEntry1"));
        assertEquals("Hallo World 2", result.get("MapEntry2"));
        assertEquals("Third Test", result.get("MapEntry3"));
        assertEquals("Fourth Test", result.get("MapEntry4"));

    }

    /**
     * TestCase to parse a Simple String with placeholders.
     * <p>
     * In this use case no spring application context is required.
     */
    public void testParseStringValueSimple() {

        // prepare SystemProperties
        System.setProperty(SYSTEM_PROPERTY_TEST_1, "value-1");
        System.setProperty(SYSTEM_PROPERTY_TEST_2, "value-2");


        // start test
        String strVal = "${a/b/test-1},\n${a/b/test-2}";
        String result = Settings4jPlaceholderConfigurer.parseStringValue(strVal);

        // validate result
        assertEquals("value-1,\nvalue-2", result);
    }

    /**
     * TestCase to parse a Simple String with placeholders.
     * <p>
     * In this use case no spring application context is required.
     * </p>
     */
    public void testParseStringValueSimpleWithPlaceholders() {

        // prepare SystemProperties
        System.setProperty(SYSTEM_PROPERTY_TEST_1, "value-1");
        System.setProperty(SYSTEM_PROPERTY_TEST_2, "value-2");


        // start test
        String strVal = "${test-1},\n${test-2}";
        String result = Settings4jPlaceholderConfigurer.parseStringValue(strVal, "a/b/");

        // validate result
        assertEquals("value-1,\nvalue-2", result);
    }


    /**
     * TestCase to parse a Simple String with placeholders.
     * <p>
     * In this use case no spring application context is required.
     * </p>
     */
    public void testParseStringValueWithDefaultProperties() {

        // prepare SystemProperties
        System.setProperty(SYSTEM_PROPERTY_TEST_2, "value-2b");
        System.setProperty(SYSTEM_PROPERTY_TEST_3, "value-3b");

        // prepare default Values 1 and 2
        Properties props = createDefaultProperties();

        // start test
        String strVal = "${a/b/test-1},\n${a/b/test-2},\n${a/b/test-3}";
        String result = Settings4jPlaceholderConfigurer.parseStringValue(strVal, StringUtils.EMPTY, props);

        // validate result
        assertEquals("value-1a,\nvalue-2b,\nvalue-3b", result);
    }

    /**
     * TestCase to parse a Simple String with prefix for placeholders.
     * <p>
     * In this use case no spring application context is required.
     * </p>
     */
    public void testParseStringValueWithDefaultPropertiesAndPrefix() {

        // prepare SystemProperties
        System.setProperty(SYSTEM_PROPERTY_TEST_2, "value-2b");
        System.setProperty(SYSTEM_PROPERTY_TEST_3, "value-3b");

        // prepare default Values 1 and 2
        Properties props = createDefaultProperties();

        // start test
        String strVal = "${test-1},\n${test-2},\n${test-3}";
        String result = Settings4jPlaceholderConfigurer.parseStringValue(strVal, "a/b/", props);

        // validate result
        assertEquals("value-1a,\nvalue-2b,\nvalue-3b", result);
    }

    private Properties createDefaultProperties() {
        Properties props = new Properties();
        props.put(SYSTEM_PROPERTY_TEST_1, "value-1a");
        props.put(SYSTEM_PROPERTY_TEST_2, "value-2a");
        return props;
    }

    private Object getObjectFromSpringConfig(final String key) {
        final SpringConfigObjectResolver springConfigObjectResolver = new SpringConfigObjectResolver();
        final Object result = springConfigObjectResolver.getObject(key, new ClasspathContentResolver());
        return result;
    }
}
