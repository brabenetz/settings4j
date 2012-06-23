/* ***************************************************************************
 * Copyright (c) 2012 Brabenetz Harald, Austria.
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

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.xml.DOMConfigurator;
import org.settings4j.Settings4j;


/**
 * @author brabenetz
 */
public class Log4jConfigurationLoader {

    /**
     * Key for log4j configuration.
     */
    public static final String LOG4J_CONFIG_SETTINGS4JKEY = "settings4jLog4jConfigurationKey";

    /**
     * If the InitParameter "Settings4jLog4jConfigurationKey" exists in the given {@link ServletContext}, then Log4j
     * will be configured with it.
     * 
     * @param servletContext The ServletContext where the InitParameters are configured.
     */
    public void initLog4jConfiguration(final ServletContext servletContext) {


        // be sure that the DefaultPropertiesLoader is initialized:
        createDefaultPropertiesLoader().initDefaultProperties(servletContext);

        final String log4jConfigSettings4jKey = servletContext.getInitParameter(LOG4J_CONFIG_SETTINGS4JKEY);
        if (log4jConfigSettings4jKey == null) {
            servletContext.log("Log4j not initialized: context parameter not set [name=" + LOG4J_CONFIG_SETTINGS4JKEY
                + "]");
            return;
        }
        final String configLocation = Settings4j.getString(log4jConfigSettings4jKey);
        if (configLocation == null) {
            servletContext.log("Log4j not initialized: configLocation not set");
        } else {
            try {
                this.initLogging(configLocation);
                servletContext.log("Log4j initialized [configurationPath=" + configLocation + "]");
            } catch (final Throwable e) {
                servletContext.log("Log4j not initialized [configurationPath=" + configLocation + "]", e);
            }
        }
    }

    /**
     * @param configLocation location.
     */
    protected void initLogging(final String configLocation) {
        LogLog.setInternalDebugging(true);
        LogLog.debug("initLogging [" + configLocation + "].");
        URL url = null;
        try {
            url = new URL(configLocation);
            LogLog.debug("found url: " + url);
        } catch (final MalformedURLException ex) {
            // so, resource is not a URL:
            // attempt to get the resource from the class path
            LogLog.debug("attempt to get the resource from the class path.");
            url = Loader.getResource(configLocation);
        }
        if (url != null) {
            LogLog.debug("Using URL [" + url + "] for automatic log4j configuration.");

            if (configLocation.toLowerCase().endsWith(".xml")) {
                DOMConfigurator.configure(url);
            } else {
                PropertyConfigurator.configure(url);
            }

        } else {
            LogLog.debug("Could not find resource: [" + configLocation + "].");
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
