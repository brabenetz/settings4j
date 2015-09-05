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
package org.settings4j.helper.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.settings4j.Connector;
import org.settings4j.Settings4j;
import org.settings4j.connector.PropertyFileConnector;

/**
 * Implementation which loads Default Properties from web.xml Init-Paramters and adds a Property connector to Settings4j.
 * <p>
 * See Example {@link DefaultPropertiesLoaderListener}.
 * </p>
 *
 * @author brabenetz
 */
public class DefaultPropertiesLoader {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultPropertiesLoader.class);

    /**
     * The Name of the Connector which will be added to the Settings4j Config.
     */
    public static final String CONNECTOR_NAME = "DefaultPropertiesFromWebXml";

    /**
     * The Init-Parameter Name from the web.xml from where the default properties can be used.
     */
    public static final String DEFAULT_PROPERTIES = "settings4jDefaultProperties";

    /**
     * If the InitParameter "settings4jDefaultProperties" exists in the given {@link ServletContext}, then a Connector
     * will be added to Settings4j.
     *
     * @param servletContext The ServletContext where the InitParameters are configured.
     */
    public void initDefaultProperties(final ServletContext servletContext) {
        synchronized (Settings4j.getSettingsRepository().getSettings()) {
            if (Settings4j.getSettingsRepository().getSettings().getConnector(CONNECTOR_NAME) == null) {
                addPropertyConnector(servletContext);
            } else {
                LOG.info("{} Connector already exists in Settings4j", CONNECTOR_NAME);
            }
        }
    }

    private void addPropertyConnector(final ServletContext servletContext) {
        if (servletContext.getInitParameter(DEFAULT_PROPERTIES) != null) {
            final Properties property = getDefaultProperties(servletContext);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Add Property Connector '{}' to the Settings4j Repository.", CONNECTOR_NAME);
                final Set<Entry<Object, Object>> entries = property.entrySet();
                for (final Entry<Object, Object> entry : entries) {
                    LOG.debug("{} = {}", entry.getKey(), entry.getValue());
                }
            }
            addPropertyConnector(property);
        } else {
            LOG.debug("No InitParameter 'settings4jDefaultProperties' found.");
        }
    }

    private void addPropertyConnector(final Properties property) {
        final PropertyFileConnector propertyFileConnector = new PropertyFileConnector();
        propertyFileConnector.setName(CONNECTOR_NAME);
        propertyFileConnector.setProperty(property);
        Settings4j.getSettingsRepository().getSettings().addConnector(propertyFileConnector);
        if (LOG.isDebugEnabled()) {
            final List<Connector> connectors = Settings4j.getConnectors();
            LOG.debug("Current Connectors are {}", connectors.size());
            for (final Connector connector : connectors) {
                LOG.debug("Connector: {}", connector.getName());
            }

        }
    }

    private Properties getDefaultProperties(final ServletContext servletContext) {
        final String defaultProperties = servletContext.getInitParameter(DEFAULT_PROPERTIES);
        final Properties property = new Properties();
        try {
            property.load(new ByteArrayInputStream(defaultProperties.getBytes("ISO-8859-1")));
        } catch (final UnsupportedEncodingException e) {
            // every JDK must have the ISO-8859-1 Charset...
            throw new IllegalStateException(e.getMessage(), e);
        } catch (final IOException e) {
            // IOException never happens in ByteArrayInputStream implementation...
            throw new IllegalStateException(e.getMessage(), e);
        }
        return property;
    }
}
