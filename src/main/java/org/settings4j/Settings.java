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
public abstract class Settings {
    
    /**
     * The Name of the Settings
     * @return
     */
    public abstract String getName();

    public abstract String getString(String key);

    public abstract byte[] getContent(String key);

    public abstract Object getObject(String key);

    public abstract void setString(String key, String value);

    public abstract void setContent(String key, byte[] value);

    public abstract void setObject(String key, Object value);

    public abstract List getConnectors();

    public abstract void addConnector(Connector connector);
    
    public abstract void removeAllConnectors();

    /**
     * Get the {@link SettingsRepository} where this Settings-Object is stored.
     * 
     * @return the {@link SettingsRepository} where this Settings-Object is stored.
     */
    public abstract SettingsRepository getSettingsRepository();
    
    /**
     * Delegate to {@link SettingsManager#getSettings(String)}
     * 
     * @see SettingsManager#getSettings(String)
     */
    public static Settings getSettings(String name) {
        return SettingsManager.getSettings(name);
    }

    /**
     * Delegate to {@link SettingsManager#getSettings(Class)}
     * 
     * @see SettingsManager#getSettings(Class)
     */
    public static Settings getSettings(Class clazz) {
        return SettingsManager.getSettings(clazz);
    }

    /**
     * Delegate to {@link SettingsManager#getSettings(String, SettingsFactory)}
     * 
     * @see SettingsManager#getSettings(String, SettingsFactory)
     */
    public static Settings getSettings(final String name, final SettingsFactory factory) {
        return SettingsManager.getSettings(name, factory);
    }

    /**
     * Delegate to {@link SettingsManager#getRootSettings()}
     * 
     * @see SettingsManager#getRootSettings()
     */
    public static Settings getRootSettings() {
        return SettingsManager.getRootSettings();
    }
}
