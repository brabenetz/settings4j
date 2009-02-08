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

import org.settings4j.exception.NoWriteableConnectorFoundException;
import org.settings4j.settings.SettingsManager;


/**
 * Settings is used to get simply access to Application settings.
 * The default Settings-Object is embedded into an Hirachical Structure like the Logger in Log4j.
 * 
 * <pre>
 * Example usage java:
 * --------------------------------------
 * public class SettingsManager {
 *     private static final Settings SETTINGS = Settings.getSettings(SettingsManager.class);
 * 
 *     public static String getMyFormula() {
 *         return SETTINGS.getString("com/mycompany/mycalculation/my-formula");
 *     }
 * }
 * --------------------------------------
 * 
 * Example configuration in settings4j.xml:
 * --------------------------------------
 * &lt;settings name="com.mycompany" &gt;
 *     &lt;connector-ref name="DBConnector" /&gt;
 * &lt;/settings&gt;
 * --------------------------------------
 * 
 * </pre>
 * 
 * @author hbrabenetz
 *
 */
public final class Settings {
    
    /**
     * Hide Constructor (Utility-Pattern)
     */
    private Settings() {}

    /**
     * return the found String-Value for the given key.<br />
     * The {@link Settings} Instance iterates all his {@link Connector} and return the first found Value.<br />
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found String-Value for the given key 
     */
    public static String getString(String key) {
    	return getSettings().getString(key);
    }

    /**
     * return the found byte[]-Value for the given key.<br /> {
    	getSettings().getAllConnectors();
    }
     * The {@link Settings} Instance iterates all his {@link Connector} and return the first found Value.<br />
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found byte[]-Value for the given key 
     */
    public static byte[] getContent(String key) {
    	return getSettings().getContent(key);
    }

    /**
     * return the found Object-Value for the given key.<br />
     * The {@link Settings} Instance iterates all his {@link Connector} and return the first found Value.<br />
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found Object-Value for the given key 
     */
    public static Object getObject(String key) {
    	return getSettings().getObject(key);
    }

    /**
     * Set ( or overwrite ) the Value for the Given key.<br />
     * The {@link Settings} Instance iterates all his {@link Connector} and
     * if a Connector can successful write the new Value,
     * then the Connector must return {@link Constants#SETTING_SUCCESS}<br />
     * 
     * If No Connector can write the Value ( all {@link Connector} returns {@link Constants#SETTING_NOT_POSSIBLE}
     * then a {@link NoWriteableConnectorFoundException} will be thrown.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new String-Value for the given key 
     * @param connectorName The name of the connector, who should be used to set the value
     * @throws NoWriteableConnectorFoundException Is thrown if no Connector was Found.
     *      One Connector must return {@link Constants#SETTING_SUCCESS}
     */
    public static void setString(String key, String value, String connectorName) throws NoWriteableConnectorFoundException {
    	getSettings().setString(key, value, connectorName);
    }


    /**
     * Set ( or overwrite ) the Value for the Given key.<br />
     * The {@link Settings} Instance iterates all his {@link Connector} and
     * if a Connector can successful write the new Value,
     * then the Connector must return {@link Constants#SETTING_SUCCESS}<br />
     * 
     * If No Connector can write the Value ( all {@link Connector} returns {@link Constants#SETTING_NOT_POSSIBLE}
     * then a {@link NoWriteableConnectorFoundException} will be thrown.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new byte[]-Value for the given key
     * @param connectorName The name of the connector, who should be used to set the value 
     * @throws NoWriteableConnectorFoundException Is thrown if no Connector was Found.
     *      One Connector must return {@link Constants#SETTING_SUCCESS}
     */
    public static void setContent(String key, byte[] value, String connectorName) throws NoWriteableConnectorFoundException {
    	getSettings().setContent(key, value, connectorName);
    }


    /**
     * Set ( or overwrite ) the Value for the Given key.<br />
     * The {@link Settings} Instance iterates all his {@link Connector} and
     * if a Connector can successful write the new Value,
     * then the Connector must return {@link Constants#SETTING_SUCCESS}<br />
     * 
     * If No Connector can write the Value ( all {@link Connector} returns {@link Constants#SETTING_NOT_POSSIBLE}
     * then a {@link NoWriteableConnectorFoundException} will be thrown.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new Object-Value for the given key 
     * @param connectorName The name of the connector, who should be used to set the value
     * @throws NoWriteableConnectorFoundException Is thrown if no Connector was Found.
     *      One Connector must return {@link Constants#SETTING_SUCCESS}
     */
    public static void setObject(String key, Object value, String connectorName) throws NoWriteableConnectorFoundException {
    	getSettings().setObject(key, value, connectorName);
    }

    /**
     * Get the {@link SettingsRepository} where this Settings-Object is stored.
     * 
     * @return the {@link SettingsRepository} where this Settings-Object is stored.
     */
    public static SettingsRepository getSettingsRepository() {
    	return SettingsManager.getSettingsRepository();
    }
    

    /**
     * Delegate to {@link SettingsManager#getRootSettings()}
     * 
     * @see SettingsManager#getRootSettings()
     */
    private static SettingsInstance getSettings() {
        return SettingsManager.getSettings();
    }
    
    /**
     * Return a List off {@link Connector} who can be used with this {@link Settings} instance
     * 
     * @return a list off all Connectors who can be used with this {@link Settings} instance 
     */
    public static List getConnectors() {
    	return getSettings().getConnectors();
    }
}
