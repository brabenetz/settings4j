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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.settings4j.Connector;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

/**
 * Wrap a Connector and caches all Values.
 * <p>
 * This wrapper will be used if you add in your settings4j.xml the cached="true" Attribute to the Connector TAG.
 * 
 * @author Harald.Brabenetz
 *
 */
public class CachedConnectorWrapper implements Connector {

    private final Connector targetConnector;

    private final Map cachedStrings = Collections.synchronizedMap(new HashMap());
    private final Map cachedContents = Collections.synchronizedMap(new HashMap());
    private final Map cachedObjects = Collections.synchronizedMap(new HashMap());


    /**
     * @param targetConnector The connector to wrap.
     */
    public CachedConnectorWrapper(final Connector targetConnector) {
        super();
        this.targetConnector = targetConnector;
    }

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        byte[] result = (byte[]) this.cachedContents.get(key);
        if (result != null) {
            return result;
        }

        result = this.targetConnector.getContent(key);

        if (result != null) {
            this.cachedContents.put(key, result);
        }
        return result;
    }

    /** {@inheritDoc} */
    public Object getObject(final String key) {
        Object result = this.cachedObjects.get(key);
        if (result != null) {
            return result;
        }

        result = this.targetConnector.getObject(key);

        if (result != null) {
            this.cachedObjects.put(key, result);
        }
        return result;
    }

    /** {@inheritDoc} */
    public String getString(final String key) {
        String result = (String) this.cachedStrings.get(key);
        if (result != null) {
            return result;
        }

        result = this.targetConnector.getString(key);

        if (result != null) {
            this.cachedStrings.put(key, result);
        }
        return result;
    }

    /**
     * @param key the key to clear from all caches.
     */
    public void clearCachedValue(final String key) {
        this.cachedStrings.remove(key);
        this.cachedContents.remove(key);
        this.cachedObjects.remove(key);
    }

    /* ****************************
     * Delegating Methodes: ***************************
     */

    /** {@inheritDoc} */
    public void addConnector(final Connector connector) {
        this.targetConnector.addConnector(connector);
    }

    /** {@inheritDoc} */
    public void setContentResolver(final ContentResolver contentResolver) {
        this.targetConnector.setContentResolver(contentResolver);
    }

    /** {@inheritDoc} */
    public void setObjectResolver(final ObjectResolver objectResolver) {
        this.targetConnector.setObjectResolver(objectResolver);
    }

    /** {@inheritDoc} */
    public void init() {
        this.targetConnector.init();
    }

    /** {@inheritDoc} */
    public String getName() {
        return this.targetConnector.getName();
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.targetConnector.setName(name);
    }

}
