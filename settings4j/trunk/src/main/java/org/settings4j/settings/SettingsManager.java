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

import java.net.URL;
import java.util.List;

import org.settings4j.Settings;
import org.settings4j.SettingsFactory;
import org.settings4j.SettingsRepository;
import org.settings4j.config.DOMConfigurator;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.settings.nop.NOPSettingsRepository;

/**
 * managed The {@link SettingsRepository} .
 * This {@link SettingsRepository} is used to store the configuration from the settings4j.xml.
 * 
 * @author hbrabenetz
 *
 */
public class SettingsManager {
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(SettingsManager.class);

    /**
     * The Classpath-Location of the settings4j.xml who i readed by default <br />
     * settings4j.xml
     */
    public static final String DEFAULT_XML_CONFIGURATION_FILE = "settings4j.xml";

    /**
     * The fallback settings4j.xml who is used if not settings4j.xml where found. <br />
     * org/settings4j/config/defaultsettings4j.xml
     */
    public static final String DEFAULT_FALLBACK_CONFIGURATION_FILE = "org/settings4j/config/defaultsettings4j.xml";

    /**
     * The internal default Settings4j Repository where all {@link org.settings4j.Settings} are stored.
     */
    private static SettingsRepository settingsRepository;
    static {
        /**
         * The root settings names itself as "root". However, the root settings cannot be retrieved
         * by name.
         */
        settingsRepository = new HierarchicalSettingsRepository(new DefaultSettings("root"));

        // read XML default Configuration to configure the repository
        URL url = ClasspathContentResolver.getResource(DEFAULT_XML_CONFIGURATION_FILE);

        // If we have a non-null url, then delegate the rest of the
        // configuration to the DOMConfigurator.configure method.
        if (url != null) {
            LOG.debug("Using URL [" + url + "] for automatic settings4j configuration.");
            try {
                DOMConfigurator.configure(url, settingsRepository);
            } catch (NoClassDefFoundError e) {
                LOG.warn("Error during default initialization", e);
            }
        } else {
            LOG.debug("Could not find resource: [" + DEFAULT_XML_CONFIGURATION_FILE + "].");
        }
    }

    /**
     * The internal default Settings4j Repository where all {@link org.settings4j.Settings} are stored.
     * 
     * @return The Settings4j Repository, Returns NEVER null put maybe a {@link NOPSettingsRepository}.
     */
    public static SettingsRepository getSettingsRepository() {
        if (settingsRepository == null) {
            settingsRepository = new NOPSettingsRepository();
            LOG.error("SettingsManager.settingsRepository was null likely due to error in class reloading, using NOPSettingsRepository.");
        }
        return settingsRepository;
    }

    /**
     * the root {@link org.settings4j.Settings} <br />
     * e.g.: Defined by the root-Tag inside the settings4j.xml
     * 
     * @return
     */
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

    /**
     * Checks, if the Settings-Object for the given key already exists.<br />
     * The difference to {@link #getSettings(Class)}: no Settings-Object will be created if it doesn't exists.
     * 
     * @param name The name of The {@link Settings}
     * @return the founded Settings Object or null.
     */
    public static Settings exists(final String name) {
        return getSettingsRepository().exists(name);
    }

    /**
     * return a List off all {@link org.settings4j.Settings} who are defind in this Repository.
     * 
     * @return the List of all defined Settings, returns NEVER null but maybe an empty list.
     */
    public static List getCurrentSettingsList() {
        return getSettingsRepository().getCurrentSettingsList();
    }
}
