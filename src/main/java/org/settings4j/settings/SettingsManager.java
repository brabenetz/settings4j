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

import org.settings4j.Settings4jFactory;
import org.settings4j.Settings4jInstance;
import org.settings4j.Settings4jRepository;
import org.settings4j.config.DOMConfigurator;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.settings.nop.NOPSettingsRepository;

/**
 * managed The {@link Settings4jRepository} . This {@link Settings4jRepository} is used to store the configuration from
 * the settings4j.xml.
 * 
 * @author Harald.Brabenetz
 */
public final class SettingsManager {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(SettingsManager.class);

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
    private static Settings4jRepository settingsRepository;
    static {

        /**
         * The root settings names itself as "root". However, the root settings cannot be retrieved by name.
         */
        settingsRepository = new DefaultSettingsRepository();

        // read XML default Configuration to configure the repository
        final URL url = ClasspathContentResolver.getResource(DEFAULT_XML_CONFIGURATION_FILE);

        // If we have a non-null url, then delegate the rest of the
        // configuration to the DOMConfigurator.configure method.
        if (url != null) {
            LOG.debug("Using URL [" + url + "] for automatic settings4j configuration.");
            try {
                DOMConfigurator.configure(url, settingsRepository);
            } catch (final NoClassDefFoundError e) {
                LOG.warn("Error during default initialization", e);
            }
        } else {
            LOG.debug("Could not find resource: [" + DEFAULT_XML_CONFIGURATION_FILE + "].");
        }
    }

    /** Hide Constructor, Utility Pattern. */
    private SettingsManager() {
        super();
    }

    /**
     * The internal default Settings4j Repository where all {@link org.settings4j.Settings4j} are stored.
     * 
     * @return The Settings4j Repository, Returns NEVER null put maybe a {@link NOPSettingsRepository}.
     */
    public static Settings4jRepository getSettingsRepository() {
        if (settingsRepository == null) {
            settingsRepository = new NOPSettingsRepository();
            LOG.error("SettingsManager.settingsRepository was null likely due to error in class reloading, " //
                + "using the NOPSettingsRepository.");
        }
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
     * Retrieve the appropriate {@link org.settings4j.Settings4j} instance or create it with the give factory if doesn'r
     * already exist.
     * 
     * @param factory The factory to create a {@link Settings4jInstance}.
     * @return the appropriate {@link org.settings4j.Settings4j} instance.
     * @deprecated will be removed. Call {@link #getSettingsRepository()}
     *      and {@link Settings4jRepository#getSettings(Settings4jFactory)} instead.
     */
    public static Settings4jInstance getSettings(final Settings4jFactory factory) {
        // TODO brabenetz 26.03.2012 : remove this method after release 1.0
        initializeRepositoryIfNecessary();
        // Delegate the actual manufacturing of the settings to the settings repository.
        return getSettingsRepository().getSettings(factory);
    }

    /**
     * Check if the repository must be configured with the defaul fallback settings4j.xml.
     */
    private static void initializeRepositoryIfNecessary() {
        if (getSettingsRepository().getConnectorCount() == 0) {
            // No connectors in hierarchy, warn user and add default-configuration.
            LOG.info("The settings4j will be configured with the default-fallback-config: "
                + SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);

            final URL url = ClasspathContentResolver.getResource(SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);

            // If we have a non-null url, then delegate the rest of the
            // configuration to the DOMConfigurator.configure method.
            if (url != null) {
                LOG.debug("Using URL [" + url + "] for automatic settings4j fallback configuration.");
                try {
                    DOMConfigurator.configure(url, getSettingsRepository());
                } catch (final NoClassDefFoundError e) {
                    LOG.warn("Error during default fallback initialization", e);
                }
            } else {
                LOG.fatal("Could not find resource: [" + SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE + "].");
            }
        }
    }
}
