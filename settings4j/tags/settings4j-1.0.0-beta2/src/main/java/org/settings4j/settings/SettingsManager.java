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

import org.settings4j.Settings;
import org.settings4j.SettingsFactory;
import org.settings4j.SettingsInstance;
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
        settingsRepository = new DefaultSettingsRepository();

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
     * Retrieve the appropriate {@link Settings} instance.
     */
    public static SettingsInstance getSettings() {
    	initializeRepositoryIfNecessary();
        // Delegate the actual manufacturing of the settings to the settings repository.
        return getSettingsRepository().getSettings();
    }

    /**
     * Retrieve the appropriate {@link Settings} instance.
     */
    public static SettingsInstance getSettings(final SettingsFactory factory) {
    	initializeRepositoryIfNecessary();
        // Delegate the actual manufacturing of the settings to the settings repository.
        return getSettingsRepository().getSettings(factory);
    }

    /**
     * Check if the repository must be configured with the defaul fallback settings4j.xml.
     */
    private static void initializeRepositoryIfNecessary(){
        if (getSettingsRepository().getConnectorCount() == 0){
            // No connectors in hierarchy, warn user and add default-configuration.
            LOG.warn("The settings4j will be configured with the default-fallback-config: " + SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);
            
            URL url = ClasspathContentResolver.getResource(SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);

            // If we have a non-null url, then delegate the rest of the
            // configuration to the DOMConfigurator.configure method.
            if (url != null) {
                LOG.debug("Using URL [" + url + "] for automatic settings4j fallback configuration.");
                try {
                    DOMConfigurator.configure(url, getSettingsRepository());
                } catch (NoClassDefFoundError e) {
                    LOG.warn("Error during default fallback initialization", e);
                }
            } else {
                LOG.fatal("Could not find resource: [" + SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE + "].");
            }
        }
    }
}