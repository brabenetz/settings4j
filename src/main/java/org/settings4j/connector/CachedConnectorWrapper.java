/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2015 Brabenetz Harald, Austria
 * ===============================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.settings4j.connector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.settings4j.Connector;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

/**
 * Wrap a Connector and caches all Values.
 * <p>
 * This wrapper will be used if you add in your settings4j.xml the cached="true" Attribute to the Connector TAG.
 * </p>
 *
 * @author Harald.Brabenetz
 */
public class CachedConnectorWrapper implements Connector {

    private final Connector targetConnector;

    private final Map<String, String> cachedStrings = Collections.synchronizedMap(new HashMap<String, String>());
    private final Map<String, byte[]> cachedContents = Collections.synchronizedMap(new HashMap<String, byte[]>());
    private final Map<String, Object> cachedObjects = Collections.synchronizedMap(new HashMap<String, Object>());


    /**
     * @param targetConnector The connector to wrap.
     */
    public CachedConnectorWrapper(final Connector targetConnector) {
        super();
        Validate.notNull(targetConnector, "CachedConnectorWrapper needs a Connector Object");
        this.targetConnector = targetConnector;
    }

    @Override
    public byte[] getContent(final String key) {
        if (this.cachedContents.containsKey(key)) {
            return this.cachedContents.get(key);
        }

        final byte[] result = this.targetConnector.getContent(key);
        this.cachedContents.put(key, result);
        return result;
    }

    @Override
    public Object getObject(final String key) {
        if (this.cachedObjects.containsKey(key)) {
            return this.cachedObjects.get(key);
        }

        final Object result = this.targetConnector.getObject(key);
        this.cachedObjects.put(key, result);
        return result;
    }

    @Override
    public String getString(final String key) {
        if (this.cachedStrings.containsKey(key)) {
            return this.cachedStrings.get(key);
        }

        final String result = this.targetConnector.getString(key);
        this.cachedStrings.put(key, result);
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

    /*
     * Delegating Methodes:
     */

    @Override
    public void addConnector(final Connector connector) {
        this.targetConnector.addConnector(connector);
    }

    @Override
    public void setContentResolver(final ContentResolver contentResolver) {
        this.targetConnector.setContentResolver(contentResolver);
    }

    @Override
    public void setObjectResolver(final ObjectResolver objectResolver) {
        this.targetConnector.setObjectResolver(objectResolver);
    }

    @Override
    public void init() {
        this.targetConnector.init();
    }

    @Override
    public String getName() {
        return this.targetConnector.getName();
    }

    @Override
    public void setName(final String name) {
        this.targetConnector.setName(name);
    }

}
