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

import org.settings4j.exception.NoWriteableConnectorFoundException;


/**
 * SettingsInstance is used to get simply access to Application settings.
 * 
 * @see Settings
 * 
 * @author hbrabenetz
 *
 */
public interface SettingsInstance {

    /**
     * return the found String-Value for the given key.<br />
     * The {@link SettingsInstance} Instance iterates all his {@link Connector} and return the first found Value.<br />
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found String-Value for the given key 
     */
    public abstract String getString(String key);

    /**
     * return the found byte[]-Value for the given key.<br />
     * The {@link SettingsInstance} Instance iterates all his {@link Connector} and return the first found Value.<br />
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found byte[]-Value for the given key 
     */
    public abstract byte[] getContent(String key);

    /**
     * return the found Object-Value for the given key.<br />
     * The {@link SettingsInstance} Instance iterates all his {@link Connector} and return the first found Value.<br />
     * <br />
     * Returns null if no connector found a Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the found Object-Value for the given key 
     */
    public abstract Object getObject(String key);

    /**
     * Set ( or overwrite ) the Value for the Given key.<br />
     * The {@link SettingsInstance} Instance iterates all his {@link Connector} and
     * if a Connector can successful write the new Value,
     * then the Connector must return {@link Constants#SETTING_SUCCESS}<br />
     * 
     * If No Connector can write the Value ( all {@link Connector} returns {@link Constants#SETTING_NOT_POSSIBLE}
     * then a {@link NoWriteableConnectorFoundException} will be thrown.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new String-Value for the given key 
     * @param connectorName The name of the connector, who should be used to set the value
     * @throws NoWriteableConnectorFoundException Is thrown if no Connector was Found.
     *      One Connector must return {@link Constants#SETTING_SUCCESS}
     */
    public abstract void setString(String key, String value, String connectorName) throws NoWriteableConnectorFoundException;


    /**
     * Set ( or overwrite ) the Value for the Given key.<br />
     * The {@link SettingsInstance} Instance iterates all his {@link Connector} and
     * if a Connector can successful write the new Value,
     * then the Connector must return {@link Constants#SETTING_SUCCESS}<br />
     * 
     * If No Connector can write the Value ( all {@link Connector} returns {@link Constants#SETTING_NOT_POSSIBLE}
     * then a {@link NoWriteableConnectorFoundException} will be thrown.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new byte[]-Value for the given key
     * @param connectorName The name of the connector, who should be used to set the value 
     * @throws NoWriteableConnectorFoundException Is thrown if no Connector was Found.
     *      One Connector must return {@link Constants#SETTING_SUCCESS}
     */
    public abstract void setContent(String key, byte[] value, String connectorName) throws NoWriteableConnectorFoundException;


    /**
     * Set ( or overwrite ) the Value for the Given key.<br />
     * The {@link SettingsInstance} Instance iterates all his {@link Connector} and
     * if a Connector can successful write the new Value,
     * then the Connector must return {@link Constants#SETTING_SUCCESS}<br />
     * 
     * If No Connector can write the Value ( all {@link Connector} returns {@link Constants#SETTING_NOT_POSSIBLE}
     * then a {@link NoWriteableConnectorFoundException} will be thrown.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new Object-Value for the given key 
     * @param connectorName The name of the connector, who should be used to set the value
     * @throws NoWriteableConnectorFoundException Is thrown if no Connector was Found.
     *      One Connector must return {@link Constants#SETTING_SUCCESS}
     */
    public abstract void setObject(String key, Object value, String connectorName) throws NoWriteableConnectorFoundException;

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
     * @param connector
     */
    public abstract void addConnector(Connector connector);
    
    /**
     * Remove all Settings.
     * (Internal use only)
     */
    public abstract void removeAllConnectors();
        
    /**
     * The key mapping defined in settings4j.xml:
     * <pre>
     * Example:
     * &lt;mapping name="defaultMapping"&gt;
     *     &lt;entry key="com/mycompany/moduleX/datasource" ref-key="global/datasource"/&gt;
     *     &lt;entry key="com/mycompany/moduleY/datasource" ref-key="global/datasource"/&gt;
     * &lt;/mapping&gt;
     * </pre>
     * 
     * Settings.getXXX("com/mycompany/moduleX/datasource"); <br />
     * should return the configured value under "global/datasource" <br />
     * <br />
     * 
     * @return the Mappings of this Settings-Object (without inheritances)
     */
    public abstract Map getMapping();
    
    /**
     * 
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
     */
    public abstract void setMapping(Map mapping);
    
    /**
     * Return a List off {@link Connector} who can be used with this {@link SettingsInstance} instance
     * 
     * @return a list off all Connectors who can be used with this {@link SettingsInstance} instance 
     */
    public abstract List getConnectors();
}
