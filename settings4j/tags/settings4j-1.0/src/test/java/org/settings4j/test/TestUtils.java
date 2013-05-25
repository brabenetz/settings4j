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

package org.settings4j.test;

import java.util.Properties;

import org.settings4j.Settings4j;


/**
 * @author brabenetz
 */
public final class TestUtils {

    /** hide default constructor (utility pattern). */
    private TestUtils() {
        super();
    }

    /**
     * reload org/settings4j/config/defaultsettings4j.xml.
     */
    public static void reconfigureSettings4jWithDefaultConfig() {
        // settings4j will be configured with the default-fallback-config if no connector exists:
        // org/settings4j/config/defaultsettings4j.xml
        Settings4j.getSettingsRepository().getSettings().removeAllConnectors();
        Settings4j.getString("something"); // reconfigure Settings4j with default values
    }

    /**
     * System.clearProperty(...) doesn't exist in JDK-1.4.
     * 
     * @param property the Property to clear
     */
    public static void clearSystemProperty(final String property) {
        clearSystemProperties(new String[]{property});
    }

    /**
     * System.clearProperty(...) doesn't exist in JDK-1.4.
     * 
     * @param properties the Properties to clear
     */
    public static void clearSystemProperties(final String[] properties) {
        final Properties props = System.getProperties();
        for (int i = 0; i < properties.length; i++) {
            props.remove(properties[i]);
        }
        System.setProperties(props);
    }
}