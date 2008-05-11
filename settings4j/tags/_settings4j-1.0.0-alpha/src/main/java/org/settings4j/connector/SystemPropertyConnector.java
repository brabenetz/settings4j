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


public class SystemPropertyConnector extends AbstractPropertyConnector {
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(SystemPropertyConnector.class);

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
    protected String getProperty(String key, String def) {
        try {
            return System.getProperty(key, def);
        } catch (Throwable e) { // MS-Java throws com.ms.security.SecurityExceptionEx
            LOG.debug("Was not allowed to read system property \"" + key + "\".");
            return def;
        }
    }
}
