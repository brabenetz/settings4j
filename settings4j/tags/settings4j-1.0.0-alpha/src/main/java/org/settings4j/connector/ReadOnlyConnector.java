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

public class ReadOnlyConnector extends AbstractConnector{

    private Connector delegateConnector;
    public ReadOnlyConnector(Connector delegateConnector) {
        super();
        this.delegateConnector = delegateConnector;
    }

    public byte[] getContent(String key) {
        return delegateConnector.getContent(key);
    }

    public Object getObject(String key) {
        return delegateConnector.getObject(key);
    }

    public String getString(String key) {
        return delegateConnector.getString(key);
    }

    public int setContent(String key, byte[] value) {
        return SETTING_NOT_POSSIBLE;
    }

    public int setObject(String key, Object value) {
        return SETTING_NOT_POSSIBLE;
    }

    public int setString(String key, String value) {
        return SETTING_NOT_POSSIBLE;
    }
}
