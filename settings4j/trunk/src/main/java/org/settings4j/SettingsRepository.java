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

public interface SettingsRepository {

    /**
     * the root {@link org.settings4j.Settings} <br />
     * e.g.: Defined by the root-Tag inside the settings4j.xml
     * 
     * @return
     */
    Settings getRootSettings();

    /**
     * Retrieve the appropriate {@link Settings} instance.
     */
    Settings getSettings(String name);

    /**
     * Retrieve the appropriate {@link Settings} instance.
     */
    Settings getSettings(String name, SettingsFactory factory);


    /**
     * Checks, if the Settings-Object for the given key already exists.<br />
     * The difference to {@link #getSettings(Class)}: no Settings-Object will be created if it doesn't exists.
     * 
     * @param name The name of The {@link Settings}
     * @return the founded Settings Object or null.
     */
    Settings exists(String name);

    /**
     * return a List off all {@link org.settings4j.Settings} who are defind in this Repository.
     * 
     * @return the List of all defined Settings, returns NEVER null.
     */
    List getCurrentSettingsList();
    
    /**
     * returns the Connectors Count who are in use.
     * A Connector where no connector-ref exists will not be listed. 
     * @return the Connectors Count who are in use
     */
    int getConnectorCount();
    
    /**
     * Will be set after parsing all Settings & Root-Tags
     * 
     * @param connectorCount
     */
    void addConnector(Connector connector);
    
    /**
     * <p>Remove all Settings, Connectors and other Objects from the Repository.
     * <p>Make it ready to fill it with a new Configuration.
     * 
     * <p>Example (maybe in an init()-Methode of your Application):
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
