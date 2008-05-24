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
import java.util.Iterator;
import java.util.List;

import org.settings4j.Connector;
import org.settings4j.SettingsRepository;
import org.settings4j.config.DOMConfigurator;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.settings.helper.ConnectorIterator;

public class DefaultSettings extends HierarchicalSettings{
    
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(DefaultSettings.class);
    
    String name;
    private List connectors = Collections.checkedList(Collections.synchronizedList(new ArrayList()), Connector.class);
    private HierarchicalSettings parent;

    // Categories need to know what Hierarchy they are in
    private SettingsRepository settingsRepository;

    public DefaultSettings(String name) {
        super();
        this.name = name;
    }

    public List getConnectors() {
        return Collections.unmodifiableList(connectors);
    }
    
    public void addConnector(Connector connector) {
        connectors.add(connector);
    }

    public void removeAllConnectors() {
        connectors.clear();
    }

    public byte[] getContent(String key) {
        checkConnectors();
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

    public Object getObject(String key) {
        checkConnectors();
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

    public String getString(String key) {
        checkConnectors();
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

    public void setContent(String key, byte[] value) {
        checkConnectors();
        int status;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            status = connector.setContent(key, value);
            if (status == Connector.SETTING_SUCCESS){
                return;
            }
        }
        throw new IllegalStateException("Content '" + key + "' cannot be writen. No writeable Connector found");
    }

    public void setObject(String key, Object value) {
        checkConnectors();
        int status;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            status = connector.setObject(key, value);
            if (status == Connector.SETTING_SUCCESS){
                return;
            }
        }
        throw new IllegalStateException("Content '" + key + "' cannot be writen. No writeable Connector found");
    }

    public void setString(String key, String value) {
        checkConnectors();
        int status;
        Iterator iterator;
        iterator = new ConnectorIterator(this);
        while (iterator.hasNext()) {
            Connector connector = (Connector) iterator.next();
            status = connector.setString(key, value);
            if (status == Connector.SETTING_SUCCESS){
                return;
            }
        }
        throw new IllegalStateException("Content '" + key + "' cannot be writen. No writeable Connector found");
    }

    public HierarchicalSettings getParent() {
        return parent;
    }

    public void setParent(HierarchicalSettings parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }

    public void setSettingsRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }
    
    /**
     * Check if the repository must be configured with the defaul fallback settings4j.xml.
     */
    private void checkConnectors(){
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
}
