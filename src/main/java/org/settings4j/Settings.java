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

import org.settings4j.settings.SettingsManager;

public abstract class Settings {
    
    public abstract String getName();

    public abstract String getString(String key);

    public abstract byte[] getContent(String key);

    public abstract Object getObject(String key);

    public abstract void setString(String key, String value);

    public abstract void setContent(String key, byte[] value);

    public abstract void setObject(String key, Object value);

    public abstract List getConnectors();

    public abstract void addConnector(Connector connector);
    
    public abstract void removeAllConnectors();

    /**
     * For internal use only
     */
    public abstract Settings getParent();

    /**
     * For internal use only
     */
    public abstract void setParent(Settings parent);

    
    public static Settings getSettings(String name) {
        return SettingsManager.getSettings(name);
    }

    public static Settings getSettings(Class clazz) {
        return SettingsManager.getSettings(clazz);
    }

    public static Settings getSettings(final String name, final SettingsFactory factory) {
        return SettingsManager.getSettings(name, factory);
    }

    public static Settings getRootSettings() {
        return SettingsManager.getRootSettings();
    }
}
