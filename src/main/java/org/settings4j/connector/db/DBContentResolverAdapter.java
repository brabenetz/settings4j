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

package org.settings4j.connector.db;

import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

/**
 * This implementation can Use a {@link AbstractDBConnector} as ContentResolver.<br />
 * <br />
 * This is needed for {@link ObjectResolver}:<br />
 * The ObjectResolver takes the ContentReolvers from the Connector
 * to transform the content into an Object<br />
 * <br />
 * This implementation of a ContentResolver has no default-Constructor.
 * So it is not possible to use it in a settings4j.xml configuration.
 * 
 * @author hbrabenetz
 *
 */
public class DBContentResolverAdapter implements ContentResolver {
    AbstractDBConnector connector;

    public DBContentResolverAdapter(AbstractDBConnector connector) {
        super();
        this.connector = connector;
    }

    /** {@inheritDoc} */
    public void addContentResolver(ContentResolver contentResolver) {
        throw new UnsupportedOperationException("DBContentResolverAdapter cannot add other ContentResolvers");
    }

    /** {@inheritDoc} */
    public byte[] getContent(String key) {
        return connector.getContent(key);
    }

    /** {@inheritDoc} */
    public int setContent(String key, byte[] value) {
        return connector.setContent(key, value);
    }
}
