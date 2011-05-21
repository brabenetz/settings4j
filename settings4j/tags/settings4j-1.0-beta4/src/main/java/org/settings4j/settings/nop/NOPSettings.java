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
package org.settings4j.settings.nop;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.settings4j.Connector;

/**
 * No-operation implementation of Settings used by NOPSettingsRepository.
 */
public class NOPSettings implements org.settings4j.Settings4jInstance {

    /** {@inheritDoc} */
    public void addConnector(final Connector connector) {
        // do nothing in NOP-Implementation
    }

    /** {@inheritDoc} */
    public void removeAllConnectors() {
        // do nothing in NOP-Implementation
    }

    /** {@inheritDoc} */
    public List getConnectors() {
        return Collections.EMPTY_LIST;
    }

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        return null;
    }

    /** {@inheritDoc} */
    public Object getObject(final String key) {
        return null;
    }

    /** {@inheritDoc} */
    public String getString(final String key) {
        return null;
    }

    /** {@inheritDoc} */
    public Map getMapping() {
        return Collections.EMPTY_MAP;
    }

    /** {@inheritDoc} */
    public void setMapping(final Map mapping) {
        // do nothing in NOP-Implementation
    }

    /** {@inheritDoc} */
    public Connector getConnector(final String connectorName) {
        return null;
    }
}
