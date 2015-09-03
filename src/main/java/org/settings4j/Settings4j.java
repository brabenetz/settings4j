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
     * return the found String-Value for the given key.<br>
     * The {@link Settings4j} Instance iterates all his {@link Connector} and return the first found Value.<br>
     * <br>
     * Returns null if no connector found a Value for the given key<br>
     * <p>
     * If no custom settings4j.xml exist in classpath, the following default order will be used:
     * </p>
     * <ol>
     * <li>check if value for {@link System#getProperty(String)} exist (see {@link org.settings4j.connector.SystemPropertyConnector} ),</li>
     * <li>else check if value for {@link javax.naming.InitialContext#lookup(String)} exist (see {@link org.settings4j.connector.JNDIConnector} ),</li>
     * <li>else check if in {@link java.util.prefs.Preferences#userRoot()} and {@link java.util.prefs.Preferences#systemRoot()} the Value for
     * {@link java.util.prefs.Preferences#get(String, String)} exist (see {@link org.settings4j.connector.PreferencesConnector} ),</li>
     * <li>else check if the value exist in Classpath (see {@link org.settings4j.connector.ClasspathConnector} ).</li>
     * </ol>
     *
     * @param key
     *        the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found String-Value for the given key
     */
    public static String getString(final String key) {
        return getSettings().getString(key);
    }

    /**
     * return the found byte[]-Value for the given key.
     * <p>
     * { getSettings().getAllConnectors(); } The {@link Settings4j} Instance iterates all his {@link Connector} and return the first found Value.
     * </p>
     * <p>
     * Returns null if no connector found a Value for the given key<br>
     * </p>
     * <p>
     * If no custom settings4j.xml exist in classpath, the behavior is like {@link #getString(String)}, but only the
     * {@link org.settings4j.connector.ClasspathConnector} can return a byte[] content directly.<br>
     * The other Connectors calls there getString(...) Method to get a valid Filesystempath or Classpath.
     * </p>
     * <p>
     * e.g {@link org.settings4j.connector.SystemPropertyConnector}:<br>
     * Start the Application with -Dcom/mycompany/myapp/myParameterKey=file:D:/PathToMyFileContent<br>
     * Then: <code>getContent("com/mycompany/myapp/myParameterKey")</code> will return the byte[] Content of <code>"file:D:/PathToMyFileContent"</code>.
     * </p>
     * <p>
     * Valid Path-Prefixes are "file:" and "classpath:".<br>
     * See {@link ContentResolver} and {@link org.settings4j.contentresolver.FSContentResolver} and
     * {@link org.settings4j.contentresolver.ClasspathContentResolver}.
     * </p>
     *
     * @param key
     *        the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found byte[]-Value for the given key
     */
    public static byte[] getContent(final String key) {
        return getSettings().getContent(key);
    }

    /**
     * return the found Object-Value for the given key.<br>
     * The {@link Settings4j} Instance iterates all his {@link Connector} and return the first found Value.<br>
     * <p>
     * Returns null if no connector found a Value for the given key<br>
     * </p>
     * <p>
     * If no custom settings4j.xml exist in classpath, the behavior is like {@link #getString(String)}, but only the
     * {@link org.settings4j.connector.JNDIConnector} can return an Object directly.<br>
     * The other Connectors calls there getContent(...) Method to get a content which can be transformed to an Object.<br>
     * See {@link ObjectResolver}.
     * </p>
     *
     * @param key
     *        the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
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
    public static List<Connector> getConnectors() {
        return getSettings().getConnectors();
    }

    /**
     * Return the {@link Connector} for the given Name.
     *
     * @param connectorName The Connector Name.
     * @return The {@link Connector} for the given Name.
     */
    public static Connector getConnector(final String connectorName) {
        return getSettings().getConnector(connectorName);
    }
}
