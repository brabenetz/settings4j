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
package org.settings4j.settings.position;

import java.util.List;

import org.settings4j.Connector;
import org.settings4j.ConnectorPosition;

/**
 * Iterates thought the existing connectors and return the position after the the connector with the given name.
 *
 * @author brabenetz
 */
public class ConnectorPositionAfterName implements ConnectorPosition {

    private final String connectorName;

    /**
     * @param connectorName
     *        The {@link Connector#getName()} to search for.
     */
    public ConnectorPositionAfterName(final String connectorName) {
        super();
        this.connectorName = connectorName;
    }

    @Override
    public int getPosition(final List<Connector> connectors) {
        int pos = 0;
        for (final Connector connector : connectors) {
            pos++;
            if (this.connectorName.equals(connector.getName())) {
                return pos;
            }
        }
        return UNKNOWN_POSITION;
    }

}
