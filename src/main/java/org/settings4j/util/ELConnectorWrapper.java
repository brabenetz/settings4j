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

package org.settings4j.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;
import org.settings4j.Connector;

/**
 * This Wrapper makes the Getter-Methods of Connectors simply accessible by Expressionlanguages like JSP 2.0, Velocity
 * or Freemarker.<BR />
 * <BR />
 * <B>Example:</B><BR />
 * <code>${connectors.string['xyz']}</code> returns the first founded Value in all Connectors:
 * <code>connector.getString("xyz");</code>
 * 
 * @author Harald.Brabenetz
 */
public class ELConnectorWrapper {

    private final Connector[] connectors;

    /**
     * The list of all connectors, where Values can be searched.
     * 
     * @param connectors a array of Connectors which will be processed in Sequence.
     */
    public ELConnectorWrapper(final Connector[] connectors) {
        super();
        this.connectors = connectors;
    }

    /**
     * Usage: <code>${connectors.string['xyz']}</code> returns the first founded Value in all Connectors:
     * <code>connector.getString("xyz");</code>.
     * 
     * @return the first founded Value in all connectors
     */
    public Map getString() {
        final Transformer transformer = new Transformer() {

            public Object transform(final Object input) {
                if (input != null && input instanceof String) {
                    final String key = input.toString();
                    for (int i = 0; i < ELConnectorWrapper.this.connectors.length; i++) {
                        final Object result = ELConnectorWrapper.this.connectors[i].getString(key);
                        if (result != null) {
                            return result;
                        }
                    }
                }
                return null;
            }
        };
        return LazyMap.decorate(new HashMap(), transformer);
    }

    /**
     * Usage: <code>${connectors.content['xyz']}</code> returns the first founded Value in all Connectors:
     * <code>connector.getContent("xyz");</code>.
     * 
     * @return the first founded Value in all connectors
     */
    public Map getContent() {
        final Transformer transformer = new Transformer() {

            public Object transform(final Object input) {
                if (input != null && input instanceof String) {
                    final String key = input.toString();
                    for (int i = 0; i < ELConnectorWrapper.this.connectors.length; i++) {
                        final Object result = ELConnectorWrapper.this.connectors[i].getContent(key);
                        if (result != null) {
                            return result;
                        }
                    }
                }
                return null;
            }
        };
        return LazyMap.decorate(new HashMap(), transformer);
    }

    /**
     * Usage: <code>${connectors.object['xyz']}</code> returns the first founded Value in all Connectors:
     * <code>connector.getObject("xyz");</code>.
     * 
     * @return the first founded Value in all connectors
     */
    public Map getObject() {
        final Transformer transformer = new Transformer() {

            public Object transform(final Object input) {
                if (input != null && input instanceof String) {
                    final String key = input.toString();
                    for (int i = 0; i < ELConnectorWrapper.this.connectors.length; i++) {
                        final Object result = ELConnectorWrapper.this.connectors[i].getObject(key);
                        if (result != null) {
                            return result;
                        }
                    }
                }
                return null;
            }
        };
        return LazyMap.decorate(new HashMap(), transformer);
    }
}
