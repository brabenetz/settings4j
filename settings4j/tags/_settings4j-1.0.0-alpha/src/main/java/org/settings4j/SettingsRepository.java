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
    Settings getRootSettings();
    Settings getSettings(String name);
    Settings getSettings(String name, SettingsFactory factory);
    Settings exists(String name);
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
    void setConnectorCount(int connectorCount);
}
