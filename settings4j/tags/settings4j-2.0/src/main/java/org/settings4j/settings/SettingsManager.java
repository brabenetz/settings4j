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

import javax.xml.parsers.FactoryConfigurationError;

import org.settings4j.Settings4jInstance;
import org.settings4j.Settings4jRepository;
import org.settings4j.config.DOMConfigurator;
import org.settings4j.contentresolver.ClasspathContentResolver;

/**
 * manage the {@link Settings4jRepository} which is used to store the configuration from the settings4j.xml.
 * 
 * @author Harald.Brabenetz
 */
public final class SettingsManager {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SettingsManager.class);

    /**
     * The Classpath-Location of the settings4j.xml who i readed by default. <br />
     * settings4j.xml
     */
    public static final String DEFAULT_XML_CONFIGURATION_FILE = "settings4j.xml";

    /**
     * The fallback settings4j.xml who is used if not settings4j.xml where found. <br />
     * org/settings4j/config/defaultsettings4j.xml
     */
    public static final String DEFAULT_FALLBACK_CONFIGURATION_FILE = "org/settings4j/config/defaultsettings4j.xml";

    /**
     * The internal default Settings4j Repository where all {@link org.settings4j.Settings4j} are stored.
     */
    private static Settings4jRepository settingsRepository = new DefaultSettingsRepository();
    static {
        initializeRepository(SettingsManager.DEFAULT_XML_CONFIGURATION_FILE);
        initializeRepositoryIfNecessary(); // default fallback if connectors are empty
    }

    /** Hide Constructor, Utility Pattern. */
    private SettingsManager() {
        super();
    }

    /**
     * The internal default Settings4j Repository where all {@link org.settings4j.Settings4j} are stored.
     * 
     * @return The Settings4j Repository, Returns NEVER null.
     */
    public static Settings4jRepository getSettingsRepository() {
        return settingsRepository;
    }

    /**
     * Retrieve the appropriate {@link org.settings4j.Settings4j} instance.
     * 
     * @return the appropriate {@link org.settings4j.Settings4j} instance.
     */
    public static Settings4jInstance getSettings() {
        initializeRepositoryIfNecessary();
        // Delegate the actual manufacturing of the settings to the settings repository.
        return getSettingsRepository().getSettings();
    }

    /**
     * Check if the repository must be configured with the defaul fallback settings4j.xml.
     */
    private static void initializeRepositoryIfNecessary() {
        if (getSettingsRepository().getConnectorCount() == 0) {
            // No connectors in hierarchy, add default-configuration.
            initializeRepository(SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);
        }
    }

    private static void initializeRepository(final String configurationFile) throws FactoryConfigurationError {
        LOG.debug("Using URL [{}] for automatic settings4j configuration.", configurationFile);

        final URL url = ClasspathContentResolver.getResource(configurationFile);

        // If we have a non-null url, then delegate the rest of the
        // configuration to the DOMConfigurator.configure method.
        if (url != null) {
            LOG.info("The settings4j will be configured with the config: {}", url);
            try {
                DOMConfigurator.configure(url, getSettingsRepository());
            } catch (final NoClassDefFoundError e) {
                LOG.warn("Error during initialization " + configurationFile, e);
            }
        } else {
            if (SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE.equals(configurationFile)) {
                LOG.error("Could not find resource: [{}].", configurationFile);
            } else {
                LOG.debug("Could not find resource: [{}]. Use default fallback.", configurationFile);
            }
        }
    }
}
