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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileConnector extends AbstractPropertyConnector {
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(PropertyFileConnector.class);
    

    Properties property;
    
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
        return property.getProperty(key, def);
    }

    public void setProperty(Properties property) {
        this.property = property;
    }

    public void setPropertyFromContent(byte[] content) {
        Properties tmpProperty = new Properties();
        try {
            tmpProperty.load(new ByteArrayInputStream(content));
            property = tmpProperty;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
