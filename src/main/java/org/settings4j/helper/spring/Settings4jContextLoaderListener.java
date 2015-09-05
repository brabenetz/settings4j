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

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

/**
 * {@link javax.servlet.ServletContextListener} to initialize the {@link Settings4jContextLoader}.
 * <p>
 * This Implementation replaces the {@link ContextLoaderListener}
 * </p>
 * <h3>Usage - Make SpringContext configurable</h3>
 * <p>
 * Example Configuration could look like the following:
 * </p>
 *
 * <pre>
 * web.xml
 * --------------------------------------
 * &lt;context-param&gt;
 *     &lt;param-name&gt;settings4jContextConfigLocation&lt;/param-name&gt;
 *     &lt;param-value&gt;
 *         ${com/myCompany/myApp/appContextSecurity},
 *         /WEB-INF/applicationContext-otherBeans.xml
 *     &lt;/param-value&gt;
 * &lt;/context-param&gt;
 *
 * &lt;listener&gt;
 *     &lt;display-name&gt;&lt;/display-name&gt;
 *     &lt;listener-class&gt;org.settings4j.helper.spring.Settings4jContextLoaderListener&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * --------------------------------------
 * </pre>
 * <p>
 * With this configuration you define with tehKey teh path to your Security configuration.
 * </p>
 * <h3>Server config Example</h3>
 * <p>
 * Example Configuration in TOMCAT:<br>
 * Start tomcat with <code>-Dcom/myCompany/myApp/appContextSecurity=file:.../applicationContext-security-alwaysAdmin.xml</code><br>
 * Or configured a JNDI Value:
 * </p>
 *
 * <pre>
 * TOMCAT context.xml
 * --------------------------------------
 * &lt;Environment name ="com/myCompany/myApp/appContextSecurity"
 *              value="/WEB-INF/applicationContext-security.xml"
 *              type="java.lang.String" /&gt;
 *
 * --------------------------------------
 * </pre>
 *
 * <h3>Default Values</h3>
 * <p>
 * It is recommended to use this in combination with {@link org.settings4j.helper.web.DefaultPropertiesLoader}:
 * </p>
 *
 * <pre>
 * web.xml
 * --------------------------------------
 * &lt;context-param&gt;
 *     &lt;param-name&gt;settings4jDefaultProperties&lt;/param-name&gt;
 *     &lt;param-value&gt;
 *         com/myCompany/myApp/appContextSecurity=/WEB-INF/applicationContext-security.xml
 *     &lt;/param-value&gt;
 * &lt;/context-param&gt;
 *
 * &lt;context-param&gt;
 *     &lt;param-name&gt;settings4jContextConfigLocation&lt;/param-name&gt;
 *     &lt;param-value&gt;
 *         ${com/myCompany/myApp/appContextSecurity},
 *         /WEB-INF/applicationContext-otherBeans.xml
 *     &lt;/param-value&gt;
 * &lt;/context-param&gt;
 *
 * &lt;listener&gt;
 *     &lt;display-name&gt;&lt;/display-name&gt;
 *     &lt;listener-class&gt;org.settings4j.helper.spring.Settings4jContextLoaderListener&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * --------------------------------------
 * </pre>
 *
 * @author brabenetz
 * @deprecated use Spring Profiles instead
 */
@Deprecated
public class Settings4jContextLoaderListener extends ContextLoaderListener {

    /** {@inheritDoc} */
    @Override
    protected ContextLoader createContextLoader() {
        return new Settings4jContextLoader();
    }

}
