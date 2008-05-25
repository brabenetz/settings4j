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

import java.util.List;

import org.settings4j.Settings;
import org.settings4j.SettingsRepository;

public abstract class HierarchicalSettings extends Settings {
    /**
     * For internal use only
     * Returns the next Hirachical parent Settings.
     */
    public abstract HierarchicalSettings getParent();

    /**
     * For internal use only
     */
    public abstract void setParent(HierarchicalSettings parent);
    
    /**
     * For internal use only
     */
    public abstract void setSettingsRepository(SettingsRepository settingsRepository);

    /**
     * Return all Connectors of this Settings-Instance
     * 
     * @return
     */
    public abstract List getConnectors();
    
}
