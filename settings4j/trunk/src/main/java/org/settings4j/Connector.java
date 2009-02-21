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

import org.settings4j.connector.ClasspathConnector;
import org.settings4j.connector.PropertyFileConnector;
import org.settings4j.connector.ReadOnlyConnectorWrapper;

/**
 * An implementation of this Interface can return Values for a given key.<br />
 * Not every implementation can write a value for a given key (e.g.: {@link ClasspathConnector} )<br />
 * String, byte[] (content) and Objects should be possible.<br />
 * <br />
 * A Connector can use the Helper Implementations
 * {@link ContentResolver} and {@link ObjectResolver} for internal use.<br />
 * Not ervery Connector needs them, Read the Javadoc of the concrete implementaiton.<br />
 * 
 * <pre>
 * 
 * Example configuration in settings4j.xml:
 * --------------------------------------
 * &lt;connector name="ClasspathConnector" class="org.settings4j.connector.ClasspathConnector" &gt;
 *     &lt;objectResolver-ref ref="DefaultObjectResolver" /&gt;
 * &lt;/connector&gt;
 * --------------------------------------
 * 
 * </pre>
 * @author hbrabenetz
 *
 */
public interface Connector extends Filterable {
    
    /**
     * return a String-Value for the given key<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the String-Value for the given key 
     */
    String getString(String key);
    
    /**
     * return a byte[]-Value for the given key<br />
     * The concrete implementation can use the ContentResolver if required
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the byte[]-Value for the given key 
     */
    byte[] getContent(String key);
    
    /**
     * return a Object-Value for the given key<br />
     * The concrete implementation can use the ObjectResolver if required<br />
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @return the Object-Value for the given key, or null if no value where found.
     */
    Object getObject(String key);
    
    /**
     * Set or replace a new Value for the given key.<br />
     * If set or replace a value is not possible because of a readonly Connector (e.g.: {@link ClasspathConnector},
     * then {@link Constants#SETTING_NOT_POSSIBLE} must be returned.
     * If set or replace was successful, then {@link Constants#SETTING_SUCCESS} must be returned.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new String-Value for the given key 
     * @return Returns {@link Constants#SETTING_SUCCESS} or {@link Constants#SETTING_NOT_POSSIBLE}
     */
    int setString(String key, String value);
    
    /**
     * Set or replace a new Value for the given key.<br />
     * If set or replace a value is not possible because of a readonly Connector (e.g.: {@link ClasspathConnector},
     * then {@link Constants#SETTING_NOT_POSSIBLE} must be returned.
     * If set or replace was successful, then {@link Constants#SETTING_SUCCESS} must be returned.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new byte[]-Value for the given key 
     * @return Returns {@link Constants#SETTING_SUCCESS} or {@link Constants#SETTING_NOT_POSSIBLE}
     */
    int setContent(String key, byte[] value);
    
    /**
     * Set or replace a new Value for the given key.<br />
     * If set or replace a value is not possible because of a readonly Connector (e.g.: {@link ClasspathConnector},
     * then {@link Constants#SETTING_NOT_POSSIBLE} must be returned.
     * If set or replace was successful, then {@link Constants#SETTING_SUCCESS} must be returned.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new Object-Value for the given key 
     * @return Returns {@link Constants#SETTING_SUCCESS} or {@link Constants#SETTING_NOT_POSSIBLE}
     */
    int setObject(String key, Object value);

    /**
     * set a ContentResolver as Helper for {@link #getContent(String)} and {@link #setContent(String, byte[])}
     * 
     * @param contentResolver
     */
    void setContentResolver(ContentResolver contentResolver);
    
    /**
     * set a ObjectResolver as Helper for {@link #getObject(String)} and {@link #setObject(String, Object)}
     * 
     * @param objectResolver
     */
    void setObjectResolver(ObjectResolver objectResolver);

    /**
     * add a Connector if you needed inside the {@link #init()} Methode.<br/>
     * Or you can use this connectors inside the settings4j.xml to set a parameter/property
     * 
     * <pre>
     * 
     * Example configuration in settings4j.xml:
     * --------------------------------------
     * &lt;connector name="PropertyFileConnector" class="org.settings4j.connector.PropertyFileConnector"&gt;
     *     &lt;param name="propertyFromContent" value="<b>${connectors.content['org/settings4j/config/propertyFile.properties']}</b>" /&gt;
     *     &lt;contentResolver-ref ref="DefaultContentResolver" /&gt;
     *     <b>&lt;connector-ref ref="ClasspathConnector" /&gt;</b>
     * &lt;/connector&gt;
     * --------------------------------------
     * 
     * </pre>
     * @param contentResolver
     */
    void addConnector(Connector connector);
    
    /**
     * Will be called after all properties have been set.
     * This function will only called one times.
     *  
     */
    public void init();
    
    /**
     * Return The name Of this Connector. The Name is required
     * in all {@link Settings}.set*(..., String connectorName) Methods.
     * 
     * @return The name Of this Connector.
     */
    public String getName();

    /**
     * Set the name of the Connector defined in the settings4j.xml configuration:
     * <pre>
     * --------------------------------------
     * &lt;connector <b>name="PropertyFileConnector"</b> ....&gt;
     *     ....
     * &lt;/connector&gt;
     * --------------------------------------
     * </pre>
     * 
     * @param name
     */
    public void setName(String name);
    
    /**
     * Return if this Connector is read Only (or Wrapped with the {@link ReadOnlyConnectorWrapper}).<br />
     * Example for some Redonly Connectors: {@link PropertyFileConnector}, {@link ClasspathConnector}
     * 
     * 
     * @return if this Connector is read Only (or Wrapped with the {@link ReadOnlyConnectorWrapper})
     */
    public boolean isReadonly();
    
}
