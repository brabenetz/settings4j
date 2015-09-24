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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * {@link javax.servlet.ServletContextListener} to initialize the {@link DefaultPropertiesLoader}.
 * <p>
 * Example Configuration could look like the following:
 * </p>
 * 
 * <pre>
 * web.xml
 * --------------------------------------
 * &lt;context-param&gt;
 *     &lt;param-name&gt;settings4jDefaultProperties&lt;/param-name&gt;
 *     &lt;param-value&gt;
 *         com/myCompany/myApp/log4j.configuration=com/myCompany/myApp/log4j.xml
 *     &lt;/param-value&gt;
 * &lt;/context-param&gt;
 *
 * &lt;listener&gt;
 *     &lt;display-name&gt;&lt;/display-name&gt;
 *     &lt;listener-class&gt;org.settings4j.helper.web.DefaultPropertiesLoaderListener&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * --------------------------------------
 * </pre>
 *
 * @author brabenetz
 */
public class DefaultPropertiesLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        new DefaultPropertiesLoader().initDefaultProperties(event.getServletContext());
    }


    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        // do nothing
    }

}
