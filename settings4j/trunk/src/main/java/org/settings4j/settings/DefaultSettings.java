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
package org.settings4j.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.settings4j.Connector;
import org.settings4j.Settings4jInstance;

/**
 * The default Settings Object.
 * 
 * @author Harald.Brabenetz
 */
public class DefaultSettings implements Settings4jInstance {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultSettings.class);

    private final List<Connector> connectors = Collections.synchronizedList(new ArrayList<Connector>());
    private final Map<String, Connector> connectorMap = Collections.synchronizedMap(new HashMap<String, Connector>());
    private Map mapping;

    /** {@inheritDoc} */
    public List<Connector> getConnectors() {
        return Collections.unmodifiableList(this.connectors);
    }

    /** {@inheritDoc} */
    public Connector getConnector(final String connectorName) {
        return this.connectorMap.get(connectorName);
    }

    /** {@inheritDoc} */
    public void addConnector(final Connector connector) {
        this.connectors.add(connector);
        this.connectorMap.put(connector.getName(), connector);
    }

    /** {@inheritDoc} */
    public void removeAllConnectors() {
        this.connectors.clear();
        this.connectorMap.clear();
    }

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        final String mappedKey = mappedKey(key);
        byte[] result = null;
        for (Connector connector : this.connectors) {
            result = connector.getContent(mappedKey);
            if (result != null) {
                logDebugFoundValueForKey("Content", key, connector);
                return result;
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    public Object getObject(final String key) {
        final String mappedKey = mappedKey(key);
        Object result = null;
        for (Connector connector : this.connectors) {
            result = connector.getObject(mappedKey);
            if (result != null) {
                logDebugFoundValueForKey("Object", key, connector);
                return result;
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    public String getString(final String key) {
        final String mappedKey = mappedKey(key);
        String result = null;
        for (Connector connector : this.connectors) {
            result = connector.getString(mappedKey);
            if (result != null) {
                logDebugFoundValueForKey("String", key, connector);
                return result;
            }
        }
        return result;
    }

    /**
     * Get the mapped Key.
     * <p>
     * if some Sub-Modules of your App defines separated Keys for the DataSource, you can refer it the the same Key:
     * 
     * <pre>
     * Example:
     * &lt;mapping name="defaultMapping"&gt;
     *     &lt;entry key="com/mycompany/moduleX/datasource" ref-key="global/datasource"/&gt;
     *     &lt;entry key="com/mycompany/moduleY/datasource" ref-key="global/datasource"/&gt;
     * &lt;/mapping&gt;
     * </pre>
     * 
     * Now you need only configure only one dataSource for your App.
     * 
     * @param key the Key to map.
     * @return The key which must be configured for the given Key.
     */
    private String mappedKey(final String key) {
        final String mappedKey = (String) this.getMapping().get(key);
        if (StringUtils.isEmpty(mappedKey)) {
            return key;
        }
        //else
        return mappedKey;
        
    }

    /** {@inheritDoc} */
    public Map getMapping() {
        if (this.mapping == null) {
            this.mapping = new HashMap();
        }
        return this.mapping;
    }

    /** {@inheritDoc} */
    public void setMapping(final Map mapping) {
        this.mapping = mapping;
    }

    private void logDebugFoundValueForKey(final String type, final String key, final Connector connector) {
        LOG.debug("Found {} for Key '{}' in connector '{}' ({})", //
            type, key, connector.getName(), connector.getClass().getName());
    }
}
