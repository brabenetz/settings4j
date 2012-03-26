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

import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;

public class Settings4jContextLoader extends ContextLoader {

    /**
     * Name of servlet context parameter (i.e., "<code>contextConfigLocation</code>") that can specify the config
     * location for the root context, falling back to the implementation's default otherwise.
     * 
     * @see org.springframework.web.context.support.XmlWebApplicationContext#DEFAULT_CONFIG_LOCATION
     */
    public static final String CONFIG_DEFAULT_VALUES = "settings4jContextConfigDefaultValues";

    protected void customizeContext(final ServletContext servletContext, final ConfigurableWebApplicationContext wac) {
        final Settings4jPlaceholderConfigurer placeholderConfigurer = createPlaceholderConfigurer();
        // TODO hbrabenetz 23.03.2012 : get Prefix from Context!?
        final Properties props = getProperties(servletContext);
        final String[] configLocations = wac.getConfigLocations();
        for (int i = 0; i < configLocations.length; i++) {
            final String configLocation = configLocations[i];
            configLocations[i] = Settings4jPlaceholderConfigurer.parseStringValue(configLocation, props);
        }
    }

    private Properties getProperties(final ServletContext servletContext) {
        // TODO get Properties from Context "settings4jDefaultValues"
        return new Properties();
    }

    protected Settings4jPlaceholderConfigurer createPlaceholderConfigurer() {
        return new Settings4jPlaceholderConfigurer();
    }

}
