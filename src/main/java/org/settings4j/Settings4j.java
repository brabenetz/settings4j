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
package org.settings4j;

import java.util.List;

import org.settings4j.settings.SettingsManager;


/**
 * Settings is used to get simply access to Application settings.
 * 
 * <pre>
 * Example usage java:
 * --------------------------------------
 * public class SettingsManager {
 *     public static String getMyFormula() {
 *         return Settings4j.getString("com/mycompany/mycalculation/my-formula");
 *     }
 * }
 * --------------------------------------
 * 
 * </pre>
 * 
 * @author Harald.Brabenetz
 */
public final class Settings4j {

    /** Hide Constructor (Utility-Pattern). */
    private Settings4j() {
        super();
    }

    /**
     * return the found String-Value for the given key.<br />
     * The {@link Settings4j} Instance iterates all his {@link Connector} and return the first found Value.<br />
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found String-Value for the given key
     */
    public static String getString(final String key) {
        return getSettings().getString(key);
    }

    /**
     * return the found byte[]-Value for the given key.<br />
     * { getSettings().getAllConnectors(); } The {@link Settings4j} Instance iterates all his {@link Connector} and
     * return the first found Value.<br />
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found byte[]-Value for the given key
     */
    public static byte[] getContent(final String key) {
        return getSettings().getContent(key);
    }

    /**
     * return the found Object-Value for the given key.<br />
     * The {@link Settings4j} Instance iterates all his {@link Connector} and return the first found Value.<br />
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found Object-Value for the given key
     */
    public static Object getObject(final String key) {
        return getSettings().getObject(key);
    }

    /**
     * Get the {@link Settings4jRepository} where this Settings-Object is stored.
     * 
     * @return the {@link Settings4jRepository} where this Settings-Object is stored.
     */
    public static Settings4jRepository getSettingsRepository() {
        return SettingsManager.getSettingsRepository();
    }

    /**
     * Delegate to {@link SettingsManager#getRootSettings()}.
     * 
     * @see SettingsManager#getRootSettings()
     */
    private static Settings4jInstance getSettings() {
        return SettingsManager.getSettings();
    }

    /**
     * Return a List off {@link Connector} who can be used with this {@link Settings4j} instance.
     * 
     * @return a list off all Connectors who can be used with this {@link Settings4j} instance
     */
    public static List getConnectors() {
        return getSettings().getConnectors();
    }
}
