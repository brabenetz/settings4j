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

package org.settings4j.exception;

public class NoWriteableConnectorFoundException extends Settings4jException {

    public static final String NO_WRITEABLE_CONNECTOR_FOUND_1 = "error.no.writeable.connector.found";
    
    private static final long serialVersionUID = -3333581658597278334L;

    /**
     * @param key The Key for which no writeable Connector was found.
     */
    public NoWriteableConnectorFoundException(String settings4jKey) {
        super(NO_WRITEABLE_CONNECTOR_FOUND_1, new Object[]{settings4jKey});
    }

}
