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
package org.settings4j;

import java.util.List;
import java.util.Map;


/**
 * SettingsInstance is used to get simply access to Application settings.
 * 
 * @see Settings4j
 * @author Harald.Brabenetz
 */
public interface Settings4jInstance {

    /**
     * return the found String-Value for the given key.<br />
     * The {@link Settings4jInstance} Instance iterates all his {@link Connector} and return the first found Value.
     * <p>
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found String-Value for the given key
     */
    String getString(String key);

    /**
     * return the found byte[]-Value for the given key.<br />
     * The {@link Settings4jInstance} Instance iterates all his {@link Connector} and return the first found Value.
     * <p>
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found byte[]-Value for the given key
     */
    byte[] getContent(String key);

    /**
     * return the found Object-Value for the given key.<br />
     * The {@link Settings4jInstance} Instance iterates all his {@link Connector} and return the first found Value.
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found Object-Value for the given key
     */
    Object getObject(String key);

    /**
     * Add a {@link Connector}.<br />
     * This method will be call, if you create a connector-ref to a connector configuration in your settings4j.xml
     * 
     * <pre>
     * Example configuration in settings4j.xml:
     * --------------------------------------
     * &lt;settings name="com.mycompany" &gt;
     *     &lt;connector-ref name="DBConnector" /&gt;
     * &lt;/settings&gt;
     * --------------------------------------
     * </pre>
     * 
     * @param connector The connector to add.
     */
    void addConnector(Connector connector);

    /**
     * Remove all Settings. (Internal use only)
     */
    void removeAllConnectors();

    /**
     * The key mapping defined in settings4j.xml.
     * <p>
     * if some Sub-Modules of your App defines separated Keys for e.g. the DataSource,
     * you can refer it to one unique Key:
     * 
     * <pre>
     * Example:
     * &lt;mapping name="defaultMapping"&gt;
     *     &lt;entry key="com/mycompany/moduleX/datasource" ref-key="global/datasource"/&gt;
     *     &lt;entry key="com/mycompany/moduleY/datasource" ref-key="global/datasource"/&gt;
     * &lt;/mapping&gt;
     * </pre>
     * 
     * Settings4j.getXXX("com/mycompany/moduleX/datasource"); <br />
     * should return the configured value under "global/datasource" <br />
     * <br />
     * 
     * @return the Mappings of this Settings-Object (without inheritances)
     */
    Map getMapping();

    /**
     * Set the mapping for this Settings-Object without inheritance.<br />
     * This method will be call, if you create a mapping-ref to a mapping configuration in your settings4j.xml
     * 
     * <pre>
     * Example:
     * &lt;root mapping-ref="<b>defaultMapping</b>" &gt;
     *    ...
     * &lt;/root&gt;
     * 
     * &lt;mapping name="<b>defaultMapping</b>"&gt;
     *     &lt;entry key="com/mycompany/moduleX/datasource" ref-key="global/datasource"/&gt;
     *     &lt;entry key="com/mycompany/moduleY/datasource" ref-key="global/datasource"/&gt;
     * &lt;/mapping&gt;
     * </pre>
     * 
     * @param mapping The Mapping between available settings to used settings.
     */
    void setMapping(Map mapping);

    /**
     * Return a List off {@link Connector} who can be used with this {@link Settings4jInstance} instance.
     * 
     * @return a list off all Connectors who can be used with this {@link Settings4jInstance} instance
     */
    List<Connector> getConnectors();

    /**
     * Return the {@link Connector} for the given Name.
     * 
     * @param connectorName The Connector Name.
     * @return The {@link Connector} for the given Name.
     */
    Connector getConnector(String connectorName);
}
