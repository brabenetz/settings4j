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
/* ***************************************************************************
 * Copyright (c) 2012 Brabenetz Harald, Austria.
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

package org.settings4j.helper.configuration;

import org.apache.commons.configuration.Configuration;
import org.settings4j.connector.AbstractPropertyConnector;


/**
 * Adapter to use an <a href="http://commons.apache.org/proper/commons-configuration/">Apache Commons Configuration</a> as Settings4j connector.
 * <h3>Example Usage</h3>
 * <p>
 * Create a {@link org.apache.commons.configuration.XMLConfiguration} instance and add it to the Settings4j instance as Connector.
 * </p>
 * 
 * <pre>
 * String connectorName = "myCommonsConfigXmlConfigConnector";
 * Connector connector =  Settings4j.getSettings().getConnector(connectorName);
 * if (connector == null) {
 *     XMLConfiguration configuration = new XMLConfiguration(new File(.....));
 *
 *     connector = new ConfigurationToConnectorAdapter(connectorName, configuration);
 *
 *     // add the connecter after the last SystemPropertyConnector or add it as first connector.
 *     Settings4j.getSettings().addConnector(connector, //
 *         ConnectorPositions.firstValid(//
 *             ConnectorPositions.afterLast(SystemPropertyConnector.class), //
 *             ConnectorPositions.atFirst() // if no SystemPropertyConnector is configured.
 *             )//
 *         );
 * }
 * </pre>
 *
 * @author brabenetz
 */
public class ConfigurationToConnectorAdapter extends AbstractPropertyConnector {

    private final Configuration configuration;

    /**
     * @param name The unique name of this connector.
     * @param configuration The apache commons configuration instance to wrap and use as Settings4j connector.
     */
    public ConfigurationToConnectorAdapter(final String name, final Configuration configuration) {
        super();
        this.configuration = configuration;
        this.setName(name);
    }

    @Override
    protected String getProperty(final String key, final String defaultValue) {
        return this.configuration.getString(key, defaultValue);
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

}
