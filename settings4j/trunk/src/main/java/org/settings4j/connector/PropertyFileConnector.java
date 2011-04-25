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


/**
 * The {@link Properties}-File implementation of an {@link org.settings4j.Connector}.
 * <p>
 * 
 * @author Harald.Brabenetz
 *
 */
public class PropertyFileConnector extends AbstractPropertyConnector {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(PropertyFileConnector.class);


    private Properties property;

    
    /** {@inheritDoc} */
    protected String getProperty(final String key, final String def) {
        return this.property.getProperty(key, def);
    }

    public void setProperty(final Properties property) {
        this.property = property;
    }

    /**
     * @param content The byte[] Content of a Property-File.
     */
    public void setPropertyFromContent(final byte[] content) {
        final Properties tmpProperty = new Properties();
        try {
            tmpProperty.load(new ByteArrayInputStream(content));
            this.property = tmpProperty;
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
