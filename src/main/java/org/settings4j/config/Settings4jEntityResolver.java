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
package org.settings4j.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * An {@link EntityResolver} specifically designed to return <code>settings4j.dtd</code> which is
 * embedded within the settings4j jar file.
 * 
 * @author Harald.Brabenetz
 */
public class Settings4jEntityResolver implements EntityResolver {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Settings4jEntityResolver.class);

    /** {@inheritDoc} */
    public InputSource resolveEntity(final String publicId, final String systemId) {
        if (systemId.endsWith("settings4j.dtd")) {
            final Class clazz = getClass();
            InputStream in = clazz.getResourceAsStream("/org/settings4j/config/settings4j.dtd");
            if (in == null) {
                LOG.warn("Could not find [settings4j.dtd] using [{}] class loader, parsed without DTD.",
                    clazz.getClassLoader());
                in = new ByteArrayInputStream(new byte[0]);
            }
            return new InputSource(in);
        }

        return null;
    }
}
