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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.settings4j.Connector;
import org.settings4j.Constants;
import org.settings4j.SettingsRepository;
import org.settings4j.config.DOMConfigurator;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.exception.NoWriteableConnectorFoundException;
import org.settings4j.settings.helper.ConnectorIterator;
import org.settings4j.settings.helper.InheritedMappedKeys;

/**
 * The default Settings Object is a {@link HierarchicalSettings} implementation.
 * 
 * @author hbrabenetz
 *
 */
public class DefaultSettings extends HierarchicalSettings{
    
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(DefaultSettings.class);
    
    private String name;
    private List connectors = Collections.checkedList(Collections.synchronizedList(new ArrayList()), Connector.class);
    private HierarchicalSettings parent;
    private Map mapping;
    private boolean additivity = true;

    // Settings needs to know what Hierarchy they are in
    private SettingsRepository settingsRepository;

    public DefaultSettings(String name) {
        super();
        this.name = name;
    }

    /** {@inheritDoc} */
    public List getConnectors() {
        return Collections.unmodifiableList(connectors);
    }
    
    /** {@inheritDoc} */
    public void addConnector(Connector connector) {
        settingsRepository.addConnector(connector);
        connectors.add(connector);
    }

    /** {@inheritDoc} */
    public void removeAllConnectors() {
        connectors.clear();
    }

    /** {@inheritDoc} */
    public byte[] getContent(String key) {
        key = mappedKey(key);
        initializeRepositoryIfNecessary();
        byte[] result = null;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
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
        initializeRepositoryIfNecessary();
        Object result = null;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
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
        initializeRepositoryIfNecessary();
        String result = null;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
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
    public void setContent(String key, byte[] value) throws NoWriteableConnectorFoundException {
        key = mappedKey(key);
        initializeRepositoryIfNecessary();
        int status;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            status = connector.setContent(key, value);
            if (status == Constants.SETTING_SUCCESS){
                return;
            }
        }
        throw new NoWriteableConnectorFoundException(key);
    }

    /** {@inheritDoc} */
    public void setObject(String key, Object value) throws NoWriteableConnectorFoundException {
        key = mappedKey(key);
        initializeRepositoryIfNecessary();
        int status;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            status = connector.setObject(key, value);
            if (status == Constants.SETTING_SUCCESS){
                return;
            }
        }
        throw new NoWriteableConnectorFoundException(key);
    }

    /** {@inheritDoc} */
    public void setString(String key, String value) throws NoWriteableConnectorFoundException {
        key = mappedKey(key);
        initializeRepositoryIfNecessary();
        int status;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            status = connector.setString(key, value);
            if (status == Constants.SETTING_SUCCESS){
                return;
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
     * This method search also all parent Settings for Mappings.
     * 
     * @param key
     * @return
     */
    private String mappedKey(String key){
        String mappedKey = new InheritedMappedKeys(this).get(key);
        if(StringUtils.isEmpty(mappedKey)){
            return key;
        } else {
            return mappedKey;
        }
    }

    /** {@inheritDoc} */
    public HierarchicalSettings getParent() {
        return parent;
    }

    /** {@inheritDoc} */
    public void setParent(HierarchicalSettings parent) {
        this.parent = parent;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }

    /** {@inheritDoc} */
    public void setSettingsRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }
    
    /**
     * Check if the repository must be configured with the defaul fallback settings4j.xml.
     */
    protected void initializeRepositoryIfNecessary(){
        if (settingsRepository.getConnectorCount() == 0){
            // No connectors in hierarchy, warn user and add default-configuration.
            LOG.warn("No connectors could be found! For Setting '" + getName() + "'.");
            LOG.warn("The settings4j will be configured with the default-fallback-config: " + SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);
            
            URL url = ClasspathContentResolver.getResource(SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE);

            // If we have a non-null url, then delegate the rest of the
            // configuration to the DOMConfigurator.configure method.
            if (url != null) {
                LOG.debug("Using URL [" + url + "] for automatic settings4j fallback configuration.");
                try {
                    DOMConfigurator.configure(url, settingsRepository);
                } catch (NoClassDefFoundError e) {
                    LOG.warn("Error during default fallback initialization", e);
                }
            } else {
                LOG.fatal("Could not find resource: [" + SettingsManager.DEFAULT_FALLBACK_CONFIGURATION_FILE + "].");
            }
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

    /** {@inheritDoc} */
    public boolean isAdditivity() {
        return additivity;
    }

    /** {@inheritDoc} */
    public void setAdditivity(boolean additivity) {
        this.additivity = additivity;
    }
}
