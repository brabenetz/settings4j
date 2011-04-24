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

import org.settings4j.settings.SettingsManager;

/**
 * Holder for a single {@link Settings4jInstance}.
 * 
 * @author Harald.Brabenetz
 *
 */
public interface Settings4jRepository {

    /**
     * Retrieve the appropriate {@link Settings4j} instance.
     * 
     * @return the appropriate {@link Settings4jInstance}
     */
	Settings4jInstance getSettings();

    /**
     * Retrieve the appropriate {@link Settings4j} instance or create it with the give factory if doesn'r already exist.
     * 
     * @param factory The factory to create a {@link Settings4jInstance}.
     * @return the appropriate {@link Settings4jInstance}
     */
    Settings4jInstance getSettings(Settings4jFactory factory);

    /**
     * Return the Connector Count.<br />
     * If the connector count is 0. the Settings will be reinitialized with
     * the default-fallback-config in {@link SettingsManager#getSettings()}
     * 
     * @return The Connector Count.
     */
    int getConnectorCount();
    
    /**
     * <p>Remove the Settings, Connectors and other Objects from the Repository.
     * <p>Make it ready to fill it with a new Configuration.
     * 
     * <p>Example (maybe in an init()-Method of your Application):
     * <pre>
     * // clear Settings from "settings4j.xml"
     * SettingsRepository settingsRepository = SettingsManager.getSettingsRepository();
     * settingsRepository.resetConfiguration();
     * 
     * // read XML Custome to configure the repository
     * URL url = ClasspathContentResolver.getResource("customizedSettings4j.xml");
     * DOMConfigurator domConfigurator = new DOMConfigurator(settingsRepository);
     * domConfigurator.doConfigure(url);
     * </pre>
     */
    void resetConfiguration();
}
