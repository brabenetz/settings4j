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
package org.settings4j.settings.nop;

import java.util.Collections;
import java.util.List;

import org.settings4j.Connector;
import org.settings4j.Settings;
import org.settings4j.SettingsFactory;
import org.settings4j.SettingsRepository;

public class NOPSettingsRepository implements SettingsRepository {

    public Settings exists(String name) {
        return null;
    }

    public List getCurrentSettingsList() {
        return Collections.EMPTY_LIST;
    }

    public Settings getRootSettings() {
        return new NOPSettings("root", this);
    }

    public Settings getSettings(String name) {
        return new NOPSettings(name, this);
    }

    public Settings getSettings(String name, SettingsFactory factory) {
        return new NOPSettings(name, this);
    }

    public int getConnectorCount() {
        return 0;
    }

    public void setConnectorCount(int connectorCount) {
    }

    public void resetConfiguration() {
    }

    public void addConnector(Connector connector) {
    }

}
