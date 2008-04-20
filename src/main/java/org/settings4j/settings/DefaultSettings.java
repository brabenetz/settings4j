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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.settings4j.Connector;
import org.settings4j.Settings;
import org.settings4j.settings.helper.ConnectorIterator;

public class DefaultSettings extends Settings {
    
    String name;
    private List connectors = Collections.checkedList(Collections.synchronizedList(new ArrayList()), Connector.class);
    private Settings parent;

    public DefaultSettings(String name) {
        super();
        this.name = name;
    }

    public List getConnectors() {
        return Collections.unmodifiableList(connectors);
    }
    
    public void addConnector(Connector connector) {
        connectors.add(connector);
    }

    public void removeAllConnectors() {
        connectors.clear();
    }

    public byte[] getContent(String key) {
        byte[] result = null;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            result = connector.getContent(key);
            if (result != null){
                return result;
            }
        }
        return result;
    }

    public Object getObject(String key) {
        Object result = null;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            result = connector.getObject(key);
            if (result != null){
                return result;
            }
        }
        return result;
    }

    public String getString(String key) {
        String result = null;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            result = connector.getString(key);
            if (result != null){
                return result;
            }
        }
        return result;
    }

    public void setContent(String key, byte[] value) {
        int status;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            status = connector.setContent(key, value);
            if (status == Connector.SETTING_SUCCESS){
                return;
            }
        }
        throw new IllegalStateException("Conntent '" + key + "' cannot be writen. No writeable Connector found");
    }

    public void setObject(String key, Object value) {
        int status;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            status = connector.setObject(key, value);
            if (status == Connector.SETTING_SUCCESS){
                return;
            }
        }
        throw new IllegalStateException("Conntent '" + key + "' cannot be writen. No writeable Connector found");
    }

    public void setString(String key, String value) {
        int status;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            status = connector.setString(key, value);
            if (status == Connector.SETTING_SUCCESS){
                return;
            }
        }
        throw new IllegalStateException("Conntent '" + key + "' cannot be writen. No writeable Connector found");
    }

    public Settings getParent() {
        return parent;
    }

    public void setParent(Settings parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

}
