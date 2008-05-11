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
import org.settings4j.SettingsRepository;

/**
 * No-operation implementation of Settings used by NOPSettingsRepository.
 */
public class NOPSettings extends Settings {
    String name;
    SettingsRepository settingsRepository;
    
    public NOPSettings(String name, SettingsRepository settingsRepository) {
        super();
        this.name = name;
        this.settingsRepository = settingsRepository;
    }

    public void addConnector(Connector connector) {
    }

    public void removeAllConnectors() {
    }
    
    public List getConnectors() {
        return Collections.EMPTY_LIST;
    }

    public byte[] getContent(String key) {
        return null;
    }

    public String getName() {
        return name;
    }

    public Object getObject(String key) {
        return null;
    }

    public Settings getParent() {
        return null;
    }

    public String getString(String key) {
        return null;
    }

    public void setContent(String key, byte[] value) {
    }

    public void setObject(String key, Object value) {
    }

    public void setParent(Settings parent) {
    }

    public void setString(String key, String value) {
    }

    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }

    public void setSettingsRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

}
