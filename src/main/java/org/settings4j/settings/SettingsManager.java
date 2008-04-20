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
package org.settings4j.settings;

import java.util.List;

import org.settings4j.Settings;
import org.settings4j.SettingsFactory;
import org.settings4j.SettingsRepository;
import org.settings4j.settings.nop.NOPSettingsRepository;

public class SettingsManager {
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(SettingsManager.class);


    public static final String DEFAULT_XML_CONFIGURATION_FILE = "settings4j.xml";
    
    public static final String DEFAULT_FALLBACK_CONFIGURATION_FILE = "org/settings4j/config/defaultsettings4j.xml";  
    
    
    private static SettingsRepository settingsRepository;
    static {
        /**
         * The root settings names itself as "root". However, the root settings cannot be retrieved
         * by name.
         */
        settingsRepository = new HierarchicalSettingsRepository(new DefaultSettings("root"));

        // TODO hbrabenetz 29.03.2008 : read XML default Configuration to configure the repository

    }

    public static SettingsRepository getSettingsRepository() {
        if (settingsRepository == null) {
            settingsRepository = new NOPSettingsRepository();
            LOG.error("SettingsManager.settingsRepository was null likely due to error in class reloading, using NOPSettingsRepository.");
        }
        return settingsRepository;
    }

    public static Settings getRootSettings() {
        return getSettingsRepository().getRootSettings();
    }

    /**
     * Retrieve the appropriate {@link Settings} instance.
     */
    public static Settings getSettings(final String name) {
        // Delegate the actual manufacturing of the settings to the settings repository.
        return getSettingsRepository().getSettings(name);
    }

    /**
     * Retrieve the appropriate {@link Settings} instance.
     */
    public static Settings getSettings(final Class clazz) {
        // Delegate the actual manufacturing of the settings to the settings repository.
        return getSettingsRepository().getSettings(clazz.getName());
    }

    /**
     * Retrieve the appropriate {@link Settings} instance.
     */
    public static Settings getSettings(final String name, final SettingsFactory factory) {
        // Delegate the actual manufacturing of the settings to the settings repository.
        return getSettingsRepository().getSettings(name, factory);
    }

    public static Settings exists(final String name) {
        return getSettingsRepository().exists(name);
    }

    public static List getCurrentSettingsList() {
        return getSettingsRepository().getCurrentSettingsList();
    }
}
