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

import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.settings4j.Settings4jRepository;
import org.settings4j.config.DOMConfigurator;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.settings.SettingsManager;

/**
 * <p><b>Prototype: </b> Each Webapp with this ServletContextListener can have his own Settings-settins
 * 
 * <pre>
 * Example usage in web.xml:
 * --------------------------------------
 * &lt;web-app ...&gt;
 *     ....
 *     &lt;listener&gt;
 *         &lt;description&gt;reconfigure Settings4j to parse also Expression like ${contextPath}&lt;/description&gt;
 *         &lt;listener-class&gt;org.settings4j.helper.web.ConfigureSettings4jWithWebContextPath&lt;/listener-class&gt;
 *     &lt;/listener&gt;
 *     ....
 * &lt;/web-app&gt;
 * --------------------------------------
 * 
 * Example configuration in settings4j.xml:
 * --------------------------------------
 * &lt;connector name="FSConnectorGlobal" class="org.settings4j.connector.FSConnector"&gt;
 *     &lt;param name="rootFolderPath" value="c:/settings/global"/&gt;
 * &lt;/connector&gt;
 * &lt;connector name="FSConnectorPerContext" class="org.settings4j.connector.FSConnector"&gt;
 *     &lt;param name="rootFolderPath" value="c:/settings/${contextPath}"/&gt;
 * &lt;/connector&gt;
 * &lt;root&gt;
 *     &lt;connector-ref ref="FSConnectorPerContext" readonly="true" /&gt;
 *     &lt;connector-ref ref="FSConnectorGlobal" readonly="true" /&gt;
 * &lt;/root&gt;
 * --------------------------------------
 * </pre>
 * 
 * <p>If you call Settings.getXXX(String key), then Settings4j will look
 * first into the Web-Context specific folder for configurations
 * and then into the global Folder for configurations.
 * 
 * <p><b>ATTENTION:</b> This can only work, if the settings4j.jar Library is embedded into the Webapp's WAR.
 * 
 * <p>Only the settings4j.xml should be placed into the Web-Containers Classpath, where it is accessible by all webapps.
 * 
 * @author Harald.Brabenetz
 *
 */
public class ConfigureSettings4jWithWebContextPath implements ServletContextListener {

    private String configurationFile = SettingsManager.DEFAULT_XML_CONFIGURATION_FILE;
    
    private String expresionAttributeName = "contextPath";
    
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(ConfigureSettings4jWithWebContextPath.class);
    
    public void contextDestroyed(ServletContextEvent sce) {
        // do nothing
    }

    public void contextInitialized(ServletContextEvent sce) {
        String realRootPath = sce.getServletContext().getRealPath("/");
        LOG.info("found realRootPath: " + realRootPath);
        // cut off the last char "/" from "http://host/contextPath/"
        realRootPath = realRootPath.substring(0, realRootPath.length()-1); 
        String contextPath = realRootPath.substring(realRootPath.lastIndexOf('/')+1);
        // get "contextPath" from "http://host/contextPath"
        LOG.info("found real contextPath: " + contextPath);
        
        Settings4jRepository settingsRepository = SettingsManager.getSettingsRepository();
        settingsRepository.resetConfiguration();
        
        // read XML default Configuration to configure the repository
        URL url = ClasspathContentResolver.getResource(configurationFile);
        
        DOMConfigurator domConfigurator = new DOMConfigurator(settingsRepository);
        domConfigurator.addExpressionAttribute(expresionAttributeName, contextPath);
        domConfigurator.doConfigure(url);
        
        
        
    }

}
