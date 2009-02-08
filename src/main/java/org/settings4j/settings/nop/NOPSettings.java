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
import java.util.Map;

import org.settings4j.Connector;
import org.settings4j.SettingsRepository;

/**
 * No-operation implementation of Settings used by NOPSettingsRepository.
 */
public class NOPSettings implements org.settings4j.SettingsInstance {
    SettingsRepository settingsRepository;
    
    public NOPSettings(SettingsRepository settingsRepository) {
        super();
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

    public Object getObject(String key) {
        return null;
    }

    public String getString(String key) {
        return null;
    }

    public void setContent(String key, byte[] value, String connectorName) {
    }

    public void setObject(String key, Object value, String connectorName) {
    }

    public void setString(String key, String value, String connectorName) {
    }

    public Map getMapping() {
        return Collections.EMPTY_MAP;
    }

    public void setMapping(Map mapping) {
    }
}
