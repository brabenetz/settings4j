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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.settings4j.Connector;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

public class CachedConnectorWrapper implements Connector{
    private Connector targetConnector;
    
    private Map cachedStrings = Collections.checkedMap(Collections.synchronizedMap(new HashMap()), String.class, String.class);
    private Map cachedContents = Collections.checkedMap(Collections.synchronizedMap(new HashMap()), String.class, byte[].class);
    private Map cachedObjects = Collections.checkedMap(Collections.synchronizedMap(new HashMap()), String.class, Object.class);
    

    public CachedConnectorWrapper(Connector targetConnector) {
        super();
        this.targetConnector = targetConnector;
    }

    public byte[] getContent(String key) {
        byte[] result = (byte[])cachedContents.get(key);
        if (result != null){
            return result;
        }
        
        result = targetConnector.getContent(key);

        if (result != null){
            cachedContents.put(key, result);
        }
        return result;
    }
    
    public Object getObject(String key) {
        Object result = (Object)cachedObjects.get(key);
        if (result != null){
            return result;
        }
        
        result = targetConnector.getObject(key);

        if (result != null){
            cachedObjects.put(key, result);
        }
        return result;
    }
    
    public String getString(String key) {
        String result = (String)cachedStrings.get(key);
        if (result != null){
            return result;
        }
        
        result = targetConnector.getString(key);

        if (result != null){
            cachedStrings.put(key, result);
        }
        return result;
    }
    
    public int setContent(String key, byte[] value) {
        int result = targetConnector.setContent(key, value);
        if (result == SETTING_SUCCESS){
            cachedContents.put(key, value);
        }
        return result;
    }
    
    public int setObject(String key, Object value) {
        int result = targetConnector.setObject(key, value);
        if (result == SETTING_SUCCESS){
            cachedObjects.put(key, value);
        }
        return result;
    }
    
    public int setString(String key, String value) {
        int result = targetConnector.setString(key, value);
        if (result == SETTING_SUCCESS){
            cachedStrings.put(key, value);
        }
        return result;
    }

    
    /* ****************************
     * Delegating Methodes:
     * ****************************/
    
    public void addConnector(Connector connector) {
        targetConnector.addConnector(connector);
    }
    public void setContentResolver(ContentResolver contentResolver) {
        targetConnector.setContentResolver(contentResolver);
    }
    public void setObjectResolver(ObjectResolver objectResolver) {
        targetConnector.setObjectResolver(objectResolver);
    }

}
