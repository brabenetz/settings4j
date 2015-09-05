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
package org.settings4j;

import java.util.List;

/**
 * Interface to show on which position a Connector should be added with {@link List#add(int, Object)}.
 * <h3>Default Implementations</h3>
 * <p>
 * The easiest way to get an instance of a ConnectorPosition implementation is to use the Factory Methods from {@link ConnectorPositions}:
 * </p>
 *
 * <pre>
 * // get the ConnectorPosition instance for the postion after the last
 * // {@link org.settings4j.connector.SystemPropertyConnector}
 * ConnectorPositions.afterLast(SystemPropertyConnector.class);
 * </pre>
 *
 * @author brabenetz
 */
public interface ConnectorPosition {

    /**
     * the position value it the position cannot be determined.
     */
    int UNKNOWN_POSITION = -1;

    /**
     * @param connectors the existing list of connectors.
     * @return the position where the new Connector should be added.
     */
    int getPosition(List<Connector> connectors);
}
