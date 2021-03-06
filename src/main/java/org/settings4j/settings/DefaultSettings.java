/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2016 Brabenetz Harald, Austria
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
package org.settings4j.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.settings4j.Connector;
import org.settings4j.ConnectorPosition;
import org.settings4j.ConnectorPositions;
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
    private Map<String, String> mapping;

    @Override
    public List<Connector> getConnectors() {
        return Collections.unmodifiableList(this.connectors);
    }

    @Override
    public Connector getConnector(final String connectorName) {
        return this.connectorMap.get(connectorName);
    }

    @Override
    public void addConnector(final Connector connector) {
        addConnector(connector, ConnectorPositions.atLast());
    }

    @Override
    public void addConnector(final Connector connector, final ConnectorPosition position) {
        final int pos = position.getPosition(this.connectors);
        Validate.isTrue(pos != ConnectorPosition.UNKNOWN_POSITION,
            "No valid Position found to add the given connector.");
        Validate.isTrue(this.connectorMap.get(connector.getName()) == null, //
            "A connector with the given name '%s' already exists!", connector.getName());
        this.connectors.add(pos, connector);
        this.connectorMap.put(connector.getName(), connector);

    }

    @Override
    public void removeAllConnectors() {
        this.connectors.clear();
        this.connectorMap.clear();
    }

    @Override
    public byte[] getContent(final String key) {
        final String mappedKey = mappedKey(key);
        byte[] result = null;
        for (final Connector connector : this.connectors) {
            result = connector.getContent(mappedKey);
            if (result != null) {
                logDebugFoundValueForKey("Content", key, connector);
                return result;
            }
        }
        return result;
    }

    @Override
    public Object getObject(final String key) {
        final String mappedKey = mappedKey(key);
        Object result = null;
        for (final Connector connector : this.connectors) {
            result = connector.getObject(mappedKey);
            if (result != null) {
                logDebugFoundValueForKey("Object", key, connector);
                return result;
            }
        }
        return result;
    }

    @Override
    public String getString(final String key) {
        final String mappedKey = mappedKey(key);
        String result = null;
        for (final Connector connector : this.connectors) {
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
     * </p>
     *
     * <pre>
     * Example:
     * &lt;mapping name="defaultMapping"&gt;
     *     &lt;entry key="com/mycompany/moduleX/datasource" ref-key="global/datasource"/&gt;
     *     &lt;entry key="com/mycompany/moduleY/datasource" ref-key="global/datasource"/&gt;
     * &lt;/mapping&gt;
     * </pre>
     * <p>
     * Now you need only configure only one dataSource for your App.
     * </p>
     *
     * @param key
     *        the Key to map.
     * @return The key which must be configured for the given Key.
     */
    private String mappedKey(final String key) {
        final String mappedKey = this.getMapping().get(key);
        if (StringUtils.isEmpty(mappedKey)) {
            return key;
        }
        // else
        return mappedKey;

    }

    @Override
    public Map<String, String> getMapping() {
        if (this.mapping == null) {
            this.mapping = new HashMap<String, String>();
        }
        return this.mapping;
    }

    @Override
    public void setMapping(final Map<String, String> mapping) {
        this.mapping = mapping;
    }

    private void logDebugFoundValueForKey(final String type, final String key, final Connector connector) {
        LOG.debug("Found {} for Key '{}' in connector '{}' ({})", //
            type, key, connector.getName(), connector.getClass().getName());
    }
}
