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

public class ContentHasChangedNotifierConnectorWrapper implements Connector{
    
    private AbstractConnector targetConnector;

    public ContentHasChangedNotifierConnectorWrapper(AbstractConnector targetConnector) {
        super();
        this.targetConnector = targetConnector;
    }

    /**
     * Notify the ObjectResolver if setContent was sucessfull.
     **/
    public int setContent(String key, byte[] value) {
        int result = targetConnector.setContent(key, value);
        if (result == Constants.SETTING_SUCCESS){
            targetConnector.getObjectResolver().notifyContentHasChanged(key);
        }
        return result;
    }
    
    /* ****************************
     * Delegating Methodes:
     * ****************************/
    
    public void addConnector(Connector connector) {
        targetConnector.addConnector(connector);
    }

    public byte[] getContent(String key) {
        return targetConnector.getContent(key);
    }

    public Object getObject(String key) {
        return targetConnector.getObject(key);
    }

    public String getString(String key) {
        return targetConnector.getString(key);
    }

    public void setContentResolver(ContentResolver contentResolver) {
        targetConnector.setContentResolver(contentResolver);
    }

    public int setObject(String key, Object value) {
        return targetConnector.setObject(key, value);
    }

    public void setObjectResolver(ObjectResolver objectResolver) {
        targetConnector.setObjectResolver(objectResolver);
    }

    public int setString(String key, String value) {
        return targetConnector.setString(key, value);
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
		return targetConnector.isReadonly();
	}
}
