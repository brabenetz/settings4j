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
package org.settings4j.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.settings4j.Connector;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

public abstract class AbstractConnector implements Connector{

    private String name;
    private ContentResolver contentResolver;
    private ObjectResolver objectResolver;
    private List connectors = Collections.checkedList(Collections.synchronizedList(new ArrayList()), Connector.class);
    
    public List getConnectors() {
        return Collections.unmodifiableList(connectors);
    }
    
    public void addConnector(Connector connector) {
        connectors.add(connector);
    }

    public void removeAllConnectors() {
        connectors.clear();
    }
    
    protected ContentResolver getContentResolver() {
        return contentResolver;
    }
    
    public void setContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }
    
    protected ObjectResolver getObjectResolver() {
        return objectResolver;
    }
    
    public void setObjectResolver(ObjectResolver objectResolver) {
        this.objectResolver = objectResolver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void init(){
        // Overwrite this methode if you want do something after all properties are set.
        // by default there is nothing to do
    }
}
