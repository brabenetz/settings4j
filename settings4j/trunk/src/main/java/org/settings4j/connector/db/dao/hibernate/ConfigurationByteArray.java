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

package org.settings4j.connector.db.dao.hibernate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

/**
 * Extends the org.hibernate.cfg.Configuration ( {@link Configuration}) with
 * one Methode to load a Hibernate Configuration-File from a ByteArray.
 * 
 * @author Harald.Brabenetz
 *
 */
public class ConfigurationByteArray extends Configuration {

    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(ConfigurationByteArray.class);
    
    private static final long serialVersionUID = 2770071843496670953L;
    
    /**
     * Use the mappings and properties specified in the given application
     * resource. The format of the resource is defined in
     * <tt>hibernate-configuration-3.0.dtd</tt>.
     * <p/>
     * 
     * @param resourceContent The Configuration Content
     * @param resourceKey The name to use in warning/error messages
     * @return A configuration configured via the byte[]
     * @throws HibernateException
     */
    public Configuration configure(byte[] resourceContent, String resourceKey) throws HibernateException {
        LOG.info( "configuring from resource: " + resourceKey );
        InputStream stream = new ByteArrayInputStream(resourceContent);
        return doConfigure( stream, resourceKey );
    }
}
