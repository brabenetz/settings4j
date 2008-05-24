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

import java.util.Iterator;

import org.settings4j.settings.HierarchicalSettings;

public class ConnectorIterator implements Iterator {
    private HierarchicalSettings settings;
    Iterator connectorIterator;
    public ConnectorIterator(HierarchicalSettings settings) {
        super();
        this.settings = settings;
        connectorIterator = settings.getConnectors().iterator();
    }

    public boolean hasNext() {
        if (connectorIterator.hasNext()) return true;
        
        HierarchicalSettings settingsTemp = settings;
        while (settingsTemp.getParent() != null){
            settingsTemp = settingsTemp.getParent();
            if (settingsTemp.getConnectors().iterator().hasNext()) return true;
        }
        
        return false;
    }

    public Object next() {
        if (connectorIterator.hasNext()) return connectorIterator.next();
        
        while (settings.getParent() != null){
            settings = settings.getParent();
            connectorIterator = settings.getConnectors().iterator();
            if (connectorIterator.hasNext()){
                return connectorIterator.next();
            }
        }
        
        return connectorIterator.next(); // throws NoSuchElement Exception
        
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
    
}
