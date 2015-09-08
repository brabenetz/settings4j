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
package org.settings4j.helper.spring;

import javax.servlet.ServletContext;

import org.settings4j.helper.web.DefaultPropertiesLoader;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Spring Context Loader which can replaces Placeholders in contextConfigLocations like "${...}" with Values from Settings4j.
 * <p>
 * This Implementation replaces the {@link ContextLoader}
 * </p>
 * <p>
 * See configuration Example: {@link Settings4jContextLoaderListener}.
 * </p>
 * TODO brabenetz 05. Sep. 2015 : remove this Class with Release 2.1.
 *
 * @author brabenetz
 * @deprecated use Spring Profiles instead. (Will be remove with 2.1)
 */
@Deprecated
public class Settings4jContextLoader extends ContextLoader {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Settings4jContextLoader.class);

    /**
     * Name of servlet context parameter (i.e., "<code>settings4jContextConfigLocation</code>")
     * that can specify the config location for the root context, falling back
     * to the implementation's default otherwise.
     * @see org.springframework.web.context.support.XmlWebApplicationContext#DEFAULT_CONFIG_LOCATION
     */
    public static final String SETTINGS4J_CONFIG_LOCATION_PARAM = "settings4jContextConfigLocation";

    /** {@inheritDoc} */
    @Override
    protected void customizeContext(final ServletContext servletContext, final ConfigurableWebApplicationContext wac) {
        final String warnMsg = "Settings4jContextLoader, Settings4jContextLoaderListener will be removed in feature releases. Use Spring Profiles instead.";
        servletContext.log("WARNING: " + warnMsg);
        LOG.warn(warnMsg);
        if (wac instanceof XmlWebApplicationContext) {
            // be sure that the DefaultPropertiesLoader is initialized:
            createDefaultPropertiesLoader().initDefaultProperties(servletContext);
            // replace Placeholders in configLocations.
            final String configLocations = servletContext.getInitParameter(SETTINGS4J_CONFIG_LOCATION_PARAM);
            LOG.debug("settings4jContextConfigLocation configLocations: {}", configLocations);
            final String parsedConfigLocations = Settings4jPlaceholderConfigurer.parseStringValue(configLocations);
            LOG.debug("settings4jContextConfigLocation parsed configLocations: {}", parsedConfigLocations);

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
