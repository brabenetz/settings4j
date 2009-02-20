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
package org.settings4j.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.settings4j.Connector;
import org.settings4j.Constants;
import org.settings4j.SettingsInstance;
import org.settings4j.exception.NoWriteableConnectorFoundException;

/**
 * The default Settings Object is a {@link HierarchicalSettings} implementation.
 * 
 * @author hbrabenetz
 *
 */
public class DefaultSettings implements SettingsInstance {
    
    private List connectors = Collections.synchronizedList(new ArrayList());
    private Map mapping;

    public DefaultSettings() {
        super();
    }

    /** {@inheritDoc} */
    public List getConnectors() {
        return Collections.unmodifiableList(connectors);
    }

	/** {@inheritDoc} */
    public void addConnector(Connector connector) {
        connectors.add(connector);
    }

    /** {@inheritDoc} */
    public void removeAllConnectors() {
        connectors.clear();
    }

    /** {@inheritDoc} */
    public byte[] getContent(String key) {
        key = mappedKey(key);
        byte[] result = null;
        Iterator iterator;
        iterator = this.connectors.iterator();
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            result = connector.getContent(key);
            if (result != null){
                return result;
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    public Object getObject(String key) {
        key = mappedKey(key);
        Object result = null;
        Iterator iterator;
        iterator = this.connectors.iterator();
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            result = connector.getObject(key);
            if (result != null){
                return result;
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    public String getString(String key) {
        key = mappedKey(key);
        String result = null;
        Iterator iterator;
        iterator = this.connectors.iterator();
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            result = connector.getString(key);
            if (result != null){
                return result;
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    public void setContent(String key, byte[] value, String connectorName) throws NoWriteableConnectorFoundException {
    	if (connectorName == null){
            throw new NoWriteableConnectorFoundException(key);
        }
    	key = mappedKey(key);
        int status;
        Iterator iterator;
        iterator = this.connectors.iterator();
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            if(connectorName.equals(connector.getName())){
	            status = connector.setContent(key, value);
	            if (status == Constants.SETTING_SUCCESS){
	                return;
	            }
            }
        }
        throw new NoWriteableConnectorFoundException(key);
    }

    /** {@inheritDoc} */
    public void setObject(String key, Object value, String connectorName) throws NoWriteableConnectorFoundException {
        if (connectorName == null){
            throw new NoWriteableConnectorFoundException(key);
        }
    	key = mappedKey(key);
        int status;
        Iterator iterator;
        iterator = this.connectors.iterator();
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            if(connectorName.equals(connector.getName())){
	            status = connector.setObject(key, value);
	            if (status == Constants.SETTING_SUCCESS){
	                return;
	            }
            }
        }
        throw new NoWriteableConnectorFoundException(key);
    }

    /** {@inheritDoc} */
    public void setString(String key, String value, String connectorName) throws NoWriteableConnectorFoundException {
        if (connectorName == null){
            throw new NoWriteableConnectorFoundException(key);
        }
    	key = mappedKey(key);
        int status;
        Iterator iterator;
        iterator = this.connectors.iterator();
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            if(connectorName.equals(connector.getName())){
	            status = connector.setString(key, value);
	            if (status == Constants.SETTING_SUCCESS){
	                return;
	            }
            }
        }
        throw new NoWriteableConnectorFoundException(key);
    }
    
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
     * 
     * @param key
     * @return
     */
    private String mappedKey(String key){
        String mappedKey = (String)this.getMapping().get(key);
        if(StringUtils.isEmpty(mappedKey)){
            return key;
        } else {
            return mappedKey;
        }
    }

    /** {@inheritDoc} */
    public Map getMapping() {
        if (mapping == null){
            mapping = new HashMap();
        }
        return mapping;
    }

    /** {@inheritDoc} */
    public void setMapping(Map mapping) {
        this.mapping = mapping;
    }
}
