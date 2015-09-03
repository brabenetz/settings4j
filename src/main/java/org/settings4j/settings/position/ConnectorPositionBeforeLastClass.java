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

package org.settings4j.settings.position;

import java.util.List;

import org.settings4j.Connector;
import org.settings4j.ConnectorPosition;

/**
 * Iterates thought the existing connectors and return the position before the last occurrence of the given Connector-class.
 *
 * @author brabenetz
 */
public class ConnectorPositionBeforeLastClass implements ConnectorPosition {

    private final Class<? extends Connector> connectorClass;

    /**
     * @param connectorClass
     *        the connector class to search in the given connectors list.
     */
    public ConnectorPositionBeforeLastClass(final Class<? extends Connector> connectorClass) {
        super();
        this.connectorClass = connectorClass;
    }

    /** {@inheritDoc} */
    public int getPosition(final List<Connector> connectors) {
        int pos = 0;
        int lastPos = UNKNOWN_POSITION;
        for (final Connector connector : connectors) {
            if (this.connectorClass.equals(connector.getClass())) {
                lastPos = pos;
            }
            pos++;
        }
        return lastPos;
    }

}
