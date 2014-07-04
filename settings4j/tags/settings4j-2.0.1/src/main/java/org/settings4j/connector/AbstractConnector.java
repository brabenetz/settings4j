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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.settings4j.Connector;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

/**
 * Basic Connector implementations like getter and Setter of contentResolver, objectResolver.
 * 
 * @author Harald.Brabenetz
 *
 */
public abstract class AbstractConnector implements Connector {

    private String name;
    private ContentResolver contentResolver;
    private ObjectResolver objectResolver;
    private final List<Connector> connectors = Collections.synchronizedList(new ArrayList<Connector>());

    public List<Connector> getConnectors() {
        return Collections.unmodifiableList(this.connectors);
    }

    /** {@inheritDoc} */
    public void addConnector(final Connector connector) {
        this.connectors.add(connector);
    }

    protected ContentResolver getContentResolver() {
        return this.contentResolver;
    }

    public void setContentResolver(final ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    protected ObjectResolver getObjectResolver() {
        return this.objectResolver;
    }

    public void setObjectResolver(final ObjectResolver objectResolver) {
        this.objectResolver = objectResolver;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    public void init() {
        // Overwrite this methode if you want do something after all properties are set.
        // by default there is nothing to do
    }

}
