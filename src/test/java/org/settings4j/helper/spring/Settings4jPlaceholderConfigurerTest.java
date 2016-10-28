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
package org.settings4j.helper.spring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Map;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.settings4j.connector.PreferencesConnector;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.objectresolver.SpringConfigObjectResolver;


/**
 * TestSuite for {@link Settings4jPlaceholderConfigurer}.
 *
 * @author brabenetz
 */
public class Settings4jPlaceholderConfigurerTest {


    private static final String SYSTEM_PROPERTY_TEST_1 = "a/b/test-1";
    private static final String SYSTEM_PROPERTY_TEST_2 = "a/b/test-2";
    private static final String SYSTEM_PROPERTY_TEST_3 = "a/b/test-3";
    private static final String PREF_UNITTEST_NODE = "org/settings4j/unittest";

    @Before
    public void setUp() throws Exception {
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
    @Test
    public void testHappyPath() {
        // Example system-Config
        System.setProperty("org/settings4j/helper/spring/test1", "Hallo World");

        // load the example Spring-Config
        final Map<?, ?> result = (Map<?, ?>) getObjectFromSpringConfig(//
            "org/settings4j/helper/spring/Settings4jPlaceholderConfigurerHappyPath");

        // validate Result
        assertThat(result.get("MapEntry1"), is((Object) "Hallo World"));
    }

    /**
     * TestCase for the complex use case in Spring configuration with prefix and default values.
     */
    @Test
    public void testHappyPathComplex() {
        // Example system-Config
        System.setProperty("org/settings4j/unittest/testPlaceholderConfigurerHappyPathComplex/test1", "Hallo World");
        PreferencesConnector prefConnector = new PreferencesConnector();
        prefConnector.setString(//
            "org/settings4j/unittest/testPlaceholderConfigurerHappyPathComplex/test2", "Hallo World 2");

        // load the example Spring-Config
        final Map<?, ?> result = (Map<?, ?>) getObjectFromSpringConfig(//
            "org/settings4j/helper/spring/Settings4jPlaceholderConfigurerHappyPathComplex");

        // validate Result
        assertThat(result.get("MapEntry1"), is((Object) "Hallo World"));
        assertThat(result.get("MapEntry2"), is((Object) "Hallo World 2"));
        assertThat(result.get("MapEntry3"), is((Object) "Third Test"));
        assertThat(result.get("MapEntry4"), is((Object) "Fourth Test"));

    }

    /**
     * TestCase to parse a Simple String with placeholders.
     * <p>
     * In this use case no spring application context is required.
     */
    @Test
    public void testParseStringValueSimple() {

        // prepare SystemProperties
        System.setProperty(SYSTEM_PROPERTY_TEST_1, "value-1");
        System.setProperty(SYSTEM_PROPERTY_TEST_2, "value-2");


        // start test
        String strVal = "${a/b/test-1},\n${a/b/test-2}";
        String result = Settings4jPlaceholderConfigurer.parseStringValue(strVal);

        // validate result
        assertThat(result, is("value-1,\nvalue-2"));
    }

    /**
     * TestCase to parse a Simple String with placeholders.
     * <p>
     * In this use case no spring application context is required.
     * </p>
     */
    @Test
    public void testParseStringValueSimpleWithPlaceholders() {

        // prepare SystemProperties
        System.setProperty(SYSTEM_PROPERTY_TEST_1, "value-1");
        System.setProperty(SYSTEM_PROPERTY_TEST_2, "value-2");


        // start test
        String strVal = "${test-1},\n${test-2}";
        String result = Settings4jPlaceholderConfigurer.parseStringValue(strVal, "a/b/");

        // validate result
        assertThat(result, is("value-1,\nvalue-2"));
    }


    /**
     * TestCase to parse a Simple String with placeholders.
     * <p>
     * In this use case no spring application context is required.
     * </p>
     */
    @Test
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
        assertThat(result, is("value-1a,\nvalue-2b,\nvalue-3b"));
    }

    /**
     * TestCase to parse a Simple String with prefix for placeholders.
     * <p>
     * In this use case no spring application context is required.
     * </p>
     */
    @Test
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
        assertThat(result, is("value-1a,\nvalue-2b,\nvalue-3b"));
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
