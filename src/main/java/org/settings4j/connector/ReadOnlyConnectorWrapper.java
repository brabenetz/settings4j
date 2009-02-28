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

import org.settings4j.Connector;
import org.settings4j.Constants;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

public class ReadOnlyConnectorWrapper implements Connector {

    private Connector targetConnector;
    
    public ReadOnlyConnectorWrapper(Connector delegateConnector) {
        super();
        this.targetConnector = delegateConnector;
    }
    
    public int setContent(String key, byte[] value) {
        return Constants.SETTING_NOT_POSSIBLE;
    }

    public int setObject(String key, Object value) {
        return Constants.SETTING_NOT_POSSIBLE;
    }

    public int setString(String key, String value) {
        return Constants.SETTING_NOT_POSSIBLE;
    }

    
    /* ****************************
     * Delegating Methodes:
     * ****************************/
    
    public byte[] getContent(String key) {
        return targetConnector.getContent(key);
    }

    public Object getObject(String key) {
        return targetConnector.getObject(key);
    }

    public String getString(String key) {
        return targetConnector.getString(key);
    }


    public void addConnector(Connector connector) {
        targetConnector.addConnector(connector);
    }

    public void setContentResolver(ContentResolver contentResolver) {
        targetConnector.setContentResolver(contentResolver);
    }

    public void setObjectResolver(ObjectResolver objectResolver) {
        targetConnector.setObjectResolver(objectResolver);
    }

    public void init() {
        targetConnector.init();
    }

    public String getName() {
        return targetConnector.getName();
    }

    public void setName(String name) {
        targetConnector.setName(name);
    }

	public boolean isReadonly() {
		return true;
	}
}
