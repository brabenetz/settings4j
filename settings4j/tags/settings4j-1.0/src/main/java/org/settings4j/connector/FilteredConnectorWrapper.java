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
import org.settings4j.ContentResolver;
import org.settings4j.Filter;
import org.settings4j.ObjectResolver;

/**
 * Wrapper to add a {@link Filter} which is used before the given {@link Connector} is called.
 * 
 * @author Harald.Brabenetz
 */
public class FilteredConnectorWrapper implements Connector {

    private final Connector targetConnector;
    private final Filter filter;

    /**
     * @param targetConnector The Connect where the settings should be read if the Filter allows it.
     * @param filter the {@link Filter} which defines if an key should be read from the given Connector.
     */
    public FilteredConnectorWrapper(final Connector targetConnector, final Filter filter) {
        super();
        if (targetConnector == null) {
            throw new IllegalArgumentException("FilteredConnectorWrapper needs a Connector Object");
        }
        if (filter == null) {
            throw new IllegalArgumentException("FilteredConnectorWrapper needs a Filter Object");
        }
        this.targetConnector = targetConnector;
        this.filter = filter;
    }

    /** {@inheritDoc} */
    public void addConnector(final Connector connector) {
        this.targetConnector.addConnector(connector);
    }

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        if (!this.filter.isValid(key)) {
            return null;
        }
        return this.targetConnector.getContent(key);
    }

    /** {@inheritDoc} */
    public String getName() {
        return this.targetConnector.getName();
    }

    /** {@inheritDoc} */
    public Object getObject(final String key) {
        if (!this.filter.isValid(key)) {
            return null;
        }
        return this.targetConnector.getObject(key);
    }

    /** {@inheritDoc} */
    public String getString(final String key) {
        if (!this.filter.isValid(key)) {
            return null;
        }
        return this.targetConnector.getString(key);
    }

    /** {@inheritDoc} */
    public void init() {
        this.targetConnector.init();
    }

    /** {@inheritDoc} */
    public void setContentResolver(final ContentResolver contentResolver) {
        this.targetConnector.setContentResolver(contentResolver);
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.targetConnector.setName(name);
    }

    /** {@inheritDoc} */
    public void setObjectResolver(final ObjectResolver objectResolver) {
        this.targetConnector.setObjectResolver(objectResolver);
    }


}
