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
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;
import org.settings4j.Settings4j;


/**
 * With this Implementation you can define a Key in your web.xml (init-parameter "settings4jLog4jConfigurationKey") with that you can configure your Path to
 * your log4j.xml.
 * <p>
 * Each WebApplication in you Servlet Container can have its own key. And so you can configure for each webApplication a separated log4j.xml.
 * </p>
 * <p>
 * See Example {@link Log4jConfigurationLoaderListener}.
 * </p>
 *
 * @author brabenetz
 */
public class Log4jConfigurationLoader {

    /**
     * Key for log4j configuration.
     */
    public static final String LOG4J_CONFIG_SETTINGS4JKEY = "settings4jLog4jConfigurationKey";

    private ServletContext servletContext;

    /**
     * If the InitParameter "Settings4jLog4jConfigurationKey" exists in the given {@link ServletContext}, then Log4j
     * will be configured with it.
     *
     * @param servCxt The ServletContext where the InitParameters are configured.
     */
    public void initLog4jConfiguration(final ServletContext servCxt) {
        this.servletContext = servCxt;

        // be sure that the DefaultPropertiesLoader is initialized:
        createDefaultPropertiesLoader().initDefaultProperties(this.servletContext);

        final String log4jConfigSettings4jKey = this.servletContext.getInitParameter(LOG4J_CONFIG_SETTINGS4JKEY);
        if (log4jConfigSettings4jKey == null) {
            log("Log4j not initialized: context parameter not set [name=" + LOG4J_CONFIG_SETTINGS4JKEY + "]");
            return;
        }
        final String configLocation = Settings4j.getString(log4jConfigSettings4jKey);
        if (configLocation == null) {
            log("Log4j not initialized: configLocation not set");
        } else {
            try {
                this.initLogging(configLocation);
                log("Log4j initialized [configurationPath=" + configLocation + "]");
            } catch (final Throwable e) {
                log("Log4j not initialized [configurationPath=" + configLocation + "]", e);
            }
        }
    }

    /**
     * @param configLocation location.
     */
    protected void initLogging(final String configLocation) {
        log("initLogging [" + configLocation + "].");
        URL url = null;
        try {
            url = new URL(configLocation);
            log("found url: " + url);
        } catch (@SuppressWarnings("unused") final MalformedURLException ex) {
            // so, resource is not a URL:
            // attempt to get the resource from the class path
            log("attempt to get the resource from the class path.");
            url = Loader.getResource(configLocation);
        }
        if (url != null) {
            log("Using URL [" + url + "] for automatic log4j configuration.");

            if (configLocation.toLowerCase(Locale.ENGLISH).endsWith(".xml")) {
                DOMConfigurator.configure(url);
            } else {
                PropertyConfigurator.configure(url);
            }

        } else {
            log("Could not find resource: [" + configLocation + "].");
        }
    }


    /**
     * Writes an explanatory message and a stack trace for a given <code>Throwable</code> exception to the servlet log
     * file. The name and type of the servlet log file is specific to the servlet container, usually an event log.
     *
     * @param message a <code>String</code> that describes the error or exception
     * @param throwable the <code>Throwable</code> error or exception
     */
    public void log(final String message, final Throwable throwable) {
        this.servletContext.log(message, throwable);
    }

    /**
     * Writes the specified message to a servlet log file, usually an event log. The name and type of the servlet log
     * file is specific to the servlet container.
     *
     * @param msg a <code>String</code> specifying the message to be written to the log file
     */
    public void log(final String msg) {
        this.servletContext.log(msg);
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
