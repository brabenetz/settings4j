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
package org.settings4j.settings.helper;

import org.settings4j.Settings;
import org.settings4j.settings.HierarchicalSettings;

/**
 * Search the current {@link Settings}-Object and all parent
 * Settings until a mapped Key where foud.
 * 
 * @author hbrabenetz
 *
 */
public class InheritedMappedKeys {
    
    private HierarchicalSettings settings;
    
    /**
     * 
     * @param settings The {@link Settings}-Object from where the search starts
     */
    public InheritedMappedKeys(HierarchicalSettings settings) {
        super();
        this.settings = settings;
    }
    
    /**
     * Search the current {@link Settings}-Object and all parent
     * Settings until a mapped Key where foud.
     * 
     * If the current Settings and all parent settings doesn't have a mapping
     * for the given key, null will be returned.
     * 
     * @param key
     * @return
     */
    public String get(Object key) {
        HierarchicalSettings settings = this.settings;
        while (settings != null){
            // get the mapped key if it exists.
            Object value = settings.getMapping().get(key);
            
            // return the founded value
            if (value != null){
                return (String)value;
            }
            
            // if this settings doesn't have a mapping,
            // maybe the parent settings have a mapping.
            settings = settings.getParent();
        }
        return null;
    }

    
}
