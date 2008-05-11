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

public interface Connector {
    public static final int SETTING_NOT_POSSIBLE = ContentResolver.SETTING_NOT_POSSIBLE;
    public static final int SETTING_SUCCESS = ContentResolver.SETTING_SUCCESS;
    
    String getString(String key);
    byte[] getContent(String key);
    Object getObject(String key);
    
    int setString(String key, String value);
    int setContent(String key, byte[] value);
    int setObject(String key, Object value);
    
    //boolean isWriteable(String key);

    void setContentResolver(ContentResolver contentResolver);
    void setObjectResolver(ObjectResolver objectResolver);
    void addConnector(Connector connector);
}
