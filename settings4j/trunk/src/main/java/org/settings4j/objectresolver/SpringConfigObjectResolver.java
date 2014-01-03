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

package org.settings4j.objectresolver;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.settings4j.ContentResolver;
import org.settings4j.helper.spring.ByteArrayXMLApplicationContext;

/**
 * This implementation parses a Spring-Beans XML File and returns the Object from the generated Spring Application
 * Context.
 * <p>
 * Per Default the bean with id = key.replace('/', '.') will be returned. But you can configure a key "bean-ref" in the
 * {@link Properties}-File for the given Object.
 * <p>
 * Example: The following code should return a {@link javax.sql.DataSource} Object:<br/>
 * <code>
 * Settings4j.getObject("com/myCompany/myApp/MyDatasource");
 * </code>
 * <p>
 * In normal Cases the DataSource comes from the JNDI-Context (available in most Servlet Containers).<br/>
 * But in some environments there are no JNDI-Context (Commandline-Clients, UnitTests).<br/>
 * <p>
 * With Settings4j (default configuration) you can also place two Files into your Classpath:
 * <ol>
 * <li><code>"com/myCompany/myApp/MyDatasource"</code>: The File which defines the DataSource
 * <li><code>"com/myCompany/myApp/MyDatasource.properties"</code>: Some Properties, like which ObjectResolver should be
 * use.
 * </ol>
 * <p>
 * The File Content whould be the following:
 * 
 * <ul>
 * <li><b>objectResolverKey:</b> A unique identifier for the Object Resolver Implementation.
 * <li><b>bean-ref:</b> The bean name/id from the Spring-Context.
 * </ul>
 * <pre>
 * Classpath File "com/myCompany/myApp/MyDatasource.properties":
 * <div style="border-width:1px;border-style:solid;">
 * objectResolverKey=org.settings4j.objectresolver.SpringConfigObjectResolver
 * bean-ref=<b>SpringBeanReferneceId</b>
 * </div>
 * </pre>
 * <p>
 * <pre>
 * Classpath File "com/myCompany/myApp/MyDatasource":
 * <div style="border-width:1px;border-style:solid;">
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd"&gt;
 * &lt;beans&gt;
 *   &lt;!-- HSQLDB --&gt;
 *   &lt;bean id="<b>SpringBeanReferneceId</b>" class="org.springframework.jdbc.datasource.DriverManagerDataSource"&gt;
 *     &lt;property name="driverClassName"&gt;&lt;value&gt;org.hsqldb.jdbcDriver&lt;/value&gt;&lt;/property&gt;
 *     &lt;property name="url"&gt;&lt;value&gt;jdbc:hsqldb:mem:test&lt;/value&gt;&lt;/property&gt;
 *     &lt;property name="username"&gt;&lt;value&gt;sa&lt;/value&gt;&lt;/property&gt;
 *     &lt;property name="password"&gt;&lt;value&gt;&lt;/value&gt;&lt;/property&gt;
 *   &lt;/bean&gt;
 * &lt;/beans&gt;
 * </div>
 * </pre>
 * <p>
 * 
 * @author Harald.Brabenetz
 */
public class SpringConfigObjectResolver extends AbstractObjectResolver {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(SpringConfigObjectResolver.class);

    private static final String PROPERTY_BEAN_REF = "bean-ref";

    /** {@inheritDoc} */
    @Override
    protected Object contentToObject(final String key, final Properties properties, final byte[] content,
            final ContentResolver contentResolver) {

        String beanRef = properties.getProperty(PROPERTY_BEAN_REF);
        if (StringUtils.isEmpty(beanRef)) {
            beanRef = key.replace('/', '.');
        }

        final Object result = ByteArrayXMLApplicationContext.getBean(content, beanRef);
        if (result == null) {
            LOG.warn("SpringContext-Object with ID '" + beanRef + "' for Key '" + key + "' is null!");
        }
        return result;
    }

}
