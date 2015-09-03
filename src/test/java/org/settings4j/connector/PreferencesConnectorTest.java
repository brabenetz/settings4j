/* ***************************************************************************
 * Copyright (c) 2011 Brabenetz Harald, Austria.
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
package org.settings4j.connector;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.settings4j.Settings4j;

import junit.framework.TestCase;

/**
 * Test Suite for {@link PreferencesConnector}. Checkstyle:OFF .*
 *
 * @author brabenetz
 */
public class PreferencesConnectorTest extends TestCase {


    private static final String PREF_UNITTEST_NODE = "org/settings4j/unittest";

    @Override
    protected void setUp() throws Exception {
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
    public void testSmokeTest() throws BackingStoreException {

        final Preferences myModulePrefs = Preferences.userRoot().node("com/myapp/mymodule");
        // put test value
        myModulePrefs.put("myKey", "myNewValue");
        // read TestValue
        final String myValue = myModulePrefs.get("myKey", null);

        // validate Result
        assertEquals("myNewValue", myValue);

        // remove node
        myModulePrefs.removeNode();
        assertEquals(false, Preferences.userRoot().nodeExists("com/myapp/mymodule"));
    }

    private void removeUnitTestNode(final Preferences userRoot) throws BackingStoreException {
        if (userRoot.nodeExists(PREF_UNITTEST_NODE)) {
            userRoot.node(PREF_UNITTEST_NODE).removeNode();
        }
    }

    public void testSaveSuccessSlash() {
        final PreferencesConnector connector = new PreferencesConnector();
        connector.setString(PREF_UNITTEST_NODE + "/testSaveSuccessSlash", "test");

        assertEquals("test", Preferences.userRoot().node(PREF_UNITTEST_NODE).get("testSaveSuccessSlash", null));
    }

    public void testSaveSuccessBackslash() {
        final PreferencesConnector connector = new PreferencesConnector();
        connector.setString(PREF_UNITTEST_NODE + "\\testSaveSuccessBackslash", "test");

        assertEquals("test", Preferences.userRoot().node(PREF_UNITTEST_NODE).get("testSaveSuccessBackslash", null));
    }

    public void testReadSuccess() {
        Preferences.userRoot().node(PREF_UNITTEST_NODE).put("testReadSuccess", "test");

        final PreferencesConnector connector = new PreferencesConnector();

        assertEquals("test", connector.getString(PREF_UNITTEST_NODE + "/testReadSuccess"));
    }

    public void testReadFail() {
        final PreferencesConnector connector = new PreferencesConnector();

        assertNull(connector.getString(PREF_UNITTEST_NODE + "/testReadFailDoesntExist"));
        assertNull(connector.getString("testReadFailDoesntExist"));
    }

    public void testRoundup() {
        final PreferencesConnector connector = new PreferencesConnector();

        assertNull(connector.getString(PREF_UNITTEST_NODE + "/testRoundup"));
        // 1. save
        connector.setString(PREF_UNITTEST_NODE + "/testRoundup", "test");
        // 2. read
        assertEquals("test", connector.getString(PREF_UNITTEST_NODE + "/testRoundup"));
    }

    public void testFormularExample() {
        final PreferencesConnector connector = new PreferencesConnector();
        connector.setString("com/mycompany/mycalculation/my-formula", "a * a - 2");

        assertEquals("a * a - 2", Settings4j.getString("com/mycompany/mycalculation/my-formula"));
    }
}
