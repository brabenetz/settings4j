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

public abstract class AbstractPropertyConnector extends AbstractConnector {

    public byte[] getContent(String key) {
        String path = getString(key);
        if (path != null && getContentResolver() != null) {
            return getContentResolver().getContent(path);
        } else {
            return null;
        }
    }

    public Object getObject(String key) {
        String path = getString(key);
        if (path != null && getObjectResolver() != null) {
            return getObjectResolver().getObject(path, getContentResolver());
        } else {
            return null;
        }
    }

    public String getString(String key) {
        return getProperty(key, null);
    }

    public int setContent(String key, byte[] value) {
        String path = getString(key);
        if (path != null && getContentResolver() != null) {
            return getContentResolver().setContent(path, value);
        } else {
            return Constants.SETTING_NOT_POSSIBLE;
        }
    }

    public int setObject(String key, Object value) {
        String path = getString(key);
        if (path != null && getObjectResolver() != null) {
            return getObjectResolver().setObject(path, getContentResolver(), value);
        } else {
            return Constants.SETTING_NOT_POSSIBLE;
        }
    }

    public int setString(String key, String value) {
        // System.setProperty is only temporary => so return NOT POSSIBLE
        return Constants.SETTING_NOT_POSSIBLE;
    }

    /**
     * Very similar to <code>System.getProperty</code> except that the {@link SecurityException}
     * is hidden.
     * 
     * @param key The key to search for.
     * @param def The default value to return.
     * @return the string value of the system property, or the default value if there is no property
     *         with that key.
     * 
     */
    protected abstract String getProperty(String key, String defaultValue);
}
