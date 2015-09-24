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
package org.settings4j.objectresolver;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.util.Properties;

import org.settings4j.ContentResolver;

/**
 * This ObjectResolver convert a byte[] to an Object with the {@link XMLDecoder}.
 * <p>
 * Example: The following code should return a {@link javax.sql.DataSource} Object:<br>
 * <code>
 * Settings4j.getObject("com/myCompany/myApp/MyDatasource");
 * </code>
 * </p>
 * <p>
 * In normal Cases the DataSource comes from the JNDI-Context (available in most Servlet Containers).<br>
 * But in some environments there are no JNDI-Context (Commandline-Clients, UnitTests).<br>
 * </p>
 * <p>
 * With Settings4j (default configuration) you can also place two Files into your Classpath:
 * </p>
 * <ol>
 * <li><code>"com/myCompany/myApp/MyDatasource"</code>: The File which defines the DataSource
 * <li><code>"com/myCompany/myApp/MyDatasource.properties"</code>: Some Properties, like which ObjectResolver should be use.
 * </ol>
 * <p>
 * The File Content whould be the following: Classpath File "com/myCompany/myApp/MyDatasource.properties":
 * </p>
 *
 * <pre style="border-width:1px;border-style:solid;">
 * objectResolverKey=org.settings4j.objectresolver.JavaXMLBeansObjectResolver
 * cached=true
 * </pre>
 * <p>
 * Classpath File "com/myCompany/myApp/MyDatasource":
 * </p>
 *
 * <pre style="border-width:1px;border-style:solid;">
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;java version="1.6.0_05" class="java.beans.XMLDecoder"&gt;
 *  &lt;object class="org.springframework.jdbc.datasource.DriverManagerDataSource"&gt;
 *   &lt;void property="driverClassName"&gt;
 *    &lt;string&gt;org.hsqldb.jdbcDriver&lt;/string&gt;
 *   &lt;/void&gt;
 *   &lt;void property="password"&gt;
 *    &lt;string&gt;&lt;/string&gt;
 *   &lt;/void&gt;
 *   &lt;void property="url"&gt;
 *    &lt;string&gt;jdbc:hsqldb:mem:test&lt;/string&gt;
 *   &lt;/void&gt;
 *   &lt;void property="username"&gt;
 *    &lt;string&gt;sa&lt;/string&gt;
 *   &lt;/void&gt;
 *  &lt;/object&gt;
 * &lt;/java&gt;
 * </pre>
 *
 * @author Harald.Brabenetz
 */
public class JavaXMLBeansObjectResolver extends AbstractObjectResolver {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JavaXMLBeansObjectResolver.class);

    @Override
    protected Object contentToObject(final String key, final Properties properties, final byte[] content,
        final ContentResolver contentResolver) {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        XMLDecoder encoder = null;
        try {
            encoder = new XMLDecoder(byteArrayInputStream);
            encoder.setExceptionListener(new LogDecoderExceptionListener(key));
            return encoder.readObject();
        } finally {
            if (encoder != null) {
                encoder.close();
            }
        }
    }

    /**
     * Log out Exception during Encoding a byte[] to an Object<br>
     * <br>
     * Example:<br>
     * The {@link org.springframework.jdbc.datasource.AbstractDataSource} Object<br>
     * had Getter and Setter for "logWriter" who throws per default an {@link UnsupportedOperationException}.<br>
     *
     * @author Harald.Brabenetz
     */
    private static class LogDecoderExceptionListener implements ExceptionListener {

        private final String key;

        public LogDecoderExceptionListener(final String key) {
            super();
            this.key = key;
        }

        @Override
        public void exceptionThrown(final Exception e) {
            LOG.warn(//
                "Ignore error on decoding Object from key: {}! {}: {}; Set Loglevel DEBUG for more informations.", //
                this.key, e.getClass().getName(), e.getMessage());
            LOG.debug(e.getMessage(), e);
        }
    }
}
