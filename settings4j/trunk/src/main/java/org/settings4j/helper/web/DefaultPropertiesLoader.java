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
package org.settings4j.helper.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.settings4j.Settings4j;
import org.settings4j.connector.PropertyFileConnector;

/**
 * Implementation which loads Default Properties from web.xml Init-Paramters and adds a Property connector to
 * Settings4j.
 * 
 * @author brabenetz
 */
public class DefaultPropertiesLoader {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(DefaultPropertiesLoader.class);

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
                LOG.info(CONNECTOR_NAME + " Connector already exists in Settings4j");
            }
        }
    }

    private void addPropertyConnector(final ServletContext servletContext) {
        if (servletContext.getInitParameter(DEFAULT_PROPERTIES) != null) {
            final Properties property = getDefaultProperties(servletContext);
            addPropertyConnector(property);
        }
    }

    private void addPropertyConnector(final Properties property) {
        final PropertyFileConnector propertyFileConnector = new PropertyFileConnector();
        propertyFileConnector.setName(CONNECTOR_NAME);
        propertyFileConnector.setProperty(property);
        Settings4j.getSettingsRepository().getSettings().addConnector(propertyFileConnector);
    }

    private Properties getDefaultProperties(final ServletContext servletContext) {
        final String defaultProperties = servletContext.getInitParameter(DEFAULT_PROPERTIES);
        final Properties property = new Properties();
        try {
            property.load(new ByteArrayInputStream(defaultProperties.getBytes("ISO-8859-1")));
        } catch (final UnsupportedEncodingException e) {
            // every JDK must have the ISO-8859-1 Charset...
            throw new IllegalStateException(e.getMessage());
        } catch (final IOException e) {
            // IOException never happens in ByteArrayInputStream implementation...
            throw new IllegalStateException(e.getMessage());
        }
        return property;
    }
}