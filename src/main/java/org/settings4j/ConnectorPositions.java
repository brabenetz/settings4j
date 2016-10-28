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
package org.settings4j;

import org.settings4j.settings.position.ConnectorPositionAfterFirstClass;
import org.settings4j.settings.position.ConnectorPositionAfterLastClass;
import org.settings4j.settings.position.ConnectorPositionAfterName;
import org.settings4j.settings.position.ConnectorPositionAtFirst;
import org.settings4j.settings.position.ConnectorPositionAtLast;
import org.settings4j.settings.position.ConnectorPositionBeforeFirstClass;
import org.settings4j.settings.position.ConnectorPositionBeforeLastClass;
import org.settings4j.settings.position.ConnectorPositionBeforeName;
import org.settings4j.settings.position.ConnectorPositionFirstValid;


/**
 * ConnectorPostitions used for {@link Settings4jInstance#addConnector(Connector, ConnectorPosition)}.
 * <h3>Exampe Usage:</h3>
 *
 * <pre>
 * --------------------------------------
 * Connector myConnector = ...
 * if (Settings4j.getSettings().getConnector(myConnector.getName()) == null) {
 *   Settings4j.getSettings().addConnector(myConnector, ConnectorPositions.firstValid(//
 *       ConnectorPositions.afterLast(SystemPropertyConnector.class),
 *       ConnectorPositions.atFirst() // fallback if no SystemPropertyConnector exists.
 *     )
 *   );
 * }
 * --------------------------------------
 * </pre>
 * <p>
 * It is even more readable with static import:
 * </p>
 *
 * <pre>
 * --------------------------------------
 * import static org.settings4j.ConnectorPositions.*;
 * ..
 * Connector myConnector = ...
 * if (Settings4j.getSettings().getConnector(myConnector.getName()) == null) {
 *   Settings4j.getSettings().addConnector(myConnector, //
 *      firstValid(afterLast(SystemPropertyConnector.class), atFirst())
 *   );
 * }
 * --------------------------------------
 * </pre>
 *
 * @author brabenetz
 */
public final class ConnectorPositions {

    /** Hide constructor (Utility Pattern). */
    private ConnectorPositions() {
        super();
    }

    /**
     * @param connectorName The {@link Connector#getName()} to search for.
     * @return {@link ConnectorPositionBeforeName}
     */
    public static ConnectorPosition before(final String connectorName) {
        return new ConnectorPositionBeforeName(connectorName);
    }

    /**
     * @param connectorClass the connector class to search in the given connectors list.
     * @return {@link ConnectorPositionBeforeFirstClass}
     */
    public static ConnectorPosition beforeFirst(final Class<? extends Connector> connectorClass) {
        return new ConnectorPositionBeforeFirstClass(connectorClass);
    }

    /**
     * @param connectorClass the connector class to search in the given connectors list.
     * @return {@link ConnectorPositionBeforeLastClass}
     */
    public static ConnectorPosition beforeLast(final Class<? extends Connector> connectorClass) {
        return new ConnectorPositionBeforeLastClass(connectorClass);
    }

    /**
     * @param connectorName The {@link Connector#getName()} to search for.
     * @return {@link ConnectorPositionAfterName}
     */
    public static ConnectorPosition after(final String connectorName) {
        return new ConnectorPositionAfterName(connectorName);
    }

    /**
     * @param connectorClass the connector class to search in the given connectors list.
     * @return {@link ConnectorPositionAfterFirstClass}
     */
    public static ConnectorPosition afterFirst(final Class<? extends Connector> connectorClass) {
        return new ConnectorPositionAfterFirstClass(connectorClass);
    }

    /**
     * @param connectorClass the connector class to search in the given connectors list.
     * @return {@link ConnectorPositionAfterLastClass}
     */
    public static ConnectorPosition afterLast(final Class<? extends Connector> connectorClass) {
        return new ConnectorPositionAfterLastClass(connectorClass);
    }

    /**
     * @return {@link ConnectorPositionAtFirst} which always returns an valid position.
     */
    public static ConnectorPosition atFirst() {
        return new ConnectorPositionAtFirst();
    }

    /**
     * @return {@link ConnectorPositionAtLast} which always returns an valid position.
     */
    public static ConnectorPosition atLast() {
        return new ConnectorPositionAtLast();
    }

    /**
     * @param connectorPosition the {@link ConnectorPosition}s to search for a valid position.
     * @return ConnectorPositionFirstValid
     */
    public static ConnectorPosition firstValid(final ConnectorPosition... connectorPosition) {
        return new ConnectorPositionFirstValid(connectorPosition);
    }
}
