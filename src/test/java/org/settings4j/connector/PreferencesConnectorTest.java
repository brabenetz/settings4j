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
package org.settings4j.connector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.junit.Before;
import org.junit.Test;
import org.settings4j.Settings4j;

/**
 * Test Suite for {@link PreferencesConnector}.
 *
 * @author brabenetz
 */
public class PreferencesConnectorTest {


    private static final String PREF_UNITTEST_NODE = "org/settings4j/unittest";

    @Before
    public void setUp() throws Exception {
        removeUnitTestNode(Preferences.userRoot());
        removeUnitTestNode(Preferences.systemRoot());
    }

    /**
     * Smoke Test if the Preferences from JDK works as expected.
     * <p>
     * This Test tests the PRE-REQUIREMENTS for the PreferencesConnector-tests and NOT the {@link PreferencesConnector} itself.
     * </p>
     *
     * @throws BackingStoreException
     *         in case of an error.
     */
    @Test
    public void testSmokeTest() throws BackingStoreException {

        final Preferences myModulePrefs = Preferences.userRoot().node("com/myapp/mymodule");
        // put test value
        myModulePrefs.put("myKey", "myNewValue");
        // read TestValue
        final String myValue = myModulePrefs.get("myKey", null);

        // validate Result
        assertThat(myValue, is("myNewValue"));

        // remove node
        myModulePrefs.removeNode();
        assertThat(Preferences.userRoot().nodeExists("com/myapp/mymodule"), is(false));
    }

    private void removeUnitTestNode(final Preferences userRoot) throws BackingStoreException {
        if (userRoot.nodeExists(PREF_UNITTEST_NODE)) {
            userRoot.node(PREF_UNITTEST_NODE).removeNode();
        }
    }

    @Test
    public void testSaveSuccessSlash() {
        final PreferencesConnector connector = new PreferencesConnector();
        connector.setString(PREF_UNITTEST_NODE + "/testSaveSuccessSlash", "test");

        assertThat(Preferences.userRoot().node(PREF_UNITTEST_NODE).get("testSaveSuccessSlash", null), is("test"));
    }

    @Test
    public void testSaveSuccessBackslash() {
        final PreferencesConnector connector = new PreferencesConnector();
        connector.setString(PREF_UNITTEST_NODE + "\\testSaveSuccessBackslash", "test");

        assertThat(Preferences.userRoot().node(PREF_UNITTEST_NODE).get("testSaveSuccessBackslash", null), is("test"));
    }

    @Test
    public void testReadSuccess() {
        Preferences.userRoot().node(PREF_UNITTEST_NODE).put("testReadSuccess", "test");

        final PreferencesConnector connector = new PreferencesConnector();

        assertThat(connector.getString(PREF_UNITTEST_NODE + "/testReadSuccess"), is("test"));
    }

    @Test
    public void testReadFail() {
        final PreferencesConnector connector = new PreferencesConnector();

        assertThat(connector.getString(PREF_UNITTEST_NODE + "/testReadFailDoesntExist"), is(nullValue()));
        assertThat(connector.getString("testReadFailDoesntExist"), is(nullValue()));
    }

    @Test
    public void testRoundup() {
        final PreferencesConnector connector = new PreferencesConnector();

        assertThat(connector.getString(PREF_UNITTEST_NODE + "/testRoundup"), is(nullValue()));
        // 1. save
        connector.setString(PREF_UNITTEST_NODE + "/testRoundup", "test");
        // 2. read
        assertThat(connector.getString(PREF_UNITTEST_NODE + "/testRoundup"), is("test"));
    }

    @Test
    public void testFormularExample() {
        final PreferencesConnector connector = new PreferencesConnector();
        connector.setString("com/mycompany/mycalculation/my-formula", "a * a - 2");

        assertThat(Settings4j.getString("com/mycompany/mycalculation/my-formula"), is("a * a - 2"));
    }
}
