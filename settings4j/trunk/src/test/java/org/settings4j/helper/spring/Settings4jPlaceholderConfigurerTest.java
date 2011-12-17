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
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

import org.settings4j.connector.PreferencesConnector;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.objectresolver.SpringConfigObjectResolver;


public class Settings4jPlaceholderConfigurerTest extends TestCase {


    private static final String PREF_UNITTEST_NODE = "org/settings4j/unittest";

    protected void setUp() throws Exception {
        removeUnitTestNode(Preferences.userRoot());
        removeUnitTestNode(Preferences.systemRoot());
    }

    private void removeUnitTestNode(final Preferences userRoot) throws BackingStoreException {
        if (userRoot.nodeExists(PREF_UNITTEST_NODE)) {
            userRoot.node(PREF_UNITTEST_NODE).removeNode();
        }
    }
    
    public void testHappyPath() {
        // Example system-Config
        System.setProperty("org/settings4j/helper/spring/test1", "Hallo World");

        // load the example Spring-Config
        final Map result = (Map) getObjectFromSpringConfig(//
            "org/settings4j/helper/spring/Settings4jPlaceholderConfigurerHappyPath");

        // validate Result
        assertEquals("Hallo World", result.get("MapEntry1"));
    }

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

    private Object getObjectFromSpringConfig(final String key) {
        final SpringConfigObjectResolver springConfigObjectResolver = new SpringConfigObjectResolver();
        final Object result = springConfigObjectResolver.getObject(key, new ClasspathContentResolver());
        return result;
    }
}
