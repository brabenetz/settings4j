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
package org.settings4j.helper.spring;

import javax.servlet.ServletContext;

import org.settings4j.helper.web.DefaultPropertiesLoader;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Spring Context Loader which can replaces Placeholders in contextConfigLocations like "${...}" with Values from
 * Settings4j.
 * <p>
 * This Implementation replaces the {@link ContextLoader}
 * <p>
 * See configuration Example: {@link Settings4jContextLoaderListener}.
 * 
 * @author brabenetz
 */
public class Settings4jContextLoader extends ContextLoader {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(Settings4jContextLoader.class);

    /**
     * Name of servlet context parameter (i.e., "<code>settings4jContextConfigLocation</code>")
     * that can specify the config location for the root context, falling back
     * to the implementation's default otherwise.
     * @see org.springframework.web.context.support.XmlWebApplicationContext#DEFAULT_CONFIG_LOCATION
     */
    public static final String SETTINGS4J_CONFIG_LOCATION_PARAM = "settings4jContextConfigLocation";
    
    /** {@inheritDoc} */
    protected void customizeContext(final ServletContext servletContext, final ConfigurableWebApplicationContext wac) {
        if (wac instanceof XmlWebApplicationContext) {
            // be sure that the DefaultPropertiesLoader is initialized:
            createDefaultPropertiesLoader().initDefaultProperties(servletContext);
            // replace Placeholders in configLocations.
            String configLocations = servletContext.getInitParameter(SETTINGS4J_CONFIG_LOCATION_PARAM);
            if (LOG.isDebugEnabled()) {
                LOG.debug("settings4jContextConfigLocation configLocations: " + configLocations);
            }
            String parsedConfigLocations = Settings4jPlaceholderConfigurer.parseStringValue(configLocations);
            if (LOG.isDebugEnabled()) {
                LOG.debug("settings4jContextConfigLocation parsed configLocations: " + parsedConfigLocations);
            }
            
            wac.setConfigLocation(parsedConfigLocations);
        } else {
            LOG.warn(//
            "Settings4jContextLoader only works with an ApplicationContext from type XmlWebApplicationContext.");
        }
    }

    /**
     * Create the DefaultPropertiesLoader to use. Can be overridden in subclasses.
     * 
     * @return the new DefaultPropertiesLoader
     */
    protected DefaultPropertiesLoader createDefaultPropertiesLoader() {
        return new DefaultPropertiesLoader();
    }

}
