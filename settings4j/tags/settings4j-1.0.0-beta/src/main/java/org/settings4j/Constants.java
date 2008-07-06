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

import java.util.ResourceBundle;

import org.settings4j.connector.ClasspathConnector;
import org.settings4j.connector.ReadOnlyConnectorWrapper;

public interface Constants {
    /**
     * The {@link ResourceBundle} for Settings4j (e.g.: Error Messages)
     */
    public static final ResourceBundle SETTINGS4J_MESSAGES = ResourceBundle.getBundle("org/settings4j/Messages");

    /**
     * Return value for:<br />
     * {@link ContentResolver#setContent(String, byte[])}<br />
     * {@link ObjectResolver#setObject(String, ContentResolver, Object)}<br />
     * {@link Connector#setString(String, String)}<br />
     * {@link Connector#setContent(String, byte[])}<br />
     * {@link Connector#setObject(String, Object)} <br />
     * <br />
     * If Settings is not possible (e.g.: {@link ReadOnlyConnectorWrapper} or {@link ClasspathConnector} )
     */
    public static final int SETTING_NOT_POSSIBLE = 0;
    
    /**
     * Return value for:<br />
     * {@link ContentResolver#setContent(String, byte[])}<br />
     * {@link ObjectResolver#setObject(String, ContentResolver, Object)}<br />
     * {@link Connector#setString(String, String)}<br />
     * {@link Connector#setContent(String, byte[])}<br />
     * {@link Connector#setObject(String, Object)} <br />
     * <br />
     * if Setting was successful: The value for the given key was replaced.
     */
    public static final int SETTING_SUCCESS = 1;
}
