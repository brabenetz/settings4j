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
package org.settings4j.settings.position;

import java.util.List;

import org.settings4j.Connector;
import org.settings4j.ConnectorPosition;

/**
 * Iterates thought the given {@link ConnectorPosition}s and returns the first valid position from the given implementations.
 *
 * @author brabenetz
 */
public class ConnectorPositionFirstValid implements ConnectorPosition {

    private final ConnectorPosition[] connectorPositions;

    /**
     * @param connectorPosition
     *        the {@link ConnectorPosition}s to search for a valid position.
     */
    public ConnectorPositionFirstValid(final ConnectorPosition... connectorPosition) {
        super();
        this.connectorPositions = connectorPosition;
    }

    @Override
    public int getPosition(final List<Connector> connectors) {
        for (final ConnectorPosition connectorPosition : this.connectorPositions) {
            final int position = connectorPosition.getPosition(connectors);
            if (position != UNKNOWN_POSITION) {
                return position;
            }
        }
        return UNKNOWN_POSITION;
    }

}
